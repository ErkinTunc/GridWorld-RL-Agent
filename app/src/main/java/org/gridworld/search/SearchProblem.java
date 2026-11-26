package org.gridworld.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.environment.StepResult;

public class SearchProblem {

    private final GridWorld world;
    private final Position start;
    private final Position goal;
    private final int maxSteps;

    public SearchProblem(GridWorld world, Position start, Position goal, int maxSteps) {
        this.world = Objects.requireNonNull(world, "World cannot be null");
        this.start = Objects.requireNonNull(start, "Start position cannot be null");
        this.goal = Objects.requireNonNull(goal, "Goal position cannot be null");

        if (maxSteps < 0) {
            throw new IllegalArgumentException("Maximum steps cannot be negative");
        }
        if (!world.isWalkable(start) || !world.isWalkable(goal)) {
            throw new IllegalArgumentException("Start and goal positions must be walkable");
        }

        this.maxSteps = maxSteps;
    }

    public SearchState initialState() {
        return new SearchState(start, 0, 0, Set.of(start));
    }

    public boolean isGoal(SearchState state) {
        Objects.requireNonNull(state, "Search state cannot be null");
        return goal.equals(state.position());
    }

    public List<Transition> successors(SearchState state) {
        Objects.requireNonNull(state, "Search state cannot be null");
        if (isGoal(state) || state.stepsUsed() >= maxSteps) {
            return List.of();
        }

        List<Transition> transitions = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            StepResult result = world.preview(state.position(), direction);
            Position nextPosition = result.position();

            if (nextPosition.equals(state.position())|| state.visitedPositions().contains(nextPosition)|| (result.done() && !nextPosition.equals(goal))) {
                continue;
            }

            Set<Position> visitedPositions = new HashSet<>(state.visitedPositions());
            visitedPositions.add(nextPosition);

            SearchState nextState = new SearchState(nextPosition,state.stepsUsed() + 1,Math.addExact(state.totalReward(), result.reward()),visitedPositions);
            transitions.add(new Transition(nextState, direction, result.reward(), 1));
        }

        return List.copyOf(transitions);
    }
}
