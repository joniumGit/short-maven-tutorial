package dev.jonium.tutorial.helpers;

import dev.jonium.tutorial.CalculatorClient;

import java.io.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class wraps the main method of the Calculator app to facilitate integration testing.
 */
public class CLITester implements AutoCloseable {
    private final InputStream oldSin;
    private final PrintStream oldSout;
    private final ExecutorService targetThread;
    private boolean shutdown = false;

    /**
     * Creates a new tester and saves references to {@link System#in} and {@link System#out}.
     */
    public CLITester() {
        oldSin = System.in;
        oldSout = System.out;
        targetThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Evaluates an expression with the Calculator and returns the lines of the full program output.
     *
     * @param input Expression to evaluate.
     * @return Lines of output produced by Calculator when starting up and evaluating the expression.
     * @implNote This will fail after 2 seconds if the Calculator does not return from the main method.
     */
    public Stream<String> test(String input) throws Throwable {
        try (var commandInput = new ByteArrayInputStream((input + "\nq\n").getBytes());
             var resultOutput = new ByteArrayOutputStream();
             var ps = new PrintStream(resultOutput)
        ) {
            System.setIn(commandInput);
            System.setOut(ps);
            var cl = new CountDownLatch(1);
            var future = targetThread.submit(() -> {
                CalculatorClient.main(new String[]{});
                cl.countDown();
            });
            if (!cl.await(2, TimeUnit.SECONDS)) {
                if (future.isDone()) {
                    try {
                        future.get();
                        fail("Failed for unknown reason");
                    } catch (ExecutionException e) {
                        throw e.getCause();
                    }
                } else {
                    throw new TimeoutException("Failed to complete evaluation before timeout (2s).");
                }
            }
            return resultOutput.toString().lines();
        } catch (IOException | InterruptedException e) {
            close();
            throw e;
        }
    }

    /**
     * Puts back the old System streams.
     */
    @Override
    public void close() {
        if (!shutdown) {
            targetThread.shutdownNow();
            System.setIn(oldSin);
            System.setOut(oldSout);
            shutdown = true;
        }
    }

}
