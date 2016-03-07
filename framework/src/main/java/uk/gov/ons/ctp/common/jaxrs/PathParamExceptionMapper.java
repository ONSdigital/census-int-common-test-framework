package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ParamException.PathParamException;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;

/**
 * This mapper will catch framework exceptions caused by invalid path param values ie "hello" provided where Integer is
 * expected
 */
@Provider
@Slf4j
public class PathParamExceptionMapper implements ExceptionMapper<PathParamException> {

  /**
   * This builds the JAX-RS response associated with the thrown PathParamException
   * @param exception a PathParamException
   * @return a JAX-RS response
   */
  public final Response toResponse(final PathParamException exception) {
    log.debug("Entering toResponse...");

    log.error("Path Param Exception due to invalid data", exception);
    CTPException ctpEx = new CTPException(CTPException.Fault.VALIDATION_FAILED,
        exception,
        exception.getCause().getMessage());
    return Response.status(HttpStatus.BAD_REQUEST.value()).entity(ctpEx).type(MediaType.APPLICATION_JSON).build();
  }

}
