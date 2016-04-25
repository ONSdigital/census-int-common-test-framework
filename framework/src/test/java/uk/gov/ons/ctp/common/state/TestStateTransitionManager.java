package uk.gov.ons.ctp.common.state;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A test of the state transition manager
 * It simply has to test a single good and a single bad transition - all it is testing is the underlying mechanism,
 * not a real implementation, where we will want to assert all of the valid and invalid transitions
 *
 */
public class TestStateTransitionManager {

  private StateTransitionManager<TestState, TestEvent> stm = new BasicStateTransitionManager<>();

  /**
   * Setup the transitions
   */
  @Before
  public void setup() {
   stm.addTransition(TestState.SUBMITTED, TestEvent.REQUEST_DISTRIBUTED, TestState.PENDING);
  }

  /**
   * test a valid transition
   * @throws StateTransitionException shouldn't!
   */
  @Test
  public void testGood() throws StateTransitionException {
   Assert.assertEquals(TestState.PENDING, stm.transition(TestState.SUBMITTED, TestEvent.REQUEST_DISTRIBUTED));
  }

  /**
   * tests a bad transition
   * @throws StateTransitionException we expect this
   */
  @Test(expected = StateTransitionException.class)
  public void testBad() throws StateTransitionException {
   stm.transition(TestState.SUBMITTED, TestEvent.REQUEST_ACCEPTED);
  }
}
