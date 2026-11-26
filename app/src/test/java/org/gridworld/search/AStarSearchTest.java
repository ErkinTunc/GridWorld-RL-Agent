package org.gridworld.search;

import java.util.List;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class AStarSearchTest {

    private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
    private static final Cell WALL = new Cell(CellType.WALL, 0);
    private static final Cell TERMINAL = new Cell(CellType.TERMINAL, 10);

    // ---------------- Path Finding Tests ----------------

    @Test
    @DisplayName("Finds the shortest path around a wall")
    void findsShortestPathAroundWall() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY, WALL, EMPTY },
                { EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, TERMINAL }
        });

        Position start = new Position(0, 0);
        Position end = new Position(2, 2);

        SearchResult result = pathfinder.search(start, end);

        assertTrue(result.pathFound());
        assertEquals(4, result.totalCost());
        assertEquals(5, result.path().size());
        assertEquals(4, result.actions().size());
        assertEquals(start, result.path().getFirst());
        assertEquals(end, result.path().getLast());
        assertEquals(10, result.totalReward());
    }

    @Test
    @DisplayName("Reports when no path exists")
    void reportsWhenNoPathExists() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY, WALL, TERMINAL },
                { EMPTY, WALL, EMPTY },
                { EMPTY, WALL, EMPTY }
        });

        SearchResult result = pathfinder.search(
                new Position(0, 0),
                new Position(0, 2));

        assertFalse(result.pathFound());
        assertTrue(result.path().isEmpty());
        assertTrue(result.actions().isEmpty());
    }

    // ---------------- Terminal State Tests ----------------

    @Test
    @DisplayName("Does not route through another terminal cell")
    void doesNotRouteThroughAnotherTerminal() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY, TERMINAL, EMPTY },
                { EMPTY, EMPTY, EMPTY }
        });

        SearchResult result = pathfinder.search(
                new Position(0, 0),
                new Position(0, 2));

        assertTrue(result.pathFound());
        assertFalse(result.path().contains(new Position(0, 1)));
        assertEquals(4, result.totalCost());
    }

    // ---------------- Edge Case Tests ----------------

    @Test
    @DisplayName("Returns an empty action sequence when start equals end")
    void startEqualsEndProducesEmptyActionSequence() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY }
        });

        Position startAndEnd = new Position(0, 0);

        SearchResult result = pathfinder.search(
                startAndEnd, startAndEnd);

        assertTrue(result.pathFound());
        assertEquals(List.of(startAndEnd), result.path());
        assertTrue(result.actions().isEmpty());
        assertEquals(0, result.totalCost());
    }

    // ---------------- Path Reconstruction Tests ----------------

    @Test
    @DisplayName("Returned actions follow the returned path")
    void returnedActionsFollowTheReturnedPath() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY, EMPTY },
                { WALL, TERMINAL }
        });

        List<Position> expectedPath = List.of(
                new Position(0, 0),
                new Position(0, 1),
                new Position(1, 1));

        List<Direction> expectedActions = List.of(
                Direction.RIGHT,
                Direction.DOWN);

        SearchResult result = pathfinder.search(
                new Position(0, 0),
                new Position(1, 1));

        assertTrue(result.pathFound());
        assertEquals(expectedPath, result.path());
        assertEquals(expectedActions, result.actions());
    }

    // ---------------- Exception Tests ----------------

    @Test
    @DisplayName("Rejects a non-walkable starting position")
    void rejectsNonWalkableStart() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { WALL, TERMINAL }
        });

        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        pathfinder.search(
                                new Position(0, 0),
                                new Position(0, 1));
                    }
                });
    }

    @Test
    @DisplayName("Rejects a non-walkable ending position")
    void rejectsNonWalkableEnd() {
        AStarSearch pathfinder = createPathfinder(new Cell[][] {
                { EMPTY, WALL }
        });

        assertThrows(IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        pathfinder.search(
                                new Position(0, 0),
                                new Position(0, 1));
                    }
                });
    }

    // ---------------- Helper Methods ----------------

    private AStarSearch createPathfinder(Cell[][] cells) {
        return new AStarSearch(new GridMap(cells));
    }
}