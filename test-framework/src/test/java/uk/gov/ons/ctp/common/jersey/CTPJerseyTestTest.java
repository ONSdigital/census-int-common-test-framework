package uk.gov.ons.ctp.common.jersey;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;


public class CTPJerseyTestTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(HelloWorldEndpoint.class, null, null, null);
  }

  @Test
  public void testResponseCodes() {
    with("http://localhost:9998/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.OK)
     .andClose();
  }

  @Test
  public void testResponseBody() {
    with("http://localhost:9998/hello/%s", "world")
     .assertStringInBody("$.hairColour", "brown")
     .andClose();
  }
  @Test
  public void testResponseList() {
    with("http://localhost:9998/list")
     .assertArrayLengthInBodyIs(2)
     .andClose();
  }

  @Test
  public void testAssertionOrderCanBeChanged() {
    with("http://localhost:9998/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.OK)
     .assertStringInBody("$.hairColour", "brown")
     .andClose();

    with("http://localhost:9998/hello/%s", "world")
     .assertStringInBody("$.hairColour", "brown")
     .assertResponseCodeIs(HttpStatus.OK)
     .andClose();
  }
  
  @Test
  public void testExceptionTesting() {
    with("http://localhost:9998/hello/ctpexception/%s", "world")
    .assertResponseCodeIs(HttpStatus.NOT_FOUND)
    .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
    .assertTimestampExists()
    .assertMessageEquals("something hit the fan")
    .andClose();
  }
}
