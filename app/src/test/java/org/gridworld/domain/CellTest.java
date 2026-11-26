package org.gridworld.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CellTest {

    // ------------------- Cell Tests --------------------

    @Test
    void storesTypeAndReward() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertEquals(CellType.TERMINAL, cell.type());
        assertEquals(10, cell.reward());
    }

    @Test
    void allowsNegativeReward() {
        Cell cell = new Cell(CellType.TERMINAL, -10);

        assertEquals(-10, cell.reward());
    }

    // ---------------- Walkability Tests ----------------

    @Test
    void emptyCellIsWalkable() {
        Cell cell = new Cell(CellType.EMPTY, 0);

        assertTrue(cell.isWalkable());
    }

    @Test
    void terminalCellIsWalkable() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertTrue(cell.isWalkable());
    }

    @Test
    void wallCellIsNotWalkable() {
        Cell cell = new Cell(CellType.WALL, 0);

        assertFalse(cell.isWalkable());
    }

    // ----------------- Terminal Tests ------------------

    @Test
    void identifiesTerminalCell() {
        Cell cell = new Cell(CellType.TERMINAL, 10);

        assertTrue(cell.isTerminal());
    }

    @Test
    void emptyCellIsNotTerminal() {
        Cell cell = new Cell(CellType.EMPTY, 0);

        assertFalse(cell.isTerminal());
    }

    @Test
    void wallCellIsNotTerminal() {
        Cell cell = new Cell(CellType.WALL, 0);

        assertFalse(cell.isTerminal());
    }

    // ---------------- Validation Tests -----------------

    @Test
    void rejectsNullType() {
        assertThrows(NullPointerException.class,
                () -> new Cell(null, 0));
    }
}