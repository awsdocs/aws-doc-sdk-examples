# AdaptiveRetryStrategy Configuration Reference

## Overview

This document provides a comprehensive reference for all configuration parameters available in AWS Java SDK's `AdaptiveRetryStrategy`. Each parameter is documented with its purpose, default values, recommended ranges, and performance implications to help you optimize retry behavior for your specific use case.

## Core Configuration Parameters

### maxAttempts

**Purpose**: Sets the maximum number of retry attempts for failed requests.

**Type**: `int`

**Default Value**: `3`

**Recommended Range**: `1-10`

**Performance Impact**: 
- Higher values increase resilience but may lead to longer response times under failure conditions
- Lower values reduce latency but may miss transient error recovery opportunities
- Consider your application's timeout requirements when setting this value
- *Reference: AWS SDK for Java 2.x Developer Guide - Retry Configuration [1]*

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .maxAttempts(5)
    .build();
```

### baseDelay

**Purpose**: Defines the initial delay before the first retry attempt.

**Type**: `Duration`

**Default Value**: `Duration.ofMillis(100)`

**Recommended Range**: `50ms - 1000ms`

**Performance Impact**:
- Shorter delays reduce overall request latency but may overwhelm struggling services
- Longer delays improve service recovery time but increase user-perceived latency
- Should be balanced with your service's typical recovery patterns
- *Reference: DynamoDB Developer Guide - Error Retries and Exponential Backoff [3]*

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .baseDelay(Duration.ofMillis(200))
    .build();
```

### maxBackoffTime

**Purpose**: Sets the maximum delay between retry attempts, preventing exponential backoff from growing indefinitely.

**Type**: `Duration`

**Default Value**: `Duration.ofSeconds(20)`

**Recommended Range**: `1s - 60s`

**Performance Impact**:
- Lower values keep retry attempts frequent but may not allow sufficient recovery time
- Higher values provide more recovery time but can significantly increase total request time
- Critical for preventing extremely long retry cycles in persistent failure scenarios
- *Reference: AWS Developer Blog - Exponential Backoff and Jitter [8]*

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .maxBackoffTime(Duration.ofSeconds(30))
    .build();
```

## Adaptive Behavior Parameters

### adaptiveMode

**Purpose**: Enables or disables the adaptive behavior that learns from success/failure patterns.

**Type**: `boolean`

**Default Value**: `true`

**Recommended Range**: `true` (recommended), `false` (for debugging)

**Performance Impact**:
- When enabled, improves retry efficiency over time by learning from patterns
- When disabled, uses standard exponential backoff without adaptation
- Adaptive mode typically reduces unnecessary retries and improves overall throughput

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .adaptiveMode(true)
    .build();
```

### throttlingDetection

**Purpose**: Enables detection and special handling of throttling errors.

**Type**: `boolean`

**Default Value**: `true`

**Recommended Range**: `true` (recommended for most use cases)

**Performance Impact**:
- When enabled, applies longer delays for throttling errors to prevent further throttling
- Reduces the likelihood of cascading throttling issues
- May increase individual request latency but improves overall system stability
- *Reference: Amazon DynamoDB Best Practices [5]*

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .throttlingDetection(true)
    .build();
```

## Advanced Tuning Parameters

### successThreshold

**Purpose**: Number of consecutive successful requests needed to reduce retry aggressiveness.

**Type**: `int`

**Default Value**: `5`

**Recommended Range**: `3-10`

**Performance Impact**:
- Lower values make the strategy more responsive to improvements but may be too sensitive
- Higher values provide more stability but slower adaptation to service recovery
- Affects how quickly the strategy reduces retry delays after service recovery

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .successThreshold(7)
    .build();
```

### failureThreshold

**Purpose**: Number of consecutive failures needed to increase retry aggressiveness.

**Type**: `int`

**Default Value**: `3`

**Recommended Range**: `2-8`

**Performance Impact**:
- Lower values make the strategy more responsive to degradation but may overreact to transient issues
- Higher values provide more stability but slower response to service degradation
- Balances responsiveness with stability in failure detection

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .failureThreshold(4)
    .build();
```

### adaptiveRateLimit

**Purpose**: Enables adaptive rate limiting based on observed service capacity.

**Type**: `boolean`

**Default Value**: `false`

**Recommended Range**: `true` for high-throughput applications, `false` for low-volume use cases

**Performance Impact**:
- When enabled, may reduce request rate during service stress but improves overall success rate
- Helps prevent overwhelming struggling services
- Most beneficial for applications with high request volumes

**Example**:
```java
AdaptiveRetryStrategy.builder()
    .adaptiveRateLimit(true)
    .build();
```

## DynamoDB-Specific Considerations

### Recommended Configurations by Use Case

#### High-Throughput Applications
```java
AdaptiveRetryStrategy.builder()
    .maxAttempts(5)
    .baseDelay(Duration.ofMillis(50))
    .maxBackoffTime(Duration.ofSeconds(10))
    .adaptiveMode(true)
    .throttlingDetection(true)
    .adaptiveRateLimit(true)
    .successThreshold(3)
    .failureThreshold(2)
    .build();
```

#### Batch Processing Applications
```java
AdaptiveRetryStrategy.builder()
    .maxAttempts(8)
    .baseDelay(Duration.ofMillis(200))
    .maxBackoffTime(Duration.ofSeconds(60))
    .adaptiveMode(true)
    .throttlingDetection(true)
    .successThreshold(5)
    .failureThreshold(4)
    .build();
```

#### Interactive Applications
```java
AdaptiveRetryStrategy.builder()
    .maxAttempts(3)
    .baseDelay(Duration.ofMillis(100))
    .maxBackoffTime(Duration.ofSeconds(5))
    .adaptiveMode(true)
    .throttlingDetection(true)
    .successThreshold(4)
    .failureThreshold(3)
    .build();
```

## Performance Tuning Guidelines

### Monitoring and Metrics

When tuning your AdaptiveRetryStrategy configuration, monitor these key metrics:

1. **Success Rate**: Percentage of requests that succeed after retries
2. **Average Latency**: Mean response time including retry delays
3. **P99 Latency**: 99th percentile response time to understand worst-case scenarios
4. **Retry Rate**: Percentage of requests that require retries
5. **Throttling Rate**: Frequency of throttling errors

*Reference: Amazon CloudWatch Metrics for DynamoDB [12] and "The Tail at Scale" [10]*

### Tuning Process

1. **Start with defaults** and measure baseline performance
2. **Adjust maxAttempts** based on your error tolerance and latency requirements
3. **Tune baseDelay** to balance responsiveness with service protection
4. **Set maxBackoffTime** to prevent unacceptably long delays
5. **Fine-tune adaptive parameters** based on your traffic patterns

*Reference: AWS Architecture Center - Reliability Pillar [6]*

### Common Anti-Patterns

❌ **Don't**: Set maxAttempts too high (>10) without considering timeout implications
❌ **Don't**: Use very short baseDelay (<50ms) for high-volume applications
❌ **Don't**: Disable adaptiveMode unless debugging specific issues
❌ **Don't**: Set failureThreshold to 1 (too sensitive to transient errors)

### Best Practices

✅ **Do**: Test configuration changes under realistic load conditions
✅ **Do**: Monitor retry patterns and adjust based on observed behavior
✅ **Do**: Consider your downstream service's characteristics when tuning
✅ **Do**: Use different configurations for different operation types if needed

## Configuration Examples

### Complete Configuration Example
```java
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.time.Duration;

public class AdaptiveRetryConfiguration {
    public static DynamoDbClient createOptimizedClient() {
        AdaptiveRetryStrategy retryStrategy = AdaptiveRetryStrategy.builder()
            .maxAttempts(5)
            .baseDelay(Duration.ofMillis(100))
            .maxBackoffTime(Duration.ofSeconds(20))
            .adaptiveMode(true)
            .throttlingDetection(true)
            .adaptiveRateLimit(false)
            .successThreshold(5)
            .failureThreshold(3)
            .build();

        RetryPolicy retryPolicy = RetryPolicy.builder()
            .retryStrategy(retryStrategy)
            .build();

        return DynamoDbClient.builder()
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .retryPolicy(retryPolicy)
                .build())
            .build();
    }
}
```

## Troubleshooting Configuration Issues

### High Latency Issues
- Reduce `maxAttempts` or `maxBackoffTime`
- Consider shorter `baseDelay` for time-sensitive operations
- Check if `adaptiveRateLimit` is unnecessarily constraining throughput

### High Error Rates
- Increase `maxAttempts` within reasonable timeout bounds
- Ensure `throttlingDetection` is enabled
- Consider increasing `successThreshold` for more stable adaptation

### Excessive Retry Attempts
- Lower `maxAttempts` to prevent retry storms
- Increase `baseDelay` to give services more recovery time
- Enable `adaptiveRateLimit` for high-volume applications

This configuration reference provides the foundation for optimizing AdaptiveRetryStrategy behavior based on your specific application requirements and service characteristics.

## References and Further Reading

### Official AWS Documentation
1. **AWS SDK for Java 2.x Developer Guide - Retry Configuration**  
   https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry.html
   
2. **AWS SDK for Java 2.x API Reference - AdaptiveRetryStrategy**  
   https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/core/retry/RetryStrategy.html

3. **DynamoDB Developer Guide - Error Retries and Exponential Backoff**  
   https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.Errors.html#Programming.Errors.RetryAndBackoff

4. **AWS SDK for Java 2.x API Reference - RetryPolicy**  
   https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/core/retry/RetryPolicy.html

### AWS Best Practices and Whitepapers
5. **Amazon DynamoDB Best Practices**  
   https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/best-practices.html

6. **AWS Architecture Center - Reliability Pillar**  
   https://docs.aws.amazon.com/wellarchitected/latest/reliability-pillar/welcome.html

7. **Implementing Microservices on AWS - Retry Logic**  
   https://docs.aws.amazon.com/whitepapers/latest/microservices-on-aws/retry-logic.html

### Technical Articles and Blogs
8. **AWS Developer Blog - Exponential Backoff and Jitter**  
   https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/

9. **AWS SDK for Java 2.x Migration Guide**  
   https://docs.aws.amazon.com/sdk-for-java/latest/migration-guide/

### Academic and Industry References
10. **"The Tail at Scale" - Dean & Barroso (2013)**  
    Communications of the ACM, Vol. 56 No. 2, Pages 74-80
    https://cacm.acm.org/magazines/2013/2/160173-the-tail-at-scale/fulltext

11. **"Adaptive Timeout and Retry for Resilient Distributed Systems"**  
    IEEE Transactions on Dependable and Secure Computing

### Related AWS Services Documentation
12. **Amazon CloudWatch Metrics for DynamoDB**  
    https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/monitoring-cloudwatch.html

13. **AWS X-Ray Developer Guide - Tracing AWS SDK Calls**  
    https://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java-awssdkclients.html

### Community Resources
14. **AWS SDK for Java GitHub Repository**  
    https://github.com/aws/aws-sdk-java-v2

15. **AWS Developer Forums - Java SDK**  
    https://forums.aws.amazon.com/forum.jspa?forumID=70

## Citation Format

When referencing this configuration guide in your documentation or code comments, please use:

```
AdaptiveRetryStrategy Configuration Reference. 
Internal Documentation. [Current Date].
Based on AWS SDK for Java 2.x Documentation and Best Practices.
```

## Version Information

This document is based on:
- AWS SDK for Java 2.x version 2.20.x and later
- DynamoDB Enhanced Client version 2.20.x and later
- Java 8+ compatibility requirements

For the most up-to-date API changes and new features, always consult the official AWS SDK for Java 2.x documentation and release notes.