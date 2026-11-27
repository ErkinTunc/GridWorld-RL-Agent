package org.gridworld.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;

public final class BacktrackingSearch implements SearchAlgorithm {

    // ---------------- Public Methods ----------------

    /**
     * Finds an optimal path by exploring possible paths
     * and maximizing total reward within the step limit.
     *
     * @param problem The search problem to solve.
     * @return The result of the path search.
     * @throws NullPointerException if the problem is null.
     */
    @Override
    public SearchResult search(SearchProblem problem) {
        Objects.requireNonNull(problem, "Problem cannot be null");

        SearchState initial = problem.initialState();
        SearchContext context = new SearchContext(initial.position());

        explore(problem, initial, 0L, context);

        return context.toResult();
    }

    // ---------------- Recursive Search ----------------

    private static void explore(
            SearchProblem problem,
            SearchState state,
            long currentCost,
            SearchContext context) {

        if (problem.isGoal(state) ) {
            context.saveIfBetter(state, currentCost);
            return;
        }

        context.expandedNodes++;

        for (Transition transition : problem.successors(state)) {
            context.addStep(transition);

            explore(
                    problem,
                    transition.state(),
                    currentCost + transition.cost(),
                    context);

            context.removeLastStep();
        }
    }

    // ---------------- Private Class ----------------

    private static final class SearchContext {

        private final List<Position> currentPath = new ArrayList<>();
        private final List<Direction> currentActions = new ArrayList<>();

        private List<Position> bestPath = List.of();
        private List<Direction> bestActions = List.of();

        private long bestCost;
        private long bestReward;
        private int expandedNodes;

        private SearchContext(Position start) {
            currentPath.add(start);
        }

        // ---------------- Path Management ----------------

        private void addStep(Transition transition) {
            currentPath.add(transition.state().position());
            currentActions.add(transition.action());
        }

        private void removeLastStep() {
            currentPath.removeLast();
            currentActions.removeLast();
        }

        // ---------------- Solution Management ----------------

        private void saveIfBetter(SearchState state, long currentCost) {
            if (hasSolution() && state.totalReward() <= bestReward) {
                return;
            }

            bestPath = List.copyOf(currentPath);
            bestActions = List.copyOf(currentActions);
            bestReward = state.totalReward();
            bestCost = currentCost;
        }

        private boolean hasSolution() {
            return !bestPath.isEmpty();
        }

        // ---------------- Result Construction ----------------

        private SearchResult toResult() {
            return new SearchResult(
                    hasSolution(),
                    bestPath,
                    bestActions,
                    bestCost,
                    bestReward,
                    expandedNodes);
        }
    }
}