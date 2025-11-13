# CloudWatch Logs Large Query Example

This folder contains a .NET feature scenario that demonstrates how to perform large-scale queries on Amazon CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit.

## Project Structure

```
LargeQuery/
├── Actions/
│   ├── CloudWatchLogsWrapper.cs          # Wrapper class for CloudWatch Logs operations
│   └── CloudWatchLogsActions.csproj      # Actions project file
├── Scenarios/
│   ├── LargeQueryWorkflow.cs             # Main workflow implementation
│   ├── README.md                         # Detailed scenario documentation
│   └── CloudWatchLogsScenario.csproj     # Scenario project file
├── Tests/
│   ├── LargeQueryWorkflowTests.cs        # Unit tests
│   ├── Usings.cs                         # Global usings
│   └── CloudWatchLogsTests.csproj        # Test project file
└── CloudWatchLogsLargeQuery.sln          # Solution file
```

## What This Example Demonstrates

- Deploying AWS resources using CloudFormation
- Generating and ingesting large volumes of log data
- Performing CloudWatch Logs Insights queries
- Using recursive binary search to retrieve more than 10,000 results
- Cleaning up resources after completion

## Running the Example

1. Navigate to the solution directory:
   ```
   cd dotnetv4/CloudWatchLogs/LargeQuery
   ```

2. Build the solution:
   ```
   dotnet build
   ```

3. Run the scenario:
   ```
   dotnet run --project Scenarios/CloudWatchLogsScenario.csproj
   ```

4. Run the tests:
   ```
   dotnet test
   ```

## Prerequisites

- .NET 8.0 or later
- AWS credentials configured
- Python 3.x (for log generation)
- Permissions for CloudWatch Logs and CloudFormation

## Related Resources

- [CloudWatch Logs Documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)
- [CloudWatch Logs Insights Query Syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)
- [AWS SDK for .NET](https://aws.amazon.com/sdk-for-net/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
