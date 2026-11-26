package org.gridworld.search;

import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.environment.StepResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

public final class SearchProblem {

    private final GridWorld world;
    private final Position start;
    private final Position goal;
    private final int maxSteps;
    private final long rewardOffset;

    // ------------------- Constructor -------------------

    public SearchProblem(
            GridWorld world,
            Position start,
            Position goal,
            int maxSteps) {

        this.world = Objects.requireNonNull(world, "World cannot be null");

        GridMap map = world.getMap();

        validateParameters(map, start, goal, maxSteps);

        this.start = start;
        this.goal = goal;
        this.maxSteps = maxSteps;
        this.rewardOffset = (long) map.rewardUpperBound() + 1;
    }

    // ------------------ Public Methods -----------------

    public SearchState initialState() {
        return new SearchState(start, 0, 0L, Set.of(start));
    }

    public Position goal() {
        return goal;
    }

    public boolean isGoal(SearchState state) {
        return state.position().equals(goal)
                && state.stepsUsed() <= maxSteps;
    }

    public List<Transition> successors(SearchState state) {
        if (isGoal(state) || state.stepsUsed() >= maxSteps) {
            return List.of();
        }

        List<Transition> transitions = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            StepResult result = world.preview(state.position(), direction);

            if (!isValidSuccessor(state, result)) {
                continue;
            }

            Set<Position> visitedPositions = new HashSet<>(state.visitedPositions());

            visitedPositions.add(result.position());

            SearchState nextState = new SearchState(
                    result.position(),
                    state.stepsUsed() + 1,
                    state.totalReward() + result.reward(),
                    visitedPositions);

            long cost = calculateCost(nextState, result.reward());

            transitions.add(new Transition(
                    nextState,
                    direction,
                    result.reward(),
                    cost));
        }

        return transitions;
    }

    // ------------------ Helper Methods -----------------

    private boolean isValidSuccessor(SearchState state, StepResult result) {
        return !result.position().equals(state.position())
                && (!result.done() || result.position().equals(goal));
    }

    private long calculateCost(SearchState state, int reward) {
        long cost = rewardOffset - reward;

        if (isGoal(state)) {
            cost += (maxSteps - state.stepsUsed()) * rewardOffset;
        }

        return cost;
    }

    // ---------------- Validation Methods ---------------

    private static void validateParameters(
            GridMap map,
            Position start,
            Position goal,
            int maxSteps) {

        if (!map.isWalkable(start) || !map.isWalkable(goal)) {
            throw new IllegalArgumentException(
                    "Start and goal must be walkable");
        }

        if (map.cellAt(start).type() == CellType.TERMINAL) {
            throw new IllegalArgumentException(
                    "Start cannot be terminal");
        }

        if (maxSteps < 0) {
            throw new IllegalArgumentException(
                    "Max steps cannot be negative");
        }
    }
}