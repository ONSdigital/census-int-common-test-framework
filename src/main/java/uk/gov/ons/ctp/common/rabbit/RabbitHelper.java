package uk.gov.ons.ctp.common.rabbit;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.rabbitmq.client.AMQP.Queue.PurgeOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.common.event.EventPublisher.EventType;
import uk.gov.ons.ctp.common.event.EventPublisher.RoutingKey;

/**
 * This is a test support class for interacting with RabbitMQ.
 *
 * <p>It runs as a singleton and the connection is established on first usage. When connecting to
 * Rabbit it uses the connection details from a property file, with any or all of these fields
 * overridable with equivalent environment variables.
 *
 * <p>The RabbitMQ Java API does not support concurrent usage of the Channel object, so this class
 * enforces this restriction with method level synchronisation.
 */
public class RabbitInteraction {
  private static final Logger log = LoggerFactory.getLogger(RabbitInteraction.class);

  private static RabbitInteraction rabbitInteraction = null;

  private Connection rabbit;
  private Channel channel;
  private String exchange;

  private RabbitInteraction(RabbitConnectionDetails rabbitDetails, String exchange)
      throws CTPException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(rabbitDetails.getUser());
    factory.setPassword(rabbitDetails.getPassword());
    factory.setHost(rabbitDetails.getHost());
    factory.setPort(rabbitDetails.getPort());

    // Connect to rabbit
    try {
      this.rabbit = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      String errorMessage = "Failed to connect to RabbitMQ";
      log.error(errorMessage, e);
      throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
    }

    this.exchange = exchange;
  }

  public static synchronized RabbitInteraction instance(String exchange) throws CTPException {
    if (rabbitInteraction == null) {
      RabbitConnectionDetails rabbitDetails = new RabbitConnectionDetails();
      rabbitDetails.setUser(System.getenv("RABBIT_USER"));
      rabbitDetails.setPassword(System.getenv("RABBIT_PASSWORD"));
      rabbitDetails.setHost(System.getenv("RABBIT_HOST"));
      rabbitDetails.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));

      rabbitInteraction = new RabbitInteraction(rabbitDetails, exchange);
    }

    if (!rabbitInteraction.exchange.equals(exchange)) {
      throw new CTPException(
          Fault.BAD_REQUEST, "Cannot switch existing connection to different exchange");
    }

    // Make sure channel has been created
    rabbitInteraction.createChannelIfNeeded();

    return rabbitInteraction;
  }

  // Make sure channel has been created. It may need to be re-established if a previous command
  // failed.
  private synchronized void createChannelIfNeeded() throws CTPException {
    if (channel == null) {
      try {
        channel = rabbit.createChannel();
        channel.exchangeDeclare(exchange, "topic", true);
      } catch (IOException e) {
        channel = null;
        String errorMessage = "Failed to create RabbitMQ channel";
        log.error(errorMessage, e);
        throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
      }
    }
  }

  /**
   * This method releases the Rabbit connection.
   *
   * @throws CTPException if an error was detected.
   */
  public synchronized void close() throws CTPException {
    if (channel != null) {
      try {
        channel.close();
        channel = null;
      } catch (IOException | TimeoutException e1) {
        String errorMessage1 = "Failed to close RabbitMQ channel";
        log.error(errorMessage1, e1);
        throw new CTPException(Fault.SYSTEM_ERROR, e1, errorMessage1);
      }
    }

    if (rabbit != null) {
      try {
        rabbit.close(1000);
      } catch (IOException e) {
        String errorMessage = "Failed to close RabbitMQ connection";
        log.error(errorMessage, e);
        throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
      }
    }

    RabbitInteraction.rabbitInteraction = null;
  }

  /**
   * Creates and binds a queue. Rabbit doesn't mind if this method is rerun on for an existing
   * queue/binding.
   *
   * @param eventType is the type of events which we need a queue to receive.
   * @return a String containing the name of the queue. For the purposes of testing this is actually
   *     the routing key.
   * @throws CTPException if the queue or binding could not be created.
   */
  public synchronized String createQueue(EventType eventType) throws CTPException {
    String queueName;

    try {
      // Find routing key for supplied event type
      RoutingKey routingKey = RoutingKey.forType(eventType);
      if (routingKey == null) {
        String errorMessage = "Routing key for eventType '" + eventType + "' not configured";
        log.error(errorMessage);
        throw new UnsupportedOperationException(errorMessage);
      }

      // Use routing key for queue name as well as binding. This gives the queue a 'fake' name, but
      // it
      // saves the Cucumber tests from having to decide on a queue name
      String routingKeyName = routingKey.getKey();
      queueName = routingKeyName;

      // Create queue and binding
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, exchange, routingKey.getKey());
    } catch (IOException e) {
      channel = null; // Channel object now in broken state. Force recreation
      String errorMessage = "Failed to create/bind queue";
      log.error(errorMessage, e);
      throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
    }

    return queueName;
  }

  /**
   * Deletes any outstanding messages on a queue.
   *
   * @param queueName is the name of the queue to be cleared.
   * @return the number of messages deleted.
   * @throws CTPException if Rabbit failed during the queue purge.
   */
  public synchronized int flushQueue(String queueName) throws CTPException {
    try {
      PurgeOk result = channel.queuePurge(queueName);
      return result.getMessageCount();
    } catch (IOException e) {
      channel = null;
      String errorMessage = "Failed to flush queue '" + queueName + "'";
      log.error(errorMessage, e);
      throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
    }
  }

  public synchronized void sendMessage() {
    // PMB    EventPublisher eventPublisher = new EventPublisher(sender);
  }

  /**
   * Reads a message from the named queue. This method will wait for up to the specified number of
   * milliseconds for a message to appear on the queue.
   *
   * @param queueName is the name of the queue to read from.
   * @param maxWaitTimeMillis is the maximum amount of time the caller is prepared to wait for the
   *     message to appear.
   * @return a String containing the content of the message body, or null if no message was found
   *     before the timeout expired.
   * @throws CTPException if Rabbit threw an exception when we attempted to read a message.
   */
  public String getMessage(String queueName, long maxWaitTimeMillis) throws CTPException {
    final long startTime = System.currentTimeMillis();
    final long timeoutLimit = startTime + maxWaitTimeMillis;

    log.info(
        "Rabbit getMessage. Reading from queue '"
            + queueName
            + "'"
            + " within "
            + maxWaitTimeMillis
            + "ms");

    // Keep trying to read a message from rabbit, or we timeout waiting
    String messageBody;
    do {
      messageBody = getMessageNoWait(queueName);
      if (messageBody != null) {
        log.info("Message read from queue");
        break;
      }

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        break;
      }
    } while (messageBody == null && System.currentTimeMillis() < timeoutLimit);

    return messageBody;
  }

  /**
   * Read the next message from a queue.
   *
   * @param queueName holds the name of the queue to attempt the read from.
   * @return a String with the content of the message body, or null if there was no message to read.
   * @throws CTPException if Rabbit threw an exception during the message get.
   */
  private synchronized String getMessageNoWait(String queueName) throws CTPException {
    // Attempt to read a message from the queue
    GetResponse result;
    try {
      result = channel.basicGet(queueName, true);
    } catch (IOException e) {
      channel = null;
      String errorMessage = "Failed to flush queue '" + queueName + "'";
      log.error(errorMessage, e);
      throw new CTPException(Fault.SYSTEM_ERROR, e, errorMessage);
    }

    return result == null ? null : new String(result.getBody());
  }
}
