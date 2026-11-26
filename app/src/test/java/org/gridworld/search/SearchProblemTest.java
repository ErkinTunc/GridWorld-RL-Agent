package org.gridworld.search;

import java.util.List;
import java.util.Set;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchProblemTest {

    private static final int MAX_STEPS = 4;

    private final Position start = new Position(0, 0);
    private final Position middle = new Position(0, 1);
    private final Position goal = new Position(0, 2);
    private final Position bonusPosition = new Position(1, 1);
    private final Position wallPosition = new Position(1, 0);

    private GridWorld world;
    private SearchProblem problem;

    // ---------------------- Setup ----------------------

    @BeforeEach
    void setUp() {
        Cell empty = new Cell(CellType.EMPTY, 0);
        Cell wall = new Cell(CellType.WALL, 0);
        Cell bonus = new Cell(CellType.EMPTY, 3);
        Cell terminal = new Cell(CellType.TERMINAL, 10);
        Cell trap = new Cell(CellType.TERMINAL, -10);

        GridMap map = new GridMap(new Cell[][] {
                { empty, empty, terminal },
                { wall, bonus, trap }
        });

        world = new GridWorld(map, start, -1);
        problem = new SearchProblem(world, start, goal, MAX_STEPS);
    }

    // ---------------- Initial State Tests --------------

    @Test
    void createsInitialState() {
        assertEquals(
                new SearchState(start, 0, 0L, Set.of(start)),
                problem.initialState());
    }

    @Test
    void returnsGoal() {
        assertEquals(goal, problem.goal());
    }

    // -------------------- Goal Tests -------------------

    @Test
    void identifiesGoal() {
        SearchState state = new SearchState(
                goal, 2, 8L,
                Set.of(start, middle, goal));

        assertTrue(problem.isGoal(state));
        assertFalse(problem.isGoal(problem.initialState()));
    }

    @Test
    void acceptsGoalAtMaxSteps() {
        SearchState state = new SearchState(
                goal, MAX_STEPS, 9L,
                Set.of(start, middle, bonusPosition, goal));

        assertTrue(problem.isGoal(state));
    }

    @Test
    void rejectsGoalBeyondMaxSteps() {
        SearchState state = new SearchState(
                goal, MAX_STEPS + 1, 8L,
                Set.of(start, goal));

        assertFalse(problem.isGoal(state));
    }

    // ----------------- Successor Tests -----------------

    @Test
    void generatesOnlyValidSuccessors() {
        List<Transition> transitions = problem.successors(problem.initialState());

        Transition expected = new Transition(
                new SearchState(
                        middle, 1, -1L, Set.of(start, middle)),
                Direction.RIGHT,
                -1,
                12L);

        assertEquals(List.of(expected), transitions);
    }

    @Test
    void stopsAtMaxSteps() {
        SearchState state = new SearchState(
                start, MAX_STEPS, -4L,
                Set.of(start, middle));

        assertTrue(problem.successors(state).isEmpty());
    }

    @Test
    void stopsAtGoal() {
        SearchState state = new SearchState(
                goal, 2, 8L,
                Set.of(start, middle, goal));

        assertTrue(problem.successors(state).isEmpty());
    }

    @Test
    void excludesOtherTerminalCells() {
        SearchState state = new SearchState(
                bonusPosition, 2, 1L,
                Set.of(start, middle, bonusPosition));

        List<Transition> transitions = problem.successors(state);

        assertEquals(1, transitions.size());
        assertEquals(Direction.UP, transitions.getFirst().action());
    }

    @Test
    void doesNotChangeWorldState() {
        problem.successors(problem.initialState());

        assertEquals(start, world.position());
    }

    @Test
    void acceptsZeroMaxSteps() {
        SearchProblem zeroStepProblem = new SearchProblem(world, start, goal, 0);

        assertTrue(zeroStepProblem.successors(
                zeroStepProblem.initialState()).isEmpty());
    }

    // --------------- Reward and Cost Tests -------------

    @Test
    void accumulatesRewards() {
        Transition first = findTransition(
                problem.initialState(), Direction.RIGHT);

        Transition second = findTransition(
                first.state(), Direction.DOWN);

        assertEquals(-1L, first.state().totalReward());
        assertEquals(1L, second.state().totalReward());
    }

    @Test
    void higherRewardPathHasLowerCost() {
        Transition first = findTransition(
                problem.initialState(), Direction.RIGHT);

        Transition direct = findTransition(
                first.state(), Direction.RIGHT);

        Transition down = findTransition(
                first.state(), Direction.DOWN);

        Transition up = findTransition(
                down.state(), Direction.UP);

        Transition finish = findTransition(
                up.state(), Direction.RIGHT);

        long shortCost = first.cost() + direct.cost();

        long longCost = first.cost() + down.cost()
                + up.cost() + finish.cost();

        assertEquals(36L, shortCost);
        assertEquals(35L, longCost);
        assertTrue(longCost < shortCost);
    }

    // -------------- Visited Positions Tests ------------

    @Test
    void updatesVisitedPositionsWithoutChangingParent() {
        SearchState initial = problem.initialState();

        Transition first = findTransition(
                initial, Direction.RIGHT);

        assertEquals(Set.of(start),
                initial.visitedPositions());

        assertEquals(Set.of(start, middle),
                first.state().visitedPositions());
    }

    @Test
    void revisitingPositionDoesNotDuplicateVisitedPositions() {
        Transition right = findTransition(
                problem.initialState(), Direction.RIGHT);

        Transition down = findTransition(
                right.state(), Direction.DOWN);

        Transition up = findTransition(
                down.state(), Direction.UP);

        assertEquals(right.state().position(),
                up.state().position());

        assertEquals(3, up.state().stepsUsed());

        assertEquals(down.state().visitedPositions(),
                up.state().visitedPositions());

        assertEquals(3, up.state().visitedPositions().size());
    }

    // ---------------- Validation Tests -----------------

    @Test
    void rejectsNullWorld() {
        assertThrows(NullPointerException.class,
                () -> new SearchProblem(null, start, goal, MAX_STEPS));
    }

    @Test
    void rejectsNullGoalState() {
        assertThrows(NullPointerException.class,
                () -> problem.isGoal(null));
    }

    @Test
    void rejectsNonWalkableStart() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchProblem(
                        world, wallPosition, goal, MAX_STEPS));
    }

    @Test
    void rejectsNonWalkableGoal() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchProblem(
                        world, start, wallPosition, MAX_STEPS));
    }

    @Test
    void rejectsTerminalStart() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchProblem(world, goal, goal, MAX_STEPS));
    }

    @Test
    void rejectsNegativeMaxSteps() {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchProblem(world, start, goal, -1));
    }

    // ------------------ Helper Methods -----------------

    private Transition findTransition(SearchState state, Direction action) {
        for (Transition transition : problem.successors(state)) {
            if (transition.action() == action) {
                return transition;
            }
        }

        throw new AssertionError("No transition for action: " + action);
    }
}
