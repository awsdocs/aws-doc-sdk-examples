# CloudWatch Logs large query

## Overview

This example shows how to use AWS SDKs to perform a query on CloudWatch logs and get more than the maximum number of 10,000 logs back.

The CloudWatch Logs API is capped at 10,000 records for requests that [read](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetLogEvents.html) or [write](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutLogEvents.html). GetLogEvents returns tokens for pagination, but [GetQueryResults](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetQueryResults.html) does not. This example breaks down one query into multiple queries if more than the maximum number of records are returned from the query.

The following components are used in this example:

- [Amazon CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html) hosts the logs that are queried using the [Amazon CloudWatch Logs API](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html).

## ⚠ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

For general prerequisites, see the [README](../../../../README.md) in the `javascriptv3` folder.

To run this example, you need a CloudWatch log group that contains over 10,000 logs. You can [create one yourself](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/Working-with-log-groups-and-streams.html), or you can follow the steps in the [Infrastructure and data](#infrastructure-and-data) section. These steps require you to [install or update the latest version of the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

### Infrastructure and data

Use the following steps to create the necessary resources in AWS CloudFormation and use the AWS CLI to upload the necessary logs.

1. In your local terminal, change directories to [resources](../../../../../scenarios/features/cloudwatch_logs_large_query/resources/).
1. Run `aws cloudformation deploy --template-file stack.yaml --stack-name CloudWatchLargeQuery`
1. Run `./make-log-files.sh`. This will output two timestamps for use in the following step.
1. Copy and run the `export QUERY_START_DATE=<...>` and `export QUERY_END_DATE=<...>` from the `make-log-files.sh` output.
3. Run `./put-log-events.sh`.
4. Wait five minutes for logs to settle and to make sure you're not querying for logs that exist in the future.

### Run the scenario

1. `node index.js`

## Additional reading

- [CloudWatch Logs Insights query syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)
- [CloudWatch Logs billing and cost](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/LogsBillingDetails.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
