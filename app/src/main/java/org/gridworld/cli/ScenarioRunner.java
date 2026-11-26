package org.gridworld.cli;

import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.search.SearchAlgorithm;
import org.gridworld.search.SearchProblem;
import org.gridworld.search.SearchResult;

import java.util.List;
import java.util.Objects;

public final class ScenarioRunner {

    private final GridWorld world;
    private final SearchProblem problem;

    // ---------------- Constructor ----------------

    public ScenarioRunner(GridWorld world, SearchProblem problem) {

        this.world = Objects.requireNonNull(
                world, "World cannot be null");

        this.problem = Objects.requireNonNull(
                problem, "Search problem cannot be null");
    }

    // ---------------- Public Methods ----------------

    public SearchResult runAlgorithm(SearchAlgorithm algorithm) {

        Objects.requireNonNull(
                algorithm, "Search algorithm cannot be null");

        world.reset();

        SearchResult result = algorithm.search(problem);

        if (result.pathFound()) {
            executeActions(result.actions());
        }

        return result;
    }

    // ---------------- Helper Methods ----------------

    private void executeActions(List<Direction> actions) {

        for (Direction action : actions) {
            world.step(action);
        }
    }
}