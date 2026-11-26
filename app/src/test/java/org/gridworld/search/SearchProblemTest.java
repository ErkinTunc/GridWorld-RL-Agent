package org.gridworld.search;

import java.util.List;
import java.util.Set;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
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

    @Test
    void successorsUsePreviewRewardAndUpdateSearchState() {
        Position rewardPosition = new Position(0, 1);
        Position goal = new Position(0, 2);
        GridWorld world = new GridWorld(
                new GridMap(new Cell[][] {
                        {
                                new Cell(CellType.EMPTY, 0),
                                new Cell(CellType.EMPTY, 5),
                                new Cell(CellType.TERMINAL, 10)
                        }
                }),
                START,
                -1);
        SearchProblem problem = new SearchProblem(world, START, goal, 2);

        Transition transition = problem.successors(problem.initialState()).getFirst();

        assertEquals(Direction.RIGHT, transition.action());
        assertEquals(4, transition.reward());
        assertEquals(rewardPosition, transition.state().position());
        assertEquals(1, transition.state().stepsUsed());
        assertEquals(4, transition.state().totalReward());
        assertEquals(Set.of(START, rewardPosition), transition.state().visitedPositions());
        assertEquals(1, transition.cost());
    }

    @Test
    void successorsExcludeBlockedMovesAndRevisits() {
        Position goal = new Position(1, 1);
        GridWorld world = new GridWorld(
                new GridMap(new Cell[][] {
                        { new Cell(CellType.EMPTY, 0), new Cell(CellType.WALL, 0) },
                        { new Cell(CellType.EMPTY, 0), new Cell(CellType.TERMINAL, 10) }
                }),
                START,
                0);
        SearchProblem problem = new SearchProblem(world, START, goal, 3);

        List<Transition> initialSuccessors = problem.successors(problem.initialState());
        SearchState middleState = initialSuccessors.getFirst().state();
        List<Transition> middleSuccessors = problem.successors(middleState);

        assertEquals(1, initialSuccessors.size());
        assertEquals(Direction.DOWN, initialSuccessors.getFirst().action());
        assertEquals(1, middleSuccessors.size());
        assertEquals(Direction.RIGHT, middleSuccessors.getFirst().action());
    }

    @Test
    void successorsStopAtGoalAndStepLimit() {
        SearchProblem problem = new SearchProblem(WORLD, START, GOAL, 1);
        SearchState atStepLimit = new SearchState(START, 1, 0, Set.of(START));
        SearchState atGoal = new SearchState(GOAL, 1, 10, Set.of(START, GOAL));

        assertTrue(problem.successors(atStepLimit).isEmpty());
        assertTrue(problem.successors(atGoal).isEmpty());
        assertThrows(NullPointerException.class, () -> problem.successors(null));
    }
}
