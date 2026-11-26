package org.gridworld.search;

import java.util.Objects;
import java.util.Set;

import org.gridworld.domain.Position;

public record SearchState(
        Position position,
        int stepsUsed,
        long totalReward,
        Set<Position> visitedPositions) {

    public SearchState {
        Objects.requireNonNull(position, "Position cannot be null");
        visitedPositions = Set.copyOf(Objects.requireNonNull(
                visitedPositions, "Visited positions cannot be null"));
        if (stepsUsed < 0) {
            throw new IllegalArgumentException("Steps used cannot be negative");
        }
        if (!visitedPositions.contains(position)) {
            throw new IllegalArgumentException("Visited positions must contain current position");
        }
    }
}
