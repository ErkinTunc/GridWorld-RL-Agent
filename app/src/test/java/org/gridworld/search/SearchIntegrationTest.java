package org.gridworld.search;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.environment.StepResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchIntegrationTest {

    // ---------------- Test Fixtures ----------------

    private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
    private static final Cell WALL = new Cell(CellType.WALL, 0);
    private static final Cell GOAL = new Cell(CellType.TERMINAL, 10);
    private static final Cell BONUS = new Cell(CellType.EMPTY, 3);
    private static final Cell TRAP = new Cell(CellType.TERMINAL, -10);

    private final Position start = new Position(0, 0);

    private final List<SearchAlgorithm> algorithms = List.of(
            new BacktrackingSearch(),
            new AStarSearch());

    // ---------------- Path Finding Tests ----------------

    @Test
    void solvesOpenGrid() {
        Cell[][] cells = {
                { EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, GOAL }
        };

        assertOptimalSolutions(cells, new Position(2, 2), 6, 6L, 60L);
    }

    @Test
    void solvesGridWithObstacles() {
        Cell[][] cells = {
                { EMPTY, WALL, GOAL },
                { EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, EMPTY }
        };

        assertOptimalSolutions(cells, new Position(0, 2), 6, 6L, 60L);
    }

    @Test
    void solvesNarrowPassage() {
        Cell[][] cells = {
                { EMPTY, EMPTY, WALL },
                { WALL, EMPTY, WALL },
                { WALL, EMPTY, GOAL }
        };

        assertOptimalSolutions(cells, new Position(2, 2), 6, 6L, 60L);
    }

    @Test
    void reportsUnreachableTarget() {
        Cell[][] cells = {
                { EMPTY, WALL, GOAL },
                { EMPTY, WALL, EMPTY }
        };

        GridWorld world = createWorld(cells);

        SearchProblem problem = new SearchProblem(
                world, start, new Position(0, 2), 4);

        for (SearchAlgorithm algorithm : algorithms) {
            SearchResult result = algorithm.search(problem);

            assertFalse(result.pathFound());
            assertTrue(result.path().isEmpty());
            assertTrue(result.actions().isEmpty());
            assertEquals(0L, result.totalReward());
            assertEquals(0L, result.totalCost());
            assertEquals(start, world.position());
        }
    }

    // ---------------- Reward Tests ----------------

    @Test
    void selectsHighestRewardPath() {
        Cell[][] cells = {
                { EMPTY, EMPTY, GOAL },
                { WALL, BONUS, TRAP }
        };

        assertOptimalSolutions(cells, new Position(0, 2), 4, 9L, 35L);
    }

    // ---------------- Assertion Methods ----------------

    private void assertOptimalSolutions(
            Cell[][] cells,
            Position goal,
            int maxSteps,
            long expectedReward,
            long expectedCost) {

        GridWorld world = createWorld(cells);

        SearchProblem problem = new SearchProblem(
                world, start, goal, maxSteps);

        for (SearchAlgorithm algorithm : algorithms) {
            SearchResult result = algorithm.search(problem);

            assertTrue(result.pathFound());
            assertEquals(expectedReward, result.totalReward());
            assertEquals(expectedCost, result.totalCost());
            assertEquals(start, world.position());

            assertPathStructure(result, goal, maxSteps);
            assertPathExecution(world, result, goal);
        }
    }

    private void assertPathStructure(
            SearchResult result,
            Position goal,
            int maxSteps) {

        assertEquals(start, result.path().getFirst());
        assertEquals(goal, result.path().getLast());

        assertEquals(result.actions().size() + 1, result.path().size());
        assertTrue(result.actions().size() <= maxSteps);
    }

    private void assertPathExecution(
            GridWorld world,
            SearchResult result,
            Position goal) {

        world.reset();

        long totalReward = 0L;
        int stepCount = result.actions().size();

        for (int i = 0; i < stepCount; i++) {
            Direction action = result.actions().get(i);
            Position previous = world.position();

            StepResult step = world.step(action);

            assertNotEquals(previous, step.position());
            assertEquals(result.path().get(i + 1), step.position());

            boolean isLastStep = i == stepCount - 1;
            assertEquals(isLastStep, step.isTerminal());

            totalReward += step.reward();
        }

        assertEquals(goal, world.position());
        assertEquals(result.totalReward(), totalReward);

        world.reset();
    }

    // ---------------- Helper Methods ----------------

    private GridWorld createWorld(Cell[][] cells) {
        return new GridWorld(new GridMap(cells), start, -1);
    }
}