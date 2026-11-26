package org.gridworld.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.environment.GridWorld;
import org.gridworld.environment.StepResult;



/**
 * Represents a search problem in the GridWorld environment.
 * It defines the initial state, goal state, and the rules for transitioning between states.
 * It also provides methods to retrieve the successors of a given state and to check if a state
 * is a goal state.
 * 
 */
public final class SearchProblem {

    //------------------Attributes---------------------

    private final GridWorld world;
    private final Position start;
    private final Position goal;
    private final int maxSteps;
    private final long rewardOffset;

    // ------------------- Constructor -------------------


    /**
     * Creates a new SearchProblem instance with the specified parameters.
     * @param world the GridWorld environment in which the search problem is defined
     * @param start the starting position for the search
     * @param goal the goal position for the search
     * @param maxSteps the maximum number of steps allowed to reach the goal
     * @throws IllegalArgumentException if the start or goal positions are not walkable, if the start position is terminal, or if maxSteps is negative
     */
    public SearchProblem(GridWorld world,Position start,Position goal,int maxSteps) throws IllegalArgumentException {

        this.world = Objects.requireNonNull(world, "World cannot be null");

        GridMap map = world.getMap();

        validateParameters(map, start, goal, maxSteps);

        this.start = start;
        this.goal = goal;
        this.maxSteps = maxSteps;
        this.rewardOffset = (long) map.rewardUpperBound() + 1;
    }

    // ------------------ Public Methods -----------------


    /**
     * Returns the initial state of the search problem.
     * The initial state consists of the starting position, zero steps used, and zero total reward.
     * @return the initial state
     */
    public SearchState initialState() {
        return new SearchState(start, 0, 0L);
    }

    /**
     * As InitialState(), but returns the start position.
     * @see SearchProblem#initialState()
     * @return the goal position of the search problem
     */
    public Position goal() {
        return goal;
    }

    /**
     * Check if the given state is a goal state.
     * It is considered as a goal state if the position matches the goal position and the number of steps used is less or equal to the maximum allowed steps.
     * @param state the current state to check
     * @return true if the state is a goal state, false otherwise
     */
    public boolean isGoal(SearchState state) {
        return state.position().equals(goal)
                && state.stepsUsed() <= maxSteps;
    }

    /**
     * Returns a list of valid successor transitions from the given state.
     * A successor transition consists of the next state, the action taken to reach that state, the reward received, and the cost of the transition.
     * If the current state is a goal state or if the maximum number of steps has been reached, an empty list is returned.
     * @param state the current state from which to generate successors
     * @return a list of valid successor transitions
     * @see Transition
     */
    public List<Transition> successors(SearchState state) {
        if (isGoal(state) || state.stepsUsed() >= maxSteps) {
            return List.of();
        }

        List<Transition> transitions = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            StepResult result = world.preview(state.position(), direction);

            if (!isValidSuccessor(state, result)) {
                continue;
            }

            SearchState nextState = createNextState(state, result);

            long cost = calculateCost(nextState, result.reward());

            transitions.add(new Transition(
                    nextState,
                    direction,
                    result.reward(),
                    cost));
        }

        return transitions;
    }

    // ------------------ Helper Methods -----------------

    /**
     * Checks if the given StepResult represents a valid successor state from the current state.
     * A successor is valid if the resulting position is different from the current position and either the resulting state 
     * is not terminal or it is terminal but matches the goal position. 
     * @param state the current state from which the successor is being generated
     * @param result the StepResult obtained from previewing the action in the GridWorld
     * @return true if the successor is valid, false otherwise
     * @see StepResult
     */
    private boolean isValidSuccessor(SearchState state, StepResult result) {
        return !result.position().equals(state.position())
                && (!result.isTerminal() || result.position().equals(goal));
    }

    /**
     * Creates the next state based on the current state and the result of taking an action.
     * The next state consists of the new position, the incremented step count, and the updated total reward.
     * This method is used to generate the next state in the search process after taking an action in the GridWorld.
     * This method guarantees that the next state is valid and does not exceed the maximum number of steps allowed.
     * @param state the current state from which the transition is made
     * @param result the StepResult obtained from previewing the action in the GridWorld
     * @return the next state after taking the action.
     */
    private static SearchState createNextState(
            SearchState state, StepResult result) {

        return new SearchState(
                result.position(),
                state.stepsUsed() + 1,
                state.totalReward() + result.reward());
    }


    /**
     * Calculates the cost of transitioning to the given state based on the reward received.
     * The cost is calculated as the difference between a predefined reward offset and the reward received.
     * If the state is a goal state, the cost is adjusted to account for the remaining steps that could have been used to reach the goal, 
     * effectively penalizing the agent for not reaching the goal in the minimum number of steps.
     * This method ensures that the cost is always non-negative and reflects the efficiency of reaching the goal state.
     * @param state the state for which the cost is being calculated
     * @param reward the reward received for transitioning to the state
     * @return the cost of transitioning to the state
     */
    private long calculateCost(SearchState state, int reward) {
        long cost = rewardOffset - reward;

        if (isGoal(state)) {
            cost += (maxSteps - state.stepsUsed()) * rewardOffset;
        }

        return cost;
    }

    // ---------------- Validation Methods ---------------

    /**
     * Validates the parameters provided to the SearchProblem constructor. Guarantees that the start and goal positions are walkable, 
     * that the start position is not terminal, and that the maximum number of steps is non-negative.
     * @param map the GridMap of the GridWorld environment
     * @param start the starting position for the search
     * @param goal the goal position for the search
     * @param maxSteps the maximum number of steps allowed to reach the goal
     */
    private static void validateParameters(
            GridMap map,
            Position start,
            Position goal,
            int maxSteps) {

        if (!map.isWalkable(start) || !map.isWalkable(goal)) {
            throw new IllegalArgumentException("Start and goal must be walkable");
        }

        if (map.cellAt(start).isTerminal()) {
            throw new IllegalArgumentException("Start cannot be terminal");
        }

        if (maxSteps < 0) {
            throw new IllegalArgumentException("Max steps cannot be negative");
        }
    }
}
