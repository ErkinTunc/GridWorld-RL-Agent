package org.gridworld.environment;

import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.domain.CellType;
import org.gridworld.domain.Cell;

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

    public boolean isWalkable(Position position) {
        return map.isWalkable(position);
    }

    public void reset() {
        currentPosition = initialPosition;
        done = false;
    }

    public StepResult step(Direction direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");

        if (done) {
            return new StepResult(currentPosition, 0, true);
        }

        StepResult result = calculateTransition(currentPosition, direction);

        currentPosition = result.position();
        done = result.done();

        return result;
    }

    public StepResult preview(Position from, Direction direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");

        if (!map.isWalkable(from)) {
            throw new IllegalArgumentException("Position must be walkable");
        }

        if (map.cellAt(from).type() == CellType.TERMINAL) {
            return new StepResult(from, 0, true);
        }

        return calculateTransition(from, direction);
    }

    // ---------------- Helper Methods ----------------

    private StepResult calculateTransition(Position from, Direction direction) {
        Position nextPosition = direction.calculateNextPosition(from,direction);

        if (!map.isWalkable(nextPosition)) {
            return new StepResult(from, stepPenalty, false);
        }

        Cell cell = map.cellAt(nextPosition);

        int reward = Math.addExact(stepPenalty, cell.reward());
        boolean terminal = cell.type() == CellType.TERMINAL;

        return new StepResult(nextPosition, reward, terminal);
    }

    // ---------------- Validation Methods ----------------

    private void validateInitialPosition(Position position) {
        if (!map.isWalkable(position)) {
            throw new IllegalArgumentException("Initial position must be walkable");
        }

        if (map.cellAt(position).type() == CellType.TERMINAL) {
            throw new IllegalArgumentException("Initial position cannot be terminal");
        }
    }

    private void validateStepPenalty(int stepPenalty) {
        if (stepPenalty > 0) {
            throw new IllegalArgumentException("Step penalty must be non-positive");
        }
    }
}