package uk.gov.ons.ctp.common.jaxrs;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * This class is the generic CTP MessageBodyReader. It will be instantiated with the relevant type in JerseyConfig for
 * each of our endpoints.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class CTPMessageBodyReader<T> implements MessageBodyReader<T> {

  // required due to type erasure
  private final Class<T> theType;

  public CTPMessageBodyReader(Class<T> aType) {
    this.theType = aType;
  }

  private static Validator validator;
  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Override
  public final boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
      final MediaType mediaType) {
    return true;
  }

  @Override
  public final T readFrom(final Class<T> type, final Type genericType,
      final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
      final InputStream entityStream) throws IOException, WebApplicationException {
    log.debug("Entering readFrom with theType= {} ", theType);
    try {
      StringWriter writer = new StringWriter();
      IOUtils.copy(entityStream, writer, "UTF-8");
      String requestJson = writer.toString();
      log.debug("requestJson = {}", requestJson);

      ObjectMapper mapper = new ObjectMapper();
      T requestObject = mapper.readValue(requestJson, theType);
      log.debug("requestObject = {}", requestObject);

      Set<ConstraintViolation<T>> constraintViolations = validator.validate(requestObject);
      int numberOfViolations = constraintViolations.size();
      if (numberOfViolations > 0) {
        log.error("{} constraints have been violated.", numberOfViolations);
        return null;
      } else {
        return requestObject;
      }
    } catch (Exception e) {
      log.error("Exception thrown while reading request body - {}", e);
      return null;
    }
  }
}
