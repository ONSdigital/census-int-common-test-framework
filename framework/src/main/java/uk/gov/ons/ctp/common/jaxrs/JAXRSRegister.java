package uk.gov.ons.ctp.common.jaxrs;

import java.util.Arrays;
import java.util.List;

import uk.gov.ons.ctp.common.jackson.JacksonConfigurator;

/**
 * Simply a home for a static method that returns the list of classes that an application using the JAXRS classes
 * from common framework should register within its own Jersey ResourceConfig. This is needed as a result of Jersey/SpringBoot 1.4 not playing
 * nicely together. Once one or the other changes to allow jersey to scan packages in the classpath of the spring boot
 * executable jar, we can replace the use of this class with an application call to packages("uk.gov.ons.ctp"); in the constructor of the applications
 * ResourceConfig
 *
 */
public class JAXRSRegister {
  @SuppressWarnings("rawtypes")
  public static List<Class> listCommonTypes() {
    return Arrays.asList(
//            AccessDeniedExceptionMapper.class,
            JacksonConfigurator.class,
            CTPExceptionMapper.class,
            GeneralExceptionMapper.class,
            PathParamExceptionMapper.class,
            QueryParamExceptionMapper.class,
            NotFoundExceptionMapper.class
    );
  }
}
