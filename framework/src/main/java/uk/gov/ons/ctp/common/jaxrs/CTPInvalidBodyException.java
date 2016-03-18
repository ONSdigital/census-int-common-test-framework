package uk.gov.ons.ctp.common.jaxrs;

import javax.ws.rs.WebApplicationException;

/**
 * A 'marker' exception to help differentiate exceptions thrown by CTPMessageBodyReader 
 *
 */
public class CTPInvalidBodyException extends WebApplicationException {
	private static final long serialVersionUID = 570913984669363923L;
}
