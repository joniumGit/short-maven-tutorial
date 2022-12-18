package dev.jonium.tutorial;

import dev.jonium.tutorial.api.Calculator;
import dev.jonium.tutorial.api.Operation;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Here are some tests using some common styles to create anonymous objects seen in current and legacy code.
 */
class TestClient {

    /**
     * This is the outdated approach to creating mocks and anonymous objects from interfaces that lots of legacy code has.
     * <p>
     * The only perk in this case is verbosity, but in a more complex example these anonymous classes could contain state
     * to count executions or parameters etc. The same effect can usually be replicated with helpers found in Mockito.
     * </p>
     */
    @Test
    void testParseDoubleSumWithAnonymousClass() {
        var calculator = new Calculator() {
            private int count = 1;

            @Override
            public Operation operationFor(String operand) {
                assertEquals("+", operand);
                assertEquals(0, --this.count);
                return new Operation() {
                    private int count = 1;

                    @Override
                    public double evaluate(double a, double b) {
                        assertEquals(1E5, a);
                        assertEquals(1, b);
                        assertEquals(0, --this.count);
                        return 0;
                    }
                };
            }
        };

        assertEquals(0, CalculatorClient.evaluateExpression("1E5 + 1", calculator));
    }

    /**
     * Same code with Mockito.
     */
    @Test
    void testParseDoubleSumWithMocks() {
        Operation operation = when(mock(Operation.class)
                .evaluate(anyDouble(), anyDouble()))
                .thenReturn(0D)
                .getMock();
        Calculator calculator = when(mock(Calculator.class)
                .operationFor(anyString()))
                .thenReturn(operation)
                .getMock();
        assertEquals(0D, CalculatorClient.evaluateExpression("1E5 + 1", calculator));
        verify(calculator, times(1)).operationFor("+");
        verify(operation, times(1)).evaluate(1E5, 1);
    }

    /**
     * Same code using lambda expressions.
     * <p>
     * This is a fairly clean way to test in some cases,
     * however it can be a bit less clear than the above two examples.
     * </p>
     */
    @Test
    void testParseDoubleSumWithLambda() {
        var calculatorCallCount = new AtomicInteger(1);
        var operatorCallCount = new AtomicInteger(1);
        assertEquals(0, CalculatorClient.evaluateExpression("1E5 + 1", op -> {
            assertEquals("+", op);
            assertEquals(0, calculatorCallCount.decrementAndGet());
            return (a, b) -> {
                assertEquals(1E5, a);
                assertEquals(1, b);
                assertEquals(0, operatorCallCount.decrementAndGet());
                return 0;
            };
        }));
    }

}
