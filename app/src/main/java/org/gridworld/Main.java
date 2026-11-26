package org.gridworld;

import org.gridworld.cli.ConsolePrinter;
import org.gridworld.cli.ScenarioRunner;
import org.gridworld.cli.SearchMetrics;

import org.gridworld.scenario.Scenario;
import org.gridworld.scenario.ScenarioFactory;

import org.gridworld.domain.GridMap;
import org.gridworld.environment.GridWorld;

import org.gridworld.search.AStarSearch;
import org.gridworld.search.BacktrackingSearch;
import org.gridworld.search.SearchAlgorithm;
import org.gridworld.search.SearchProblem;
import org.gridworld.search.SearchResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // ---------------- Public Methods ----------------

    public static void main(String[] args) {

        if (args.length > 0) {
            selectScenario(args[0]);
            return;
        }

        runMenu();
    }

    // ---------------- Menu Management ----------------

    private static void runMenu() {

        try (Scanner scanner = new Scanner(System.in)) {

            ConsolePrinter.clearScreen();

            while (true) {

                ConsolePrinter.printMenu();

                if (!scanner.hasNextLine()) {
                    return;
                }

                String choice = scanner.nextLine().trim();

                if (choice.equals("0")) {
                    System.out.println("Goodbye!");
                    return;
                }

                selectScenario(choice);

                if (!waitForEnter(scanner)) {
                    return;
                }

                ConsolePrinter.clearScreen();
            }
        }
    }

    private static void selectScenario(String choice) {

        ScenarioFactory.create(choice).ifPresentOrElse(
                scenario -> {
                    ConsolePrinter.clearScreen();
                    runScenario(scenario);
                },
                () -> System.out.println("Invalid choice."));
    }

    private static boolean waitForEnter(Scanner scanner) {

        System.out.println();
        System.out.print("Press Enter to return to the main menu...");

        if (!scanner.hasNextLine()) {
            return false;
        }

        scanner.nextLine();
        return true;
    }

    // ---------------- Scenario Execution ----------------

    private static void runScenario(Scenario scenario) {

        GridWorld world = createWorld(scenario);
        SearchProblem problem = createProblem(world, scenario);

        ScenarioRunner runner = new ScenarioRunner(world, problem);

        Map<String, SearchResult> results = new LinkedHashMap<>();

        printInitialScenario(scenario);

        for (SearchAlgorithm algorithm : createAlgorithms()) {

            String name = algorithm.getClass().getSimpleName();

            SearchMetrics metrics = runner.runAlgorithm(algorithm);
            SearchResult result = metrics.result();

            results.put(name, result);

            ConsolePrinter.printResult(
                    name, metrics, world.position());

            if (result.pathFound()) {

                ConsolePrinter.printGrid(
                        scenario.cells(),
                        scenario.start(),
                        scenario.goal(),
                        result.path());
            }
        }

        ConsolePrinter.printComparison(results);
    }

    // ---------------- Initialization Helpers ----------------

    private static GridWorld createWorld(Scenario scenario) {

        return new GridWorld(
                new GridMap(scenario.cells()),
                scenario.start(),
                scenario.stepPenalty());
    }

    private static SearchProblem createProblem(
            GridWorld world, Scenario scenario) {

        return new SearchProblem(
                world,
                scenario.start(),
                scenario.goal(),
                scenario.maxSteps());
    }

    private static List<SearchAlgorithm> createAlgorithms() {

        return List.of(
                new BacktrackingSearch(),
                new AStarSearch());
    }

    // ---------------- Scenario Display ----------------

    private static void printInitialScenario(Scenario scenario) {

        ConsolePrinter.printHeader(scenario.title());

        System.out.println("Max steps: " + scenario.maxSteps());
        System.out.println("Step penalty: " + scenario.stepPenalty());

        ConsolePrinter.printHeader("INITIAL GRID");

        ConsolePrinter.printGrid(
                scenario.cells(),
                scenario.start(),
                scenario.goal(),
                List.of());

        ConsolePrinter.printLegend();
    }
}