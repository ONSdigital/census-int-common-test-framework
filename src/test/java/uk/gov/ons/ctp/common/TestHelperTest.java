package uk.gov.ons.ctp.common;

import org.junit.Test;

public class TestHelperTest {

  @Test
  public void testValidateAsDateTimeWithValidDateTime() {
    TestHelper.validateAsDateTime("2019-04-10T15:32:38.941+01:00");
  }

  @Test(expected = AssertionError.class)
  public void testValidateAsDateTimeWithNullDateTime() {
    TestHelper.validateAsDateTime(null);
  }

  @Test(expected = AssertionError.class)
  public void testValidateAsDateTimeWithInvalidDateTime() {
    TestHelper.validateAsDateTime("2019-04-10T15:32pm");
  }

  @Test
  public void testValidateAsUUIDValid() {
    String uuid = "4c2cad7f-a942-4fe8-a04e-8d0fbd99f462";
    TestHelper.validateAsUUID(uuid);
  }

  @Test(expected = AssertionError.class)
  public void testValidateAsUUIDNull() {
    TestHelper.validateAsUUID(null);
  }

  @Test(expected = AssertionError.class)
  public void testValidateAsUUIDInvalid() {
    String uuid = "2344-234234";
    TestHelper.validateAsUUID(uuid);
  }
}
