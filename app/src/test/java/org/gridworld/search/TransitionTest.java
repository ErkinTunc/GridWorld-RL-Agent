package org.gridworld.search;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransitionTest {

    private final SearchState state = new SearchState(
            new Position(0, 1),
            1,
            4L);

    // ---------------------- Tests ----------------------

    @Test
    @DisplayName ("Transition stores state, action, reward and cost correctly")
    void storesTransitionValues() {
        Transition transition = new Transition(state, Direction.RIGHT, -1, 12L);

        assertEquals(state, transition.state());
        assertEquals(Direction.RIGHT, transition.action());
        assertEquals(-1, transition.reward());
        assertEquals(12L, transition.cost());
    }

    // ---------------- Validation Tests -----------------

    @Test
    @DisplayName ("Transition accepts zero cost")
    void acceptsZeroCost() {
        Transition transition = new Transition(state, Direction.RIGHT, 10, 0L);

        assertEquals(0L, transition.cost());
        assertEquals(10, transition.reward());
    }

    @Test
    @DisplayName ("Transition accepts zero reward")
    void acceptsZeroReward() {
        Transition transition = new Transition(state, Direction.RIGHT, 0, 1L);

        assertEquals(0, transition.reward());
    }

    @Test
    @DisplayName ("Transition rejects null state")
    void rejectsNullState() {
        assertThrows(NullPointerException.class,
                () -> new Transition(null, Direction.RIGHT, -1, 12L));
    }

    @Test
    @DisplayName ("Transition rejects null action")
    void rejectsNullAction() {
        assertThrows(NullPointerException.class,
                () -> new Transition(state, null, -1, 12L));
    }

    @Test
    @DisplayName ("Transition rejects negative cost")
    void rejectsNegativeCost() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transition(state, Direction.RIGHT, -1, -1L));
    }
}