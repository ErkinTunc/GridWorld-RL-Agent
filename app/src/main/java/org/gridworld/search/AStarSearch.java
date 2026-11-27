package org.gridworld.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

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

    /**
     * This method creates a priority queue that is used as the frontire. 
     * @param initial The initial state of the search problem
     * @param goal The goal position of the search problem
     * @return
     */
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

    /**
     * This method implements the A* search algorithm to find an optimal path from the initial state to the goal state.
     * @param problem The search problem to solve
     * @param bestCosts A map that keeps track of the best cost to reach each state
     * @param frontier A priority queue that stores the states to be explored, ordered by their estimated total cost
     * @return The result of the path search, including whether a path was found, the path itself, the actions taken, the total cost, and the total reward
     */
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

    /**
     * This method expands the neighbors of the current node by generating successor nodes for each valid transition from the current state.
     * @param current The current search node
     * @param problem The search problem
     * @param bestCosts A map that keeps track of the best cost to reach each state
     * @param frontier A priority queue that stores the states to be explored, ordered by their estimated total cost
     */
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

    /**
     * This method creates a new search node for a successor state based on the current node, 
     * the transition to the successor, the cost from the start to the successor, and the goal position.
     * @param parent The parent search node from which the successor is generated
     * @param transition The transition that leads to the successor state
     * @param costFromStart The cost incurred to reach the successor state from the initial state
     * @param goal The goal position of the search problem
     * @return A new search node representing the successor state, including its cost from the start, estimated total cost, parent node, 
     * and action taken to reach it
     */
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

    /**
     * This method checks if the given search node is outdated by comparing its cost from the start with the best cost recorded for its state in the bestCosts map.
     * @param node The search node to check for outdatedness
     * @param bestCosts A map that keeps track of the best cost to reach each state
     * @return true if the node is outdated (i.e., its cost from the start does not match the best cost recorded for its state), false otherwise
     */
    private static boolean isOutdated(
            SearchNode node,
            Map<StateKey, Long> bestCosts) {

        return node.costFromStart() != bestCosts.get(keyOf(node.state()));
    }

    /**
     * This method generates a unique key for a given search state based on its position and the number of steps used to reach that state.
     * @param state The search state for which to generate a key
     * @return A StateKey object that uniquely identifies the search state based on its position and steps used
     */
    private static StateKey keyOf(SearchState state) {
        return new StateKey(
                state.position(), state.stepsUsed());
    }

    // ---------------- Result Construction ----------------

    /**
     * This method constructs a SearchResult object representing a successful path found by the A* search algorithm.
     * Private static because it is only used internally within the AStarSearch class to build the result when a goal state is reached. 
     * Static because it does not depend on any instance variables of the AStarSearch class and can be called without creating an instance of the class.
     * @param goal The search node representing the goal state reached by the search algorithm
     * @param expandedNodes The number of nodes that were expanded during the search process
     * @return A SearchResult object containing the path found, the actions taken, the total cost, the total reward, and the number of expanded nodes
     */
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