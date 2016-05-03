package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;

/**
 * This mapper will catch all unexpected exceptions from our application and map
 * them to an HTTP 500 System Error
 */
@Provider
@Slf4j
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

  public static final String BAD_JSON = "Provided json is incorrect.";
  public static final String BAD_XML = "Provided xml is incorrect.";
  public static final String JSON_FAILS_VALIDATION = "Provided json fails validation.";

  /**
   * This builds the JAX-RS response associated with the thrown Throwable
   *
   * @param exception a Throwable
   * @return a JAX-RS response
   */
  public final Response toResponse(final Throwable exception) {
    log.error("Internal System Error", exception);
    HttpStatus status = null;
    Exception ex = null;

    if (exception instanceof CTPInvalidBodyException) {
      status = HttpStatus.BAD_REQUEST;
      ex = new CTPException(CTPException.Fault.VALIDATION_FAILED, exception, BAD_JSON);
    } else if (exception instanceof CTPInvalidXmlBodyException) {
      status = HttpStatus.BAD_REQUEST;
      ex = new CTPException(CTPException.Fault.VALIDATION_FAILED, exception, BAD_XML);
    } else {
      if (exception instanceof CTPValidationException) {
        status = HttpStatus.BAD_REQUEST;
        ex = new CTPException(CTPException.Fault.VALIDATION_FAILED, exception, JSON_FAILS_VALIDATION);
      } else {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        ex = new CTPException(CTPException.Fault.SYSTEM_ERROR, exception, exception.getMessage());
      }
    }

    log.error("Responding with ", ex);
    return Response.status(status.value()).entity(ex).type(MediaType.APPLICATION_JSON).build();
  }
}
