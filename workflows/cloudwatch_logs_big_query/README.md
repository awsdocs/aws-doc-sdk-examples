# CloudWatch Logs big query

## Overview

This example shows how to use AWS SDKs to perform a query on CloudWatch logs and get more than the maximum amount of 10,000 logs back.

The CloudWatch Logs API is capped at 10,000 records for requests that [read](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetLogEvents.html) or [write](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutLogEvents.html). GetLogEvents returns tokens for pagination, but [GetQueryResults](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetQueryResults.html) does not. This example breaks down one query into multiple queries if more than the maximum amount of records is returned from the query.

The following components are used in this example:

- [Amazon CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html) hosts the logs that are queried using the [Amazon CloudWatch Logs API](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html).

### CloudFormation

An AWS CloudFormation [template](./resources/stack.yaml) exists in the [resources folder](./resources/). The template contains two resources: a CloudWatch log group, and a CloudWatch log stream that will be used as the default log group and stream for the queries made in the example.

### Sample logs

A lot of logs are needed to make a robust example. If you happen to have a log group with over 10,000 logs at the ready, great! If not, there are two resources that can help:

**Prerequisites**

- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

**Resources**

1. [make-log-files](./resources/make-log-files.sh) will create 50,000 logs and divide them amongst 5 files of 10,000 logs each (the maximum for each call to 'PutLogEvents'). Two timestamps will be output to the console. These timestamps can be used to configure the query. **Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.**
2. [put-log-events](./resources/put-log-events.sh) will use the AWS CLI to put the created files from Step 1 into the log group/stream created by the [CloudFormation template](#cloudformation).

## Implementations

This example is implemented in the following languages:

- [JavaScript](../../javascriptv3/example_code/cloudwatch-logs/scenarios/big-query/README.md)

## Additional reading

- [CloudWatch Logs Insights query syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
