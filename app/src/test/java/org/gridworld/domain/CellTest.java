package org.gridworld.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

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

    @Test
    void rejectsNullType() {
        assertThrows(NullPointerException.class,
                () -> new Cell(null, 0));
    }
}