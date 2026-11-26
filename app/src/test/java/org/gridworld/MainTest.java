package org.gridworld;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    // ---------------- Scenario Tests ----------------

    @ParameterizedTest(name = "Scenario {0}: {1}")
    @CsvSource({
            "1, Reward maximization",
            "2, Alternative optimal paths",
            "3, Obstacle avoidance",
            "4, Unreachable goal"
    })
    void runsScenarioSuccessfully(
            String scenarioId, String scenarioName) {

        assertDoesNotThrow(
                () -> Main.main(new String[] { scenarioId }));
    }
}