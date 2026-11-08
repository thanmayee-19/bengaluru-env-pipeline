package com.thanmayee.envpipeline;

import com.thanmayee.envpipeline.service.RetryExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryExecutorTest {

    private RetryExecutor retryExecutor;

    @BeforeEach
    void setUp() {
        retryExecutor = new RetryExecutor();
    }

    @Test
    void succeedsOnFirstAttempt() throws Exception {

        AtomicInteger attempts = new AtomicInteger(0);

        String result = retryExecutor.execute(
                () -> {
                    attempts.incrementAndGet();
                    return "SUCCESS";
                },
                "weather",
                "KORAMANGALA"
        );

        assertEquals("SUCCESS", result);
        assertEquals(1, attempts.get());
    }

    @Test
    void succeedsAfterRetry() throws Exception {

        AtomicInteger attempts = new AtomicInteger(0);

        String result = retryExecutor.execute(
                () -> {
                    int count = attempts.incrementAndGet();

                    if (count == 1) {
                        throw new RuntimeException("Temporary API failure");
                    }

                    return "SUCCESS";
                },
                "weather",
                "INDIRANAGAR"
        );

        assertEquals("SUCCESS", result);
        assertEquals(2, attempts.get());
    }

    @Test
    void failsAfterAllRetries() {

        AtomicInteger attempts = new AtomicInteger(0);

        assertThrows(
                IllegalStateException.class,
                () -> retryExecutor.execute(
                        () -> {
                            attempts.incrementAndGet();
                            throw new IllegalStateException("API unavailable");
                        },
                        "air-quality",
                        "WHITEFIELD"
                )
        );

        assertEquals(4, attempts.get());
    }
}
