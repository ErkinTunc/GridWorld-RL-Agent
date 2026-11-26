package org.gridworld.environment;

import org.gridworld.domain.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepResultTest {

    private final Position position = new Position(1, 2);

    private StepResult normalResult;
    private StepResult terminalResult;

    // ---------------- Setup ----------------

    @BeforeEach
    void setUp() {
        normalResult = new StepResult(position, -1, false);
        terminalResult = new StepResult(position, 10, true);
    }

    // ---------------- Result Tests ----------------

    @Test
    @DisplayName ("StepResult stores position, reward and terminal state correctly")
    void storesStepResult() {
        assertEquals(position, normalResult.position());
        assertEquals(-1, normalResult.reward());
        assertFalse(normalResult.isTerminal());
    }

    // ---------------- Terminal State Tests ----------------

    @Test
    @DisplayName ("StepResult identifies terminal and non-terminal states correctly")
    void identifiesTerminalState() {
        assertTrue(terminalResult.isTerminal());
        assertTrue(terminalResult.isTerminal());
    }

    @Test
    @DisplayName ("StepResult identifies terminal and non-terminal states correctly")
    void identifiesNonTerminalState() {
        assertFalse(normalResult.isTerminal());
    }

    // ---------------- Exception Tests ----------------

    @Test
    @DisplayName ("StepResult rejects null position")
    void rejectsNullPosition() {
        assertThrows(NullPointerException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        new StepResult(null, 0, false);
                    }
                });
    }
}