package org.gridworld.environment;

import org.gridworld.domain.Position;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DirectionTest {

    private final Position position = new Position(1, 1);

    // ------------------ Movement Tests -----------------

    @Test
    @DisplayName ("Direction moves position correctly")
    void movesUp() {
        assertEquals(new Position(0, 1),
                Direction.UP.moveFrom(position));
    }

    @Test
    @DisplayName ("Direction moves position correctly")
    void movesDown() {
        assertEquals(new Position(2, 1),
                Direction.DOWN.moveFrom(position));
    }

    @Test
    @DisplayName ("Direction moves position correctly")
    void movesLeft() {
        assertEquals(new Position(1, 0),
                Direction.LEFT.moveFrom(position));
    }

    @Test
    @DisplayName ("Direction moves position correctly")
    void movesRight() {
        assertEquals(new Position(1, 2),
                Direction.RIGHT.moveFrom(position));
    }

    // ---------------- Validation Tests -----------------

    @Test
    @DisplayName ("Direction throws exception when given null position")
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> Direction.UP.moveFrom(null));
    }
}