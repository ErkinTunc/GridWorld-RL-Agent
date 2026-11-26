
package org.gridworld.cli;

import org.gridworld.domain.Cell;
import org.gridworld.domain.Position;

public record Scenario(
        String title,
        Cell[][] cells,
        Position start,
        Position goal,
        int maxSteps,
        int stepPenalty) {
}
