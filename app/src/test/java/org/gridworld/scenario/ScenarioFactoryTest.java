package org.gridworld.scenario;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioFactoryTest {

    // ---------------- Catalog Tests ----------------

    @Test
    void returnsAllScenariosInMenuOrder() {

        List<String> expectedTitles = List.of(
                "REWARD MAXIMIZATION",
                "ALTERNATIVE OPTIMAL PATHS",
                "OBSTACLE AVOIDANCE",
                "UNREACHABLE GOAL",
                "OPEN GRID",
                "NARROW PASSAGE");

        List<String> actualTitles = ScenarioFactory.createAll()
                .stream()
                .map(Scenario::title)
                .toList();

        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    void createsScenarioForEachValidChoice() {

        List<Scenario> scenarios = ScenarioFactory.createAll();

        for (int index = 0; index < scenarios.size(); index++) {

            Scenario scenario = ScenarioFactory
                    .create(Integer.toString(index + 1))
                    .orElseThrow();

            assertEquals(
                    scenarios.get(index).title(),
                    scenario.title());
        }
    }

    @Test
    void returnsEmptyForInvalidChoices() {
        assertTrue(ScenarioFactory.create("0").isEmpty());
        assertTrue(ScenarioFactory.create("7").isEmpty());
        assertTrue(ScenarioFactory.create("abc").isEmpty());
        assertTrue(ScenarioFactory.create("").isEmpty());
    }

    @Test
    void returnsUnmodifiableScenarioList() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> ScenarioFactory.createAll().clear());
    }
}