package org.gridworld.search;

import org.gridworld.domain.Position;

import java.util.Objects;
import java.util.Set;

public record SearchState(
        Position position,
        int stepsUsed,
        long totalReward,
        Set<Position> visitedPositions) {

    // ------------------- Constructor -------------------

    public SearchState {
        Objects.requireNonNull(position, "Position cannot be null");
        Objects.requireNonNull(visitedPositions, "Visited positions cannot be null");

        visitedPositions = Set.copyOf(visitedPositions);

        validateState(position, stepsUsed, visitedPositions);
    }

    // ---------------- Validation Methods ---------------

    private static void validateState(
            Position position,
            int stepsUsed,
            Set<Position> visitedPositions) {

        if (stepsUsed < 0) {
            throw new IllegalArgumentException(
                    "Steps used cannot be negative");
        }

        if (!visitedPositions.contains(position)) {
            throw new IllegalArgumentException(
                    "Visited positions must contain current position");
        }
    }
}