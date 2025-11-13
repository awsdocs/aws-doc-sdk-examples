# CloudWatch Logs Large Query Workflow

## Overview

This example demonstrates how to perform large-scale queries on Amazon CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit. The workflow showcases how to use CloudWatch Logs Insights queries with a recursive algorithm to fetch all matching log entries.

## Workflow Steps

This workflow demonstrates the following steps and tasks:

1. **Prepare the Application**
   - Prompts the user to deploy a CloudFormation stack and generate sample logs
   - Deploys the CloudFormation template to create a log group and log stream
   - Executes a Python script to generate 50,000 sample log entries
   - Waits 5 minutes for logs to be fully ingested and indexed

2. **Execute Large Query**
   - Prompts the user for query parameters (limit)
   - Performs recursive queries using binary search to retrieve all logs
   - Displays progress for each query executed with date ranges and result counts
   - Shows total execution time and total logs found
   - Optionally displays a sample of the retrieved logs

3. **Clean Up**
   - Prompts the user to confirm deletion of resources
   - Deletes the CloudFormation stack
   - Waits for stack deletion to complete

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

Before running this workflow, ensure you have:

- An AWS account with proper permissions to use Amazon CloudWatch Logs and AWS CloudFormation
- Python 3.x installed (for log generation script)
- AWS credentials configured

### AWS Services Used

This workflow uses the following AWS services:

- Amazon CloudWatch Logs
- AWS CloudFormation

### Resources

The feature scenario deploys an AWS CloudFormation stack with the required resources:

- CloudWatch Logs Log Group: `/workflows/cloudwatch-logs/large-query`
- CloudWatch Logs Log Stream: `stream1`

### Instructions

After the example compiles, you can run it from the command line. To do so, navigate to the folder that contains the .sln file and run the following command:

```
dotnet run --project Scenarios/CloudWatchLogsScenario.csproj
```

Alternatively, you can run the example from within your IDE.

This starts an interactive scenario that walks you through:

1. Deploying a CloudFormation stack with CloudWatch Logs resources
2. Generating 50,000 sample log entries
3. Performing recursive queries to retrieve all logs
4. Cleaning up resources

## How the Recursive Query Works

The recursive query algorithm uses binary search to retrieve more than the 10,000 result limit:

1. Execute a query with the specified date range
2. If results < limit, return the results
3. If results >= limit:
   - Get the timestamp of the last result
   - Calculate the midpoint between the last result and the end date
   - Recursively query the first half (last result to midpoint)
   - Recursively query the second half (midpoint to end date)
   - Concatenate all results

This approach efficiently retrieves all matching logs by splitting the date range whenever the result limit is reached.

## CloudWatch Logs Actions

The workflow covers the following CloudWatch Logs API actions:

- [`StartQuery`](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_StartQuery.html) - Initiates a CloudWatch Logs Insights query
- [`GetQueryResults`](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetQueryResults.html) - Retrieves results from a query
- [`PutLogEvents`](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutLogEvents.html) - Uploads log events to a log stream

## Additional Resources

* [CloudWatch Logs User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)
* [CloudWatch Logs Insights Query Syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)
* [CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
