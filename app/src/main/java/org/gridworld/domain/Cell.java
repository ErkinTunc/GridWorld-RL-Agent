package org.gridworld.domain;

import java.util.Objects;

public record Cell(CellType type, int reward) {

    // ------------------- Constructor -------------------

    public Cell {
        Objects.requireNonNull(type, "type");
    }

    // ------------------ Public Methods -----------------

    public boolean isWalkable() {
        return this.type != CellType.WALL;
    }

    public boolean isTerminal() {
        return this.type == CellType.TERMINAL;
    }
}