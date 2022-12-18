package dev.jonium.tutorial.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestCalculator {

    /**
     * This is an example of a simple parametrized test
     */

    @ParameterizedTest
    @ValueSource(strings = {
            "+",
            "-",
            "*",
            "^",
            "/",
    })
    void testHasOperand(String op) {
        assertNotNull(new CalculatorImpl().operationFor(op));
    }

}
