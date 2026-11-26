package org.gridworld.environment;

import org.gridworld.domain.Position;

public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    // ------------------ Public Methods -----------------

    public Position moveFrom(Position from) {
        int row = from.row();
        int col = from.col();

        return switch (this) {
            case UP -> new Position(row - 1, col);
            case DOWN -> new Position(row + 1, col);
            case LEFT -> new Position(row, col - 1);
            case RIGHT -> new Position(row, col + 1);
        };
    }
}