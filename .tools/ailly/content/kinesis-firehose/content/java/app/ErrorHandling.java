package com.example.firehoseingestion.retry;

import com.amazonaws.AmazonServiceException;

import java.util.concurrent.Callable;

public class ErrorHandling {

    private static final int MAX_RETRIES = 5;
    private static final int BASE_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 30000;

    public <T> T retryWithExponentialBackoff(Callable<T> operation) {
        return retryWithExponentialBackoff(operation, 0);
    }

    private <T> T retryWithExponentialBackoff(Callable<T> operation, int retryCount) {
        try {
            return operation.call();
        } catch (AmazonServiceException e) {
            if (retryCount < MAX_RETRIES) {
                long delay = calculateDelay(retryCount);
                System.out.println("Operation failed, retrying in " + delay + " ms...");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                return retryWithExponentialBackoff(operation, retryCount + 1);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long calculateDelay(int retryCount) {
        long delay = BASE_DELAY_MS * (long) Math.pow(2, retryCount);
        long jitter = (long) (Math.random() * 0.3 * delay);
        return Math.min(delay + jitter, MAX_DELAY_MS);
    }
}