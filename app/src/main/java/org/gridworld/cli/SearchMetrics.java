package org.gridworld.cli;

import org.gridworld.search.SearchResult;

public record SearchMetrics(
        SearchResult result,
        long executionTimeNanos) {

    // ---------------- Public Methods ----------------

    public double executionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }
}