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

  public Response toResponse(Throwable exception) {
    log.debug("Entering toResponse...");

    log.error("Internal System Error", exception);
    CTPException ctpEx = new CTPException(CTPException.Fault.SYSTEM_ERROR, exception, exception.getMessage());
    log.error("Responding with ", ctpEx);
    return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).entity(ctpEx).type(MediaType.APPLICATION_JSON).build();
  }

}
