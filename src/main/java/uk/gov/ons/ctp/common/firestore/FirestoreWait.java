package uk.gov.ons.ctp.common.firestore;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;

/** This is a Firestore utility class to help test code interact with Firestore. */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirestoreWait {
  private static final Logger log = LoggerFactory.getLogger(FirestoreWait.class);

  @NonNull private String collection;

  @NonNull private String key;

  private Long newerThan;

  private String contentCheckPath;
  private String expectedValue;

  @NonNull private String timeout;

  /**
   * This method allows the caller to wait for an object to appear in Firestore. If the object is
   * found within the timeout period then it returns with the update time of the object, otherwise
   * it returns with null.
   *
   * <p>The caller can optionally wait for an object to be updated by specifying the updated
   * timestamp or by the content of a named field. If both criteria are specified then both
   * conditions must be satisfied before we regard the object has having arrived in Firestore.
   *
   * @param collection is the name of the collection to search, eg, 'case'
   * @param key is the key of the target object in the collection. eg,
   *     'f868fcfc-7280-40ea-ab01-b173ac245da3'
   * @param newerThan, is an optional argument to specify the timestamp that the an object must have
   *     been updated after. Waiting will continue until the until the update timestamp of the
   *     object is greater than this value, or the timeout period is reached. This value is the
   *     number of milliseconds since the epoch.
   * @param contentCheckPath, is an optional path to a field whose content we check to decide if an
   *     object has been updated, eg, 'contact.forename' or 'state'. If the target object does not
   *     contain the field with the expected value then waiting will continue until it does, or the
   *     timeout is reached.
   * @param expectedValue, is the value that a field must contain if 'contentCheckPath' has been
   *     specified.
   * @param timeout specifies how long the caller is prepared to wait for an object to appear in
   *     Firestore. This string must end with either a 'ms' suffix for milliseconds or 's' for
   *     seconds, eg, '750ms', '10s or '2.5s'.
   * @return The update timestamp of a found object, or null if not found within the timeout.
   * @throws CTPException in the event of a failure being detected. This will be of type
   *     Fault.VALIDATION_FAILED if any arguments fail validation, or type Fault.SYSTEM_ERROR if
   *     there is a Firestore exception.
   */
  public Long waitForObject() throws CTPException {
    long startTime = System.currentTimeMillis();
    long timeoutMillis = parseTimeoutString(timeout);
    long timeoutLimit = startTime + timeoutMillis;

    log.info(
        "Firestore wait. Looking for for collection '"
            + collection
            + "' to contain '"
            + key
            + "' "
            + "for up to '"
            + timeout
            + "'");

    if (newerThan != null) {
      log.info("Firestore wait. Update timestamp must be newer than '" + newerThan + "'");
    } else {
      log.info("Firestore wait. Not waiting on object update timestamp");
    }

    if (contentCheckPath != null) {
      log.info(
          "Firestore wait. Content of '"
              + contentCheckPath
              + "' to contain '"
              + expectedValue
              + "'");
    } else {
      log.info("Firestore wait. Not waiting on object state");
    }

    // Validate matching path+value arguments
    if (contentCheckPath != null ^ expectedValue != null) {
      String errorMessage =
          "Mismatched 'path' and 'value' arguments."
              + " Either both must be supplied or neither supplied";
      log.error(errorMessage);
      throw new CTPException(Fault.VALIDATION_FAILED, errorMessage);
    }

    // Wait until the object appears in Firestore, or we timeout waiting
    boolean found = false;
    long objectUpdateTimestamp;
    do {
      objectUpdateTimestamp =
          FirestoreService.instance()
              .objectExists(collection, key, newerThan, contentCheckPath, expectedValue);
      if (objectUpdateTimestamp > 0) {
        log.debug("Found object");
        found = true;
        break;
      }

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        break;
      }
    } while (System.currentTimeMillis() < timeoutLimit);

    if (!found) {
      log.debug("Failed to find object");
      return null;
    }

    return objectUpdateTimestamp;
  }

  private long parseTimeoutString(String timeout) throws CTPException {
    int multiplier;
    if (timeout.endsWith("ms")) {
      multiplier = 1;
    } else if (timeout.endsWith("s")) {
      multiplier = 1000;
    } else {
      String errorMessage =
          "timeout specification ('"
              + timeout
              + "') must end with either 'ms' for milliseconds or 's' for seconds";
      log.error(errorMessage);
      throw new CTPException(Fault.VALIDATION_FAILED, errorMessage);
    }

    String timeoutValue = timeout.replaceAll("(ms|s)", "");

    double timeoutAsDouble = Double.parseDouble(timeoutValue) * multiplier;
    return (long) timeoutAsDouble;
  }
}
