package uk.gov.ons.ctp.common.jersey;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;


public class CTPJerseyTestTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    // set the port to differ from that used by the real tests (9998) to avoid collision
    // when CI server runs parallel builds
    forceSet(TestProperties.CONTAINER_PORT, "9997");
    return super.init(HelloWorldEndpoint.class, null, null, null);
  }

  @Test
  public void testResponseCodesPass() {
    with("http://localhost:9997/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.OK)
     .andClose();
  }

  @Test(expected=AssertionError.class)
  public void testResponseCodesFail() {
    with("http://localhost:9997/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.I_AM_A_TEAPOT)
     .andClose();
  }

  @Test
  public void testResponseBodyPass() {
    with("http://localhost:9997/hello/%s", "world")
     .assertStringInBody("$.hairColour", "brown")
     .andClose();
  }

  @Test(expected=AssertionError.class)
  public void testResponseBodyFail() {
    with("http://localhost:9997/hello/%s", "world")
     .assertStringInBody("$.hairColour", "bright pink")
     .andClose();
  }

  @Test
  public void testResponseListPass() {
    with("http://localhost:9997/hello/list")
     .assertArrayLengthInBodyIs(2)
     .andClose();
  }

  @Test(expected=AssertionError.class)
  public void testResponseListFail() {
    with("http://localhost:9997/hello/list")
     .assertArrayLengthInBodyIs(100)
     .andClose();
  }

  @Test
  public void testAssertionOrderCanBeChanged() {
    with("http://localhost:9997/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.OK)
     .assertStringInBody("$.hairColour", "brown")
     .andClose();

    with("http://localhost:9997/hello/%s", "world")
     .assertStringInBody("$.hairColour", "brown")
     .assertResponseCodeIs(HttpStatus.OK)
     .andClose();
  }
  
  @Test
  public void testExceptionTesting() {
    with("http://localhost:9997/hello/ctpexception/%s", "world")
    .assertResponseCodeIs(HttpStatus.NOT_FOUND)
    .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
    .assertTimestampExists()
    .assertMessageEquals("something hit the fan")
    .andClose();
  }
}
