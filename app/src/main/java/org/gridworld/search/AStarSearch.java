package org.gridworld.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

public final class AStarSearch implements SearchAlgorithm {

    private final GridMap gridMap;

    // ------------- Constructor ------------

    public AStarSearch(GridMap gridMap) {
        this.gridMap = Objects.requireNonNull(gridMap, "Grid map cannot be null");
    }

    // ---------------- Public Methods ----------------

    /**
     * Searches for a path from the start position to the end position
     * using the A* algorithm.
     *
     * @param start The starting position.
     * @param end   The ending position.
     * @return The result of the path search.
     * @throws IllegalArgumentException if the positions are not walkable.
     * @throws NullPointerException     if the positions are null.
     */
    @Override
    public SearchResult search(Position start, Position end) {
        validatePositions(start, end);

        Map<Position, Integer> costFromStart = new HashMap<>();
        Map<Position, Predecessor> predecessors = new HashMap<>();

        PriorityQueue<SearchNode> frontier = createFrontier();

        costFromStart.put(start, 0);
        frontier.add(new SearchNode(start, 0, manhattanDistance(start, end)));

        int expandedNodes = 0;

        while (!frontier.isEmpty()) {
            SearchNode current = frontier.remove();

            if (isOutdated(current, costFromStart)) {
                continue;
            }

            if (current.position().equals(end)) {
                return buildResult(
                        start,
                        end,
                        predecessors,
                        current.costFromStart(),
                        expandedNodes);
            }

            expandedNodes++;

            expandNeighbors(current, end, costFromStart, predecessors, frontier);
        }

        return new SearchResult(false, List.of(), List.of(), 0, 0, expandedNodes);
    }

    // ---------------- Search Helper Methods ----------------

    private static PriorityQueue<SearchNode> createFrontier() {

        Comparator<SearchNode> comparator = Comparator
                .comparingInt(SearchNode::estimatedTotalCost)
                .thenComparingInt(SearchNode::costFromStart)
                .thenComparingInt(node -> node.position().row())
                .thenComparingInt(node -> node.position().col());

        return new PriorityQueue<>(comparator);
    }

    private void expandNeighbors(
            SearchNode current,
            Position end,
            Map<Position, Integer> costFromStart,
            Map<Position, Predecessor> predecessors,
            PriorityQueue<SearchNode> frontier) {

        int tentativeCost = current.costFromStart() + 1;

        for (Direction direction : Direction.values()) {
            Position neighbor = Direction.calculateNextPosition(
                    current.position(), direction);

            if (!canVisit(neighbor, end)) {
                continue;
            }

            Integer previousCost = costFromStart.get(neighbor);

            if (previousCost != null && tentativeCost >= previousCost) {
                continue;
            }

            costFromStart.put(neighbor, tentativeCost);
            predecessors.put(
                    neighbor,
                    new Predecessor(current.position(), direction));

            frontier.add(new SearchNode(
                    neighbor,
                    tentativeCost,
                    tentativeCost + manhattanDistance(neighbor, end)));
        }
    }

    private static boolean isOutdated(
            SearchNode node,
            Map<Position, Integer> costFromStart) {

        int currentCost = node.costFromStart();
        int bestKnownCost = costFromStart.get(node.position());

        return currentCost != bestKnownCost;
    }

    private boolean canVisit(Position neighbor, Position end) {
        if (!gridMap.isWalkable(neighbor)) {
            return false;
        }

        return neighbor.equals(end)
                || gridMap.cellAt(neighbor).type() != CellType.TERMINAL;
    }

    // ---------------- Path Reconstruction Methods ----------------

    private SearchResult buildResult(
            Position start,
            Position end,
            Map<Position, Predecessor> predecessors,
            int totalCost,
            int expandedNodes) {

        List<Position> path = new ArrayList<>();
        List<Direction> actions = new ArrayList<>();

        Position current = end;
        path.add(current);

        while (!current.equals(start)) {
            Predecessor predecessor = predecessors.get(current);

            actions.add(predecessor.direction());

            current = predecessor.position();
            path.add(current);
        }

        Collections.reverse(path);
        Collections.reverse(actions);

        int totalReward = calculateTotalReward(path);

        return new SearchResult(
                true,
                List.copyOf(path),
                List.copyOf(actions),
                totalCost,
                totalReward,
                expandedNodes);
    }

    // ---------------- Calculation Methods ----------------

    private int calculateTotalReward(List<Position> path) {
        int totalReward = 0;

        for (int i = 1; i < path.size(); i++) {
            totalReward = Math.addExact(
                    totalReward,
                    gridMap.cellAt(path.get(i)).reward());
        }

        return totalReward;
    }

    private static int manhattanDistance(Position position, Position end) {
        int rowDistance = Math.abs(position.row() - end.row());
        int colDistance = Math.abs(position.col() - end.col());

        return rowDistance + colDistance;
    }

    // ---------------- Validation Methods ----------------

    private void validatePositions(Position start, Position end) {
        Objects.requireNonNull(start, "Start position cannot be null");
        Objects.requireNonNull(end, "End position cannot be null");

        if (!gridMap.isWalkable(start) || !gridMap.isWalkable(end)) {
            throw new IllegalArgumentException(
                    "Start and end positions must be walkable");
        }
    }

    // ------------- Private Classes -----------
    /**
     * Represents a node in the search space for the A* algorithm.
     * Each node contains a position, the cost from the start position to this node,
     * and the estimated total cost from the start position to the end position
     * through this node.
     */
    private record SearchNode(Position position, int costFromStart, int estimatedTotalCost) {
    }

    private record Predecessor(Position position, Direction direction) {
    }
}
