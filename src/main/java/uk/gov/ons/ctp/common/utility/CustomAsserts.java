package uk.gov.ons.ctp.common.utility;

import static org.junit.Assert.assertTrue;

/** CustomAsserts contains some asserts not available in the standard asserts */
public class CustomAsserts {

  /**
   * This idea was borrowed from JUnit5 and allows us to assert that a piece of code will throw the
   * expected exception, and allow us to examine the exception. This also comes in useful if we are
   * performing asserts on multiple items in a stream, where the test would otherwise exit on the
   * first exception.
   *
   * @param expected - a class object for the expected exception
   * @param code - an arbitrary executable lambda which we expect to throw an exception
   * @param <T> - generics boilerplate
   * @return the thrown exception, if it happens
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> T assertThrows(
      Class<? extends Throwable> expected, Executable code) {
    try {
      code.run();
    } catch (Throwable t) {
      var message =
          String.format(
              "Was expecting an exception of type %s but got ^%s instead",
              expected.getCanonicalName(), t.getClass().getCanonicalName());
      assertTrue(message, t.getClass().isAssignableFrom(expected));
      return (T) t;
    }
    throw new AssertionError(
        String.format(
            "Was expecting an exception of type %s but nothing was thrown",
            expected.getCanonicalName()));
  }

  /** A functional interface for executing arbitrary code and seeing if it throws exceptions */
  public interface Executable {

    void run() throws Throwable;
  }
}
