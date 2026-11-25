package org.gridworld.environment;

import org.gridworld.domain.Position;
import java.util.Objects;

public record StepResult(
        Position position,
        int reward,
        boolean done) {

    public StepResult {
        Objects.requireNonNull(position, "Position cannot be null");
    }

    public boolean isTerminal() {
        return done;
    }
}