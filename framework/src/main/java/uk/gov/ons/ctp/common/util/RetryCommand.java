package uk.gov.ons.ctp.common.util;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryCommand<T> {

  private int maxRetries;
  private int retryPause;

  public RetryCommand(int maxRetries, int retryPause) {
    this.maxRetries = maxRetries;
    this.retryPause = retryPause;
  }

  /**
   * take the function and try to execute it - if it fails retry
   * @param function
   * @return
   */
  public T run(Supplier<T> function) throws RuntimeException {
    log.debug("FAILED - Command failed, will be retried {} times",maxRetries);
    int retryCount = 0;
    T response = null;
    while (retryCount < maxRetries) {
      try {
        response = function.get();
        break;
      } catch (Exception ex) {
        retryCount++;
        log.info("FAILED - Command failed on retry {} of {} error: {}",retryCount,maxRetries,ex);
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
