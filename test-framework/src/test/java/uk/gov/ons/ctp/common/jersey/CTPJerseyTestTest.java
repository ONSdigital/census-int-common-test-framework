package uk.gov.ons.ctp.common.jersey;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;


/**
 * Unit test for the CTPJerseyTest class - a test for a test
 *
 */
public class CTPJerseyTestTest extends CTPJerseyTest {

  private static final int EXPECTED_ELEMENTS_IN_ARRAY = 100;

  @Override
  public Application configure() {
    // set the port to differ from that used by the real tests (9998) to avoid collision
    // when CI server runs parallel builds
    forceSet(TestProperties.CONTAINER_PORT, "9997");
    return super.init(HelloWorldEndpoint.class, null, null, null);
  }

  /**
   * A test
   */
  @Test
  public void testResponseCodesPass() {
    with("http://localhost:9997/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.OK)
     .andClose();
  }

  /**
   * A test
   */
  @Test(expected = AssertionError.class)
  public void testResponseCodesFail() {
    with("http://localhost:9997/hello/%s", "world")
     .assertResponseCodeIs(HttpStatus.I_AM_A_TEAPOT)
     .andClose();
  }

  /**
   * A test
   */
  @Test
  public void testResponseBodyPass() {
    with("http://localhost:9997/hello/%s", "world")
     .assertStringInBody("$.hairColour", "brown")
     .andClose();
  }

  /**
   * A test
   */
  @Test(expected = AssertionError.class)
  public void testResponseBodyFail() {
    with("http://localhost:9997/hello/%s", "world")
     .assertStringInBody("$.hairColour", "bright pink")
     .andClose();
  }

  /**
   * A test
   */
  @Test
  public void testResponseListPass() {
    with("http://localhost:9997/hello/list")
     .assertArrayLengthInBodyIs(2)
     .andClose();
  }

  /**
   * A test
   */
  @Test(expected = AssertionError.class)
  public void testResponseListFail() {
    with("http://localhost:9997/hello/list")
     .assertArrayLengthInBodyIs(EXPECTED_ELEMENTS_IN_ARRAY)
     .andClose();
  }

  /**
   * A test
   */
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

  /**
   * A test
   */
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
