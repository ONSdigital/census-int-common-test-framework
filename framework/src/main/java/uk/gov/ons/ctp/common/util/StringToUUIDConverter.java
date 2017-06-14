package uk.gov.ons.ctp.common.util;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.UUID;

/**
 * Created by stevee on 14/06/2017.
 */
public class StringToUUIDConverter extends BidirectionalConverter<String, UUID> {

  public UUID convertTo(String source, Type<UUID> destinationType) {
    return UUID.fromString(source);
  }

  public String convertFrom(UUID source, Type<String> destinationType) {
    return source.toString();
  }

}
