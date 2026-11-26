package org.gridworld.environment;

import org.gridworld.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DirectionTest {

    private final Position position = new Position(1, 1);

    // ------------------ Movement Tests -----------------

    @Test
    void movesUp() {
        assertEquals(new Position(0, 1),
                Direction.UP.moveFrom(position));
    }

    @Test
    void movesDown() {
        assertEquals(new Position(2, 1),
                Direction.DOWN.moveFrom(position));
    }

    @Test
    void movesLeft() {
        assertEquals(new Position(1, 0),
                Direction.LEFT.moveFrom(position));
    }

    @Test
    void movesRight() {
        assertEquals(new Position(1, 2),
                Direction.RIGHT.moveFrom(position));
    }

    // ---------------- Validation Tests -----------------

    @Test
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> Direction.UP.moveFrom(null));
    }
}