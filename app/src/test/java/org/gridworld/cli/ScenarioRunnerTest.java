package org.gridworld.cli;

import org.gridworld.domain.GridMap;
import org.gridworld.environment.GridWorld;
import org.gridworld.scenario.Scenario;
import org.gridworld.scenario.ScenarioFactory;
import org.gridworld.search.BacktrackingSearch;
import org.gridworld.search.SearchProblem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScenarioRunnerTest {

    // ---------------- Execution Tests ----------------

    @Test
    @DisplayName("Records goal position after successful search")
    void recordsGoalPositionAfterSuccessfulSearch() {

        Scenario scenario = ScenarioFactory.create("5").orElseThrow();

        SearchMetrics metrics = runScenario(scenario);

        assertTrue(metrics.result().pathFound());
        assertEquals(scenario.goal(), metrics.finalPosition());
    }

    @Test
    @DisplayName("Records start position after failed search")
    void recordsStartPositionAfterFailedSearch() {

        Scenario scenario = ScenarioFactory.create("4").orElseThrow();

        SearchMetrics metrics = runScenario(scenario);

        assertFalse(metrics.result().pathFound());
        assertEquals(scenario.start(), metrics.finalPosition());
    }

    // ---------------- Helper Methods ----------------

    private SearchMetrics runScenario(Scenario scenario) {

        GridWorld world = new GridWorld(
                new GridMap(scenario.cells()),
                scenario.start(),
                scenario.stepPenalty());

        SearchProblem problem = new SearchProblem(
                world,
                scenario.start(),
                scenario.goal(),
                scenario.maxSteps());

        ScenarioRunner runner = new ScenarioRunner(world, problem);

        SearchMetrics metrics = runner.runAlgorithm(
                new BacktrackingSearch());

        assertEquals(world.position(), metrics.finalPosition());

        return metrics;
    }
}