package org.gridworld.search;

import org.gridworld.domain.Position;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchStateTest {

    private final Position position = new Position(0, 0);

    // ---------------------- Tests ----------------------

    @Test
    @DisplayName ("Search state stores position, steps used and total reward correctly")
    void storesStateValues() {
        SearchState state = new SearchState(position, 0, 0L);

        assertEquals(position, state.position());
        assertEquals(0, state.stepsUsed());
        assertEquals(0L, state.totalReward());
    }

    // ------------------ Equality Tests ------------------
    @DisplayName ("Search state compares by value correctly")
    @Test
    void comparesStatesByValue() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 2, 5L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    @DisplayName ("Search state distinguishes different positions correctly")
    void distinguishesDifferentPositions() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(
                new Position(0, 1), 2, 5L);

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName ("Search state distinguishes different step counts correctly")
    void distinguishesDifferentStepCounts() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 4, 5L);

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName ("Search state distinguishes different rewards correctly")
    void distinguishesDifferentRewards() {
        SearchState first = new SearchState(position, 2, 5L);
        SearchState second = new SearchState(position, 2, 10L);

        assertNotEquals(first, second);
    }

    // ---------------- Validation Tests -----------------

    @Test
    @DisplayName ("Search state accepts negative reward")
    void acceptsNegativeReward() {
        SearchState state = new SearchState(position, 2, -5L);

        assertEquals(-5L, state.totalReward());
    }

    @Test
    @DisplayName ("Search state rejects null position")
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> new SearchState(null, 0, 0L));
    }

    @Test
    @DisplayName ("Search state rejects negative steps")
    void rejectsNegativeSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchState(position, -1, 0L));
    }
}