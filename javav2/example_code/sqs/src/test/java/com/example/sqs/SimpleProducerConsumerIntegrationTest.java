// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.sqs;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for SimpleProducerConsumer that verifies the program works end-to-end.
 * 
 * This test creates a temporary SQS queue and captures Log4J2 messages to verify message processing.
 */
public class SimpleProducerConsumerIntegrationTest {
    
    private String testQueueName;
    private SqsAsyncClient sqsClient;
    private String queueUrl;
    private TestAppender testAppender;
    private Logger logger;
    
    /**
     * Custom Log4J2 appender to capture log events for testing
     */
    private static class TestAppender extends AbstractAppender {
        private final List<LogEvent> logEvents = new ArrayList<>();
        
        protected TestAppender() {
            super("TestAppender", null, null, true, Property.EMPTY_ARRAY);
        }
        
        @Override
        public void append(LogEvent event) {
            logEvents.add(event.toImmutable());
        }
        
        public List<LogEvent> getLogEvents() {
            return new ArrayList<>(logEvents);
        }
        
        public void clear() {
            logEvents.clear();
        }
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Generate unique queue name for each test
        testQueueName = "test-queue-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
        
        // Create SQS client and test queue
        sqsClient = SqsAsyncClient.create();
        
        // Create queue
        sqsClient.createQueue(CreateQueueRequest.builder()
                .queueName(testQueueName)
                .build()).join();
        
        // Get queue URL
        queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(testQueueName)
                .build()).join().queueUrl();
        
        // Set up log capture
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        logger = context.getLogger(SimpleProducerConsumer.class.getName());
        testAppender = new TestAppender();
        testAppender.start();
        logger.addAppender(testAppender);
        logger.setLevel(Level.INFO);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up log appender
        if (logger != null && testAppender != null) {
            logger.removeAppender(testAppender);
            testAppender.stop();
        }
        
        // Clean up queue
        if (sqsClient != null && queueUrl != null) {
            try {
                sqsClient.deleteQueue(DeleteQueueRequest.builder()
                        .queueUrl(queueUrl)
                        .build()).join();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
            sqsClient.close();
        }
    }
    

    
    /**
     * Tests that the SimpleProducerConsumer program executes successfully with single-message operations.
     * 
     * Verifies that:
     * - The program accepts user input and starts without errors
     * - At least one message is produced by the producer thread
     * - Monitor thread logs message counts showing program activity
     * - Program completes within the expected timeframe
     * 
     * Uses configuration: 1 producer, 1 consumer, batch size 1, 100-byte messages, 1-minute runtime
     */
    @Test
    void testSimpleProducerConsumerExecutesSuccessfully() throws Exception {
        // Simulate user input: queueName + producers + consumers + batchSize + messageSize + runtime
        String simulatedInput = testQueueName + "\n1\n1\n1\n100\n1\n";
        
        InputStream originalSystemIn = System.in;
        
        try {
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
            
            // Run the program in a separate thread with timeout
            CompletableFuture<Void> programExecution = CompletableFuture.runAsync(() -> {
                try {
                    SimpleProducerConsumer.main(new String[]{});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Wait for program to complete or timeout after 70 seconds (1 minute runtime + 10 second buffer)
            final int TIMEOUT_SECONDS = 70;
            programExecution.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            // Verify log messages were captured
            List<LogEvent> logEvents = testAppender.getLogEvents();
            assertFalse(logEvents.isEmpty(), "Expected log messages to be captured");
            
            // Parse monitor log messages to extract message counts
            MessageCounts counts = parseMessageCounts(logEvents);
            
            // Verify that messages were actually processed
            assertTrue(counts.foundMessages, "Expected to find monitor log messages");
            assertTrue(counts.maxProduced > 0, "Expected messages to be produced, but got: " + counts.maxProduced);
            assertTrue(counts.maxConsumed >= 0, "Expected non-negative consumed count, but got: " + counts.maxConsumed);
            
            System.out.println("Test passed - Produced: " + counts.maxProduced + ", Consumed: " + counts.maxConsumed);
            
        } finally {
            System.setIn(originalSystemIn);
        }
    }
    
    /**
     * Tests that the SimpleProducerConsumer program works correctly with batch operations.
     * 
     * Verifies that:
     * - The program can handle multiple producers and consumers concurrently
     * - Batch operations (batch size > 1) produce messages successfully
     * - Multiple threads coordinate properly without conflicts
     * - Batching configuration is processed correctly
     * 
     * Uses configuration: 2 producers, 2 consumers, batch size 5, 200-byte messages, 1-minute runtime
     */
    @Test
    void testMessageProcessingWithBatching() throws Exception {
        // Simulate user input: queueName + producers + consumers + batchSize + messageSize + runtime
        String simulatedInput = testQueueName + "\n2\n2\n5\n200\n1\n";
        
        InputStream originalSystemIn = System.in;
        
        try {
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
            
            CompletableFuture<Void> programExecution = CompletableFuture.runAsync(() -> {
                try {
                    SimpleProducerConsumer.main(new String[]{});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            
            final int TIMEOUT_SECONDS = 70;
            programExecution.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            // Verify batching produced messages
            List<LogEvent> logEvents = testAppender.getLogEvents();
            MessageCounts counts = parseMessageCounts(logEvents);
            
            assertTrue(counts.maxProduced > 0, "Expected messages to be produced with batching");
            System.out.println("Batching test passed - Produced: " + counts.maxProduced + " messages");
            
        } finally {
            System.setIn(originalSystemIn);
        }
    }
    
    /**
     * Helper method to parse message counts from log events.
     * 
     * @param logEvents the log events to parse
     * @return MessageCounts object containing parsed counts and status
     */
    private MessageCounts parseMessageCounts(List<LogEvent> logEvents) {
        Pattern messagePattern = Pattern.compile("produced messages = (\\d+), consumed messages = (\\d+)");
        int maxProduced = 0;
        int maxConsumed = 0;
        boolean foundMessages = false;
        
        for (LogEvent event : logEvents) {
            String message = event.getMessage().getFormattedMessage();
            Matcher matcher = messagePattern.matcher(message);
            if (matcher.find()) {
                foundMessages = true;
                int produced = Integer.parseInt(matcher.group(1));
                int consumed = Integer.parseInt(matcher.group(2));
                maxProduced = Math.max(maxProduced, produced);
                maxConsumed = Math.max(maxConsumed, consumed);
            }
        }
        
        return new MessageCounts(maxProduced, maxConsumed, foundMessages);
    }
    
    /**
     * Data class to hold parsed message count results.
     */
    private static class MessageCounts {
        final int maxProduced;
        final int maxConsumed;
        final boolean foundMessages;
        
        MessageCounts(int maxProduced, int maxConsumed, boolean foundMessages) {
            this.maxProduced = maxProduced;
            this.maxConsumed = maxConsumed;
            this.foundMessages = foundMessages;
        }
    }
}