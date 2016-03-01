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

  public Response toResponse(CTPException e) {
    log.debug("Entering toResponse...");

    int status = 0;
    switch (e.getFault()) {
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
      log.error("Internal System Error", e);
      break;
    default:
      status = HttpStatus.I_AM_A_TEAPOT.value();
      break;
    }

    log.debug("Responding with {} to trapped exception {}", status, e.getMessage());
    return Response.status(status).entity(e).type(MediaType.APPLICATION_JSON).build();
  }

}
