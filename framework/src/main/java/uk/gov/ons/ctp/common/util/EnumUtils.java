package uk.gov.ons.ctp.common.util;

import java.util.Optional;

public class EnumUtils {
  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String value) {
    if (enumClass == null) {
      throw new IllegalArgumentException("cant be null");

    }

    for (Enum<?> enumValue : enumClass.getEnumConstants()) {
      if (enumValue.toString().equalsIgnoreCase(value)) {
        return (T) enumValue;
      }
    }

    StringBuilder errorMessage = new StringBuilder();
    boolean firstTime = true;
    for (Enum<?> enumValue : enumClass.getEnumConstants()) {
      errorMessage.append(firstTime ? "" : ", ").append(enumValue);
      firstTime = false;
    }
    throw new IllegalArgumentException(value + " is invalid value. only " + errorMessage);
  }

  public static <T extends Enum<T>> Optional<T> getOptionalEnumFromString(Class<T> enumClass, String value) {
    T inst = null;
    try {
      inst = getEnumFromString(enumClass, value);
    } catch (IllegalArgumentException iae) {
      // That's OK - is optional!
    }
    return Optional.ofNullable((T) inst);
  }
}
