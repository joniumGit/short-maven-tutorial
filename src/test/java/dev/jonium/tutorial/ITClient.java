package dev.jonium.tutorial;

import dev.jonium.tutorial.helpers.CLITester;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sample integration test for the produced jar where we test that the main method works
 * <p>
 * The <i>Execution</i> mode is set to single-threaded for this test class.
 * </p>
 */
@Execution(ExecutionMode.SAME_THREAD)
class ITClient {

    /**
     * Holds the testing helper class instance.
     */
    private static CLITester tester;

    /**
     * This runs a one-off setup before any tests from this class are run.
     */
    @BeforeAll
    static void setup() {
        tester = new CLITester();
    }

    /**
     * This runs after all tests from this class have run.
     */
    @AfterAll
    static void teardown() {
        tester.close();
    }

    /**
     * Wraps the tester test method so that assertDoesNotThrow is not needed in every testcase.
     */
    static Stream<String> test(String input) {
        return assertDoesNotThrow(() -> tester.test(input));
    }

    /**
     * Integration testcase which uses the test helper to evaluate a simple expressions.
     * <p>
     * This method gets its parameters from the CSV strings in the annotation.
     * This is especially convenient as you can choose the separator used to write expressions here.
     * </p>
     */
    @ParameterizedTest
    @CsvSource(value = {
            "1 + 2 = 3",
            "2 * 4 = 8",
            "2 ^ 4 = 16",
            "1 / 4 = 0,25",
    }, delimiter = '=')
    void testClient(String input, String output) {
        assertTrue(test(input).anyMatch(s -> s.matches("^= " + output + "(?:(?=,),)?0*$")));
    }

    /**
     * This test tests the output completes without failing on empty input.
     */
    @Test
    void testEmpty() {
        test("");
    }

}
