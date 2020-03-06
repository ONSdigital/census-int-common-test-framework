package uk.gov.ons.ctp.common;

import static org.junit.Assert.*;
import static uk.gov.ons.ctp.common.utility.CustomAsserts.assertThrows;

import org.junit.Test;

public class CustomAssertsTests {

  @Test
  public void assertThrowsHappilyPassesWhenExceptionThrown() {

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          throw new IllegalArgumentException();
        });
  }

  @Test
  public void assertThrowsWillFailIfExceptionNotThrown() {

    try {
      assertThrows(IllegalArgumentException.class, () -> {});
    } catch (AssertionError error) {
      assertTrue(error.getMessage().contains("but nothing was thrown"));
    }
  }

  @Test
  public void assertThrowsWillFailIfWrongExceptionThrown() {

    try {
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            throw new NullPointerException("oops");
          });
    } catch (AssertionError error) {
      assertTrue(error.getMessage().contains("but got"));
    }
  }

  @Test
  public void assertThrowsReturnsTheThrownException() {

    String message = "this is the message I will throw";

    IllegalArgumentException iae =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              throw new IllegalArgumentException(message);
            });

    assertNotNull(iae);
    assertEquals(message, iae.getMessage());
  }
}
