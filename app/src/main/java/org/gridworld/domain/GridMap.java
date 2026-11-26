package org.gridworld.domain;

import java.util.Objects;

public final class GridMap {

    private final Cell[][] cells;

    // ------------- Constructor ------------

    public GridMap(Cell[][] cells) {

        validateGrid(cells);
        this.cells = copyGrid(cells);
    }

    // ------------- Public Methods -----------

    public boolean isInside(Position pos) {
        Objects.requireNonNull(pos, "Position cannot be null");

        boolean validRow = isInRange(pos.row(), cells.length);
        boolean validCol = isInRange(pos.col(), cells[0].length);

        return validRow && validCol;
    }

    /**
     * Checks if the specified position is walkable.
     * 
     * @param pos The position to check.
     * @return true if the position is walkable, false otherwise.
     */
    public boolean isWalkable(Position pos) {
        return isInside(pos) && getCellUnchecked(pos).isWalkable();
    }

    /**
     * Gets the cell at the specified position.
     * 
     * @param pos The position of the cell to get.
     * @return The cell at the specified position.
     */
    public Cell cellAt(Position pos) {
        validateBounds(pos);

        return getCellUnchecked(pos);
    }

    public int rewardUpperBound() {
        int maxReward = 0;

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isWalkable()) {
                    maxReward = Math.max(maxReward, cell.reward());
                }
            }
        }

        return maxReward;
    }

    // ------ Helper Methods -----

    private Cell getCellUnchecked(Position pos) {
        return cells[pos.row()][pos.col()];
    }

    // ------------ Validation Methods ---------------

    private void validateBounds(Position pos) {
        if (!isInside(pos)) {
            throw new IndexOutOfBoundsException(
                    "Outside grid: " + pos);
        }
    }

    private boolean isInRange(int index, int limit) {
        return index >= 0 && index < limit;
    }

    private void validateGrid(Cell[][] cells) {
        Objects.requireNonNull(cells, "Grid cannot be null");

        if (cells.length == 0) {
            throw new IllegalArgumentException("Empty grid");
        }

        Objects.requireNonNull(cells[0], "Row cannot be null");

        int cols = cells[0].length;

        if (cols == 0) {
            throw new IllegalArgumentException("Empty row");
        }

        for (Cell[] row : cells) {
            validateRow(row, cols);
        }
    }

    private void validateRow(Cell[] row, int cols) {
        Objects.requireNonNull(row, "Row cannot be null");

        if (row.length != cols) {
            throw new IllegalArgumentException("Grid must be rectangular");
        }

        for (Cell cell : row) {
            Objects.requireNonNull(cell, "Cell cannot be null");
        }
    }

    // ------------------ Utility Methods -----------------

    private Cell[][] copyGrid(Cell[][] cells) {
        Cell[][] copy = new Cell[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            copy[row] = cells[row].clone();
        }

        return copy;
    }
}