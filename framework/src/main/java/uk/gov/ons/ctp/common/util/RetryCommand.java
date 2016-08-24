package uk.gov.ons.ctp.common.util;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * A class which allows for the repeated execution of a provided lambda. If the
 * lambda throws an exception the RetryCommand sleeps, and attempts again, until
 * the max retries is exceeded, upon which it will throw the exception to the
 * caller.
 *
 * @param <T> the type that the lambda will supply as return
 */
@Slf4j
public class RetryCommand<T> {

  private int maxRetries;
  private int retryPause;

  /**
   * Step 1 - create the command up front with its retry and sleep config params
   * @param maxRetries how many times should we try?
   * @param retryPause how long should we sleep when we catch an exception?
   */
  public RetryCommand(int maxRetries, int retryPause) {
    this.maxRetries = maxRetries;
    this.retryPause = retryPause;
  }

  /**
   * Step 2 - run the lambda
   * Sleep and Retry when the call fails, until we meet the max retry value
   * @param function the lambda that is the doing the work we wish to retry
   * @return the value returned from the lambda
   */
  public T run(Supplier<T> function) throws RuntimeException {
    int retryCount = 0;
    T response = null;
    while (retryCount < maxRetries) {
      try {
        response = function.get();
        break;
      } catch (Exception ex) {
        retryCount++;
        log.info("FAILED - Command failed on retry {} of {} error: {}", retryCount, maxRetries, ex);
        if (retryCount >= maxRetries) {
          log.warn("Max retries exceeded.");
          throw ex;
        }
        try {
          Thread.sleep(retryPause);
        } catch (InterruptedException ie) {
          log.warn("Unexpected retry pause interrupted - in the interests of resilience, carrying on.");
        }
      }
    }
    return response;
  }
}
