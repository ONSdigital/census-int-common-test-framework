package uk.gov.ons.ctp.common.util;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.UUID;

public class StringToUUIDConverter extends BidirectionalConverter<String, UUID> {
  public UUID convertTo(String source, Type<UUID> destinationType, MappingContext mappingContext) {
    return UUID.fromString(source);
  }

  public String convertFrom(UUID source, Type<String> destinationType, MappingContext mappingContext) {
    return source.toString();
  }
}
