package org.gridworld.search;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.GridWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchProblemTest {

    private static final Position START = new Position(0, 0);
    private static final Position GOAL = new Position(0, 1);
    private static final GridWorld WORLD = new GridWorld(
            new GridMap(new Cell[][] {
                    { new Cell(CellType.EMPTY, 0), new Cell(CellType.TERMINAL, 10) }
            }),
            START,
            0);

    @Test
    void acceptsWalkableStartAndGoalWithNonNegativeStepLimit() {
        assertDoesNotThrow(() -> new SearchProblem(WORLD, START, GOAL, 1));
    }

    @Test
    void rejectsNegativeStepLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SearchProblem(WORLD, START, GOAL, -1));
    }

    @Test
    void rejectsNonWalkableGoal() {
        GridWorld worldWithWall = new GridWorld(
                new GridMap(new Cell[][] {
                        { new Cell(CellType.EMPTY, 0), new Cell(CellType.WALL, 0) }
                }),
                START,
                0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new SearchProblem(worldWithWall, START, GOAL, 1));
    }
}
