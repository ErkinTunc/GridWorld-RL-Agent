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

public final class AStarSeach implements SearchAlgorithm {

    private final GridMap gridMap;

    // ------------- Constructor ------------
    public AStarSeach(GridMap gridMap) {
        this.gridMap = Objects.requireNonNull(gridMap, "Grid map cannot be null");
    }


    // ------------- Public Methods -----------

    /**
     * Searches for a path from the start position to the end position using the A* algorithm.
     * @param start The starting position.
     * @param end The ending position.
     * @return A SearchResult containing the path, actions, total cost, total reward, and the number of expanded nodes.
     * @throws IllegalArgumentException if the start or end positions are not walkable.
     * @throws NullPointerException if the start or end positions are null
     */
    @Override
    public SearchResult search(Position start, Position end) {  
        Objects.requireNonNull(start, "Start position cannot be null");
        Objects.requireNonNull(end, "End position cannot be null");

        if (!gridMap.isWalkable(start) || !gridMap.isWalkable(end)) {
            throw new IllegalArgumentException("Start and end positions must be walkable");
        }

        Map<Position, Integer> costFromStart = new HashMap<>();
        Map<Position, Position> predecessor = new HashMap<>();
        Map<Position, Direction> directionFromPredecessor = new HashMap<>();


        PriorityQueue<SearchNode> frontier = new PriorityQueue<>(
                Comparator.comparingInt(SearchNode::estimatedTotalCost)
                        .thenComparingInt(SearchNode::costFromStart)
                        .thenComparingInt(node -> node.position().row())
                        .thenComparingInt(node -> node.position().col()));

        costFromStart.put(start, 0);
        frontier.add(new SearchNode(start, 0, heuristic(start, end)));

        int expandedNodes = 0;

        while (!frontier.isEmpty()) {
            SearchNode current = frontier.remove();

            if (current.costFromStart() != costFromStart.get(current.position())) {
                continue;
            }

            if (current.position().equals(end)) {
                return buildResult(
                        start,
                        end,
                        predecessor,
                        directionFromPredecessor,
                        current.costFromStart(),
                        expandedNodes);
            }

            expandedNodes++;

            for (Direction direction : Direction.values()) {
                Position neighbor = move(current.position(), direction);

                if (!gridMap.isWalkable(neighbor)) {
                    continue;
                }

                if (!neighbor.equals(end)
                        && gridMap.cellAt(neighbor).type() == CellType.TERMINAL) {
                    continue;
                }

                int tentativeCost = current.costFromStart() + 1;
                Integer previousCost = costFromStart.get(neighbor);

                if (previousCost != null && tentativeCost >= previousCost) {
                    continue;
                }

                costFromStart.put(neighbor, tentativeCost);
                predecessor.put(neighbor, current.position());
                directionFromPredecessor.put(neighbor, direction);
                frontier.add(new SearchNode(
                        neighbor,
                        tentativeCost,
                        tentativeCost + heuristic(neighbor, end)));
            }
        }

        return new SearchResult(false, List.of(), List.of(), 0, 0, expandedNodes);
    }

    private SearchResult buildResult(Position start,Position end,Map<Position, Position> predecessor,Map<Position, Direction> directionFromPredecessor,int totalCost,int expandedNodes) {
        List<Position> path = new ArrayList<>();
        List<Direction> actions = new ArrayList<>();
        Position current = end;

        path.add(current);
        while (!current.equals(start)) {
            actions.add(directionFromPredecessor.get(current));
            current = predecessor.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        Collections.reverse(actions);

        int totalReward = 0;
        for (int i = 1; i < path.size(); i++) {
            totalReward = Math.addExact(totalReward, gridMap.cellAt(path.get(i)).reward());
        }

        return new SearchResult(true,List.copyOf(path),List.copyOf(actions),totalCost,totalReward,expandedNodes);
    }

    private Position move(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.row() - 1, position.col());
            case DOWN -> new Position(position.row() + 1, position.col());
            case LEFT -> new Position(position.row(), position.col() - 1);
            case RIGHT -> new Position(position.row(), position.col() + 1);
        };
    }

    private int heuristic(Position position, Position end) {
        return Math.abs(position.row() - end.row()) + Math.abs(position.col() - end.col());
    }

    // ------------- Private Classes -----------
    /**
     * Represents a node in the search space for the A* algorithm.
     * Each node contains a position, the cost from the start position to this node,
     * and the estimated total cost from the start position to the end position through this node.
     */
    private record SearchNode(Position position, int costFromStart, int estimatedTotalCost) {
    }
}
