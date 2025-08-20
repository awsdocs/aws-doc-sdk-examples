# AWS Java SDK AdaptiveRetryStrategy Guide

This guide provides comprehensive documentation and examples for implementing AWS Java SDK's `AdaptiveRetryStrategy` with AWS service clients.

> **⚠️ Important**: AdaptiveRetryStrategy is designed for specialized use cases with high resource constraints and single-resource clients. AWS recommends StandardRetryStrategy for most applications.

## Documentation Structure

| Document | Purpose | When to Use |
|----------|---------|-------------|
| **[Concepts](concepts.md)** | Conceptual foundation and theory | Understanding when/why to use AdaptiveRetryStrategy |
| **[Migration Guide](migration-guide.md)** | Step-by-step migration instructions | Converting from older retry policies |
| **[Configuration Reference](configuration-reference.md)** | Complete parameter reference | Fine-tuning and optimization |

## Getting Started

1. **Quick Start**: Check the `examples/` directory for ready-to-use code
2. **Learn Concepts**: Read [Concepts](concepts.md) to understand when AdaptiveRetryStrategy is appropriate
3. **Migration**: Use [Migration Guide](migration-guide.md) if converting existing code
4. **Fine-tuning**: Consult [Configuration Reference](configuration-reference.md) for parameter optimization

## Documentation Sources

This guide is based on official AWS SDK for Java 2.x documentation and follows AWS best practices as documented in:

- [AWS SDK for Java 2.x Developer Guide - Configure retry behavior](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html)
- [AWS SDK for Java 2.x API Reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/retries/package-summary.html)

All examples and recommendations align with AWS's official guidance on retry strategy implementation.