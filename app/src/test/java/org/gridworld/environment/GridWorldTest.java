package org.gridworld.environment;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName ("GridWorld starts at initial position")
    void startsAtInitialPosition() {
        assertEquals(start, world.position());
    }

    @Test
    @DisplayName ("GridWorld starts with correct initial reward")
    void reportsWhetherPositionIsWalkable() {
        assertTrue(world.isWalkable(start));
        assertFalse(world.isWalkable(new Position(1, 0)));
        assertFalse(world.isWalkable(new Position(-1, 0)));
    }

    // ---------------- Movement Tests ----------------

    @Test
    @DisplayName ("GridWorld moves position correctly")
    void movesRight() {
        StepResult result = world.step(Direction.RIGHT);

        assertPosition(new Position(0, 1), result);
        assertFalse(result.isTerminal());
    }

    @Test
    @DisplayName ("GridWorld moves position correctly")
    void movesDown() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.DOWN);

        assertPosition(new Position(1, 1), result);
    }

    @Test
    @DisplayName ("GridWorld moves position correctly")
    void movesLeft() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.LEFT);

        assertPosition(start, result);
    }

    @Test
    @DisplayName ("GridWorld moves position correctly")
    void movesUp() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        StepResult result = world.step(Direction.UP);

        assertPosition(new Position(0, 1), result);
    }

    // ---------------- Invalid Movement Tests ----------------

    @Test
    @DisplayName ("GridWorld does not move outside grid or into wall")
    void cannotMoveOutsideUpperBoundary() {
        assertBlockedMovement(Direction.UP);
    }

    @Test
    @DisplayName ("GridWorld does not move outside grid or into wall")
    void cannotMoveOutsideLeftBoundary() {
        assertBlockedMovement(Direction.LEFT);
    }

    @Test
    @DisplayName ("GridWorld does not move outside grid or into wall")
    void cannotMoveOutsideLowerBoundary() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);
        world.step(Direction.DOWN);

        assertEquals(new Position(2, 1), world.position());

        assertBlockedMovement(Direction.DOWN);
    }

    @Test
    @DisplayName ("GridWorld does not move outside grid or into wall")
    void cannotMoveOutsideRightBoundary() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);
        world.step(Direction.DOWN);
        world.step(Direction.RIGHT);

        assertEquals(new Position(2, 2), world.position());

        assertBlockedMovement(Direction.RIGHT);
    }

    @Test
    @DisplayName ("GridWorld does not move outside grid or into wall")
    void cannotMoveIntoWall() {
        assertBlockedMovement(Direction.DOWN);
    }

    // ---------------- Reward Tests ----------------

    @Test
    @DisplayName ("GridWorld applies step penalty on empty cell")
    void appliesStepPenaltyOnEmptyCell() {
        StepResult result = world.step(Direction.RIGHT);

        assertEquals(-1, result.reward());
    }

    @Test
    @DisplayName ("GridWorld applies step penalty on empty cell")
    void addsBonusRewardToStepPenalty() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.DOWN);

        assertEquals(2, result.reward());
    }

    @Test
    @DisplayName ("GridWorld applies step penalty on empty cell")
    void addsPositiveTerminalRewardToStepPenalty() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.RIGHT);

        assertEquals(9, result.reward());
    }

    @Test
    @DisplayName ("GridWorld applies step penalty on empty cell")
    void addsNegativeTerminalRewardToStepPenalty() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        StepResult result = world.step(Direction.RIGHT);

        assertEquals(-11, result.reward());
    }

    // ---------------- Terminal State Tests ----------------

    @Test
    @DisplayName ("GridWorld identifies terminal state correctly")
    void reachesGoal() {
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.RIGHT);

        assertPosition(new Position(0, 2), result);
        assertTrue(result.isTerminal());
    }

    @Test
    @DisplayName ("GridWorld identifies terminal state correctly")
    void reachesTrap() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        StepResult result = world.step(Direction.RIGHT);

        assertPosition(new Position(1, 2), result);
        assertTrue(result.isTerminal());
    }

    @Test
    @DisplayName ("GridWorld identifies terminal state correctly")
    void cannotMoveAfterTerminalState() {
        world.step(Direction.RIGHT);
        world.step(Direction.RIGHT);

        StepResult result = world.step(Direction.LEFT);

        assertPosition(new Position(0, 2), result);
        assertEquals(0, result.reward());
        assertTrue(result.isTerminal());
    }

    // ---------------- Constructor Validation Tests ----------------

    @Test
    @DisplayName ("GridWorld rejects null map")
    void rejectsNullMap() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(null, start, -1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld rejects null initial position")
    void rejectsNullInitialPosition() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(map, null, -1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld rejects initial position outside grid")
    void rejectsInitialPositionOutsideGrid() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(map, new Position(-1, 0), -1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld rejects initial position on wall")
    void rejectsInitialPositionOnWall() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(map, new Position(1, 0), -1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld rejects initial position on terminal cell")
    void rejectsInitialPositionOnTerminalCell() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(map, new Position(0, 2), -1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld rejects positive step penalty")
    void rejectsPositiveStepPenalty() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridWorld(map, start, 1);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld accepts zero step penalty")
    void acceptsZeroStepPenalty() {
        GridWorld zeroPenaltyWorld = new GridWorld(map, start, 0);

        StepResult result = zeroPenaltyWorld.step(Direction.RIGHT);

        assertEquals(0, result.reward());
    }

    // ---------------- Exception Tests ----------------

    @Test
    @DisplayName ("GridWorld rejects null direction for step method")
    void rejectsNullDirection() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        world.step(null);
                    }
                });

        assertEquals(start, world.position());
    }

    @Test
    @DisplayName ("GridWorld rejects null position for isWalkable method")
    void rejectsRewardOverflow() {
        world = new GridWorld(map, start, Integer.MIN_VALUE);

        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        Position previousPosition = world.position();

        assertThrows(ArithmeticException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        world.step(Direction.RIGHT);
                    }
                });

        assertEquals(previousPosition, world.position());

        StepResult result = world.step(Direction.UP);

        assertPosition(new Position(0, 1), result);
        assertFalse(result.isTerminal());
    }

    // ---------------- Reset Tests ----------------

    @Test
    @DisplayName ("GridWorld resets to initial position")
    void resetRestoresInitialPosition() {
        world.step(Direction.RIGHT);
        world.step(Direction.DOWN);

        world.reset();

        assertEquals(start, world.position());
    }

    @Test
    @DisplayName ("GridWorld resets to initial position and step penalty")
    void resetAllowsMovementAfterTerminalState() {
        world.step(Direction.RIGHT);
        world.step(Direction.RIGHT);

        world.reset();

        assertEquals(start, world.position());

        StepResult result = world.step(Direction.RIGHT);

        assertPosition(new Position(0, 1), result);
        assertEquals(-1, result.reward());
        assertFalse(result.isTerminal());
    }

    @Test
    @DisplayName ("GridWorld resets to initial position and step penalty")
    void resetRestoresCustomInitialPosition() {
        Position initialPosition = new Position(2, 1);

        world = new GridWorld(map, initialPosition, -1);

        world.step(Direction.RIGHT);

        assertEquals(new Position(2, 2), world.position());

        world.reset();

        assertEquals(initialPosition, world.position());
    }

    // ---------------- Preview Tests ----------------


    @Test
    @DisplayName ("GridWorld preview matches step result")
    void previewMatchesStepResult() {
        StepResult expected = world.preview(start, Direction.RIGHT);
        StepResult actual = world.step(Direction.RIGHT);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName ("GridWorld preview does not change world state")
    void previewCalculatesBonusReward() {
        StepResult expected = new StepResult(new Position(1, 1), 2, false);

        StepResult actual = world.preview(new Position(0, 1), Direction.DOWN);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName ("GridWorld preview does not change world state")
    void previewCannotMoveIntoWallOrOutsideGrid() {
        StepResult expected = new StepResult(start, -1, false);

        assertEquals(expected, world.preview(start, Direction.DOWN));
        assertEquals(expected, world.preview(start, Direction.UP));
    }

    @Test
    @DisplayName ("GridWorld preview does not change world state")
    void previewDoesNotChangeWorldState() {
        StepResult expected = new StepResult(new Position(0, 2), 9, true);

        StepResult result = world.preview(new Position(0, 1), Direction.RIGHT);

        assertEquals(expected, result);
        assertEquals(start, world.position());

        StepResult actual = world.step(Direction.RIGHT);

        assertEquals(new StepResult(new Position(0, 1), -1, false), actual);
    }

    @Test
    @DisplayName ("GridWorld preview does not change world state")
    void previewCannotMoveFromTerminalCell() {
        Position terminal = new Position(0, 2);
        StepResult expected = new StepResult(terminal, 0, true);

        StepResult actual = world.preview(terminal, Direction.LEFT);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName ("GridWorld preview does not change world state")
    void previewWorksAfterWorldReachesTerminal() {
        world.step(Direction.RIGHT);
        world.step(Direction.RIGHT);

        StepResult result = world.preview(start, Direction.RIGHT);

        assertEquals(new StepResult(new Position(0, 1), -1, false), result);
        assertEquals(new Position(0, 2), world.position());

        StepResult actual = world.step(Direction.LEFT);

        assertEquals(new StepResult(new Position(0, 2), 0, true), actual);
    }

    // ---------------- Preview Exception Tests ----------------
    @Test 
    @DisplayName ("GridWorld preview rejects wall position")
    void previewRejectsWallPosition() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        world.preview(new Position(1, 0), Direction.RIGHT);
                    }
                });
    }

    @Test
    @DisplayName ("GridWorld preview rejects null position or direction")
    void previewRejectsOutsidePosition() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        world.preview(new Position(-1, 0), Direction.RIGHT);
                    }
                });
    }

    // ---------------- Helper Methods ----------------

    private void assertPosition(Position expected, StepResult result) {
        assertEquals(expected, result.position());
        assertEquals(expected, world.position());
    }

    private void assertBlockedMovement(Direction direction) {
        Position previousPosition = world.position();

        StepResult result = world.step(direction);

        assertPosition(previousPosition, result);
        assertEquals(-1, result.reward());
        assertFalse(result.isTerminal());
    }
}