package dev.jonium.tutorial.impl;

import dev.jonium.tutorial.api.Calculator;
import dev.jonium.tutorial.api.Operation;

import java.util.HashMap;
import java.util.Map;

public class CalculatorImpl implements Calculator {

    private final Map<String, Operation> operations = new HashMap<>();

    public CalculatorImpl() {
        operations.putAll(Map.of(
                "+", this::sum,
                "-", (a, b) -> this.sum(-1 * a, b),
                "*", this::mul,
                "/", this::div,
                "^", this::pow
        ));
    }

    private double sum(double a, double b) {
        return a + b;
    }

    private double mul(double a, double b) {
        return a * b;
    }

    private double div(double a, double b) {
        return a / b;
    }

    private double pow(double a, double b) {
        return Math.pow(a, b);
    }

    @Override
    public Operation operationFor(String operand) {
        return operations.get(operand);
    }

}
