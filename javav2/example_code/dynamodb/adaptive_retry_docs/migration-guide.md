# Migration Guide: From Standard Retry Policies to AdaptiveRetryStrategy

## Overview

This guide provides step-by-step instructions for migrating from AWS SDK's standard retry policies to `AdaptiveRetryStrategy`. The adaptive retry strategy includes client-side rate limiting and dynamic backoff based on current load conditions.

> **⚠️ Important**: AdaptiveRetryStrategy is designed for use cases with high resource constraints and assumes single-resource clients. For most applications, AWS recommends using `StandardRetryStrategy` instead. Consider AdaptiveRetryStrategy only if you have specific throttling challenges and can ensure single-resource client usage.

## Why Migrate to AdaptiveRetryStrategy?

- **Client-Side Rate Limiting**: Includes a rate limiter that measures throttled vs non-throttled requests
- **Dynamic Load Adaptation**: Uses dynamic backoff delay based on current load against downstream resources
- **Throttling Prevention**: Attempts to stay within safe bandwidth to minimize throttling errors
- **Resource-Constrained Environments**: Designed for use cases with high resource constraints

### Important Considerations

**⚠️ Single Resource Assumption**: AdaptiveRetryStrategy assumes the client works against a single resource (e.g., one DynamoDB table or one S3 bucket). If you use a single client for multiple resources, throttling or outages with one resource can affect all other resources.

**⚠️ Recommendation**: AWS recommends using StandardRetryStrategy for most use cases, as it is "generally useful across all retry use cases" unlike AdaptiveRetryStrategy which is specialized for resource-constrained scenarios.

## Step-by-Step Migration Process

### Step 1: Identify Your Current Retry Configuration

First, locate your existing retry policy configuration. Common patterns include:

**Standard Retry Policy Example:**
```java
// Current standard retry configuration
RetryPolicy retryPolicy = RetryPolicy.builder()
    .numRetries(3)
    .retryCondition(RetryCondition.defaultRetryCondition())
    .backoffStrategy(BackoffStrategy.defaultStrategy())
    .throttlingBackoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
    .build();

DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryPolicy(retryPolicy)
        .build())
    .build();
```

### Step 2: Replace with AdaptiveRetryStrategy

Convert your standard retry policy to AdaptiveRetryStrategy:

**AdaptiveRetryStrategy Equivalent:**
```java
// Simplest approach: Use RetryMode.ADAPTIVE
DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)
        .build())
    .build();

// Or using builder pattern for custom configuration
AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(4) // numRetries(3) + 1 initial attempt = 4 total attempts
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
        .retryStrategy(adaptiveRetryStrategy)
        .build())
    .build();
```

### Step 3: Parameter Mapping Reference

Use this table to map your existing retry policy parameters to AdaptiveRetryStrategy equivalents:

| Standard Retry Policy | AdaptiveRetryStrategy | Notes |
|----------------------|----------------------|-------|
| `numRetries(n)` | `maxAttempts(n+1)` | Add 1 to account for initial attempt |
| `retryCondition()` | Built-in adaptive logic | AdaptiveRetryStrategy handles this automatically |
| `backoffStrategy()` | Built-in adaptive backoff | Uses intelligent backoff based on error patterns |
| `throttlingBackoffStrategy()` | Built-in throttling handling | Automatically adapts to throttling scenarios |

### Step 4: Advanced Configuration Migration

If you have custom retry configurations, here's how to migrate them:

**Before - Custom Standard Retry:**
```java
RetryPolicy customRetryPolicy = RetryPolicy.builder()
    .numRetries(5)
    .retryCondition(RetryCondition.defaultRetryCondition()
        .and(context -> context.exception() instanceof DynamoDbException))
    .backoffStrategy(BackoffStrategy.exponentialDelay(Duration.ofMillis(100), Duration.ofSeconds(10)))
    .build();
```

**After - Custom AdaptiveRetryStrategy:**
```java
// AdaptiveRetryStrategy allows custom backoff configuration
AdaptiveRetryStrategy customAdaptiveStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(6) // 5 retries + 1 initial = 6 total attempts
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(200),  // custom base delay
        Duration.ofSeconds(30)   // increased max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(500),  // longer base delay for throttling
        Duration.ofMinutes(1)    // extended max delay for throttling
    ))
    // Note: circuitBreakerEnabled() is not available - circuit breaking is built into adaptive strategy
    .build();
```

### Step 5: Validation and Testing

After migration, validate your new configuration:

1. **Compile and Run**: Ensure your application compiles and runs without errors
2. **Monitor Retry Behavior**: Observe retry patterns in your logs
3. **Performance Testing**: Compare performance before and after migration
4. **Error Handling**: Verify that error scenarios are handled appropriately

**Example Validation Code:**
```java
// Test your new adaptive retry configuration
@Test
public void testAdaptiveRetryMigration() {
    AdaptiveRetryStrategy strategy = AdaptiveRetryStrategy.builder()
        .maxAttempts(4)
        .build();
    
    DynamoDbClient client = DynamoDbClient.builder()
        .overrideConfiguration(ClientOverrideConfiguration.builder()
            .retryStrategy(strategy)
            .build())
        .build();
    
    // Perform operations and verify retry behavior
    assertNotNull(client);
}
```

## Critical API Differences and Common Mistakes

### Key API Changes in AWS SDK v2

When migrating to AdaptiveRetryStrategy, be aware of these critical API differences:

#### 1. Package Changes
```java
// WRONG - SDK v1 style packages
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;

// CORRECT - SDK v2 packages
import software.amazon.awssdk.retries.AdaptiveRetryStrategy;
import software.amazon.awssdk.retries.api.BackoffStrategy;
```

#### 2. Builder API Changes
```java
// WRONG - RetryPolicy.builder() is deprecated
RetryPolicy retryPolicy = RetryPolicy.builder()
    .numRetries(3)
    .circuitBreakerEnabled(true)  // This method doesn't exist in AdaptiveRetryStrategy
    .build();

// CORRECT - AdaptiveRetryStrategy.builder()
AdaptiveRetryStrategy adaptiveRetryStrategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    // Note: circuitBreakerEnabled() method doesn't exist - circuit breaking is built-in
    .build();
```

#### 3. Configuration Method Changes
```java
// WRONG - .retryPolicy() doesn't exist in SDK v2
.overrideConfiguration(builder -> builder.retryPolicy(retryPolicy))

// CORRECT - Use .retryStrategy()
.overrideConfiguration(ClientOverrideConfiguration.builder()
    .retryStrategy(adaptiveRetryStrategy)
    .build())
```

#### 4. RetryMode Usage
```java
// WRONG - retryMode() method signature is incorrect
.retryMode(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)

// CORRECT - Use retryStrategy() with RetryMode
.retryStrategy(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)
```

### Common Compilation Errors and Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| `cannot find symbol: method circuitBreakerEnabled(boolean)` | Method doesn't exist in AdaptiveRetryStrategy | Remove - circuit breaking is built-in |
| `method retryMode(...) cannot be applied to given types` | Wrong method signature | Use `.retryStrategy(RetryMode.ADAPTIVE)` |
| `cannot find symbol: method retryPolicy(...)` | Method doesn't exist in SDK v2 | Use `.retryStrategy(...)` |
| `RetryPolicy` import errors | Using deprecated v1-style API | Import `AdaptiveRetryStrategy` instead |

## Common Migration Scenarios

### Scenario 1: Basic DynamoDB Client Migration

**Before:**
```java
DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryPolicy(RetryPolicy.builder()
            .numRetries(2)
            .build())
        .build())
    .build();
```

**After:**
```java
// Simplest approach using RetryMode
DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(software.amazon.awssdk.core.retry.RetryMode.ADAPTIVE)
        .build())
    .build();

// Or with custom max attempts using AdaptiveRetryStrategy.builder()
AdaptiveRetryStrategy strategy = AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    .build();

DynamoDbClient client = DynamoDbClient.builder()
    .overrideConfiguration(ClientOverrideConfiguration.builder()
        .retryStrategy(strategy)
        .build())
    .build();
```

### Scenario 2: High-Throughput Application Migration

**Before:**
```java
RetryPolicy highThroughputRetry = RetryPolicy.builder()
    .numRetries(1) // Minimal retries for speed
    .backoffStrategy(BackoffStrategy.fixedDelay(Duration.ofMillis(50)))
    .build();
```

**After:**
```java
// AdaptiveRetryStrategy allows custom backoff configuration
AdaptiveRetryStrategy highThroughputAdaptive = AdaptiveRetryStrategy.builder()
    .maxAttempts(2) // 1 retry + 1 initial attempt
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofMillis(50),   // fast base delay
        Duration.ofSeconds(5)    // shorter max delay
    ))
    .build();
```

### Scenario 3: Batch Operation Migration

**Before:**
```java
RetryPolicy batchRetry = RetryPolicy.builder()
    .numRetries(5) // More retries for batch operations
    .backoffStrategy(BackoffStrategy.exponentialDelay(Duration.ofSeconds(1), Duration.ofMinutes(1)))
    .build();
```

**After:**
```java
// AdaptiveRetryStrategy with custom configuration for batch operations
AdaptiveRetryStrategy batchAdaptive = AdaptiveRetryStrategy.builder()
    .maxAttempts(6) // 5 retries + 1 initial attempt
    .backoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(1),   // longer base delay for batch
        Duration.ofMinutes(1)    // extended max delay
    ))
    .throttlingBackoffStrategy(BackoffStrategy.exponentialDelay(
        Duration.ofSeconds(2),   // even longer for throttling
        Duration.ofMinutes(2)    // extended throttling delay
    ))
    .build();
```

## Key Differences After Migration

### Behavioral Changes
1. **Adaptive Learning**: The strategy learns from retry patterns and adjusts automatically
2. **Adaptive Backoff**: Uses dynamic backoff algorithms based on error types
3. **Throttling Awareness**: Better handling of throttling scenarios with adaptive delays
4. **Error Classification**: Different retry behavior for different types of errors

### Performance Improvements
- Reduced unnecessary retries through intelligent pattern recognition
- Improved recovery from transient errors
- Better handling of sustained error conditions
- Improved overall application responsiveness

## Troubleshooting Migration Issues

### Common Issues and Solutions

**Issue 1: Compilation Errors - Method Not Found**
- **Problem**: `cannot find symbol: method circuitBreakerEnabled(boolean)`
- **Solution**: Remove `.circuitBreakerEnabled()` calls - circuit breaking is built into AdaptiveRetryStrategy by default

**Issue 2: Compilation Errors - Wrong Method Signature**
- **Problem**: `method retryMode(...) cannot be applied to given types`
- **Solution**: Use `.retryStrategy(RetryMode.ADAPTIVE)` instead of `.retryMode(RetryMode.ADAPTIVE)`

**Issue 3: Compilation Errors - Method Not Found**
- **Problem**: `cannot find symbol: method retryPolicy(...)`
- **Solution**: Replace `.retryPolicy()` with `.retryStrategy()` in ClientOverrideConfiguration

**Issue 4: Import Errors**
- **Problem**: `RetryPolicy` not found or deprecated warnings
- **Solution**: Update imports to use `software.amazon.awssdk.retries.AdaptiveRetryStrategy` and related classes

**Issue 5: Different Retry Behavior**
- **Problem**: Application behaves differently after migration
- **Solution**: Review parameter mapping and adjust `maxAttempts` and timing parameters

**Issue 6: Performance Changes**
- **Problem**: Unexpected performance characteristics
- **Solution**: Monitor retry patterns and fine-tune adaptive strategy parameters

### Migration Checklist

- [ ] Identified all existing retry policy configurations
- [ ] Updated import statements to use `software.amazon.awssdk.retries.*` packages
- [ ] Replaced `RetryPolicy.builder()` with `AdaptiveRetryStrategy.builder()`
- [ ] Replaced `retryPolicy()` with `retryStrategy()` in ClientOverrideConfiguration
- [ ] Removed `.circuitBreakerEnabled()` calls (built into AdaptiveRetryStrategy)
- [ ] Adjusted `numRetries` to `maxAttempts` (adding 1)
- [ ] Fixed `.retryMode()` calls to use `.retryStrategy(RetryMode.ADAPTIVE)`
- [ ] Tested compilation and basic functionality
- [ ] Validated retry behavior under error conditions
- [ ] Monitored performance after migration

## Next Steps

After completing the migration:

1. **Monitor Performance**: Track retry patterns and application performance
2. **Fine-Tune Configuration**: Adjust parameters based on observed behavior
3. **Review Best Practices**: Consult the best practices guide for optimization tips
4. **Advanced Configuration**: Explore advanced AdaptiveRetryStrategy features

For more detailed configuration options, see the [Configuration Reference](configuration-reference.md).
For advanced usage patterns, see the [Integration Patterns](integration-patterns.md).

## Sources and References

This migration guide is based on official AWS SDK for Java 2.x documentation and best practices:

### Primary Sources

1. **AWS SDK for Java 2.x Developer Guide - Configure retry behavior**  
   *AWS Documentation*  
   https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html  
   Retrieved: August 18, 2025

2. **AWS SDK for Java 2.x API Reference - AdaptiveRetryStrategy**  
   *AWS SDK API Documentation*  
   https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/retries/AdaptiveRetryStrategy.html  
   Retrieved: August 18, 2025

3. **AWS SDK for Java 2.x API Reference - RetryPolicy (Deprecated)**  
   *AWS SDK API Documentation*  
   https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/core/retry/RetryPolicy.html  
   Retrieved: August 18, 2025

### Key Information Sources

- **Retry Strategy Introduction**: The retry strategy API was introduced in AWS SDK for Java 2.x version 2.26.0 as part of the AWS-wide effort to unify interfaces and behavior across SDKs¹
- **Migration Compatibility**: RetryPolicy (the retry policy API) will be supported for the foreseeable future, with the Java SDK adapting it to a RetryStrategy behind the scenes¹
- **Default Values**: Standard retry strategy defaults to 3 maximum attempts (2 retries + 1 initial attempt), 100ms base delay for non-throttling errors, and 1000ms base delay for throttling errors¹
- **Adaptive Strategy Characteristics**: AdaptiveRetryStrategy includes all features of the standard strategy plus a client-side rate limiter that measures throttled vs non-throttled requests¹

### Additional Context

The migration examples and parameter mappings in this guide are derived from the official AWS documentation patterns and the documented default values for each retry strategy type. All code examples follow the patterns established in the AWS SDK for Java 2.x Developer Guide.

---
**Citations:**
1. AWS SDK for Java 2.x Developer Guide. "Configure retry behavior in the AWS SDK for Java 2.x." Amazon Web Services. https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html