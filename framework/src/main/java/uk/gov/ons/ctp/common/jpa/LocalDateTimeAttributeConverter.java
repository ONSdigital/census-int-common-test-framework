package uk.gov.ons.ctp.common.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter to/from java8 LocalDateTime and sql Timestamp
 * NOT CURRENTLY IN USE BUT POST 2016 WE SHOULD SWITCH TO USE LOCALDATETIME IN PREF
 * TO SQL TIMESTAMP AND JAVA DATE SO LEFT AS A PLACEHOLDER
 */
@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
    return (localDateTime == null ? null : Timestamp.valueOf(localDateTime));
  }

  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
    return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime());
  }
}
