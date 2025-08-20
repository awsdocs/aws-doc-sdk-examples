// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.dynamodb;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.AdaptiveRetryStrategy;
import software.amazon.awssdk.retries.StandardRetryStrategy;
import software.amazon.awssdk.retries.api.BackoffStrategy;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Duration;
import java.util.Map;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 * 
 * Migration Examples: From Standard Retry Policies to AdaptiveRetryStrategy
 * 
 * This class demonstrates before/after code comparisons for migrating from
 * standard retry policies to AdaptiveRetryStrategy using AWS SDK v2.
 * 
 * Each example shows:
 * 1. BEFORE: Standard retry configuration (what you might have currently)
 * 2. AFTER: Equivalent AdaptiveRetryStrategy implementation
 * 3. Key differences and improvements gained from migration
 * 
 * @snippet MigrationExamples.createClientWithStandardRetry_BEFORE
 * @snippet MigrationExamples.createClientWithAdaptiveRetry_AFTER
 * @snippet MigrationExamples.createClientWithCustomAdaptiveRetry_AFTER
 * @snippet MigrationExamples.createHighThroughputClient_AFTER
 * @snippet MigrationExamples.createBatchOperationClient_AFTER
 */
public class MigrationExamples {

    public static void main(String[] args) {
        System.out.println("=== Migration Examples: Standard Retry to AdaptiveRetryStrategy ===\n");
        
        // Demonstrate each migration scenario
        demonstrateBasicMigration();
        demonstrateCustomBackoffMigration();
        demonstrateHighThroughputMigration();
        demonstrateBatchOperationMigration();
        
        System.out.println("All migration examples completed successfully!");
    }

    // ========================================================================
    // MIGRATION EXAMPLE 1: Basic Standard Retry to AdaptiveRetryStrategy
    // ========================================================================
    
    // snippet-start:[dynamodb.java2.migration.standard_retry_before]
    /**
     * BEFORE: Basic standard retry configuration
     * This is what many applications currently use for retry logic.
     * 
     * @return DynamoDbClient with standard retry strategy
     */
    public static DynamoDbClient createClientWithStandardRetry_BEFORE() {
        // Standard retry strategy with basic configuration
        // This provides fixed retry behavior without adaptation
        StandardRetryStrategy standardRetryStrategy = StandardRetryStrategy.builder()
                // Maximum attempts = initial attempt + retries
                .maxAttempts(3)  // 1 initial + 2 retries
                
                // Fixed exponential backoff for all errors
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(100),  // base delay: 100ms
                    Duration.ofSeconds(20)   // max delay: 20s
                ))
                
                // Same backoff for throttling errors (not optimized)
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(100),  // same base delay
                    Duration.ofSeconds(20)   // same max delay
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(standardRetryStrategy)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.migration.standard_retry_before]

    // snippet-start:[dynamodb.java2.migration.adaptive_retry_after]
    /**
     * AFTER: Equivalent AdaptiveRetryStrategy configuration
     * This provides the same retry behavior but with adaptive improvements.
     * 
     * @return DynamoDbClient with adaptive retry strategy
     */
    public static DynamoDbClient createClientWithAdaptiveRetry_AFTER() {
        // AdaptiveRetryStrategy with equivalent configuration
        // Adds client-side rate limiting and intelligent backoff
        AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
                // Same maximum attempts as before
                .maxAttempts(3)  // 1 initial + 2 retries
                
                // Enhanced exponential backoff that adapts to error patterns
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(100),  // same base delay: 100ms
                    Duration.ofSeconds(20)   // same max delay: 20s
                ))
                
                // Optimized backoff specifically for throttling scenarios
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(1),   // longer base delay for throttling
                    Duration.ofSeconds(20)   // same max delay
                ))
                
                // Note: Circuit breaking is built-in (no configuration needed)
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(adaptiveRetryStrategy)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.migration.adaptive_retry_after]

    /**
     * KEY DIFFERENCES AND IMPROVEMENTS:
     * 
     * 1. CLIENT-SIDE RATE LIMITING: AdaptiveRetryStrategy includes a rate limiter
     *    that measures throttled vs non-throttled requests, helping prevent retry storms.
     * 
     * 2. INTELLIGENT THROTTLING HANDLING: Uses longer delays (1s vs 100ms base) for
     *    throttling errors, giving the service more time to recover.
     * 
     * 3. ADAPTIVE LEARNING: The strategy learns from retry patterns and adjusts
     *    behavior dynamically based on observed success/failure rates.
     * 
     * 4. BUILT-IN CIRCUIT BREAKING: Automatically stops retries when failure rate
     *    is too high, preventing cascading failures.
     * 
     * 5. SAME EXTERNAL BEHAVIOR: Your application code doesn't change - only the
     *    retry strategy configuration is updated.
     */

    // ========================================================================
    // MIGRATION EXAMPLE 2: Custom Backoff Configuration
    // ========================================================================
    
    /**
     * BEFORE: Custom standard retry with specific timing requirements
     */
    public static DynamoDbClient createClientWithCustomStandardRetry_BEFORE() {
        StandardRetryStrategy customStandardRetry = StandardRetryStrategy.builder()
                .maxAttempts(5)  // More aggressive retry count
                
                // Custom backoff timing for faster recovery
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(200),  // higher base delay
                    Duration.ofSeconds(30)   // longer max delay
                ))
                
                // Same timing for throttling (not optimized)
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(200),  // same as regular backoff
                    Duration.ofSeconds(30)   // same max delay
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(customStandardRetry)
                    .build())
                .build();
    }

    // snippet-start:[dynamodb.java2.migration.custom_adaptive_retry]
    /**
     * AFTER: Custom AdaptiveRetryStrategy with optimized throttling handling
     * 
     * @return DynamoDbClient with custom adaptive retry configuration
     */
    public static DynamoDbClient createClientWithCustomAdaptiveRetry_AFTER() {
        AdaptiveRetryStrategy customAdaptiveRetry = AdaptiveRetryStrategy.builder()
                .maxAttempts(5)  // Same retry count
                
                // Same custom backoff for regular errors
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(200),  // same base delay
                    Duration.ofSeconds(30)   // same max delay
                ))
                
                // IMPROVED: Optimized throttling backoff
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(500),  // longer base delay for throttling
                    Duration.ofMinutes(1)    // extended max delay for throttling
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(customAdaptiveRetry)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.migration.custom_adaptive_retry]

    // ========================================================================
    // MIGRATION EXAMPLE 3: High-Throughput Application
    // ========================================================================
    
    /**
     * BEFORE: Minimal retry configuration for high-throughput scenarios
     */
    public static DynamoDbClient createHighThroughputClient_BEFORE() {
        StandardRetryStrategy minimalRetry = StandardRetryStrategy.builder()
                .maxAttempts(2)  // Minimal retries for speed
                
                // Fast backoff to minimize latency
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(50),   // very fast base delay
                    Duration.ofSeconds(5)    // short max delay
                ))
                
                // Same fast timing for throttling
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(50),   // same fast timing
                    Duration.ofSeconds(5)    // same short max
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(minimalRetry)
                    .build())
                .build();
    }

    // snippet-start:[dynamodb.java2.migration.high_throughput_adaptive]
    /**
     * AFTER: High-throughput AdaptiveRetryStrategy with smart throttling
     * 
     * @return DynamoDbClient optimized for high-throughput scenarios with adaptive retry
     */
    public static DynamoDbClient createHighThroughputClient_AFTER() {
        AdaptiveRetryStrategy adaptiveHighThroughput = AdaptiveRetryStrategy.builder()
                .maxAttempts(2)  // Same minimal retries
                
                // Same fast backoff for regular errors
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(50),   // same fast base delay
                    Duration.ofSeconds(5)    // same short max delay
                ))
                
                // IMPROVED: Smarter throttling handling even in high-throughput scenarios
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(200),  // slightly longer for throttling
                    Duration.ofSeconds(10)   // longer max for throttling recovery
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(adaptiveHighThroughput)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.migration.high_throughput_adaptive]

    // ========================================================================
    // MIGRATION EXAMPLE 4: Batch Operations
    // ========================================================================
    
    /**
     * BEFORE: Standard retry optimized for batch operations
     */
    public static DynamoDbClient createBatchOperationClient_BEFORE() {
        StandardRetryStrategy batchRetry = StandardRetryStrategy.builder()
                .maxAttempts(6)  // More retries for batch operations
                
                // Longer delays suitable for batch processing
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(1),   // 1 second base delay
                    Duration.ofMinutes(1)    // 1 minute max delay
                ))
                
                // Same timing for throttling
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(1),   // same base delay
                    Duration.ofMinutes(1)    // same max delay
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(batchRetry)
                    .build())
                .build();
    }

    // snippet-start:[dynamodb.java2.migration.batch_operations_adaptive]
    /**
     * AFTER: AdaptiveRetryStrategy optimized for batch operations with enhanced throttling
     * 
     * @return DynamoDbClient optimized for batch operations with adaptive retry
     */
    public static DynamoDbClient createBatchOperationClient_AFTER() {
        AdaptiveRetryStrategy adaptiveBatchRetry = AdaptiveRetryStrategy.builder()
                .maxAttempts(6)  // Same retry count for batch operations
                
                // Same backoff for regular errors
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(1),   // same 1 second base delay
                    Duration.ofMinutes(1)    // same 1 minute max delay
                ))
                
                // IMPROVED: Enhanced throttling handling for batch scenarios
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(2),   // longer base delay for batch throttling
                    Duration.ofMinutes(2)    // extended max delay for recovery
                ))
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(adaptiveBatchRetry)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.migration.batch_operations_adaptive]

    // ========================================================================
    // DEMONSTRATION METHODS
    // ========================================================================
    
    private static void demonstrateBasicMigration() {
        System.out.println("1. BASIC MIGRATION EXAMPLE");
        System.out.println("   BEFORE: Standard retry with fixed behavior");
        
        DynamoDbClient beforeClient = createClientWithStandardRetry_BEFORE();
        System.out.println("   ✅ Standard retry client created successfully");
        beforeClient.close();
        
        System.out.println("   AFTER: Adaptive retry with intelligent behavior");
        DynamoDbClient afterClient = createClientWithAdaptiveRetry_AFTER();
        System.out.println("   ✅ Adaptive retry client created successfully");
        System.out.println("   🎯 IMPROVEMENT: Added client-side rate limiting and optimized throttling handling");
        afterClient.close();
        System.out.println();
    }

    private static void demonstrateCustomBackoffMigration() {
        System.out.println("2. CUSTOM BACKOFF MIGRATION EXAMPLE");
        System.out.println("   BEFORE: Custom standard retry with specific timing");
        
        DynamoDbClient beforeClient = createClientWithCustomStandardRetry_BEFORE();
        System.out.println("   ✅ Custom standard retry client created successfully");
        beforeClient.close();
        
        System.out.println("   AFTER: Custom adaptive retry with optimized throttling");
        DynamoDbClient afterClient = createClientWithCustomAdaptiveRetry_AFTER();
        System.out.println("   ✅ Custom adaptive retry client created successfully");
        System.out.println("   🎯 IMPROVEMENT: Separate throttling backoff strategy (500ms vs 200ms base delay)");
        afterClient.close();
        System.out.println();
    }

    private static void demonstrateHighThroughputMigration() {
        System.out.println("3. HIGH-THROUGHPUT MIGRATION EXAMPLE");
        System.out.println("   BEFORE: Minimal retry for maximum speed");
        
        DynamoDbClient beforeClient = createHighThroughputClient_BEFORE();
        System.out.println("   ✅ High-throughput standard retry client created successfully");
        beforeClient.close();
        
        System.out.println("   AFTER: High-throughput adaptive retry with smart throttling");
        DynamoDbClient afterClient = createHighThroughputClient_AFTER();
        System.out.println("   ✅ High-throughput adaptive retry client created successfully");
        System.out.println("   🎯 IMPROVEMENT: Maintains speed while adding intelligent throttling protection");
        afterClient.close();
        System.out.println();
    }

    private static void demonstrateBatchOperationMigration() {
        System.out.println("4. BATCH OPERATION MIGRATION EXAMPLE");
        System.out.println("   BEFORE: Standard retry optimized for batch processing");
        
        DynamoDbClient beforeClient = createBatchOperationClient_BEFORE();
        System.out.println("   ✅ Batch operation standard retry client created successfully");
        beforeClient.close();
        
        System.out.println("   AFTER: Adaptive retry with enhanced batch throttling handling");
        DynamoDbClient afterClient = createBatchOperationClient_AFTER();
        System.out.println("   ✅ Batch operation adaptive retry client created successfully");
        System.out.println("   🎯 IMPROVEMENT: Extended throttling delays (2s vs 1s base) for better batch recovery");
        afterClient.close();
        System.out.println();
    }

    // ========================================================================
    // UTILITY METHODS FOR TESTING RETRY BEHAVIOR
    // ========================================================================
    
    /**
     * Utility method to demonstrate retry behavior differences.
     * This method intentionally triggers retries to show the behavioral differences.
     */
    public static void compareRetryBehavior() {
        System.out.println("=== RETRY BEHAVIOR COMPARISON ===");
        
        // Create both client types
        DynamoDbClient standardClient = createClientWithStandardRetry_BEFORE();
        DynamoDbClient adaptiveClient = createClientWithAdaptiveRetry_AFTER();
        
        try {
            // Attempt an operation that will likely fail (non-existent table)
            // This demonstrates how each retry strategy handles failures
            
            System.out.println("Testing standard retry behavior...");
            testRetryBehavior(standardClient, "Standard");
            
            System.out.println("Testing adaptive retry behavior...");
            testRetryBehavior(adaptiveClient, "Adaptive");
            
        } finally {
            standardClient.close();
            adaptiveClient.close();
        }
    }
    
    private static void testRetryBehavior(DynamoDbClient client, String strategyType) {
        try {
            // This will likely fail and trigger retries
            GetItemRequest request = GetItemRequest.builder()
                    .tableName("Music")
                    .key(Map.of("Artist", AttributeValue.builder().s("test-artist").build()))
                    .build();
            
            long startTime = System.currentTimeMillis();
            client.getItem(request);
            long endTime = System.currentTimeMillis();
            
            System.out.println(strategyType + " strategy completed in " + (endTime - startTime) + "ms");
            
        } catch (Exception e) {
            System.out.println(strategyType + " strategy failed as expected: " + e.getClass().getSimpleName());
            // This is expected - we're testing retry behavior, not successful operations
        }
    }

    // ========================================================================
    // MIGRATION SUMMARY AND KEY TAKEAWAYS
    // ========================================================================
    
    /**
     * MIGRATION SUMMARY:
     * 
     * 1. CONFIGURATION COMPATIBILITY: AdaptiveRetryStrategy uses the same builder
     *    pattern and parameters as StandardRetryStrategy, making migration straightforward.
     * 
     * 2. BEHAVIORAL IMPROVEMENTS: 
     *    - Client-side rate limiting prevents retry storms
     *    - Intelligent throttling handling with separate backoff strategies
     *    - Built-in circuit breaking for failure protection
     *    - Adaptive learning from retry patterns
     * 
     * 3. PERFORMANCE BENEFITS:
     *    - Reduced unnecessary retries through pattern recognition
     *    - Faster recovery from transient errors
     *    - Better handling of sustained throttling conditions
     *    - Improved overall application responsiveness
     * 
     * 4. MIGRATION EFFORT: Minimal code changes required
     *    - Replace StandardRetryStrategy with AdaptiveRetryStrategy
     *    - Optionally optimize throttlingBackoffStrategy for better performance
     *    - No changes to application logic or error handling
     * 
     * 5. BACKWARD COMPATIBILITY: Existing retry behavior is preserved
     *    - Same maxAttempts and backoffStrategy behavior
     *    - Enhanced with adaptive improvements
     *    - No breaking changes to application flow
     */
}
