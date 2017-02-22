package uk.gov.ons.ctp.common.distributed;

/**
 * Used to indicate to the caller that the optimistic locking of an object in the distributed store
 * has failed - another instance has updated the object since the local manager last read it
 */
public class LockingException extends Exception {

  private static final long serialVersionUID = 7509566308215475091L;

  public LockingException() {
  }

  public LockingException(String message) {
    super(message);
  }

  public LockingException(Throwable cause) {
    super(cause);
  }

  public LockingException(String message, Throwable cause) {
    super(message, cause);
  }

  public LockingException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
