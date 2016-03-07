package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;

/**
 * Used by Jersey to catch and map CTP Business exceptions to suitable HTTP
 * responses.
 */
@Provider
@Slf4j
public class CTPExceptionMapper implements ExceptionMapper<CTPException> {

  /**
   * This builds the JAX-RS response associated with the thrown CTPException
   * @param exception a CTPException
   * @return a JAX-RS response
   */
  public final Response toResponse(final CTPException exception) {
    log.debug("Entering toResponse...");

    int status = 0;
    switch (exception.getFault()) {
    case RESOURCE_NOT_FOUND:
      status = HttpStatus.NOT_FOUND.value();
      break;
    case RESOURCE_VERSION_CONFLICT:
      status = HttpStatus.CONFLICT.value();
      break;
    case ACCESS_DENIED:
      status = HttpStatus.UNAUTHORIZED.value();
      break;
    case VALIDATION_FAILED:
      status = HttpStatus.BAD_REQUEST.value();
      break;
    case SYSTEM_ERROR:
      status = HttpStatus.INTERNAL_SERVER_ERROR.value();
      log.error("Internal System Error", exception);
      break;
    default:
      status = HttpStatus.I_AM_A_TEAPOT.value();
      break;
    }

    log.debug("Responding with {} to trapped exception {}", status, exception.getMessage());
    return Response.status(status).entity(exception).type(MediaType.APPLICATION_JSON).build();
  }

}
