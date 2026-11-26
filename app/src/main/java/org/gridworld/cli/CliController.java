
package org.gridworld.cli;

import org.gridworld.scenario.Scenario;
import org.gridworld.scenario.ScenarioFactory;
import org.gridworld.search.SearchResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public final class CliController {

    private final ScenarioRunner runner;

    // ---------------- Constructor ----------------

    public CliController(ScenarioRunner runner) {
        this.runner = Objects.requireNonNull(
                runner, "Runner cannot be null");
    }

    // ---------------- Public Methods ----------------

    public void run(String[] args) {

        if (args.length > 0) {
            selectScenario(args[0]);
            return;
        }

        runMenu();
    }

    // ---------------- Menu Management ----------------

    private void runMenu() {

        try (Scanner scanner = new Scanner(System.in)) {

            ConsolePrinter.clearScreen();

            while (true) {

                ConsolePrinter.printMenu(ScenarioFactory.createAll());

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

    private void selectScenario(String choice) {

        ScenarioFactory.create(choice).ifPresentOrElse(
                scenario -> {
                    ConsolePrinter.clearScreen();
                    runScenario(scenario);
                },
                () -> System.out.println("Invalid choice."));
    }

    private boolean waitForEnter(Scanner scanner) {

        System.out.println();
        System.out.print(
                "Press Enter to return to the main menu...");

        if (!scanner.hasNextLine()) {
            return false;
        }

        scanner.nextLine();
        return true;
    }

    // ---------------- Scenario Execution ----------------

    private void runScenario(Scenario scenario) {

        printInitialScenario(scenario);

        Map<String, SearchMetrics> results = runner.runScenario(scenario);

        for (var entry : results.entrySet()) {

            String name = entry.getKey();
            SearchMetrics metrics = entry.getValue();
            SearchResult result = metrics.result();

            ConsolePrinter.printResult(name, metrics);

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

    // ---------------- Scenario Display ----------------

    private void printInitialScenario(Scenario scenario) {

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
