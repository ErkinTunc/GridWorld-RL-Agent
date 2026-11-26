package org.gridworld.search;

import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AStarSearchTest {

        // ---------------- Test Fixtures ----------------

        private static final Cell EMPTY = new Cell(CellType.EMPTY, 0);
        private static final Cell WALL = new Cell(CellType.WALL, 0);
        private static final Cell TERMINAL = new Cell(CellType.TERMINAL, 10);

        private final Position start = new Position(0, 0);
        private final SearchAlgorithm algorithm = new AStarSearch();

        // ---------------- Path Finding Tests ----------------

        @Test
        @DisplayName ("A* search finds path around wall")
        void findsPathAroundWall() {
                Position goal = new Position(2, 2);

                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, WALL, EMPTY },
                                { EMPTY, EMPTY, EMPTY },
                                { EMPTY, EMPTY, TERMINAL }
                }, goal, 4);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());
                assertEquals(4, result.actions().size());
                assertEquals(5, result.path().size());
                assertEquals(start, result.path().getFirst());
                assertEquals(goal, result.path().getLast());
                assertEquals(6L, result.totalReward());
                assertEquals(38L, result.totalCost());
        }

        @Test
        @DisplayName ("A* search reports when no path exists")
        void reportsWhenNoPathExists() {
                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, WALL, TERMINAL },
                                { EMPTY, WALL, EMPTY }
                }, new Position(0, 2), 4);

                SearchResult result = algorithm.search(problem);

                assertFalse(result.pathFound());
                assertTrue(result.path().isEmpty());
                assertTrue(result.actions().isEmpty());
        }

        @Test
        @DisplayName ("A* search does not route through another terminal cell")
        void doesNotRouteThroughAnotherTerminal() {
                Position trap = new Position(0, 1);

                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, TERMINAL, EMPTY },
                                { EMPTY, EMPTY, EMPTY }
                }, new Position(0, 2), 4);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());
                assertFalse(result.path().contains(trap));
                assertEquals(4, result.actions().size());
                assertEquals(-4L, result.totalReward());
                assertEquals(48L, result.totalCost());
        }

        @Test
        @DisplayName ("A* search finds path with multiple terminal cells")
        void returnedActionsFollowTheReturnedPath() {
                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, EMPTY },
                                { WALL, TERMINAL }
                }, new Position(1, 1), 2);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());

                assertEquals(List.of(
                                start,
                                new Position(0, 1),
                                new Position(1, 1)), result.path());

                assertEquals(List.of(
                                Direction.RIGHT,
                                Direction.DOWN), result.actions());
        }

        // ---------------- Reward Tests ----------------

        @Test
        @DisplayName ("A* search prefers higher reward over shorter path")
        void prefersHigherRewardOverShorterPath() {
                Cell bonus = new Cell(CellType.EMPTY, 3);
                Cell trap = new Cell(CellType.TERMINAL, -10);

                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, EMPTY, TERMINAL },
                                { WALL, bonus, trap }
                }, new Position(0, 2), 4);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());

                assertEquals(List.of(
                                Direction.RIGHT,
                                Direction.DOWN,
                                Direction.UP,
                                Direction.RIGHT), result.actions());

                assertEquals(9L, result.totalReward());
                assertEquals(35L, result.totalCost());
        }

        @Test
        @DisplayName("A* search supports negative total reward")
        void supportsNegativeTotalReward() {
                Cell negativeGoal = new Cell(CellType.TERMINAL, -10);

                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, negativeGoal }
                }, new Position(0, 1), 1);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());
                assertEquals(-11L, result.totalReward());
                assertEquals(12L, result.totalCost());
        }

        @Test
        @DisplayName("A* search supports reward beyond integer range")
        void supportsRewardBeyondIntegerRange() {
                Cell bonus = new Cell(CellType.EMPTY, Integer.MAX_VALUE);
                Cell goal = new Cell(CellType.TERMINAL, Integer.MAX_VALUE);

                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, bonus, goal }
                }, new Position(0, 2), 2);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());
                assertEquals(4_294_967_292L, result.totalReward());
                assertEquals(4L, result.totalCost());
        }

        // ---------------- Edge Case Tests ----------------

        @Test
        @DisplayName ("A* search respects max steps")
        void respectsMaxSteps() {
                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY, EMPTY, TERMINAL }
                }, new Position(0, 2), 1);

                assertFalse(algorithm.search(problem).pathFound());
        }

        @Test
        @DisplayName ("A* search finds path when max steps is sufficient")
        void startEqualsGoalProducesEmptyActionSequence() {
                SearchProblem problem = createProblem(new Cell[][] {
                                { EMPTY }
                }, start, 4);

                SearchResult result = algorithm.search(problem);

                assertTrue(result.pathFound());
                assertEquals(List.of(start), result.path());
                assertTrue(result.actions().isEmpty());
                assertEquals(0L, result.totalCost());
                assertEquals(0L, result.totalReward());
        }

        // ---------------- State Integrity Tests ----------------

        @Test
        @DisplayName ("A* search does not change world state")
        void doesNotChangeWorldState() {
                GridMap map = new GridMap(new Cell[][] {
                                { EMPTY, TERMINAL }
                });

                GridWorld world = new GridWorld(map, start, -1);

                SearchProblem problem = new SearchProblem(
                                world, start, new Position(0, 1), 1);

                assertTrue(algorithm.search(problem).pathFound());
                assertEquals(start, world.position());
        }

        // ---------------- Validation Tests ----------------

        @Test
        @DisplayName ("A* search rejects null problem")
        void rejectsNullProblem() {
                assertThrows(NullPointerException.class,
                                () -> algorithm.search(null));
        }

        // ---------------- Helper Methods ----------------

        private SearchProblem createProblem(
                        Cell[][] cells, Position goal, int maxSteps) {

                GridMap map = new GridMap(cells);
                GridWorld world = new GridWorld(map, start, -1);

                return new SearchProblem(world, start, goal, maxSteps);
        }
}