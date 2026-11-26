package org.gridworld.search;

import org.gridworld.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchStateTest {

    private final Position position = new Position(1, 1);

    @Test
    void storesStateValues() {
        SearchState state = new SearchState(position, 2);

        assertEquals(position, state.position());
        assertEquals(2, state.stepsUsed());
    }

    @Test
    void comparesStatesByValue() {
        SearchState first = new SearchState(position, 2);
        SearchState second = new SearchState(new Position(1, 1), 2);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void distinguishesDifferentStepCounts() {
        SearchState first = new SearchState(position, 2);
        SearchState second = new SearchState(position, 6);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentPositions() {
        SearchState first = new SearchState(position, 2);
        SearchState second = new SearchState(new Position(1, 2), 2);

        assertNotEquals(first, second);
    }

    @Test
    void acceptsZeroSteps() {
        SearchState state = new SearchState(position, 0);

        assertEquals(0, state.stepsUsed());
    }

    @Test
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> new SearchState(null, 0));
    }

    @Test
    void rejectsNegativeSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchState(position, -1));
    }
}