package uk.gov.ons.ctp.common.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * Used to trap any xsd validation failure events and throw a runtime exception to stop the loader in their tracks
 * @author philw
 */
public class XmlValidationEventHandler implements ValidationEventHandler {
  public boolean handleEvent(ValidationEvent event)  {
    throw new RuntimeException(
      "SEVERITY:  " + event.getSeverity() + 
      "MESSAGE:  " + event.getMessage() + 
      "LINKED EXCEPTION:  " + event.getLinkedException() + 
      "LOCATOR" + 
      "    LINE NUMBER:  " + event.getLocator().getLineNumber() + 
      "    COLUMN NUMBER:  " + event.getLocator().getColumnNumber() + 
      "    OFFSET:  " + event.getLocator().getOffset() + 
      "    OBJECT:  " + event.getLocator().getObject() + 
      "    NODE:  " + event.getLocator().getNode() + 
      "    URL:  " + event.getLocator().getURL()
      );
  }
}
