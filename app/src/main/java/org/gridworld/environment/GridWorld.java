package org.gridworld.environment;

import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.domain.CellType;

import java.util.Objects;

public final class GridWorld {

    private final GridMap map;
    private final Position initialPosition;
    private final int stepPenalty;

    private Position currentPosition;
    private boolean done;

    // ---------------- Constructor ----------------

    public GridWorld(GridMap map, Position initialPosition, int stepPenalty) {

        this.map = Objects.requireNonNull(map, "Map cannot be null");
        this.initialPosition = Objects.requireNonNull(initialPosition, "Initial position cannot be null");

        validateInitialPosition(initialPosition);
        validateStepPenalty(stepPenalty);

        this.stepPenalty = stepPenalty;
        this.reset();
    }

    // -------------- Public Methods ---------------------

    public Position position() {
        return currentPosition;
    }

    public void reset() {
        currentPosition = initialPosition;
        done = false;
    }

    // ---------------- Validation Methods ----------------

    private void validateInitialPosition(Position position) {
        if (!map.isWalkable(position)) {
            throw new IllegalArgumentException(
                    "Initial position must be walkable");
        }

        if (map.cellAt(position).type() == CellType.TERMINAL) {
            throw new IllegalArgumentException(
                    "Initial position cannot be terminal");
        }
    }

    private void validateStepPenalty(int stepPenalty) {
        if (stepPenalty > 0) {
            throw new IllegalArgumentException(
                    "Step penalty must be non-positive");
        }
    }
}