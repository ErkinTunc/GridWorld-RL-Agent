
package org.gridworld.cli;

import org.gridworld.scenario.Scenario;
import org.gridworld.scenario.ScenarioFactory;
import org.gridworld.search.AStarSearch;
import org.gridworld.search.BacktrackingSearch;
import org.gridworld.search.SearchAlgorithm;
import org.gridworld.search.SearchProblem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScenarioRunnerTest {

    // ---------------- Execution Tests ----------------

    @Test
    @DisplayName("Records goal position after successful search")
    void recordsGoalPositionAfterSuccessfulSearch() {

        Scenario scenario = ScenarioFactory.create("5").orElseThrow();

        Map<String, SearchMetrics> results = runScenario(scenario);

        assertEquals(2, results.size());

        for (SearchMetrics metrics : results.values()) {

            assertTrue(metrics.result().pathFound());
            assertEquals(scenario.goal(), metrics.finalPosition());
        }
    }

    @Test
    @DisplayName("Records start position after failed search")
    void recordsStartPositionAfterFailedSearch() {

        Scenario scenario = ScenarioFactory.create("4").orElseThrow();

        Map<String, SearchMetrics> results = runScenario(scenario);

        assertEquals(2, results.size());

        for (SearchMetrics metrics : results.values()) {

            assertFalse(metrics.result().pathFound());
            assertEquals(scenario.start(), metrics.finalPosition());
        }
    }

    @Test
    @DisplayName("Preserves algorithm result order")
    void preservesAlgorithmResultOrder() {

        Scenario scenario = ScenarioFactory.create("5").orElseThrow();

        Map<String, SearchMetrics> results = runScenario(scenario);

        assertEquals(
                List.of("BacktrackingSearch", "AStarSearch"),
                List.copyOf(results.keySet()));
    }

    @Test
    @DisplayName("Creates distinct search problems for each algorithm")
    void createsDistinctSearchProblems() {

        Scenario scenario = ScenarioFactory.create("5").orElseThrow();

        List<SearchProblem> problems = new ArrayList<>();

        SearchAlgorithm algorithm = problem -> {

            problems.add(problem);

            return new BacktrackingSearch().search(problem);
        };

        ScenarioRunner runner = new ScenarioRunner(
                Map.of("First", algorithm, "Second", algorithm));

        runner.runScenario(scenario);

        assertEquals(2, problems.size());
        assertNotSame(problems.get(0), problems.get(1));
    }

    // ---------------- Helper Methods ----------------

    private Map<String, SearchMetrics> runScenario(Scenario scenario) {

        Map<String, SearchAlgorithm> algorithms = new LinkedHashMap<>();

        algorithms.put("BacktrackingSearch", new BacktrackingSearch());
        algorithms.put("AStarSearch", new AStarSearch());

        ScenarioRunner runner = new ScenarioRunner(algorithms);

        return runner.runScenario(scenario);
    }
}
