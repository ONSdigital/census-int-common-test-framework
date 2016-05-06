package uk.gov.ons.ctp.common.jaxrs;


import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is the generic CTP MessageBodyReader for XML.
 *
 * It will be instantiated with the relevant type in JerseyConfig for each of our endpoints.
 *
 * Note the variable simpleName which enables us to ignore the outer portions of the XML document representing
 * information relevant to the Web Service and focus on the inner portions representing the data we want to convert to
 * our domain model.
 */
@Consumes(MediaType.APPLICATION_XML)
@Slf4j
public class CTPXmlMessageBodyReader<T> implements MessageBodyReader<T> {

  // required due to type erasure
  private final Class<T> theType;

  public CTPXmlMessageBodyReader(Class<T> aType) {
    this.theType = aType;
  }

  private static Validator validator;

  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Override
  public final boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                                  final MediaType mediaType) {
    return true;
  }

  @Override
  public final T readFrom(final Class<T> type, final Type genericType,
                          final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
                          final InputStream entityStream) throws IOException, WebApplicationException {
    log.debug("Entering readFrom with theType= {} ", theType);

    try {
      XMLInputFactory xif = XMLInputFactory.newFactory();
      XMLStreamReader xsr = xif.createXMLStreamReader(entityStream);
      xsr.nextTag();
      String simpleName = theType.getSimpleName();
      while(!xsr.getLocalName().equals(simpleName)) {
        xsr.nextTag();
      }

      JAXBContext jc = JAXBContext.newInstance(theType);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      JAXBElement<T> jb = unmarshaller.unmarshal(xsr, theType);
      xsr.close();

      T requestObject = jb.getValue();

      if (hasValidAnnotation(annotations)) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(requestObject);
        int numberOfViolations = constraintViolations.size();
        if (numberOfViolations > 0) {
          log.error("{} constraints have been violated.", numberOfViolations);
          throw new CTPValidationException();
        }
      }

      return requestObject;
    } catch (Exception e) {
      log.error("Exception thrown while reading request body - {}", e);
      throw new CTPInvalidXmlBodyException();
    }
  }

  /**
   * Test if @Valid annotation is sent indicating validation required
   * @param annotations Annotations on method
   * @return boolean true if requires javax bean validation
   */
  private boolean hasValidAnnotation(final Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (Valid.class.equals(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }
}
