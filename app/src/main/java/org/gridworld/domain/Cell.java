package org.gridworld.domain;

import java.util.Objects;

public record Cell(CellType type, int reward) {

    public Cell {
        Objects.requireNonNull(type, "type");
    }
}