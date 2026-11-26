package org.gridworld;

import org.gridworld.cli.CliController;
import org.gridworld.cli.ScenarioRunner;
import org.gridworld.search.AStarSearch;
import org.gridworld.search.BacktrackingSearch;
import org.gridworld.search.SearchAlgorithm;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    // ---------------- Public Methods ----------------

    public static void main(String[] args) {

        ScenarioRunner runner = new ScenarioRunner(createAlgorithms());

        CliController controller = new CliController(runner);

        controller.run(args);
    }

    // ---------------- Initialization Helpers ----------------

    private static Map<String, SearchAlgorithm> createAlgorithms() {

        Map<String, SearchAlgorithm> algorithms = new LinkedHashMap<>();

        algorithms.put("BacktrackingSearch", new BacktrackingSearch());
        algorithms.put("AStarSearch", new AStarSearch());

        return algorithms;
    }
}