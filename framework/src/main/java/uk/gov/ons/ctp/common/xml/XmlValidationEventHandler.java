package uk.gov.ons.ctp.common.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * Used to trap any xsd validation failure events and throws an exception to stop the loader in their tracks
 */
public class XmlValidationEventHandler implements ValidationEventHandler {
  public boolean handleEvent(ValidationEvent event) {
    throw new RuntimeException(String.format("SEVERITY: %s - MESSAGE: %s - LINKED EXCEPTION: %s - LOCATOR: " +
            "LINE NUMBER: %d - COLUMN NUMBER: %d - OFFSET: %d - OBJECT: %s - NODE: %s - URL: %s", event.getSeverity(),
            event.getMessage(), event.getLinkedException(), event.getLocator().getLineNumber(),
            event.getLocator().getColumnNumber(), event.getLocator().getOffset(), event.getLocator().getObject(),
            event.getLocator().getNode(), event.getLocator().getURL()));
  }
}
