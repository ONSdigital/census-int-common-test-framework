package uk.gov.ons.ctp.common.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
 
/**
 * This util class is constructed with the path root within the classpath, and the primary xsd file name in that path, to be used to validate
 * an xml file as it is unmarshalled
 * @author philw
 *
 * @param <T> the root entity we will unmarshal xml to
 */
@Data
@AllArgsConstructor
public class ValidatingXmlUnmarshaller<T> {
  private String xsdPath;
  private String xsdName;
  private Class<T> entityClass;
  
  /**
   * using this instance of our unmarshaller, unmarshal the given xml and validate it against the predetermined xsd in the root path,
   * as well as resolving in the same root path, any included xsds
   * @param xmlLocation
   * @return
   * @throws Exception
   */
  public T unmarshal(String xmlLocation) throws Exception {
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    InputStream xsdStream = this.getClass().getClassLoader().getResourceAsStream(xsdPath + "/" + xsdName);
    StreamSource xsdSource = new StreamSource(xsdStream);
    sf.setResourceResolver(new XsdResourceResolver(xsdPath));

    Schema schema = sf.newSchema(xsdSource);

    File file = new File(xmlLocation);
    JAXBContext jaxbContext = JAXBContext.newInstance(entityClass);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    jaxbUnmarshaller.setSchema(schema);
    jaxbUnmarshaller.setEventHandler(new XmlValidationEventHandler());
    @SuppressWarnings("unchecked")
    T unmarshalledObj = (T) jaxbUnmarshaller.unmarshal(file);
    return unmarshalledObj;
  }
}