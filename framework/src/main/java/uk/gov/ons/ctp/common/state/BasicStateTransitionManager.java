package uk.gov.ons.ctp.common.state;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;

/**
 * A Simple impl of StateTransitionManager
 *
 * @param <S> The state type we transit from and to
 * @param <E> The event type that effects the transition
 */
@Data
@Getter
public class BasicStateTransitionManager<S, E> implements StateTransitionManager<S, E> {

  private Map<S, Map<E, S>> transitions = new HashMap<>();

  @Override
  public S transition(S sourceState, E event) throws StateTransitionException {
    S destinationState = null;
    Map<E, S> outputMap = transitions.get(sourceState);
    if (outputMap != null) {
      destinationState = outputMap.get(event);
    }
    if (destinationState == null) {
      throw new StateTransitionException("State Transition from " + sourceState + " via " + event + " is verboten");
    }
    return destinationState;
  }

  @Override
  public void addTransition(S sourceState, E event, S destinationState) {
    Map<E, S> outputMap = transitions.get(sourceState);
    if (outputMap == null) {
      outputMap = new HashMap<>();
      transitions.put(sourceState,  outputMap);
    }
    outputMap.put(event, destinationState);
  }

}
