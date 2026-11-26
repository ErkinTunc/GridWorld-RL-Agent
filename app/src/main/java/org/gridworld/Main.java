package org.gridworld;

import org.gridworld.cli.ConsolePrinter;
import org.gridworld.cli.ScenarioRunner;
import org.gridworld.domain.Cell;
import org.gridworld.domain.CellType;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;
import org.gridworld.environment.GridWorld;
import org.gridworld.search.AStarSearch;
import org.gridworld.search.BacktrackingSearch;
import org.gridworld.search.SearchAlgorithm;
import org.gridworld.search.SearchProblem;
import org.gridworld.search.SearchResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    // ---------------- Configuration ----------------

    private static final int MAX_STEPS = 4;
    private static final int STEP_PENALTY = -1;

    // ---------------- Public Methods ----------------

    public static void main(String[] args) {

        Position start = new Position(0, 0);
        Position goal = new Position(0, 2);

        GridWorld world = createWorld(start);

        SearchProblem problem = new SearchProblem(
                world, start, goal, MAX_STEPS);

        ScenarioRunner runner = new ScenarioRunner(world, problem);

        List<SearchAlgorithm> algorithms = List.of(
                new BacktrackingSearch(),
                new AStarSearch());

        Map<String, SearchResult> results = new LinkedHashMap<>();

        ConsolePrinter.printHeader("GRIDWORLD - SEARCH RESULTS");

        for (SearchAlgorithm algorithm : algorithms) {

            String name = algorithm.getClass().getSimpleName();
            SearchResult result = runner.runAlgorithm(algorithm);

            results.put(name, result);

            ConsolePrinter.printResult(name, result, world.position());
        }

        ConsolePrinter.printComparison(results);
    }

    // ---------------- World Initialization ----------------

    private static GridWorld createWorld(Position start) {

        Cell empty = new Cell(CellType.EMPTY, 0);
        Cell wall = new Cell(CellType.WALL, 0);
        Cell bonus = new Cell(CellType.EMPTY, 3);
        Cell goal = new Cell(CellType.TERMINAL, 10);
        Cell trap = new Cell(CellType.TERMINAL, -10);

        Cell[][] cells = {
                { empty, empty, goal },
                { wall, bonus, trap }
        };

        return new GridWorld(
                new GridMap(cells), start, STEP_PENALTY);
    }
}