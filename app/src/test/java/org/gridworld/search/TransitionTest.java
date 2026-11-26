package org.gridworld.search;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransitionTest {

    private final SearchState state = new SearchState(new Position(0, 1), 1);

    @Test
    void storesTransitionValues() {
        Transition transition = new Transition(state, Direction.RIGHT, -1, 12L);

        assertEquals(state, transition.state());
        assertEquals(Direction.RIGHT, transition.action());
        assertEquals(-1, transition.reward());
        assertEquals(12L, transition.cost());
    }

    @Test
    void acceptsZeroCost() {
        Transition transition = new Transition(state, Direction.RIGHT, 10, 0L);

        assertEquals(0L, transition.cost());
        assertEquals(10, transition.reward());
    }

    @Test
    void acceptsZeroReward() {
        Transition transition = new Transition(state, Direction.RIGHT, 0, 1L);

        assertEquals(0, transition.reward());
    }

    @Test
    void rejectsNullState() {
        assertThrows(NullPointerException.class,
                () -> new Transition(null, Direction.RIGHT, -1, 12L));
    }

    @Test
    void rejectsNullAction() {
        assertThrows(NullPointerException.class,
                () -> new Transition(state, null, -1, 12L));
    }

    @Test
    void rejectsNegativeCost() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transition(state, Direction.RIGHT, -1, -1L));
    }
}