# CloudWatch Logs large query

## Overview

This example shows how to use AWS SDKs to perform a query on Amazon CloudWatch Logs and get more than the maximum number of 10,000 logs back.

The CloudWatch Logs API is capped at 10,000 records for requests that [read](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_GetLogEvents.html) or [write](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutLogEvents.html). This example breaks down one query into multiple queries if more than the maximum number of records are returned from the query.

The following components are used in this example:

- [Amazon CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html) hosts the logs that are queried using the [Amazon CloudWatch Logs API](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html).

## Supporting infrastructure

An AWS CloudFormation [template](./resources/stack.yaml) exists in the [resources folder](./resources/). The template contains two resources: a CloudWatch log group, and a CloudWatch log stream that will be used as the default log group and stream for the queries made in the example. We recommend deploying a stack with this template.

### Prerequisites

If you need to, [install or update the latest version of the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html).

### Deploy resources

To deploy the stack using the template, run the following command:

```
aws cloudformation deploy --template-file stack.yaml --stack-name LargeQueryStack
```

### Destroy resources

To destroy the stack, run the following command:

```
aws cloudformation delete-stack --stack-name LargeQueryStack
```

## Sample logs

A lot of logs are needed to make a robust example. If you happen to have a log group with over 10,000 logs at the ready, great! If not, there are two resources that can help:

### Resources

1. [make-log-files.sh](./resources/make-log-files.sh) will create 50,000 logs and divide them among 5 files of 10,000 logs each (the maximum for each call to 'PutLogEvents'). Two timestamps will output to the console. These timestamps can be used to configure the query. **Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.**
2. [put-log-events.sh](./resources/put-log-events.sh) will use the AWS CLI to put the created files from Step 1 into the log group/stream created by the [CloudFormation template](#cloudformation).

## Implementations

This example is implemented in the following languages:

- [JavaScript](../../../javascriptv3/example_code/cloudwatch-logs/scenarios/large-query/README.md)
- [Python](../../../python/example_code/cloudwatch-logs/scenarios/large-query/README.md)

## Additional reading

- [CloudWatch Logs Insights query syntax](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
