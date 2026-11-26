package org.gridworld.search;

import org.gridworld.domain.Position;

import java.util.Objects;

public record SearchState(
        Position position,
        int stepsUsed,
        long totalReward) {

    // ------------------- Constructor -------------------

    public SearchState {
        Objects.requireNonNull(position, "Position cannot be null");

        if (stepsUsed < 0) {
            throw new IllegalArgumentException(
                    "Steps used cannot be negative");
        }
    }
}