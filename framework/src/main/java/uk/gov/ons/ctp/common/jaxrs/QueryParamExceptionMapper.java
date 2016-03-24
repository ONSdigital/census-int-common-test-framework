package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ParamException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;

/**
* This mapper will catch framework exceptions caused by illegal argument values eg Invalid enum type 
* mapping sent
*/
@Provider
@Slf4j
public class QueryParamExceptionMapper implements ExceptionMapper<ParamException> {

  /**
   * This builds the JAX-RS response associated with the thrown IllegalArgumentException
   * @param exception a IllegalArgumentException
   * @return a JAX-RS response
   */
  public final Response toResponse(final ParamException exception) {
    log.debug("Entering toResponse...");

    log.error("Illegal argument provided", exception);
    CTPException ctpEx = new CTPException(CTPException.Fault.VALIDATION_FAILED,
        exception,
        exception.getCause().getMessage());
    return Response.status(HttpStatus.BAD_REQUEST.value()).entity(ctpEx).type(MediaType.APPLICATION_JSON).build();
  }

}

