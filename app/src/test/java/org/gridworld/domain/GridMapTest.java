package org.gridworld.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests grid boundaries, walkability, cell access,
 * constructor validation and defensive copying.
 */
class GridMapTest {

    private final Cell empty = new Cell(CellType.EMPTY, 0);
    private final Cell wall = new Cell(CellType.WALL, 0);
    private final Cell goal = new Cell(CellType.TERMINAL, 10);

    private Cell[][] cells;
    private GridMap map;

    // ---------------- Setup ----------------

    @BeforeEach
    void setUp() {
        cells = new Cell[][] {
                { empty, wall, goal },
                { empty, empty, empty }
        };

        map = new GridMap(cells);
    }

    // ---------------- Boundary Tests ----------------

    @Test
    @DisplayName ("GridMap correctly identifies positions inside and outside the grid")
    void acceptsPositionsInsideGrid() {
        assertTrue(map.isInside(new Position(0, 0)));
        assertTrue(map.isInside(new Position(1, 2)));
    }

    @Test
    @DisplayName ("GridMap correctly identifies positions outside the grid")
    void rejectsPositionsOutsideGrid() {
        assertFalse(map.isInside(new Position(-1, 0)));
        assertFalse(map.isInside(new Position(0, -1)));
        assertFalse(map.isInside(new Position(2, 0)));
        assertFalse(map.isInside(new Position(0, 3)));
    }

    // ---------------- Walkability Tests ----------------

    @Test
    @DisplayName ("GridMap correctly identifies walkable and non-walkable positions")
    void emptyAndTerminalCellsAreWalkable() {
        assertTrue(map.isWalkable(new Position(0, 0)));
        assertTrue(map.isWalkable(new Position(0, 2)));
    }

    @Test
    @DisplayName ("GridMap correctly identifies non-walkable positions")
    void wallIsNotWalkable() {
        assertFalse(map.isWalkable(new Position(0, 1)));
    }

    @Test
    @DisplayName ("GridMap correctly identifies positions outside the grid as non-walkable")
    void outsidePositionIsNotWalkable() {
        assertFalse(map.isWalkable(new Position(-1, 0)));
    }

    // ---------------- Cell Access Tests ----------------

    @Test
    @DisplayName ("GridMap returns correct cell for valid position")
    void returnsCorrectCell() {
        assertEquals(empty, map.cellAt(new Position(0, 0)));
        assertEquals(wall, map.cellAt(new Position(0, 1)));
        assertEquals(goal, map.cellAt(new Position(0, 2)));
        assertEquals(empty, map.cellAt(new Position(1, 1)));
    }

    @Test
    @DisplayName ("GridMap rejects positions outside the grid when accessing cells")
    void cellAtRejectsOutsidePosition() {
        assertThrows(IndexOutOfBoundsException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        map.cellAt(new Position(-1, 0));
                    }
                });
    }

    // ---------------- Position Validation Tests ----------------

    @Test
    @DisplayName ("GridMap rejects null position for isInside, isWalkable, and cellAt methods")
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        map.isInside(null);
                    }
                });

        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        map.isWalkable(null);
                    }
                });

        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        map.cellAt(null);
                    }
                });
    }

    // ---------------- Grid Validation Tests ----------------

    @Test
    @DisplayName ("GridMap constructor rejects null, empty, non-rectangular grids and null rows or cells")
    void rejectsNullGrid() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(null);
                    }
                });
    }

    @Test
    @DisplayName ("GridMap constructor rejects empty grid")
    void rejectsEmptyGrid() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[0][0]);
                    }
                });
    }

    @Test
    @DisplayName ("GridMap constructor rejects empty row")
    void rejectsEmptyRow() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] { {} });
                    }
                });
    }


    @Test
    @DisplayName ("GridMap constructor rejects null row or null cell")
    void rejectsNullRow() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] { null });
                    }
                });

        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] { { empty }, null });
                    }
                });
    }

    @Test
    @DisplayName ("GridMap constructor rejects non-rectangular grid")
    void rejectsNonRectangularGrid() {
        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] {
                                { empty, wall },
                                { empty }
                        });
                    }
                });
    }

    @Test
    @DisplayName ("GridMap constructor rejects null cell")
    void rejectsNullCell() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] { { null } });
                    }
                });
    }

    // ------------------- Reward Tests -------------------

    @Test
    @DisplayName ("GridMap returns the highest reward among walkable cells")
    void returnsHighestWalkableReward() {
        GridMap rewardMap = new GridMap(new Cell[][] {
                { empty, goal },
                { new Cell(CellType.EMPTY, 20), empty }
        });

        assertEquals(20, rewardMap.rewardUpperBound());
    }

    @Test
    @DisplayName ("GridMap ignores rewards from non-walkable cells")
    void ignoresWallRewards() {
        GridMap rewardMap = new GridMap(new Cell[][] {
                { empty, new Cell(CellType.WALL, 100), goal }
        });

        assertEquals(10, rewardMap.rewardUpperBound());
    }

    @Test
    @DisplayName ("GridMap returns zero when all walkable cells have negative rewards")
    void returnsZeroWhenAllRewardsAreNegative() {
        GridMap rewardMap = new GridMap(new Cell[][] {
                {
                        new Cell(CellType.EMPTY, -3),
                        new Cell(CellType.TERMINAL, -10)
                }
        });

        assertEquals(0, rewardMap.rewardUpperBound());
    }

    // ---------------- Encapsulation Tests ----------------

    @Test
    @DisplayName ("GridMap protects its internal grid from external changes")
    void protectsGridFromExternalChanges() {
        cells[0][0] = wall;
        cells[1] = new Cell[] { wall, wall, wall };

        assertEquals(empty, map.cellAt(new Position(0, 0)));
        assertEquals(empty, map.cellAt(new Position(1, 0)));
    }
}