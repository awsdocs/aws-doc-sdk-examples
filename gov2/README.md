# Amazon CloudWatch examples for the AWS SDK for Go (v2).

## Overview

The code examples in this directory show you how to use the AWS SDK for Go (v2)
with Amazon CloudWatch.

mazon CloudWatch is a monitoring and observability service built for DevOps
engineers, developers, site reliability engineers (SREs), IT managers, and
product owners. CloudWatch provides you with data and actionable insights to
monitor your applications, respond to system-wide performance changes, and
optimize resource utilization. 

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the 
  minimum permissions required to perform the task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, 
  see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code Examples

### Single Actions

- [Create a custom metric](CreateCustomMetric/) (`CreateCustomMetric`)
- [Create an alarm that watches a metric](CreateEnableMetricAlarm/) (`CreateMetricAlarm`, `EnableAlarmActionsInput`)
- [Describe alarms](DescribeAlarms/) (`DescribeAlarms`)
- [Disable alarm actions](DisableAlarm) (`DisableAlarmActions`)
- [Get metric events](GetLogEvents/) (`GetLogEvents`)
- [Get metric statistics](GetMetricData/) (`GetMetricData`) 
- [List metrics](ListMetrics/) (`ListMetrics`)
- [Send an Amazon CloudWatch event to EventBridge](PutEvent/) (`PurEvents`)

## Run the examples

### Prerequisites

* You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the
  [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
* Go 1.18 or later

## Tests

All tests use go test, and you can find them alongside the code in the folder for each 
example.

### Unit tests

The unit tests in this module use stubbed responses from [AwsmStubber](testtools/awsm_stubber.go).
AwsmStubber is a tool that uses the AWS SDK for Go middleware to intercept calls to
AWS, verify inputs, and return a mocked response. This means that when the unit tests 
are run, requests are not sent to AWS and no charges are incurred on your account.

Run unit tests in the folder for each service or cross-service example at a command
prompt.

```
go test ./...
```

### Integration tests

⚠️ Running the integration tests might result in charges to your AWS account.

The integration tests make actual requests to AWS. This means that when
the integration tests are run, they can create and destroy resources in your account.

Run integration tests in the folder for each single action or cross-service example at a
command prompt by including the `integration` tag.

```
go test -tags=integration ./...
```

## Additional resources
* [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
* [AWS SDK for Go (v2) Developer Guide](https://aws.github.io/aws-sdk-go-v2/docs/)
* [AWS SDK for Go (v2) package](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
