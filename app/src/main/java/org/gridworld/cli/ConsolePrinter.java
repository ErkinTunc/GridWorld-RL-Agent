package org.gridworld.cli;

import org.gridworld.domain.Position;
import org.gridworld.environment.Direction;
import org.gridworld.search.SearchResult;

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

    // ---------------- Display Configuration ----------------

    private static final int TABLE_WIDTH = 70;

    private static final String NOT_AVAILABLE = "N/A";
    private static final String ARROW = " -> ";
    private static final String SEPARATOR = "-".repeat(TABLE_WIDTH);

    private static final String RESULT_FORMAT = "%s | %s%n";

    private static final String COMPARISON_FORMAT = "%-22s | %s | %s | %15s%n";

    private static final String METRIC_FORMAT = "%12s";

    // ---------------- Constructor ----------------

    private ConsolePrinter() {
    }

    // ---------------- Public Methods ----------------

    public static void printHeader(String title) {

        System.out.println();

        printSeparator();
        System.out.println(colorize(title, BOLD + CYAN));
        printSeparator();
    }

    public static void printResult(
            String algorithm,
            SearchResult result,
            Position finalPosition) {

        printHeader(algorithm);

        if (!result.pathFound()) {
            System.out.println(colorize("No path found.", RED));
            return;
        }

        System.out.println(colorize("Path found successfully!", GREEN));

        printSeparator();

        printRow("Path", formatPath(result.path()), CYAN);
        printRow("Actions", formatActions(result.actions()), RESET);
        printRow("Total reward", result.totalReward(), GREEN);
        printRow("Total cost", result.totalCost(), YELLOW);
        printRow("Steps", result.actions().size(), RESET);
        printRow("Final position", formatPosition(finalPosition), GREEN);

        printSeparator();
    }

    public static void printComparison(Map<String, SearchResult> results) {

        printHeader("SEARCH RESULTS COMPARISON");

        printComparisonHeader();
        printSeparator();

        results.forEach(ConsolePrinter::printComparisonRow);

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
            String algorithm, SearchResult result) {

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

    // ---------------- Table Helpers ----------------

    private static void printRow(
            String label, Object value, String color) {

        System.out.printf(
                RESULT_FORMAT,
                colorize(String.format("%-16s", label), GRAY),
                colorize(value, color));
    }

    private static void printSeparator() {
        System.out.println(colorize(SEPARATOR, GRAY));
    }

    // ---------------- Formatting Helpers ----------------

    private static String formatPosition(Position position) {
        return String.format("(%d,%d)", position.row(), position.col());
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

    private static String formatMetric(Object value, String color) {
        return colorize(alignMetric(value), color);
    }

    private static String colorize(Object value, String color) {
        return color + value + RESET;
    }
}