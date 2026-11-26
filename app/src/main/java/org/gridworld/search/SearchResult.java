package org.gridworld.search;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

import java.util.List;

public record SearchResult(
        boolean pathFound,
        List<Position> path,
        List<Direction> actions,
        int totalCost,
        int totalReward,
        int expandedNodes) {

    // ------------------- Constructor -------------------

    public SearchResult {
        path = List.copyOf(path);
        actions = List.copyOf(actions);
    }
}