package org.gridworld.search;

import java.util.Objects;

import org.gridworld.domain.Position;

/**
 * Represents a state in the search space of the GridWorld environment.
 * It encapsulates the current position of the agent, the number of steps taken to reach this state, and the total reward accumulated so far.
 * 
 * <strong>Attention:</strong> It is different from the SearchResult, which represents the outcome of a search algorithm. 
 * The SearchState represents a single state in the search space, while the SearchResult provides a summary of the entire search process.
 * @param position the current position of the agent in the grid
 * @param stepsUsed the number of steps taken to reach this state from the initial state
 * @param totalReward the total reward accumulated by the agent up to this state
 */
public record SearchState(
        Position position,
        int stepsUsed,
        long totalReward) {

    // ------------------- Constructor -------------------

    public SearchState {
        Objects.requireNonNull(position, "Position cannot be null");

        if (stepsUsed < 0) {
            throw new IllegalArgumentException(
                    "Steps used cannot be negative");
        }
    }
}