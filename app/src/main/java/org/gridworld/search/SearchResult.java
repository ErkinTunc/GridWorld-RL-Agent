package org.gridworld.search;

import java.util.List;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

/**
 * SearchResult represents the outcome of a search algorithm in the GridWorld environment.
 * It encapsulates whether a path was found, the sequence of positions and actions taken to reach
 * the goal, the total cost and reward accumulated during the search, and the number of nodes expanded.
 *
 * <strong>Attention:</strong> It is different from the SearchState, which represents a single state in the search space. The SearchResult provides a summary of the entire search process.
 * 
 * @param pathFound indicates whether a path to the goal was found
 * @param path the sequence of positions from the start to the goal, including both endpoints
 * @param actions the sequence of actions taken to move from one position to the next in the path
 * @param totalCost the total cost incurred during the search, which may include movement costs and other penalties
 * @param totalReward the total reward accumulated during the search, which may include rewards from reaching terminal states or other beneficial positions
 * @param expandedNodes the number of nodes that were expanded during the search process, which can be an indicator of the search algorithm's efficiency
 */
public record SearchResult(
        boolean pathFound,
        List<Position> path,
        List<Direction> actions,
        long totalCost,
        long totalReward,
        int expandedNodes) {

    // ------------------- Constructor -------------------

    public SearchResult {
        path = List.copyOf(path);
        actions = List.copyOf(actions);
    }
}