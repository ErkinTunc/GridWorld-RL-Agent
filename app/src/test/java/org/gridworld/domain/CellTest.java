package org.gridworld.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CellTest {

    // ------------------- Cell Tests --------------------

    @Test
    @DisplayName ("Cell stores type and reward correctly")
    void storesTypeAndReward() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertEquals(CellType.TERMINAL, cell.type());
        assertEquals(10, cell.reward());
    }

    @Test
    @DisplayName ("Cell allows negative reward")
    void allowsNegativeReward() {
        Cell cell = new Cell(CellType.TERMINAL, -10);

        assertEquals(-10, cell.reward());
    }

    // ---------------- Walkability Tests ----------------

    @Test
    @DisplayName ("Empty cell is walkable")
    void emptyCellIsWalkable() {
        Cell cell = new Cell(CellType.EMPTY, 0);

        assertTrue(cell.isWalkable());
    }

    @Test
    @DisplayName ("Terminal cell is walkable")
    void terminalCellIsWalkable() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertTrue(cell.isWalkable());
    }

    @Test
    @DisplayName ("Wall cell is not walkable")
    void wallCellIsNotWalkable() {
        Cell cell = new Cell(CellType.WALL, 0);

        assertFalse(cell.isWalkable());
    }

    // ----------------- Terminal Tests ------------------

    @Test
    @DisplayName ("Terminal cell is identified correctly")
    void identifiesTerminalCell() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertTrue(cell.isTerminal());
    }

    @Test
    @DisplayName ("Empty cell is not terminal")
    void emptyCellIsNotTerminal() {
        Cell cell = new Cell(CellType.EMPTY, 0);

        assertFalse(cell.isTerminal());
    }

    @Test
    @DisplayName ("Wall cell is not terminal")
    void wallCellIsNotTerminal() {
        Cell cell = new Cell(CellType.WALL, 0);

        assertFalse(cell.isTerminal());
    }

    // ---------------- Validation Tests -----------------

    @Test
    @DisplayName ("Cell constructor rejects null type")
    void rejectsNullType() {
        assertThrows(NullPointerException.class,
                () -> new Cell(null, 0));
    }
}