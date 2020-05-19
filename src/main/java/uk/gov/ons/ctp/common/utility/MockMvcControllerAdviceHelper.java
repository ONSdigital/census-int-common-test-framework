package uk.gov.ons.ctp.common.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/** Mock Mvc Controller Advice Helper */
public class MockMvcControllerAdviceHelper<T> extends ExceptionHandlerExceptionResolver {

  private static final String ERROR_MSG = "Unable to instantiate exception handler %s";
  private final Class<T> exceptionHandlerClass;

  /**
   * MockMvcControllerAdviceHelper constructor
   *
   * @param exceptionHandlerClass Exception Handler Class
   */
  public MockMvcControllerAdviceHelper(Class<T> exceptionHandlerClass) {
    super();
    getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
    this.exceptionHandlerClass = exceptionHandlerClass;
    afterPropertiesSet();
  }

  /**
   * Default MockMvcControllerAdviceHelper Constructor
   *
   * @param exceptionHandlerClass Exception Handler Class
   * @return MockMvcControllerAdviceHelper object
   */
  public static <T> MockMvcControllerAdviceHelper<T> mockAdviceFor(Class<T> exceptionHandlerClass) {
    return new MockMvcControllerAdviceHelper<T>(exceptionHandlerClass);
  }

  /**
   * Exception Handler getter
   *
   * @param handlerMethod HandlerMethod
   * @param exception Exception
   * @return ServletInvocableHandlerMethod containing new exceptionhandler and method.
   */
  protected ServletInvocableHandlerMethod getExceptionHandlerMethod(
      HandlerMethod handlerMethod, Exception exception) {
    Object exceptionHandler = null;

    try {
      exceptionHandler = exceptionHandlerClass.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException
        | InstantiationException
        | NoSuchMethodException
        | IllegalArgumentException
        | InvocationTargetException
        | SecurityException e) {
      throw new RuntimeException(
          String.format(ERROR_MSG, exceptionHandlerClass.getCanonicalName()), e);
    }

    Method method =
        new ExceptionHandlerMethodResolver(exceptionHandlerClass).resolveMethod(exception);
    return new ServletInvocableHandlerMethod(exceptionHandler, method);
  }
}
