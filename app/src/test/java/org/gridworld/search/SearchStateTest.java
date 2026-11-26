package org.gridworld.search;

import org.gridworld.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchStateTest {

    private final Position position = new Position(0, 0);

    // ---------------------- Tests ----------------------

    @Test
    void storesStateValues() {
        SearchState state = new SearchState(position, 0, 0L);

        assertEquals(position, state.position());
        assertEquals(0, state.stepsUsed());
        assertEquals(0L, state.totalReward());
    }

    // ------------------ Equality Tests ------------------

    @Test
    void comparesStatesByValue() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 2, 5L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void distinguishesDifferentPositions() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(
                new Position(0, 1), 2, 5L);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentStepCounts() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 4, 5L);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentRewards() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 2, 10L);

        assertNotEquals(first, second);
    }

    // ---------------- Validation Tests -----------------

    @Test
    void acceptsNegativeReward() {
        SearchState state = new SearchState(position, 2, -5L);

        assertEquals(-5L, state.totalReward());
    }

    @Test
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> new SearchState(null, 0, 0L));
    }

    @Test
    void rejectsNegativeSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchState(position, -1, 0L));
    }
}