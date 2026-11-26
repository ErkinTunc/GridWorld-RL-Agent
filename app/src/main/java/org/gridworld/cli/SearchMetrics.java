package org.gridworld.cli;

import org.gridworld.domain.Position;
import org.gridworld.search.SearchResult;

public record SearchMetrics(
        SearchResult result,
        long executionTimeNanos,
        Position finalPosition) {

    // ---------------- Public Methods ----------------

    public double executionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }
}