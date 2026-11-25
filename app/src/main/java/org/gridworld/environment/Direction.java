package org.gridworld.environment;

import org.gridworld.domain.Position;

/**
 * Direction
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    // ---------------- Public Methods ----------------

        public static Position calculateNextPosition(Position from, Direction direction) {
        int row = from.row();
        int col = from.col();

        return switch (direction) {
            case UP -> new Position(row - 1, col);
            case DOWN -> new Position(row + 1, col);
            case LEFT -> new Position(row, col - 1);
            case RIGHT -> new Position(row, col + 1);
        };
    }

}
