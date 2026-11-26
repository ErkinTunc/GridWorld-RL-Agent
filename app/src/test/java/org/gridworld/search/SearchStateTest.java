package org.gridworld.search;

import org.gridworld.domain.Position;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchStateTest {

    private final Position position = new Position(0, 0);
    private final Set<Position> visited = Set.of(position);

    // ---------------------- Tests ----------------------

    @Test
    void storesStateValues() {
        SearchState state = new SearchState(position, 0, 0, visited);

        assertEquals(position, state.position());
        assertEquals(0, state.stepsUsed());
        assertEquals(0L, state.totalReward());
        assertEquals(visited, state.visitedPositions());
    }

    // ------------------ Equality Tests ------------------

    @Test
    void comparesStatesByValue() {
        SearchState first = new SearchState(position, 2, 5, visited);
        SearchState second = new SearchState(position, 2, 5, visited);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void distinguishesDifferentPositions() {
        Position otherPosition = new Position(0, 1);
        Set<Position> positions = Set.of(position, otherPosition);

        SearchState first = new SearchState(position, 2, 5, positions);
        SearchState second = new SearchState(otherPosition, 2, 5, positions);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentStepCounts() {
        SearchState first = new SearchState(position, 2, 5, visited);
        SearchState second = new SearchState(position, 4, 5, visited);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentRewards() {
        SearchState first = new SearchState(position, 2, 5, visited);
        SearchState second = new SearchState(position, 2, 10, visited);

        assertNotEquals(first, second);
    }

    @Test
    void distinguishesDifferentVisitedPositions() {
        Set<Position> otherVisited = Set.of(position, new Position(0, 1));

        SearchState first = new SearchState(position, 2, 5, visited);
        SearchState second = new SearchState(position, 2, 5, otherVisited);

        assertNotEquals(first, second);
    }

    // --------------- Encapsulation Tests ----------------

    @Test
    void protectsVisitedPositionsFromExternalChanges() {
        Set<Position> mutableVisited = new HashSet<>(visited);

        SearchState state = new SearchState(position, 0, 0, mutableVisited);

        mutableVisited.clear();

        assertEquals(visited, state.visitedPositions());
    }

    // ---------------- Validation Tests -----------------

    @Test
    void acceptsNegativeReward() {
        SearchState state = new SearchState(position, 2, -5, visited);

        assertEquals(-5L, state.totalReward());
    }

    @Test
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                () -> new SearchState(null, 0, 0, visited));
    }

    @Test
    void rejectsNegativeSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchState(position, -1, 0, visited));
    }

    @Test
    void rejectsNullVisitedPositions() {
        assertThrows(NullPointerException.class,
                () -> new SearchState(position, 0, 0, null));
    }

    @Test
    void rejectsMissingCurrentPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchState(position, 0, 0, Set.of()));
    }
}