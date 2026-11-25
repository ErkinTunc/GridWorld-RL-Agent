package org.gridworld.environment;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GridWorldTest {

    private final Cell empty = new Cell(CellType.EMPTY, 0);
    private final Cell wall = new Cell(CellType.WALL, 0);
    private final Cell goal = new Cell(CellType.TERMINAL, 10);
    private final Cell trap = new Cell(CellType.TERMINAL, -10);
    private final Cell bonus = new Cell(CellType.EMPTY, 3);

    private final Position start = new Position(0, 0);

    private GridMap map;
    private GridWorld world;

    // ---------------- Setup ----------------

    @BeforeEach
    void setUp() {
        map = new GridMap(new Cell[][] {
                { empty, empty, goal },
                { wall, bonus, trap },
                { empty, empty, empty }
        });

        world = new GridWorld(map, start, -1);
    }

    // ---------------- Initial State Tests ----------------

    @Test
    void startsAtInitialPosition() {
        assertEquals(start, world.position());
    }

    // ---------------- Movement Tests ----------------

    @Test
    void movesRight() {
        StepResult result = world.step(Direction.RIGHT);

        assertPosition(new Position(0, 1), result);
        assertEquals(-1, result.reward());
        assertFalse(result.done());
    }

    @Test
    void movesDown() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.DOWN);

        assertPosition(new Position(1, 1), result);
    }

    @Test
    void movesLeft() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.LEFT);

        assertPosition(start, result);
    }

    @Test
    void movesUp() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        StepResult result = world.step(Direction.UP);

        assertPosition(new Position(0, 1), result);
    }

    // ---------------- Invalid Movement Tests ----------------

    @Test
    void cannotMoveOutsideUpperBoundary() {
        assertBlockedMovement(Direction.UP);
    }

    @Test
    void cannotMoveOutsideLeftBoundary() {
        assertBlockedMovement(Direction.LEFT);
    }

    @Test
    void cannotMoveIntoWall() {
        assertBlockedMovement(Direction.DOWN);
    }

    // ---------------- Helper Methods ----------------

    private void assertPosition(Position expected, StepResult result) {
        assertEquals(expected, result.position());
        assertEquals(expected, world.position());
    }

    private void assertBlockedMovement(Direction direction) {
        StepResult result = world.step(direction);

        assertPosition(start, result);
        assertEquals(-1, result.reward());
        assertFalse(result.done());
    }
}