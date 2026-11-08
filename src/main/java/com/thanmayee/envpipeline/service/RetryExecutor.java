package com.thanmayee.envpipeline.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class RetryExecutor {

    private static final Logger logger =
            LoggerFactory.getLogger(RetryExecutor.class);

    private static final int MAX_RETRIES = 3;

    private static final long[] RETRY_DELAYS = {
            1000, 2000, 4000
    };

    public <T> T execute(
            Callable<T> operation,
            String source,
            String area) throws Exception {

        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {

            try {
                return operation.call();

            } catch (Exception e) {

                if (attempt == MAX_RETRIES) {
                    logger.error(
                            "All retries exhausted: source={}, area={}, attempts={}, reason={}",
                            source,
                            area,
                            attempt + 1,
                            e.getMessage()
                    );

                    throw e;
                }

                long delay = RETRY_DELAYS[attempt];

                logger.warn(
                        "Retrying request: source={}, area={}, retry={}/{}, waiting={} ms, reason={}",
                        source,
                        area,
                        attempt + 1,
                        MAX_RETRIES,
                        delay,
                        e.getMessage()
                );

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw interruptedException;
                }
            }
        }

        throw new IllegalStateException("Unexpected retry execution state");
    }
}
