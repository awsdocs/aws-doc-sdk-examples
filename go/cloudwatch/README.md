# AWS SDK for Go Code Examples for Amazon CloudWatch

## Purpose

These examples demonstrate how to perform the following tasks in your default AWS Region
using your default credentials:

- Get a list of your alarms
- Enable an alarm
- Disable an alarm

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

Each operation is in a sub-folder.

Most unit tests for the operations require that you fill in some values
in *config.json".

### CreateCustomMetric

This operation creates a new metric in a namespace.

`go run CreateCustomMetric.go -n NAMESPACE -m METRIC-NAME -u UNITS -v VALUE -dn DIMENSION-NAME -dv DIMENSION-VALUE`

where all of the values are required:

- NAMESPACE is the namespace
- METRIC-NAME is the name of the metric
- UNITS are the units for the metric
- VALUE is the value of the units
- DIMENSION-NAME is the name of the dimension
- DIMENSION-VALUE is the value of the dimension

The unit test requires these values in *config.json*.

### DescribeAlarms

This operation lists your Amazon CloudWatch alarms.

`go run DescribeAlarms.go`

### DisableAlarm

This operation disables an alarm.

`go run DisableAlarm -a ALARM-NAME`

where **ALARM-NAME** is the required name of the alarm to disable.

The unit test requires this value in *config.json*.

### EnableAlarm

This operation creates an alarm when the CPU utilization of an EC2 instance goes above 70%,
triggering a reboot of the instance.

`go run EnableAlarm -n INSTANCE-NAME -i INSTANCE-ID -a ALARM-NAME`

where all of these values are required:

- INSTANCE-NAME is the name of your EC2 instance
- INSTANCE-ID is the ID of your EC2 instance
- ALARM-NAME is the name of the alarm

The unit test requires these values in *config.json*.

### ListMetrics

This operation lists up to 500 of your metrics.

`go run ListMetrics.go`

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum  permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the Unit Tests

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account.

To run the unit tests, navigate to a sub-folder and enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 65.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

You can confirm it has deleted any resources it created by running:

```go run describe_alarms.got | | grep ???-```
