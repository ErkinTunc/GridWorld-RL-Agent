package org.gridworld.scenario;

import org.gridworld.domain.Cell;
import org.gridworld.domain.Position;

public record Scenario(
        String title,
        Cell[][] cells,
        Position start,
        Position goal,
        int maxSteps,
        int stepPenalty) {

    // ---------------- Constructor ----------------

    public Scenario {
        cells = copyGrid(cells);
    }

    // ---------------- Public Methods ----------------

    @Override
    public Cell[][] cells() {
        return copyGrid(cells);
    }

    // ---------------- Helper Methods ----------------

    private static Cell[][] copyGrid(Cell[][] cells) {
        Cell[][] copy = new Cell[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            copy[row] = cells[row].clone();
        }

        return copy;
    }
}