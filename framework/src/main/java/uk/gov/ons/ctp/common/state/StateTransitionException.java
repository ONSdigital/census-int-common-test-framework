package uk.gov.ons.ctp.common.state;

/**
 * An exception that indicates an illegal transition request has been made of a StateTransitionManager
 */
public class StateTransitionException extends Exception {

  private static final long serialVersionUID = -656111826776189524L;

  /**
   * Constructor
   */
  public StateTransitionException() {
    super();
  }

  /**
   * Constructor
   * @param message message
   * @param cause cause
   * @param enableSuppression enableSuppression
   * @param writableStackTrace writableStackTrace
   */
  public StateTransitionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Constructor
   * @param message message
   * @param cause cause
   */
  public StateTransitionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor
   * @param message message
   */
  public StateTransitionException(String message) {
    super(message);
  }

  /**
   * Constructor
   * @param cause cause
   */
  public StateTransitionException(Throwable cause) {
    super(cause);
  }

}
