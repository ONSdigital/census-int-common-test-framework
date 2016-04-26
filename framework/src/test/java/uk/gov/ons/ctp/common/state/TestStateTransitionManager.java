package uk.gov.ons.ctp.common.state;

import java.util.HashMap;
import java.util.Map;

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

  private StateTransitionManager<TestState, TestEvent> stm;

  /**
   * Setup the transitions
   */
  @Before
  public void setup() {
    Map<TestState, Map<TestEvent, TestState>> transitions = new HashMap<>();
    Map<TestEvent, TestState> transitionMapForSubmitted = new HashMap<>();
    transitionMapForSubmitted.put(TestEvent.REQUEST_DISTRIBUTED, TestState.PENDING);
    transitions.put(TestState.SUBMITTED, transitionMapForSubmitted);
    stm = new BasicStateTransitionManager<>(transitions);
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
