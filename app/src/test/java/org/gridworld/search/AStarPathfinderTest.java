package org.gridworld.search;

import java.util.List;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AStarPathfinderTest {

    private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
    private static final Cell WALL = new Cell(CellType.WALL, 0);
    private static final Cell TERMINAL = new Cell(CellType.TERMINAL, 10);

    @Test
    @DisplayName ("Finds the shortest path around a wall")
    void findsShortestPathAroundWall() {
        GridMap map = new GridMap(new Cell[][] {
                { EMPTY, WALL, EMPTY },
                { EMPTY, EMPTY, EMPTY },
                { EMPTY, EMPTY, TERMINAL }
        });
        AStarPathfinder pathfinder = new AStarPathfinder(map);

        SearchResult result = pathfinder.search(new Position(0, 0), new Position(2, 2));

        assertTrue(result.pathFound());
        assertEquals(4, result.totalCost());
        assertEquals(5, result.path().size());
        assertEquals(4, result.actions().size());
        assertEquals(new Position(0, 0), result.path().getFirst());
        assertEquals(new Position(2, 2), result.path().getLast());
        assertEquals(10, result.totalReward());
    }

    @Test
    @DisplayName ("Reports when no path exists")
    void reportsWhenNoPathExists() {
        GridMap map = new GridMap(new Cell[][] {
                { EMPTY, WALL, TERMINAL },
                { EMPTY, WALL, EMPTY },
                { EMPTY, WALL, EMPTY }
        });

        SearchResult result = new AStarPathfinder(map)
                .search(new Position(0, 0), new Position(0, 2));

        assertFalse(result.pathFound());
        assertTrue(result.path().isEmpty());
        assertTrue(result.actions().isEmpty());
    }

    @Test
    @DisplayName ("Does not route through another terminal")
    void doesNotRouteThroughAnOtherTerminal() {
        GridMap map = new GridMap(new Cell[][] {
                { EMPTY, TERMINAL, EMPTY },
                { EMPTY, EMPTY, EMPTY }
        });

        SearchResult result = new AStarPathfinder(map)
                .search(new Position(0, 0), new Position(0, 2));

        assertTrue(result.pathFound());
        assertFalse(result.path().contains(new Position(0, 1)));
        assertEquals(4, result.totalCost());
    }

    @Test
    @DisplayName ("Start already at goal produces empty action sequence")
    void startAlreadyAtGoalProducesEmptyActionSequence() {
        Position startAndEnd = new Position(0, 0);
        SearchResult result = new AStarPathfinder(new GridMap(new Cell[][] { { EMPTY } }))
                .search(startAndEnd, startAndEnd);

        assertTrue(result.pathFound());
        assertEquals(List.of(startAndEnd), result.path());
        assertTrue(result.actions().isEmpty());
        assertEquals(0, result.totalCost());
    }

    @Test
    @DisplayName ("Rejects non-walkable start or end")
    void rejectsNonWalkableStartOrEnd() {
        AStarPathfinder pathfinder = new AStarPathfinder(new GridMap(new Cell[][] {
                { EMPTY, WALL, TERMINAL }
        }));

        assertThrows(IllegalArgumentException.class,
                () -> pathfinder.search(new Position(0, 1), new Position(0, 2)));
        assertThrows(IllegalArgumentException.class,
                () -> pathfinder.search(new Position(0, 0), new Position(0, 1)));
    }

    @Test
    @DisplayName ("Returned actions follow the returned path")
    void returnedActionsFollowTheReturnedPath() {
        GridMap map = new GridMap(new Cell[][] {
                { EMPTY, EMPTY },
                { EMPTY, TERMINAL }
        });
        SearchResult result = new AStarPathfinder(map)
                .search(new Position(0, 0), new Position(1, 1));

        Position current = result.path().getFirst();
        for (int i = 0; i < result.actions().size(); i++) {
            Direction action = result.actions().get(i);
            Position next = switch (action) {
                case UP -> new Position(current.row() - 1, current.col());
                case DOWN -> new Position(current.row() + 1, current.col());
                case LEFT -> new Position(current.row(), current.col() - 1);
                case RIGHT -> new Position(current.row(), current.col() + 1);
            };
            assertEquals(result.path().get(i + 1), next);
            current = next;
        }
    }
}
