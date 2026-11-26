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
     * Finds an optimal path by maximizing total reward
     * within the step limit.
     *
     * @param problem The search problem to solve.
     * @return The result of the path search.
     * @throws NullPointerException if the problem is null.
     */
    @Override
    public SearchResult search(SearchProblem problem) {
        Objects.requireNonNull(problem, "Problem cannot be null");

        SearchState initial = problem.initialState();

        Map<StateKey, Long> bestCosts = new HashMap<>();
        bestCosts.put(keyOf(initial), 0L);

        PriorityQueue<SearchNode> frontier = createFrontier(initial, problem.goal());

        return findPath(problem, bestCosts, frontier);
    }

    // ---------------- Initialization Methods ----------------

    private static PriorityQueue<SearchNode> createFrontier(
            SearchState initial, Position goal) {

        PriorityQueue<SearchNode> frontier = new PriorityQueue<>(
                Comparator.comparingLong(SearchNode::estimatedTotalCost)
                        .thenComparingLong(SearchNode::costFromStart));

        frontier.add(new SearchNode(
                initial,
                0L,
                manhattanDistance(initial.position(), goal),
                null,
                null));

        return frontier;
    }

    // ---------------- Search Methods ----------------

    private static SearchResult findPath(
            SearchProblem problem,
            Map<StateKey, Long> bestCosts,
            PriorityQueue<SearchNode> frontier) {

        int expandedNodes = 0;

        while (!frontier.isEmpty()) {
            SearchNode current = frontier.remove();

            if (isOutdated(current, bestCosts)) {
                continue;
            }

            if (problem.isGoal(current.state())) {
                return buildSuccessResult(current, expandedNodes);
            }

            expandedNodes++;
            expandNeighbors(current, problem, bestCosts, frontier);
        }

        return buildFailureResult(expandedNodes);
    }

    private static void expandNeighbors(
            SearchNode current,
            SearchProblem problem,
            Map<StateKey, Long> bestCosts,
            PriorityQueue<SearchNode> frontier) {

        for (Transition transition : problem.successors(current.state())) {
            long tentativeCost = current.costFromStart() + transition.cost();
            StateKey key = keyOf(transition.state());

            if (tentativeCost >= bestCosts.getOrDefault(key, Long.MAX_VALUE)) {
                continue;
            }

            bestCosts.put(key, tentativeCost);

            frontier.add(createSuccessorNode(
                    current, transition, tentativeCost, problem.goal()));
        }
    }

    private static SearchNode createSuccessorNode(
            SearchNode parent,
            Transition transition,
            long costFromStart,
            Position goal) {

        SearchState state = transition.state();

        long heuristic = manhattanDistance(state.position(), goal);

        return new SearchNode(
                state,
                costFromStart,
                costFromStart + heuristic,
                parent,
                transition.action());
    }

    // ---------------- Search Helper Methods ----------------

    private static boolean isOutdated(
            SearchNode node,
            Map<StateKey, Long> bestCosts) {

        return node.costFromStart() != bestCosts.get(keyOf(node.state()));
    }

    private static StateKey keyOf(SearchState state) {
        return new StateKey(
                state.position(), state.stepsUsed());
    }

    // ---------------- Result Construction ----------------

    private static SearchResult buildSuccessResult(
            SearchNode goal, int expandedNodes) {

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

    private static SearchResult buildFailureResult(int expandedNodes) {
        return new SearchResult(
                false,
                List.of(),
                List.of(),
                0L,
                0L,
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