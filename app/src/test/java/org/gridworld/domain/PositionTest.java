package org.gridworld.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    @DisplayName ("Position stores coordinates correctly")
    void storesCoordinates() {
        Position position = new Position(1, 2);

        assertEquals(1, position.row());
        assertEquals(2, position.col());
    }

    @Test
    @DisplayName ("Position compares coordinates by value")
    void comparesPositionsByValue() {
        Position p1 = new Position(1, 2);
        Position p2 = new Position(1, 2);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName ("Position allows negative coordinates")
    void allowsNegativeCoordinates() {
        Position position = new Position(-1, 0);

        assertEquals(-1, position.row());
    }
}