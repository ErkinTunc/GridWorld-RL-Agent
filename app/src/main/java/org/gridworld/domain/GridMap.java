package org.gridworld.domain;

import java.util.Objects;

/**
 * Represents a grid map in the GridWorld environment.
 * It consists of a 2D array of cells, where each cell can be of different types (e.g., empty, wall, terminal) and has an associated reward value.
 * The GridMap class provides methods to check if a position is inside the grid, if it is walkable, and to retrieve the cell at a specific position.
 * It also provides a method to get the upper bound of rewards in the grid, which can be useful for search algorithms.
 * @see Cell
 * @see Position
 */
public final class GridMap {

    private final Cell[][] cells;

    // ------------- Constructor ------------

    /**
     * Creates a new GridMap instance with the specified 2D array of cells.
     * The grid must be rectangular, and all cells must be non-null.
     * @param cells the 2D array of cells representing the grid
     * @throws NullPointerException if the cells array or any of its rows or cells are null
     * @throws IllegalArgumentException if the grid is empty, has empty rows, or
     */
    public GridMap(Cell[][] cells)throws NullPointerException, IllegalArgumentException {

        validateGrid(cells);
        this.cells = copyGrid(cells);
    }

    // ------------- Public Methods -----------

    /**
     * Checks if the specified position is inside the bounds of the grid.
     * @param pos the position to check
     * @return true if the position is inside the grid, false otherwise
     * @throws NullPointerException if the position is null
     */
    public boolean isInside(Position pos) throws NullPointerException {
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

    /**
     * Returns the upper bound of rewards in the grid.
     * @return the maximum reward value among all walkable cells in the grid
     */
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

    /**
     * Returns the cell at the specified position without performing bounds checking.
     * This method assumes that the position is valid and inside the grid.
     * @param pos the position of the cell to get
     * @return the cell at the specified position
     * @throws IndexOutOfBoundsException if the position is outside the grid
     */
    private Cell getCellUnchecked(Position pos) throws IndexOutOfBoundsException {
        if (!isInside(pos)) {
            throw new IndexOutOfBoundsException(
                    "Position is outside the grid: " + pos);
        }
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

    /**
     * Validates a single row of the grid to ensure it is not null, has the expected number of columns, and contains no null cells.
     * @param row the row to validate
     * @param cols the expected number of columns in the row
     * @throws NullPointerException if the row is null
     * @throws IllegalArgumentException if the row has the wrong number of columns or contains null cells
     */
    private void validateRow(Cell[] row, int cols) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(row, "Row cannot be null");

        if (row.length != cols) {
            throw new IllegalArgumentException("Grid must be rectangular");
        }

        for (Cell cell : row) {
            Objects.requireNonNull(cell, "Cell cannot be null");
        }
    }

    // ------------------ Utility Methods -----------------

    /**
     * Creates a deep copy of the given 2D array of cells.
     * This method ensures that the original grid remains unchanged when the copy is modified.
     * Use it with caution, as it can be expensive for large grids.
     * @param cells the 2D array of cells to copy
     * @return  a new 2D array of cells that is a deep copy of the original
     */
    private Cell[][] copyGrid(Cell[][] cells) {
        Cell[][] copy = new Cell[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            copy[row] = cells[row].clone();
        }

        return copy;
    }
}