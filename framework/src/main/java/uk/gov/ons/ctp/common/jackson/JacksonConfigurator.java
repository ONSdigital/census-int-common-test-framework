package uk.gov.ons.ctp.common.jackson;


import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Custom Jackson configurator - allows to handle output formats
 */
@Provider
@Produces("application/json")
public class JacksonConfigurator implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper = new ObjectMapper();

  /**
   * The constructor for our Custom Jackson configurator.
   */
  public JacksonConfigurator() {
      mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

  /**
   * This returns the Jackson ObjectMapper.
   * @param arg0
   * @return the Jackson ObjectMapper
   */
    @Override
    public final ObjectMapper getContext(final Class<?> arg0) {
        return mapper;
    }

}
