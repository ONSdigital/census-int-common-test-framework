package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;

/**
 * This mapper will catch an implicit JAXRS NotFoundException caused by the client requesting a 
 * completely wrong URL - not to be confused with our own NotFound handling by throwing CTPException
 */
@Provider
@Slf4j
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  public Response toResponse(NotFoundException exception) {
    log.debug("Entering toResponse...");

    log.error("In built JAXRS Not Found Exception", exception);
    CTPException ctpEx = new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, exception, exception.getMessage());
    return Response.status(HttpStatus.NOT_FOUND.value()).entity(ctpEx).type(MediaType.APPLICATION_JSON).build();
  }

}
