package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.WebApplicationException;

/**
 * A 'marker' exception to help differentiate exceptions thrown by CTPMessageBodyReader
 *
 */
public class CTPValidationException extends WebApplicationException {
  private static final long serialVersionUID = 2208393094760032137L;

}
