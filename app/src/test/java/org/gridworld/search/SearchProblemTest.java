package org.gridworld.search;

import java.util.Set;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.GridWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchProblemTest {

    private static final Position START = new Position(0, 0);
    private static final Position GOAL = new Position(0, 1);
    private static final GridWorld WORLD = new GridWorld(
            new GridMap(new Cell[][] {
                    { new Cell(CellType.EMPTY, 0), new Cell(CellType.TERMINAL, 10) }
            }),
            START,
            0);

    @Test
    void acceptsWalkableStartAndGoalWithNonNegativeStepLimit() {
        assertDoesNotThrow(() -> new SearchProblem(WORLD, START, GOAL, 1));
    }

    @Test
    void rejectsNegativeStepLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SearchProblem(WORLD, START, GOAL, -1));
    }

    @Test
    void rejectsNonWalkableGoal() {
        GridWorld worldWithWall = new GridWorld(
                new GridMap(new Cell[][] {
                        { new Cell(CellType.EMPTY, 0), new Cell(CellType.WALL, 0) }
                }),
                START,
                0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new SearchProblem(worldWithWall, START, GOAL, 1));
    }

    @Test
    void initialStateStartsAtStartWithNoStepsOrReward() {
        SearchState state = new SearchProblem(WORLD, START, GOAL, 1).initialState();

        assertEquals(START, state.position());
        assertEquals(0, state.stepsUsed());
        assertEquals(0, state.totalReward());
        assertEquals(Set.of(START), state.visitedPositions());
    }

    @Test
    void identifiesGoalStateByPosition() {
        SearchProblem problem = new SearchProblem(WORLD, START, GOAL, 1);
        SearchState goalState = new SearchState(GOAL, 1, 10, Set.of(START, GOAL));
        SearchState otherState = problem.initialState();

        assertTrue(problem.isGoal(goalState));
        assertFalse(problem.isGoal(otherState));
        assertThrows(NullPointerException.class, () -> problem.isGoal(null));
    }
}
