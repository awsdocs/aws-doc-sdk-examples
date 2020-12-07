# AWS SDK for Go V2 code examples for Amazon CloudWatch

## Purpose

These examples demonstrates how to perform several Amazon CloudWatch operations
using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateCustomMetric/CreateCustomMetricv2.go

This example creates a new Amazon CloudWatch metric in a namespace.

`go run CreateCustomMetricv2.go -n NAMESPACE -m METRIC-NAME -s SECONDS -dn DIMENSION-NAME -dv DIMENSION-VALUE`

- _NAMESPACE_ is the namespace for the metric.
- _METRIC-NAME_ is the name of the metric.
- _SECONDS_ is the number of seconds for the metric.
- _DIMENSION-NAME_ is the name of the dimension.
- _DIMENSION-VALUE_ is the value of the dimension.

The unit test accepts similar values in _config.json_.

### CreateEnableMetricAlarm/CreateEnableMetricAlarmv2.go

This example enables the specified Amazon CloudWatch alarm.

`go run CreateEnableMetricAlarmv2.go -n INSTANCE-NAME -i INSTANCE-ID -a ALARM-NAME`

- _INSTANCE-NAME_ is the name of the Amazon Elastic Compute Cloud (Amazon EC2) instance for which the alarm is enabled.
- _INSTANCE-ID_ is the ID of the Amazon EC2 instance for which the alarm is enabled.
- _ALARM-NAME_ is the name of the alarm.

The unit test accepts similar values in _config.json_.

### DescribeAlarms/DescribeAlarmsv2.go

This example displays a list of your Amazon CloudWatch alarms.

`go run DescribeAlarmsv2.go`

### DisableAlarm/DisableAlarmv2.go

This example disables an Amazon CloudWatch alarm.

`go run DisableAlarmv2.go -a ALARM-NAME`

- _ALARM-NAME_ is the name of the alarm to disable.

The unit test accepts a similar value in _config.json_.

### ListMetrics/ListMetricsv2.go

This example displays the name, namespace, and dimension name of your Amazon CloudWatch metrics.

`go run ListMetricsv2.go`

### PutEvent/PutEventv2.go

This example sends an Amazon CloudWatch event to Amazon EventBridge.

`go run PutEventv2.go -l LAMBDA-ARN -f EVENT-FILE`

- _LAMBDA-ARN_ is the ARN of the AWS Lambda function of which the event is concerned.
- _EVENT-FILE_ is the local file specifying details of the event to send to Amazon EventBridge.

The unit test accepts similar values in _config.json_.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the unit tests

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

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
