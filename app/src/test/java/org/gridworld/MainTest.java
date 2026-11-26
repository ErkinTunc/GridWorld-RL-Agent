package org.gridworld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    // ---------------- Scenario Tests ----------------

    @Test
    @DisplayName("Reward maximization scenario runs successfully")
    void runsRewardMaximizationScenario() {
        assertDoesNotThrow(() -> Main.main(new String[] { "1" }));
    }

    @Test
    @DisplayName("Alternative optimal paths scenario runs successfully")
    void runsAlternativeOptimalPathsScenario() {
        assertDoesNotThrow(() -> Main.main(new String[] { "2" }));
    }

    @Test
    @DisplayName("Obstacle avoidance scenario runs successfully")
    void runsObstacleAvoidanceScenario() {
        assertDoesNotThrow(() -> Main.main(new String[] { "3" }));
    }

    @Test
    @DisplayName("Unreachable goal scenario runs successfully")
    void runsUnreachableGoalScenario() {
        assertDoesNotThrow(() -> Main.main(new String[] { "4" }));
    }
}