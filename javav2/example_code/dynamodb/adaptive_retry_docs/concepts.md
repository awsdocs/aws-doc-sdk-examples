# Adaptive Retry Strategy Concepts

## Overview

AWS Java SDK's `AdaptiveRetryStrategy` is a specialized retry strategy designed for use cases with high resource constraints. It includes all features of the standard retry strategy plus a client-side rate limiter that measures throttled vs non-throttled requests to minimize throttling errors.

> **⚠️ Important**: AdaptiveRetryStrategy assumes the client works against a single resource (e.g., one DynamoDB table or one S3 bucket). AWS recommends StandardRetryStrategy for most use cases.

## Standard Retry vs Adaptive Retry

### Standard Retry Policies

Traditional retry policies use fixed algorithms with predetermined backoff strategies:

- **Fixed Backoff**: Same delay between all retry attempts
- **Exponential Backoff**: Exponentially increasing delays (2^attempt * base_delay)
- **Linear Backoff**: Linearly increasing delays (attempt * base_delay)

**Limitations:**
- Cannot adapt to real-time service conditions
- May be too aggressive during service degradation
- May be too conservative during normal operations
- Fixed retry counts regardless of error type

### Adaptive Retry Strategy

Adaptive retry includes all features of the standard strategy and adds:

- **Client-Side Rate Limiter**: Measures the rate of throttled requests compared to non-throttled requests
- **Dynamic Request Rate**: Slows down requests to stay within safe bandwidth
- **Load-Based Adaptation**: Uses dynamic backoff delay based on current load against downstream resources
- **Circuit Breaking**: May prevent second attempts in outage scenarios to protect downstream services

**Benefits:**
- **Throttling Prevention**: Attempts to cause zero throttling errors through rate limiting
- **Resource Protection**: Protects downstream services from retry storms
- **Load Adaptation**: Adjusts to real-time service conditions and traffic patterns
- **Bandwidth Optimization**: Adjusts request rate to minimize throttling

## How Adaptive Retry Works

### 1. Client-Side Rate Limiting
- Implements a token bucket mechanism to control request rates
- Measures throttled vs non-throttled request ratios
- Reduces request rate when throttling is detected
- May delay initial attempts in high-traffic scenarios

### 2. Dynamic Backoff Strategy
- Uses dynamic backoff delay based on current load against downstream resources
- Adapts in real-time to changing service conditions and traffic patterns
- Different from standard exponential backoff - timing adjusts based on service load

### 3. Circuit Breaking Protection
- Performs circuit breaking when high downstream failures are detected
- May prevent second attempts during outage scenarios
- Designed to protect downstream services from retry storms
- First attempt is always executed, only retries may be disabled

## When to Use Adaptive Retry

### Appropriate Use Cases
- **High resource constraint environments** where minimizing throttling is critical
- **Single-resource applications** (one DynamoDB table, one S3 bucket per client)
- **Applications experiencing frequent throttling** with standard retry strategies
- **Environments where all clients use adaptive retry** against the same resource

### Use Standard Retry Instead When
- **Multi-resource clients** (one client accessing multiple tables/buckets)
- **General use cases** - AWS recommends StandardRetryStrategy for most applications
- **Mixed client environments** where not all clients use adaptive retry
- **Applications requiring predictable retry timing**

### Critical Limitations
⚠️ **Single Resource Assumption**: If you use a single client for multiple resources, throttling or outages with one resource will cause increased latency and failures for all other resources accessed by that client.

## Key Concepts

### Client-Side Rate Limiting
Adaptive retry includes built-in client-side rate limiting to prevent overwhelming services during degradation.

### Token Bucket Mechanism
Each client maintains a token bucket that provides a mechanism to stop retries when a large percentage of requests are failing and retries are unsuccessful.

### Rate Measurement
The strategy measures the rate of throttled requests compared to non-throttled requests to determine when to slow down request rates.

### Error Classification
Different retry strategies for different error types:
- **Throttling errors**: Aggressive backoff with rate limiting
- **Server errors**: Standard exponential backoff
- **Client errors**: Minimal or no retries

## Performance Implications

### Latency
- **Improved**: Enhanced recovery from transient issues
- **Adaptive**: Longer delays during service degradation (by design)

### Throughput
- **Higher**: Better success rates through intelligent retry timing
- **Stable**: Maintains throughput during service stress

### Resource Usage
- **Optimized**: Reduces unnecessary retry attempts
- **Efficient**: Better CPU and network utilization

## Testing and Validation

### Compilation Testing
Always test that your AdaptiveRetryStrategy configuration compiles correctly:

```java
@Test
public void testAdaptiveRetryStrategyCompilation() {
    // Test basic configuration
    AdaptiveRetryStrategy strategy = AdaptiveRetryStrategy.builder()
        .maxAttempts(3)
        .build();
    
    DynamoDbClient client = DynamoDbClient.builder()
        .overrideConfiguration(ClientOverrideConfiguration.builder()
            .retryStrategy(strategy)
            .build())
        .build();
    
    assertNotNull(client);
    client.close();
}

@Test
public void testRetryModeConfiguration() {
    // Test RetryMode approach
    DynamoDbClient client = DynamoDbClient.builder()
        .overrideConfiguration(ClientOverrideConfiguration.builder()
            .retryStrategy(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)
            .build())
        .build();
    
    assertNotNull(client);
    client.close();
}
```

### Runtime Validation
Monitor retry behavior in your application logs to ensure the adaptive strategy is working as expected:

- Watch for adaptive backoff patterns
- Monitor throttling error rates
- Observe request rate adjustments during load spikes

## Implementation Details

### Correct API Usage in AWS SDK v2

When implementing AdaptiveRetryStrategy, use the correct AWS SDK v2 API:

```java
// CORRECT - Basic AdaptiveRetryStrategy configuration
AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(100),  // base delay
        Duration.ofSeconds(20)   // max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(1),   // base delay for throttling
        Duration.ofSeconds(20)   // max delay for throttling
    ))
    // Note: circuitBreakerEnabled() method doesn't exist - circuit breaking is built-in
    .build();

DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(adaptiveRetryStrategy)  // Use retryStrategy(), not retryPolicy()
        .build())
    .build();
```

### Simplest Configuration Approach

For basic adaptive retry behavior, use RetryMode:

```java
// CORRECT - Simplest adaptive retry configuration
DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)
        .build())
    .build();
```

### Key API Differences from Standard Retry

- **Package**: `software.amazon.awssdk.retries.AdaptiveRetryStrategy` (not `core.retry`)
- **Builder**: `AdaptiveRetryStrategy.builder()` (not `RetryPolicy.builder()`)
- **Configuration**: `.retryStrategy()` (not `.retryPolicy()`)
- **Circuit Breaking**: Built-in (no `.circuitBreakerEnabled()` method)

## Next Steps

Now that you understand the concepts, proceed to:
1. [Migration Guide](migration-guide.md) - Convert existing retry policies with correct API usage
2. [Configuration Reference](configuration-reference.md) - Detailed parameter documentation
3. [Examples](../examples/) - Working, tested code implementations

## Sources and References

This conceptual guide is based on official AWS SDK for Java 2.x documentation:

### Primary Sources

1. **AWS SDK for Java 2.x Developer Guide - Configure retry behavior**  
   *AWS Documentation*  
   https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html  
   Retrieved: August 18, 2025

2. **AWS SDK for Java 2.x API Reference - AdaptiveRetryStrategy**  
   *AWS SDK API Documentation*  
   https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/retries/AdaptiveRetryStrategy.html  
   Retrieved: August 18, 2025

### Key Technical Details

- **Retry Strategy Introduction**: Retry strategies were introduced in AWS SDK for Java 2.x version 2.26.0 as part of the AWS-wide effort to unify interfaces and behavior¹
- **Adaptive Strategy Purpose**: Designed specifically for "use cases with a high level of resource constraints"¹
- **Rate Limiting**: "Includes all the features of the standard strategy and adds a client-side rate limiter that measures the rate of throttled requests compared to non-throttled requests"¹
- **Single Resource Assumption**: "The adaptive retry strategy assumes that the client works against a single resource (for example, one DynamoDB table or one Amazon S3 bucket)"¹
- **AWS Recommendation**: StandardRetryStrategy is "the recommended RetryStrategy implementation for normal use cases" and "generally useful across all retry use cases" unlike AdaptiveRetryStrategy¹

---
**Citations:**
1. AWS SDK for Java 2.x Developer Guide. "Configure retry behavior in the AWS SDK for Java 2.x." Amazon Web Services. https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html