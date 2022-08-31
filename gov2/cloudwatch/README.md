# Amazon CloudWatch examples for the AWS SDK for Go (V2)

## Purpose

These examples in this directory demonstrate how to perform Amazon CloudWatch
operations using version 2 of the AWS SDK for Go.

Amazon CloudWatch is a monitoring and observability service built for DevOps
engineers, developers, site reliability engineers (SREs), IT managers, and
product owners. CloudWatch provides you with data and actionable insights to
monitor your applications, respond to system-wide performance changes, and
optimize resource utilization.

## ⚠️ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website.
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Create a custom metric](CreateCustomMetric/) (`CreateCustomMetric`)
- [Create an alarm that watches a metric](CreateEnableMetricAlarm/) (`CreateEnableMetricAlarm`)
- [Describe alarms for a metric](DescribeAlarms/) (`DescribeAlarms`)
- [Disable alarm actions](DisableAlarm/) (`DisableAlarm`)
- [Get logged events](GetLogEvents/) (`GetLogEvents`)
- [Get metric statistics](GetMetricData/) (`GetMetricData`)
- [List metrics](ListMetrics/) (`ListMetrics`)
- [Put an event](PutEvent/) (`PutEvent`)

### Run the examples

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```
### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

### Tests

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

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

- [AWS SDK for Go V3 Amazon IAM service reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3)
- [AWS IAM documentation](https://docs.aws.amazon.com/iam)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
