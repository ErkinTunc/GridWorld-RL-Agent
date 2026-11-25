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

    public void reset() {
        currentPosition = initialPosition;
        done = false;
    }

    public StepResult step(Direction direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");

        if (done) {
            return new StepResult(currentPosition, 0, true);
        }

        Position nextPosition = calculateNextPosition(direction);

        if (!map.isWalkable(nextPosition)) {
            return new StepResult(currentPosition, stepPenalty, false);
        }

        return moveTo(nextPosition);
    }

    // ---------------- Helper Methods ----------------

    private StepResult moveTo(Position nextPosition) {
        Cell cell = map.cellAt(nextPosition);

        int reward = Math.addExact(stepPenalty, cell.reward());

        currentPosition = nextPosition;
        done = cell.type() == CellType.TERMINAL;

        return new StepResult(currentPosition, reward, done);
    }

    private Position calculateNextPosition(Direction direction) {
        int row = currentPosition.row();
        int col = currentPosition.col();

        return switch (direction) {
            case UP -> new Position(row - 1, col);
            case DOWN -> new Position(row + 1, col);
            case LEFT -> new Position(row, col - 1);
            case RIGHT -> new Position(row, col + 1);
        };
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