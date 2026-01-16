# CloudWatch Logs Large Query Example

This folder contains a .NET feature scenario that demonstrates how to perform large-scale queries on Amazon CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit.

## Overview

CloudWatch Logs Insights queries have a maximum result limit of 10,000 records per query. This example demonstrates how to overcome this limitation by using a recursive binary search algorithm that splits the time range into smaller segments when the limit is reached.

The scenario performs the following steps:

1. **Setup**: Deploys a CloudFormation stack with a log group and log stream
2. **Data Generation**: Creates and uploads 50,000 sample log entries
3. **Query Execution**: Performs recursive queries to retrieve all logs using binary search
4. **Cleanup**: Removes all created resources

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
│   ├── LargeQueryWorkflowTests.cs        # Integration tests
│   ├── Usings.cs                         # Global usings
│   └── CloudWatchLogsTests.csproj        # Test project file
└── CloudWatchLogsLargeQuery.sln          # Solution file
```

## What This Example Demonstrates

- Deploying AWS resources using CloudFormation
- Generating and ingesting large volumes of log data using PutLogEvents
- Performing CloudWatch Logs Insights queries with StartQuery and GetQueryResults
- Using recursive binary search to retrieve more than 10,000 results
- Handling timestamp precision for accurate query splitting
- Cleaning up resources after completion

## Running the Example

### Interactive Mode

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

4. Follow the prompts to:
   - Deploy the CloudFormation stack
   - Generate sample logs
   - Execute the recursive query
   - View sample results
   - Clean up resources

### Non-Interactive Mode (Testing)

Run the integration tests to execute the scenario without user prompts:

```
dotnet test
```

The test verifies that the scenario completes without errors and successfully retrieves all 50,000 log entries.

## Prerequisites

- .NET 8.0 or later
- AWS credentials configured
- Permissions for:
  - CloudWatch Logs (CreateLogGroup, CreateLogStream, PutLogEvents, StartQuery, GetQueryResults, DeleteLogGroup)
  - CloudFormation (CreateStack, DescribeStacks, DeleteStack)

## How It Works

### Recursive Query Algorithm

The key to retrieving more than 10,000 results is the recursive binary search algorithm:

1. Execute a query with the full date range
2. If results < 10,000, return them (we have all logs in this range)
3. If results = 10,000, there may be more logs:
   - Get the timestamp of the last result
   - Calculate the midpoint between the last timestamp and end date
   - Recursively query the first half (last timestamp to midpoint)
   - Recursively query the second half (midpoint to end date)
   - Combine all results

This approach ensures all logs are retrieved by progressively narrowing the time ranges until each segment contains fewer than 10,000 results.

### Timestamp Precision

The algorithm uses millisecond precision for timestamps to ensure accurate splitting and prevent duplicate or missing log entries. Each query adjusts the start time by 1 millisecond to avoid overlapping results.

## Expected Output

When running the scenario, you'll see output similar to:

```
--------------------------------------------------------------------------------
Welcome to the CloudWatch Logs Large Query Scenario.
--------------------------------------------------------------------------------
Preparing the application...
Deploying CloudFormation stack: CloudWatchLargeQueryStack
CloudFormation stack creation started: CloudWatchLargeQueryStack
Waiting for CloudFormation stack creation to complete...
CloudFormation stack creation complete.
Stack output RoleARN: arn:aws:iam::123456789012:role/...
Generating 50,000 sample log entries...
Batch 1/5: Created 10,000 log entries
Batch 2/5: Created 10,000 log entries
...
Waiting 5 minutes for logs to be fully ingested...
--------------------------------------------------------------------------------
Starting recursive query to retrieve all logs...
Query date range: 2024-01-15T10:00:00.000Z to 2024-01-15T10:05:00.000Z. Found 10000 logs.
Query date range: 2024-01-15T10:02:30.000Z to 2024-01-15T10:03:45.000Z. Found 10000 logs.
...
Queries finished in 8.234 seconds.
Total logs found: 50000
--------------------------------------------------------------------------------
```

## Related Resources

- [CloudWatch Logs Documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)
- [CloudWatch Logs Insights Query Syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)
- [AWS SDK for .NET](https://aws.amazon.com/sdk-for-net/)
- [CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
