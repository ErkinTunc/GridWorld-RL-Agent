package org.gridworld.cli;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.Position;

import java.util.Optional;

public final class ScenarioFactory {

    // ---------------- Default Configuration ----------------

    private static final Position START = new Position(0, 0);
    private static final int STEP_PENALTY = -1;

    // ---------------- Cell Definitions ----------------

    private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
    private static final Cell WALL = new Cell(CellType.WALL, 0);
    private static final Cell GOAL = new Cell(CellType.TERMINAL, 10);
    private static final Cell TRAP = new Cell(CellType.TERMINAL, -10);
    private static final Cell BONUS_3 = new Cell(CellType.EMPTY, 3);
    private static final Cell BONUS_4 = new Cell(CellType.EMPTY, 4);

    // ---------------- Constructor ----------------

    private ScenarioFactory() {
    }

    // ---------------- Public Methods ----------------

    public static Optional<Scenario> create(String choice) {

        return switch (choice) {
            case "1" -> Optional.of(rewardMaximization());
            case "2" -> Optional.of(alternativeOptimalPaths());
            case "3" -> Optional.of(obstacleAvoidance());
            case "4" -> Optional.of(unreachableGoal());
            default -> Optional.empty();
        };
    }

    // ---------------- Scenario Definitions ----------------

    private static Scenario rewardMaximization() {

        Cell[][] cells = {
                { EMPTY, EMPTY, EMPTY, GOAL },
                { WALL, BONUS_3, TRAP, WALL },
                { EMPTY, EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, EMPTY, EMPTY }
        };

        return createScenario(
                "REWARD MAXIMIZATION",
                cells,
                new Position(0, 3),
                5);
    }

    private static Scenario alternativeOptimalPaths() {

        Cell[][] cells = {
                { EMPTY, BONUS_4, EMPTY, EMPTY },
                { EMPTY, WALL, WALL, EMPTY },
                { EMPTY, WALL, WALL, EMPTY },
                { EMPTY, BONUS_4, EMPTY, GOAL }
        };

        return createScenario(
                "ALTERNATIVE OPTIMAL PATHS",
                cells,
                new Position(3, 3),
                6);
    }

    private static Scenario obstacleAvoidance() {

        Cell[][] cells = {
                { EMPTY, WALL, EMPTY, GOAL },
                { EMPTY, WALL, EMPTY, EMPTY },
                { EMPTY, WALL, EMPTY, EMPTY },
                { EMPTY, EMPTY, EMPTY, EMPTY }
        };

        return createScenario(
                "OBSTACLE AVOIDANCE",
                cells,
                new Position(0, 3),
                9);
    }

    private static Scenario unreachableGoal() {

        Cell[][] cells = {
                { EMPTY, WALL, EMPTY, GOAL },
                { EMPTY, WALL, EMPTY, EMPTY },
                { EMPTY, WALL, EMPTY, EMPTY },
                { EMPTY, WALL, EMPTY, EMPTY }
        };

        return createScenario(
                "UNREACHABLE GOAL",
                cells,
                new Position(0, 3),
                9);
    }

    // ---------------- Factory Helpers ----------------

    private static Scenario createScenario(
            String title,
            Cell[][] cells,
            Position goal,
            int maxSteps) {

        return new Scenario(
                title,
                cells,
                START,
                goal,
                maxSteps,
                STEP_PENALTY);
    }
}