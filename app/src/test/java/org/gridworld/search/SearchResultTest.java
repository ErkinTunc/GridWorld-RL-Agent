package org.gridworld.search;

import java.util.ArrayList;
import java.util.List;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import  org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchResultTest {

    private final Position start = new Position(0, 0);
    private final Position goal = new Position(0, 1);

    private final List<Position> path = List.of(start, goal);
    private final List<Direction> actions = List.of(Direction.RIGHT);

    // ------------------- Result Tests -------------------

    @Test
    @DisplayName ("Search result stores values correctly")
    void storesSearchResultValues() {
        SearchResult result = new SearchResult(
                true, path, actions, 1L, 10L, 2);

        assertTrue(result.pathFound());
        assertEquals(path, result.path());
        assertEquals(actions, result.actions());
        assertEquals(1L, result.totalCost());
        assertEquals(10L, result.totalReward());
        assertEquals(2, result.expandedNodes());
    }

    @Test
    @DisplayName ("Search result accepts empty path when no path found")
    void acceptsEmptyPathWhenNoPathFound() {
        SearchResult result = new SearchResult(false, List.of(), List.of(), 0, 0, 2);

        assertFalse(result.pathFound());
        assertTrue(result.path().isEmpty());
        assertTrue(result.actions().isEmpty());
    }

    @Test
    @DisplayName ("Search result supports values beyond integer range")
    void supportsValuesBeyondIntegerRange() {
        long totalCost = 5_000_000_000L;
        long totalReward = 3_000_000_000L;

        SearchResult result = new SearchResult(
                true, path, actions,
                totalCost, totalReward, 2);

        assertEquals(totalCost, result.totalCost());
        assertEquals(totalReward, result.totalReward());
    }

    // --------------- Encapsulation Tests ----------------

    @Test
    @DisplayName ("Search result protects path from external changes")
    void protectsPathFromExternalChanges() {
        List<Position> mutablePath = new ArrayList<>(path);

        SearchResult result = new SearchResult(true, mutablePath, actions, 1, 10, 2);

        mutablePath.clear();

        assertEquals(path, result.path());
    }

    @Test
    @DisplayName ("Search result protects actions from external changes")
    void protectsActionsFromExternalChanges() {
        List<Direction> mutableActions = new ArrayList<>(actions);

        SearchResult result = new SearchResult(true, path, mutableActions, 1, 10, 2);

        mutableActions.clear();

        assertEquals(actions, result.actions());
    }


    @Test
    @DisplayName ("Search result protects path from external changes")
    void preventsModificationOfReturnedPath() {
        List<Position> mutablePath = new ArrayList<>(path);

        SearchResult result = new SearchResult(true, mutablePath, actions, 1, 10, 2);

        assertThrows(UnsupportedOperationException.class,
                () -> result.path().clear());
    }

    @Test
    @DisplayName ("Search result protects actions from external changes")
    void preventsModificationOfReturnedActions() {
        List<Direction> mutableActions = new ArrayList<>(actions);

        SearchResult result = new SearchResult(true, path, mutableActions, 1, 10, 2);

        assertThrows(UnsupportedOperationException.class,
                () -> result.actions().clear());
    }

    // ---------------- Validation Tests -----------------

    @Test
    @DisplayName ("Search result rejects null path")
    void rejectsNullPath() {
        assertThrows(NullPointerException.class,
                () -> new SearchResult(true, null, actions, 1, 10, 2));
    }

    @Test
    @DisplayName ("Search result rejects null actions")
    void rejectsNullActions() {
        assertThrows(NullPointerException.class,
                () -> new SearchResult(true, path, null, 1, 10, 2));
    }
}