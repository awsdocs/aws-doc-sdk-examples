# CloudWatch Logs code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with Amazon CloudWatch Logs.

<!--custom.overview.start-->
<!--custom.overview.end-->

_CloudWatch Logs monitor, store, and access your log files from Amazon Elastic Compute Cloud instances, AWS CloudTrail, or other sources._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [GetQueryResults](LargeQuery/Actions/CloudWatchLogsWrapper.cs#L79)
- [StartQuery](LargeQuery/Actions/CloudWatchLogsWrapper.cs#L30)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Run a large query](LargeQuery/Scenarios/LargeQueryWorkflow.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Run a large query

This example shows you how to use CloudWatch Logs to query more than 10,000 records.


<!--custom.scenario_prereqs.cloudwatch-logs_Scenario_BigQuery.start-->
<!--custom.scenario_prereqs.cloudwatch-logs_Scenario_BigQuery.end-->


<!--custom.scenarios.cloudwatch-logs_Scenario_BigQuery.start-->
<!--custom.scenarios.cloudwatch-logs_Scenario_BigQuery.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch Logs User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)
- [CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html)
- [SDK for .NET (v4) CloudWatch Logs reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/Cloudwatch-logs/NCloudwatch-logs.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
