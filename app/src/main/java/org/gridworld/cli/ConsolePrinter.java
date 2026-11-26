package org.gridworld.cli;

import org.gridworld.domain.Cell;
import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ConsolePrinter {

    // ---------------- Console Colors ----------------

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[90m";
    private static final String BOLD = "\u001B[1m";

    // ---------------- Console Control ----------------

    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    // ---------------- Display Configuration ----------------

    private static final int TABLE_WIDTH = 70;
    private static final int CELL_WIDTH = 15;

    private static final String NOT_AVAILABLE = "N/A";
    private static final String ARROW = " -> ";
    private static final String SEPARATOR = "-".repeat(TABLE_WIDTH);

    private static final String RESULT_FORMAT = "%s | %s%n";

    private static final String COMPARISON_FORMAT = "%-22s | %s | %s | %15s%n";

    private static final String METRIC_FORMAT = "%12s";

    // ---------------- Constructor ----------------

    private ConsolePrinter() {
    }

    // ---------------- Screen Management ----------------

    public static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    // ---------------- Menu Display ----------------

    public static void printMenu() {
        printHeader("GRIDWORLD - SEARCH DEMO");

        System.out.println("1. Reward Maximization");
        System.out.println("2. Alternative Optimal Paths");
        System.out.println("3. Obstacle Avoidance");
        System.out.println("4. Unreachable Goal");
        System.out.println("5. Open Grid");
        System.out.println("6. Narrow Passage");
        System.out.println("0. Exit");

        printSeparator();

        System.out.print("Select a scenario: ");
    }

    // ---------------- Header Display ----------------

    public static void printHeader(String title) {
        System.out.println();

        printSeparator();
        System.out.println(colorize(title, BOLD + CYAN));
        printSeparator();
    }

    // ---------------- Grid Display ----------------

    public static void printGrid(
            Cell[][] cells,
            Position start,
            Position goal,
            List<Position> path) {

        System.out.println();

        for (int row = 0; row < cells.length; row++) {
            for (int col = 0; col < cells[row].length; col++) {

                Position position = new Position(row, col);

                String symbol = formatCell(
                        cells[row][col], position, start, goal);

                String displayedCell = formatPathCell(
                        symbol, position, path);

                System.out.printf(
                        "%-" + CELL_WIDTH + "s", displayedCell);
            }

            System.out.println();
        }

        System.out.println();
    }

    public static void printLegend() {
        System.out.println("Legend:");

        System.out.println("S = Start");
        System.out.println("G = Goal");
        System.out.println("# = Wall");
        System.out.println("T = Trap");
        System.out.println(". = Empty cell");
        System.out.println("+n = Bonus reward");
        System.out.println("[n] = Path step number");
    }

    // ---------------- Result Display ----------------

    public static void printResult(
            String algorithm,
            SearchMetrics metrics) {

        SearchResult result = metrics.result();

        printHeader(algorithm);

        if (!result.pathFound()) {
            System.out.println(colorize("No path found.", RED));
            printPerformance(metrics);
            return;
        }

        System.out.println(
                colorize("Path found successfully!", GREEN));

        printSeparator();

        printRow("Path", formatPath(result.path()), CYAN);
        printRow("Actions", formatActions(result.actions()), RESET);
        printRow("Total reward", result.totalReward(), GREEN);
        printRow("Total cost", result.totalCost(), YELLOW);
        printRow("Steps", result.actions().size(), RESET);
        printRow("Final position", formatPosition(metrics.finalPosition()), GREEN);

        printPerformance(metrics);

        printSeparator();
    }

    // ---------------- Result Display Helpers ----------------

    private static void printPerformance(SearchMetrics metrics) {

        printRow(
                "Expanded nodes",
                metrics.result().expandedNodes(),
                CYAN);

        printRow(
                "Execution time",
                String.format("%.4f ms", metrics.executionTimeMillis()),
                YELLOW);
    }

    // ---------------- Comparison Display ----------------

    public static void printComparison(
            Map<String, SearchMetrics> results) {

        printHeader("SEARCH RESULTS COMPARISON");

        printComparisonHeader();
        printSeparator();

        results.forEach((name, metrics) -> printComparisonRow(name, metrics.result()));

        printSeparator();
    }

    // ---------------- Comparison Helpers ----------------

    private static void printComparisonHeader() {
        String header = String.format(
                COMPARISON_FORMAT,
                "Algorithm",
                alignMetric("Reward"),
                alignMetric("Cost"),
                "Steps");

        System.out.print(colorize(header, BOLD));
    }

    private static void printComparisonRow(
            String algorithm,
            SearchResult result) {

        if (!result.pathFound()) {
            printUnavailableRow(algorithm);
            return;
        }

        System.out.printf(
                COMPARISON_FORMAT,
                algorithm,
                formatMetric(result.totalReward(), GREEN),
                formatMetric(result.totalCost(), YELLOW),
                result.actions().size());
    }

    private static void printUnavailableRow(String algorithm) {
        String unavailable = formatMetric(NOT_AVAILABLE, GRAY);

        System.out.printf(
                COMPARISON_FORMAT,
                algorithm,
                unavailable,
                unavailable,
                NOT_AVAILABLE);
    }

    // ---------------- Grid Formatting Helpers ----------------

    private static String formatCell(
            Cell cell,
            Position position,
            Position start,
            Position goal) {

        if (position.equals(start)) {
            return "S";
        }

        if (position.equals(goal)) {
            return "G(" + cell.reward() + ")";
        }

        if (!cell.isWalkable()) {
            return "#";
        }

        if (cell.isTerminal()) {
            return "T(" + cell.reward() + ")";
        }

        if (cell.reward() != 0) {
            return String.format("%+d", cell.reward());
        }

        return ".";
    }

    private static String formatPathCell(
            String symbol,
            Position position,
            List<Position> path) {

        List<Integer> visits = new ArrayList<>();

        for (int step = 0; step < path.size(); step++) {
            if (position.equals(path.get(step))) {
                visits.add(step);
            }
        }

        return visits.isEmpty()
                ? symbol
                : symbol + visits;
    }

    // ---------------- Table Helpers ----------------

    private static void printRow(
            String label,
            Object value,
            String color) {

        System.out.printf(
                RESULT_FORMAT,
                colorize(String.format("%-16s", label), GRAY),
                colorize(value, color));
    }

    private static void printSeparator() {
        System.out.println(colorize(SEPARATOR, GRAY));
    }

    // ---------------- Result Formatting Helpers ----------------

    private static String formatPosition(Position position) {
        return String.format(
                "(%d,%d)", position.row(), position.col());
    }

    private static String formatPath(List<Position> path) {
        return path.stream()
                .map(ConsolePrinter::formatPosition)
                .collect(Collectors.joining(ARROW));
    }

    private static String formatActions(List<Direction> actions) {
        return actions.stream()
                .map(Direction::name)
                .collect(Collectors.joining(ARROW));
    }

    private static String alignMetric(Object value) {
        return String.format(METRIC_FORMAT, value);
    }

    private static String formatMetric(
            Object value,
            String color) {

        return colorize(alignMetric(value), color);
    }

    private static String colorize(
            Object value,
            String color) {

        return color + value + RESET;
    }
}