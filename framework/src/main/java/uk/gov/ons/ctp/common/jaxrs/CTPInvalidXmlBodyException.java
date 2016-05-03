package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.WebApplicationException;

/**
 * A 'marker' exception to help differentiate exceptions thrown by CTPXmlMessageBodyReader
 *
 */
public class CTPInvalidXmlBodyException extends WebApplicationException {
  private static final long serialVersionUID = -8403440451717988745L;
}
