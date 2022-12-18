package dev.jonium.tutorial;

import dev.jonium.tutorial.api.Calculator;
import dev.jonium.tutorial.impl.CalculatorImpl;

import java.io.PrintStream;
import java.util.Scanner;

public class CalculatorClient {
    public static double evaluateExpression(String input, Calculator calculator) {
        var parts = input.trim().split(" ", 3);
        var operator = parts[1];
        return calculator.operationFor(operator).evaluate(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[2])
        );
    }

    public static void main(String[] args) {
        try (var input = new Scanner(System.in); var output = new PrintStream(System.out)) {
            output.println("Hello! This is a simple calculator.");
            output.println("Input q to quit or an operation to calculate the result.");
            var running = true;
            var calc = new CalculatorImpl();
            while (running && !Thread.interrupted()) {
                output.print("Input an operation: ");
                var inputString = input.nextLine();
                if ("q".equals(inputString)) {
                    running = false;
                } else {
                    output.println(inputString);
                    output.printf("= %f%n", evaluateExpression(inputString, calc));
                }
            }
        }
    }

}