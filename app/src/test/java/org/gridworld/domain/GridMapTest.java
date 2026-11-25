package org.gridworld.domain;

import org.junit.jupiter.api.BeforeEach;
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
    void acceptsPositionsInsideGrid() {
        assertTrue(map.isInside(new Position(0, 0)));
        assertTrue(map.isInside(new Position(1, 2)));
    }

    @Test
    void rejectsPositionsOutsideGrid() {
        assertFalse(map.isInside(new Position(-1, 0)));
        assertFalse(map.isInside(new Position(0, -1)));
        assertFalse(map.isInside(new Position(2, 0)));
        assertFalse(map.isInside(new Position(0, 3)));
    }

    // ---------------- Walkability Tests ----------------

    @Test
    void emptyAndTerminalCellsAreWalkable() {
        assertTrue(map.isWalkable(new Position(0, 0)));
        assertTrue(map.isWalkable(new Position(0, 2)));
    }

    @Test
    void wallIsNotWalkable() {
        assertFalse(map.isWalkable(new Position(0, 1)));
    }

    @Test
    void outsidePositionIsNotWalkable() {
        assertFalse(map.isWalkable(new Position(-1, 0)));
    }

    // ---------------- Cell Access Tests ----------------

    @Test
    void returnsCorrectCell() {
        assertEquals(empty, map.cellAt(new Position(0, 0)));
        assertEquals(wall, map.cellAt(new Position(0, 1)));
        assertEquals(goal, map.cellAt(new Position(0, 2)));
        assertEquals(empty, map.cellAt(new Position(1, 1)));
    }

    @Test
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
    void rejectsNullCell() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new GridMap(new Cell[][] { { null } });
                    }
                });
    }

    // ---------------- Encapsulation Tests ----------------

    @Test
    void protectsGridFromExternalChanges() {
        cells[0][0] = wall;
        cells[1] = new Cell[] { wall, wall, wall };

        assertEquals(empty, map.cellAt(new Position(0, 0)));
        assertEquals(empty, map.cellAt(new Position(1, 0)));
    }
}