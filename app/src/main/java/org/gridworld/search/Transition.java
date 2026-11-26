package org.gridworld.search;

import java.util.Objects;

import org.gridworld.environment.Direction;

/**
 * Represents a transition in the search space.
 * A transition is defined by a current state, an action taken, a reward received, and a cost incurred.
 * It is used to represent the result of taking an action in a given state, leading to a new state.
 * The Transition class is immutable and provides a convenient way to encapsulate the information related to a transition in the search process.
 * @param state the current state
 * @param action the action taken
 * @param reward the reward received
 * @param cost the cost incurred
 */
 
public record Transition(
        SearchState state,
        Direction action,
        int reward,
        long cost) {

    public Transition {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
    }
}