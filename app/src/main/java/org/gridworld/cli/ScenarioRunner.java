package org.gridworld.cli;

import org.gridworld.domain.GridMap;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.scenario.Scenario;
import org.gridworld.search.SearchAlgorithm;
import org.gridworld.search.SearchProblem;
import org.gridworld.search.SearchResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ScenarioRunner {

    private final Map<String, SearchAlgorithm> algorithms;

    // ---------------- Constructor ----------------

    public ScenarioRunner(Map<String, SearchAlgorithm> algorithms) {

        this.algorithms = new LinkedHashMap<>(
                Objects.requireNonNull(algorithms,
                        "Algorithms cannot be null"));
    }

    // ---------------- Public Methods ----------------

    public Map<String, SearchMetrics> runScenario(Scenario scenario) {

        Objects.requireNonNull(scenario, "Scenario cannot be null");

        Map<String, SearchMetrics> results = new LinkedHashMap<>();

        for (var entry : algorithms.entrySet()) {

            SearchMetrics metrics = runAlgorithm(
                    scenario, entry.getValue());

            results.put(entry.getKey(), metrics);
        }

        return results;
    }

    // ---------------- Algorithm Execution ----------------

    private SearchMetrics runAlgorithm(
            Scenario scenario,
            SearchAlgorithm algorithm) {

        GridWorld world = createWorld(scenario);
        SearchProblem problem = createProblem(world, scenario);

        long startTime = System.nanoTime();

        SearchResult result = algorithm.search(problem);

        long executionTime = System.nanoTime() - startTime;

        if (result.pathFound()) {
            executeActions(world, result);
        }

        return new SearchMetrics(
                result,
                executionTime,
                world.position());
    }

    // ---------------- Initialization Helpers ----------------

    private GridWorld createWorld(Scenario scenario) {

        return new GridWorld(
                new GridMap(scenario.cells()),
                scenario.start(),
                scenario.stepPenalty());
    }

    private SearchProblem createProblem(
            GridWorld world, Scenario scenario) {

        return new SearchProblem(
                world,
                scenario.start(),
                scenario.goal(),
                scenario.maxSteps());
    }

    // ---------------- Execution Helpers ----------------

    private void executeActions(
            GridWorld world, SearchResult result) {

        for (Direction action : result.actions()) {
            world.step(action);
        }
    }
}