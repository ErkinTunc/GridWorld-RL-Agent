package org.gridworld.search;

import java.util.Set;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransitionTest {

    private static final SearchState STATE = new SearchState(
            new Position(0, 1),
            1,
            4,
            Set.of(new Position(0, 0), new Position(0, 1)));

    @Test
    void storesTheTransitionValues() {
        Transition transition = new Transition(STATE, Direction.RIGHT, 4, 1);

        assertEquals(STATE, transition.state());
        assertEquals(Direction.RIGHT, transition.action());
        assertEquals(4, transition.reward());
        assertEquals(1, transition.cost());
    }

    @Test
    void rejectsNullStateOrAction() {
        assertThrows(
                NullPointerException.class,
                () -> new Transition(null, Direction.RIGHT, 0, 1));
        assertThrows(
                NullPointerException.class,
                () -> new Transition(STATE, null, 0, 1));
    }

    @Test
    void rejectsNegativeCost() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Transition(STATE, Direction.RIGHT, 0, -1));
    }
}
