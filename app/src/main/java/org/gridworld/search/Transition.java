package org.gridworld.search;

import java.util.Objects;

import org.gridworld.environment.Direction;

public record Transition(SearchState state, Direction action, int reward, long cost) {

    public Transition {
        Objects.requireNonNull(state, "State cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        if (cost < 0) {
            throw new IllegalArgumentException("Transition cost cannot be negative");
        }
    }
}
