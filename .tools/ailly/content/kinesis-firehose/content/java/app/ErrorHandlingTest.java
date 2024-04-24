package com.example.firehoseingestion.retry;

import com.amazonaws.AmazonServiceException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlingTest {

    private final ErrorHandling errorHandling = new ErrorHandling();

    @Test
    void testRetryWithExponentialBackoff_Success() {
        Callable<String> operation = () -> "Success";
        String result = errorHandling.retryWithExponentialBackoff(operation);
        assertEquals("Success", result);
    }

    @Test
    void testRetryWithExponentialBackoff_FailedWithRetry() {
        int[] attempts = {0};
        Callable<String> operation = () -> {
            if (attempts[0] < 2) {
                attempts[0]++;
                throw new AmazonServiceException("Failed attempt " + attempts[0]);
            }
            return "Success";
        };

        String result = errorHandling.retryWithExponentialBackoff(operation);
        assertEquals("Success", result);
        assertEquals(2, attempts[0]);
    }

    @Test
    void testRetryWithExponentialBackoff_MaxRetriesExceeded() {
        Callable<String> operation = () -> {
            throw new AmazonServiceException("Failed");
        };

        assertThrows(AmazonServiceException.class, () -> errorHandling.retryWithExponentialBackoff(operation));
    }
}