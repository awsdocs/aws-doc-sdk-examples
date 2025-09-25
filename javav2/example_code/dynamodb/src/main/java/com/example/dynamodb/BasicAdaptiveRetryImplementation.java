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
 * Basic AdaptiveRetryStrategy Implementation for AWS SDK v2
 * 
 * This example demonstrates how to configure a DynamoDbClient with
 * AdaptiveRetryStrategy using AWS SDK v2. The adaptive retry strategy 
 * automatically adjusts retry behavior based on observed success/failure 
 * patterns, providing better performance than fixed retry policies.
 * 
 * @snippet BasicAdaptiveRetryImplementation.createDynamoDbClientWithAdaptiveRetry
 * @snippet BasicAdaptiveRetryImplementation.createDynamoDbClientWithCustomAdaptiveRetry
 * @snippet BasicAdaptiveRetryImplementation.createDynamoDbClientWithRetryMode
 */
public class BasicAdaptiveRetryImplementation {

    public static void main(String[] args) {
        // Create a DynamoDbClient with basic AdaptiveRetryStrategy configuration
        DynamoDbClient dynamoDbClient = createDynamoDbClientWithAdaptiveRetry();

        // Example usage: perform a simple DynamoDB operation
        try {
            performSampleDynamoDbOperation(dynamoDbClient);
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        } finally {
            dynamoDbClient.close();
        }
    }

    // snippet-start:[dynamodb.java2.basic_adaptive_retry.create_client]
    /**
     * Creates a DynamoDbClient configured with AdaptiveRetryStrategy.
     * 
     * AWS SDK v2 uses the new RetryStrategy API (not RetryPolicy) for configuring
     * retry behavior. AdaptiveRetryStrategy is one of the built-in strategies that
     * automatically adjusts retry timing based on observed throttling patterns.
     * 
     * @return DynamoDbClient configured with adaptive retry strategy
     */
    public static DynamoDbClient createDynamoDbClientWithAdaptiveRetry() {
        // Create an AdaptiveRetryStrategy with default settings
        // The adaptive strategy builds on StandardRetryStrategy and adds
        // client-side rate limiting to minimize throttling errors
        AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
                // Set maximum number of attempts (default is 3 for standard strategy)
                // This includes the initial attempt, so 3 means 2 retries
                .maxAttempts(3)
                
                // Configure backoff strategy for non-throttling exceptions
                // Uses exponential backoff with base delay of 100ms, max 20s
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(100),  // base delay
                    Duration.ofSeconds(20)   // max delay
                ))
                
                // Configure backoff strategy specifically for throttling exceptions
                // Uses longer delays for throttling to give service time to recover
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofSeconds(1),   // base delay for throttling
                    Duration.ofSeconds(20)   // max delay for throttling
                ))
                
                // Note: AdaptiveRetryStrategy includes built-in circuit breaking behavior
                // No explicit configuration needed - it's part of the adaptive algorithm
                
                .build();

        // Build the DynamoDbClient with the configured adaptive retry strategy
        return DynamoDbClient.builder()
                // Set the AWS region - replace with your preferred region
                .region(Region.US_EAST_1)
                
                // Use default credentials provider chain
                // This will look for credentials in environment variables,
                // system properties, credential files, IAM roles, etc.
                .credentialsProvider(DefaultCredentialsProvider.create())
                
                // Apply our adaptive retry strategy using ClientOverrideConfiguration
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(adaptiveRetryStrategy)
                    .build())
                
                .build();
    }
    // snippet-end:[dynamodb.java2.basic_adaptive_retry.create_client]

    /**
     * Performs a sample DynamoDB operation to demonstrate retry behavior.
     * This method attempts to get an item from a table, which may trigger
     * retries if the operation fails due to throttling or server errors.
     */
    private static void performSampleDynamoDbOperation(DynamoDbClient client) {
        try {
            // Example: Get an item from a DynamoDB table
            // Using "Music" table which is commonly used in DynamoDB examples
            GetItemRequest request = GetItemRequest.builder()
                    .tableName("Music")
                    .key(Map.of("Artist", AttributeValue.builder().s("sample-artist").build()))
                    .build();

            System.out.println("Performing DynamoDB GetItem operation...");
            GetItemResponse response = client.getItem(request);

            if (response.hasItem()) {
                System.out.println("Item retrieved successfully: " + response.item());
            } else {
                System.out.println("Item not found");
            }

        } catch (DynamoDbException e) {
            // The adaptive retry strategy will automatically retry on retryable errors
            // before this exception is thrown
            System.err.println("DynamoDB operation failed after retries: " + e.getMessage());
            System.err.println("Error code: " + e.awsErrorDetails().errorCode());
            throw e;
        }
    }

    // snippet-start:[dynamodb.java2.basic_adaptive_retry.create_custom_client]
    /**
     * Alternative configuration method showing more explicit adaptive retry setup.
     * This demonstrates how to create a more customized adaptive retry configuration
     * while still using SDK v2's built-in adaptive capabilities.
     * 
     * @return DynamoDbClient with custom adaptive retry configuration
     */
    public static DynamoDbClient createDynamoDbClientWithCustomAdaptiveRetry() {
        // Create a custom AdaptiveRetryStrategy with specific settings
        AdaptiveRetryStrategy customAdaptiveStrategy = AdaptiveRetryStrategy.builder()
                // Increase max attempts for operations that might face throttling
                .maxAttempts(5)
                
                // Use exponential backoff with custom base delay
                // Starting with 200ms base delay instead of default 100ms
                .backoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(200),  // custom base delay
                    Duration.ofSeconds(30)   // increased max delay
                ))
                
                // Configure throttling backoff with longer delays
                // This helps when dealing with DynamoDB throttling scenarios
                .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
                    Duration.ofMillis(500),  // longer base delay for throttling
                    Duration.ofMinutes(1)    // extended max delay for throttling
                ))
                
                // Note: AdaptiveRetryStrategy includes built-in circuit breaking behavior
                
                .build();

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .retryStrategy(customAdaptiveStrategy)
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.basic_adaptive_retry.create_custom_client]

    // snippet-start:[dynamodb.java2.basic_adaptive_retry.simple_client]
    /**
     * Simplest way to enable adaptive retry behavior using AdaptiveRetryStrategy.
     * This is the easiest approach for basic adaptive retry functionality.
     * 
     * @return DynamoDbClient with simple adaptive retry configuration
     */
    public static DynamoDbClient createDynamoDbClientWithRetryMode() {
        // Use the built-in AdaptiveRetryStrategy for simplest configuration
        // This automatically configures AdaptiveRetryStrategy with sensible defaults
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                    // Use AdaptiveRetryStrategy directly with default configuration
                    // This provides adaptive retry behavior with minimal setup
                    .retryStrategy(AdaptiveRetryStrategy.builder().build())
                    .build())
                .build();
    }
    // snippet-end:[dynamodb.java2.basic_adaptive_retry.simple_client]
}
