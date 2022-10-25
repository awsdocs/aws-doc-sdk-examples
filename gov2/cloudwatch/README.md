# Amazon CloudWatch examples for the AWS SDK for Go (V2)

## Overview

These examples in this directory demonstrate how to perform Amazon CloudWatch
operations using version 2 of the AWS SDK for Go.

Amazon CloudWatch is a monitoring and observability service built for DevOps
engineers, developers, site reliability engineers (SREs), IT managers, and
product owners. CloudWatch provides you with data and actionable insights to
monitor your applications, respond to system-wide performance changes, and
optimize resource utilization.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a custom metric](CreateCustomMetric/) (`CreateCustomMetric`)
- [Create an alarm that watches a metric](CreateEnableMetricAlarm/) (`CreateEnableMetricAlarm`)
- [Describe alarms for a metric](DescribeAlarms/) (`DescribeAlarms`)
- [Disable alarm actions](DisableAlarm/) (`DisableAlarm`)
- [Get logged events](GetLogEvents/) (`GetLogEvents`)
- [Get metric statistics](GetMetricData/) (`GetMetricData`)
- [List metrics](ListMetrics/) (`ListMetrics`)
- [Put an event](PutEvent/) (`PutEvent`)

## Run the examples

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

### Instructions

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

## Additional resources

- [AWS IAM documentation](https://docs.aws.amazon.com/cloudwatch)
- [AWS SDK for Go V2 Amazon CloudWatch service reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/cloudwatch)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
