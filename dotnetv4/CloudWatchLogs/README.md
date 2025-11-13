# CloudWatch Logs Examples for .NET

This folder contains examples for Amazon CloudWatch Logs using the AWS SDK for .NET.

## Examples

### Feature Scenarios

- **[LargeQuery](LargeQuery/)** - Demonstrates how to perform large-scale queries on CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit.

## Running the Examples

Each example includes its own README with specific instructions. Generally, you can:

1. Navigate to the example directory
2. Build the solution: `dotnet build`
3. Run the example: `dotnet run --project Scenarios/{ProjectName}.csproj`
4. Run tests: `dotnet test`

## Prerequisites

- .NET 8.0 or later
- AWS credentials configured
- Appropriate AWS permissions for CloudWatch Logs

## Additional Resources

- [CloudWatch Logs Documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)
- [AWS SDK for .NET Documentation](https://docs.aws.amazon.com/sdk-for-net/)
- [CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
