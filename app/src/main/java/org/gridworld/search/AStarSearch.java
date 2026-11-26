package org.gridworld.search;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public final class AStarSearch implements SearchAlgorithm {

    // ---------------- Public Methods ----------------

    /**
     * Searches for an optimal path using the A* algorithm
     * while maximizing the total reward.
     *
     * @param problem The search problem to solve.
     * @return The result of the path search.
     * @throws NullPointerException if the problem is null.
     */
    @Override
    public SearchResult search(SearchProblem problem) {
        Objects.requireNonNull(problem, "Problem cannot be null");

        Map<StateKey, Long> bestCosts = new HashMap<>();
        PriorityQueue<SearchNode> frontier = createFrontier();

        SearchState initial = problem.initialState();

        frontier.add(new SearchNode(
                initial,
                0L,
                manhattanDistance(initial.position(), problem.goal()),
                null,
                null));

        bestCosts.put(keyOf(initial), 0L);

        int expandedNodes = 0;

        while (!frontier.isEmpty()) {
            SearchNode current = frontier.remove();

            if (isOutdated(current, bestCosts)) {
                continue;
            }

            if (problem.isGoal(current.state())) {
                return buildResult(current, expandedNodes);
            }

            expandedNodes++;

            expandNeighbors(current, problem, bestCosts, frontier);
        }

        return new SearchResult(
                false, List.of(), List.of(),
                0L, 0L, expandedNodes);
    }

    // ---------------- Search Helper Methods ----------------

    private static PriorityQueue<SearchNode> createFrontier() {
        return new PriorityQueue<>(
                Comparator.comparingLong(
                        SearchNode::estimatedTotalCost)
                        .thenComparingLong(SearchNode::costFromStart));
    }

    private static void expandNeighbors(
            SearchNode current,
            SearchProblem problem,
            Map<StateKey, Long> bestCosts,
            PriorityQueue<SearchNode> frontier) {

        for (Transition transition : problem.successors(current.state())) {

            SearchState next = transition.state();

            long tentativeCost = current.costFromStart() + transition.cost();

            StateKey key = keyOf(next);

            if (tentativeCost >= bestCosts.getOrDefault(key, Long.MAX_VALUE)) {
                continue;
            }

            bestCosts.put(key, tentativeCost);

            long heuristic = manhattanDistance(
                    next.position(), problem.goal());

            frontier.add(new SearchNode(
                    next,
                    tentativeCost,
                    tentativeCost + heuristic,
                    current,
                    transition.action()));
        }
    }

    private static boolean isOutdated(
            SearchNode node,
            Map<StateKey, Long> bestCosts) {

        return node.costFromStart() != bestCosts.get(keyOf(node.state()));
    }

    private static StateKey keyOf(SearchState state) {
        return new StateKey(
                state.position(), state.stepsUsed());
    }

    // ---------------- Path Reconstruction ----------------

    private static SearchResult buildResult(SearchNode goal, int expandedNodes) {

        List<Position> path = new ArrayList<>();
        List<Direction> actions = new ArrayList<>();

        SearchNode current = goal;
        while (current != null) {
            path.add(current.state().position());

            if (current.action() != null) {
                actions.add(current.action());
            }

            current = current.parent();
        }

        Collections.reverse(path);
        Collections.reverse(actions);

        return new SearchResult(
                true,
                path,
                actions,
                goal.costFromStart(),
                goal.state().totalReward(),
                expandedNodes);
    }

    // ---------------- Heuristic ----------------

    private static long manhattanDistance(
            Position position, Position goal) {

        return Math.abs((long) position.row() - goal.row())
                + Math.abs((long) position.col() - goal.col());
    }

    // ---------------- Private Records ----------------

    private record StateKey(
            Position position,
            int stepsUsed) {
    }

    private record SearchNode(
            SearchState state,
            long costFromStart,
            long estimatedTotalCost,
            SearchNode parent,
            Direction action) {
    }
}