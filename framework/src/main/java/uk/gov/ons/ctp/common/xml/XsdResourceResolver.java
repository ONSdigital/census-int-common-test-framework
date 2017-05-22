package uk.gov.ons.ctp.common.xml;

import java.io.InputStream;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Used to resolve any included xsds from a primary xsd. The included xsds must be located relative to the primary xsd
 * @author philw
 *
 */
@Data
@AllArgsConstructor
public class XsdResourceResolver implements LSResourceResolver {

  private String xsdPathPrefix;
  
  public LSInput resolveResource(String type, String namespaceURI,
      String publicId, String systemId, String baseURI) {

    // note: in this sample, the XSD's are expected to be in the root of the classpath
    InputStream resourceAsStream = this.getClass().getClassLoader()
        .getResourceAsStream(xsdPathPrefix + "/" + systemId);
    return new Input(publicId, systemId, resourceAsStream);
  }

}