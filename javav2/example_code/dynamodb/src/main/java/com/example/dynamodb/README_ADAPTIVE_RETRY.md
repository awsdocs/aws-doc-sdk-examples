# Adaptive Retry Strategy Examples for DynamoDB

This document provides comprehensive guidance on implementing and migrating to AdaptiveRetryStrategy for Amazon DynamoDB using AWS SDK for Java 2.x.

## Table of Contents

1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Getting Started](#getting-started)
4. [Migration Guide](#migration-guide)
5. [Configuration Examples](#configuration-examples)
6. [Best Practices](#best-practices)
7. [Troubleshooting](#troubleshooting)

## Overview

AdaptiveRetryStrategy is an enhanced retry mechanism in AWS SDK for Java 2.x that automatically adjusts retry behavior based on observed success/failure patterns. It builds upon StandardRetryStrategy and adds intelligent features like client-side rate limiting and adaptive backoff algorithms.

### What's New in AdaptiveRetryStrategy

- **Client-side rate limiting** to prevent retry storms
- **Intelligent throttling detection** with separate backoff strategies
- **Built-in circuit breaking** for failure protection
- **Adaptive learning** from retry patterns
- **Backward compatibility** with existing StandardRetryStrategy configurations

## Key Features

### 1. Client-Side Rate Limiting
AdaptiveRetryStrategy includes a rate limiter that tracks throttled vs. non-throttled requests, helping to prevent retry storms that can overwhelm services.

### 2. Intelligent Throttling Handling
Uses longer delays specifically for throttling errors (e.g., 1 second vs. 100ms base delay), giving the service more time to recover.

### 3. Adaptive Learning
The strategy learns from retry patterns and adjusts behavior dynamically based on observed success/failure rates.

### 4. Built-in Circuit Breaking
Automatically stops retries when failure rate is too high, preventing cascading failures.

## Getting Started

### Basic Implementation

```java
// Create a DynamoDbClient with AdaptiveRetryStrategy
AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(100),  // base delay
        Duration.ofSeconds(20)   // max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(1),   // longer base delay for throttling
        Duration.ofSeconds(20)   // max delay for throttling
    ))
    .build();

DynamoDbClient client = DynamoDbClient.builder()
    .region(Region.US_EAST_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(adaptiveRetryStrategy)
        .build())
    .build();
```

### Simple Implementation

For basic adaptive retry functionality with default settings:

```java
DynamoDbClient client = DynamoDbClient.builder()
    .region(Region.US_EAST_1)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(AdaptiveRetryStrategy.builder().build())
        .build())
    .build();
```

## Migration Guide

### From StandardRetryStrategy to AdaptiveRetryStrategy

#### Before (StandardRetryStrategy)
```java
StandardRetryStrategy standardRetryStrategy = StandardRetryStrategy.builder()
    .maxAttempts(3)
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(100),
        Duration.ofSeconds(20)
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(100),  // same as regular backoff
        Duration.ofSeconds(20)
    ))
    .build();
```

#### After (AdaptiveRetryStrategy)
```java
AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(100),
        Duration.ofSeconds(20)
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(1),   // optimized for throttling
        Duration.ofSeconds(20)
    ))
    .build();
```

### Migration Benefits

1. **No Breaking Changes**: Existing application logic remains unchanged
2. **Enhanced Performance**: Reduced unnecessary retries through pattern recognition
3. **Better Throttling Handling**: Separate backoff strategies for different error types
4. **Improved Resilience**: Built-in circuit breaking and rate limiting

## Configuration Examples

### 1. High-Throughput Applications

For applications requiring minimal latency:

```java
AdaptiveRetryStrategy highThroughputStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(2)  // minimal retries for speed
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(50),   // fast base delay
        Duration.ofSeconds(5)    // short max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(200),  // slightly longer for throttling
        Duration.ofSeconds(10)   // longer recovery time
    ))
    .build();
```

### 2. Batch Operations

For long-running batch operations:

```java
AdaptiveRetryStrategy batchStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(6)  // more retries for batch operations
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(1),   // longer base delay
        Duration.ofMinutes(1)    // extended max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(2),   // even longer for batch throttling
        Duration.ofMinutes(2)    // extended recovery time
    ))
    .build();
```

### 3. Custom Configuration

For specific application requirements:

```java
AdaptiveRetryStrategy customStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(5)
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(200),  // custom base delay
        Duration.ofSeconds(30)   // custom max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(500),  // optimized throttling base
        Duration.ofMinutes(1)    // extended throttling max
    ))
    .build();
```

## Best Practices

### 1. Choose Appropriate Max Attempts
- **Interactive applications**: 2-3 attempts for fast response
- **Background processing**: 5-6 attempts for reliability
- **Batch operations**: 6+ attempts for maximum retry coverage

### 2. Configure Backoff Strategies Appropriately
- Use shorter delays for regular errors
- Use longer delays for throttling errors
- Consider your application's latency requirements

### 3. Monitor Retry Behavior
- Track retry patterns and success rates
- Adjust configuration based on observed behavior
- Monitor for retry storms or excessive throttling

### 4. Test Under Load
- Test retry behavior under various load conditions
- Validate that adaptive features work as expected
- Ensure throttling handling improves over time

## Troubleshooting

### Common Issues

#### 1. Excessive Retries
**Symptoms**: High latency, increased costs
**Solution**: Reduce `maxAttempts` or increase backoff delays

#### 2. Throttling Not Improving
**Symptoms**: Continued throttling despite adaptive strategy
**Solution**: Increase `throttlingBackoffStrategy` delays

#### 3. Circuit Breaker Triggering
**Symptoms**: Requests failing without retries
**Solution**: Check underlying service health, consider reducing load

### Debugging Tips

1. **Enable Logging**: Use appropriate log levels to track retry behavior
2. **Monitor Metrics**: Track retry counts, latency, and error rates
3. **Test Incrementally**: Start with conservative settings and adjust gradually

## Code Examples

The following files demonstrate different aspects of AdaptiveRetryStrategy:

- `BasicAdaptiveRetryImplementation.java`: Basic setup and configuration
- `MigrationExamples.java`: Before/after migration examples with detailed comparisons

## Additional Resources

- [AWS SDK for Java 2.x Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [DynamoDB Best Practices](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/best-practices.html)
- [AWS SDK Retry Behavior](https://docs.aws.amazon.com/sdkref/latest/guide/feature-retry-behavior.html)
