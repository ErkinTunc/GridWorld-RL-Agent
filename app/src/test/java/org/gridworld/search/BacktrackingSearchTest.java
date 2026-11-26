package org.gridworld.search;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BacktrackingSearchTest {

    // ---------------- Test Fixtures ----------------

    private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
    private static final Cell WALL = new Cell(CellType.WALL, 0);
    private static final Cell TERMINAL = new Cell(CellType.TERMINAL, 10);

    private final Position start = new Position(0, 0);
    private final SearchAlgorithm algorithm = new BacktrackingSearch();

    // ---------------- Path Finding Tests ----------------

    @Test
    void findsPath() {
        Position goal = new Position(0, 1);

        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, TERMINAL }
        }, goal, 1);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());
        assertEquals(List.of(start, goal), result.path());
        assertEquals(List.of(Direction.RIGHT), result.actions());
        assertEquals(9L, result.totalReward());
        assertEquals(2L, result.totalCost());
    }

    @Test
    void reportsWhenNoPathExists() {
        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, WALL, TERMINAL }
        }, new Position(0, 2), 4);

        SearchResult result = algorithm.search(problem);

        assertFalse(result.pathFound());
        assertTrue(result.path().isEmpty());
        assertTrue(result.actions().isEmpty());
    }

    // ---------------- Reward Tests ----------------

    @Test
    void prefersHigherRewardOverShorterPath() {
        Cell bonus = new Cell(CellType.EMPTY, 3);

        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, bonus, TERMINAL }
        }, new Position(0, 2), 4);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());

        assertEquals(List.of(
                Direction.RIGHT,
                Direction.LEFT,
                Direction.RIGHT,
                Direction.RIGHT), result.actions());

        assertEquals(12L, result.totalReward());
        assertEquals(32L, result.totalCost());
    }

    @Test
    void supportsNegativeTotalReward() {
        Cell negativeGoal = new Cell(CellType.TERMINAL, -10);

        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, negativeGoal }
        }, new Position(0, 1), 1);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());
        assertEquals(-11L, result.totalReward());
        assertEquals(12L, result.totalCost());
    }

    @Test
    void supportsRewardBeyondIntegerRange() {
        Cell bonus = new Cell(CellType.EMPTY, Integer.MAX_VALUE);
        Cell goal = new Cell(CellType.TERMINAL, Integer.MAX_VALUE);

        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, bonus, goal }
        }, new Position(0, 2), 2);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());
        assertEquals(4_294_967_292L, result.totalReward());
        assertEquals(4L, result.totalCost());
    }

    @Test
    void continuesSearchingAfterFirstSolution() {
        Cell bonus = new Cell(CellType.EMPTY, 3);

        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, bonus },
                { EMPTY, TERMINAL }
        }, new Position(1, 1), 2);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());

        assertEquals(List.of(
                Direction.RIGHT,
                Direction.DOWN), result.actions());

        assertEquals(11L, result.totalReward());
        assertEquals(11L, result.totalCost());
    }

    // ---------------- Edge Case Tests ----------------

    @Test
    void respectsMaxSteps() {
        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY, EMPTY, TERMINAL }
        }, new Position(0, 2), 1);

        assertFalse(algorithm.search(problem).pathFound());
    }

    @Test
    void startEqualsGoalProducesEmptyActionSequence() {
        SearchProblem problem = createProblem(new Cell[][] {
                { EMPTY }
        }, start, 4);

        SearchResult result = algorithm.search(problem);

        assertTrue(result.pathFound());
        assertEquals(List.of(start), result.path());
        assertTrue(result.actions().isEmpty());
        assertEquals(0L, result.totalCost());
        assertEquals(0L, result.totalReward());
    }

    // ---------------- State Integrity Tests ----------------

    @Test
    void doesNotChangeWorldState() {
        GridMap map = new GridMap(new Cell[][] {
                { EMPTY, TERMINAL }
        });

        GridWorld world = new GridWorld(map, start, -1);

        SearchProblem problem = new SearchProblem(
                world, start, new Position(0, 1), 1);

        assertTrue(algorithm.search(problem).pathFound());
        assertEquals(start, world.position());
    }

    // ---------------- Validation Tests ----------------

    @Test
    void rejectsNullProblem() {
        assertThrows(NullPointerException.class,
                () -> algorithm.search(null));
    }

    // ---------------- Helper Methods ----------------

    private SearchProblem createProblem(
            Cell[][] cells, Position goal, int maxSteps) {

        GridMap map = new GridMap(cells);
        GridWorld world = new GridWorld(map, start, -1);

        return new SearchProblem(world, start, goal, maxSteps);
    }
}