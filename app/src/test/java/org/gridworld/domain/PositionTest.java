package org.gridworld.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void storesCoordinates() {
        Position position = new Position(1, 2);

        assertEquals(1, position.row());
        assertEquals(2, position.col());
    }

    @Test
    void comparesPositionsByValue() {
        Position p1 = new Position(1, 2);
        Position p2 = new Position(1, 2);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void allowsNegativeCoordinates() {
        Position position = new Position(-1, 0);

        assertEquals(-1, position.row());
    }
}