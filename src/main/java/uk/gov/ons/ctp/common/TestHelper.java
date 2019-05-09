package uk.gov.ons.ctp.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import uk.gov.ons.ctp.common.time.DateTimeUtil;

/** Some individual methods for unit tests to reuse */
public class TestHelper {

  /**
   * Creates an instance of the target class, using its default constructor, and invokes the private
   * method, passing the provided params.
   *
   * @param target the Class owning the provate method
   * @param methodName the name of the private method we wish to invoke
   * @param params the params we wish to send to the private method
   * @return the object that came back from the method!
   * @throws Exception Something went wrong with reflection, Get over it.
   */
  public static Object callPrivateMethodOfDefaultConstructableClass(
      final Class<?> target, final String methodName, final Object... params) throws Exception {
    Constructor<?> constructor = target.getConstructor();
    Object instance = constructor.newInstance();
    Class<?>[] parameterTypes = new Class[params.length];
    for (int i = 0; i < params.length; i++) {
      parameterTypes[i] = params[i].getClass();
    }
    Method methodUnderTest = instance.getClass().getDeclaredMethod(methodName, parameterTypes);
    methodUnderTest.setAccessible(true);
    return methodUnderTest.invoke(instance, params);
  }

  /**
   * Creates and returns Test Date
   *
   * @param date date to parse
   * @return String test date as String
   */
  public static String createTestDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    ZonedDateTime zdt = ZonedDateTime.parse(date, formatter);
    ZonedDateTime compareDate = zdt.withZoneSameInstant(ZoneOffset.systemDefault());
    return formatter.format(compareDate);
  }

  /**
   * Validates that a dateTime string is formatted as: "yyyy-MM-dd'T'HH:mm:ss.SSSZ".
   *
   * @param dateTimeAsString is a string containing a dataTime value to check.
   * @throws AssertionError if the supplied dateTime string is null or in the correct time format.
   */
  public static void validateAsDateTime(String dateTimeAsString) {
    assertNotNull("datetime cannot be null", dateTimeAsString);

    String dateTimePattern = DateTimeUtil.DATE_FORMAT_IN_JSON;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
    try {
      dateTimeFormatter.parse(dateTimeAsString);
    } catch (DateTimeParseException e) {
      fail("String is not in date time format: " + dateTimeAsString);
    }
  }

  /**
   * Validates that the supplied string is in UUID format. If the string
   *
   * @param uuidAsString is the string to check.
   * @throws AssertionError if the string is null or not in UUID format.
   */
  public static void validateAsUUID(String uuidAsString) {
    assertNotNull("uuid cannot be null", uuidAsString);

    try {
      UUID.fromString(uuidAsString);
    } catch (IllegalArgumentException e) {
      fail("String is not in UUID format: " + uuidAsString);
    }
  }
}
