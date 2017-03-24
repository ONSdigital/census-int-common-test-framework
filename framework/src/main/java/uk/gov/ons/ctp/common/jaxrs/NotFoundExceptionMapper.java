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
 * This mapper will catch a javax.ws.rs.NotFoundException - not to be confused with our own NotFound handling by
 * throwing CTPException
 */
@Provider
@Slf4j
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  private static final String SPRING_SECURITY_WEB_EXCEPTION = "org.springframework.security.web";

  /**
   * This builds the JAX-RS response associated with the thrown NotFoundException
   * @param exception a NotFoundException
   * @return a JAX-RS response
   */
  public final Response toResponse(final NotFoundException exception) {
    log.debug("Entering toResponse...");

    boolean securityIssue = false;
    StackTraceElement[] stackTrace = exception.getStackTrace();
    for (int i = 0; i < stackTrace.length; i++) {
      if (stackTrace[i].getClassName().startsWith(SPRING_SECURITY_WEB_EXCEPTION)) {
        securityIssue = true;
        break;
      }
    }

    if (securityIssue) {
      log.debug("case where provided credentials are invalid");
      return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(new CTPException(CTPException.Fault.ACCESS_DENIED))
              .type(MediaType.APPLICATION_JSON).build();
    } else {
      log.error("In-built JAX-RS Not Found Exception", exception);
      CTPException ctpEx = new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, exception, exception.getMessage());
      return Response.status(HttpStatus.NOT_FOUND.value()).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }
  }
}
