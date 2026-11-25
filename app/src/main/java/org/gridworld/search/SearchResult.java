package org.gridworld.search;

import java.util.List;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

public record SearchResult(
        boolean pathFound,
        List<Position> path,
        List<Direction> actions,
        int totalCost,
        int totalReward,
        int expandedNodes) {

}
