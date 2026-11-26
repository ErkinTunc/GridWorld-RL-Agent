package org.gridworld.search;

import java.util.Objects;

import org.gridworld.domain.Position;
import org.gridworld.environment.GridWorld;

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
}
