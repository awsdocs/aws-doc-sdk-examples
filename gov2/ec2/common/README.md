# AWS SDK for Go V2 code examples for Amazon Elastic Compute Cloud (Amazon EC2).

## Purpose

These examples demonstrates how to perform several Amazon EC2 operations
using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateImagev2.go

This example creates an Amazon EC2 image.

`go run CreateImagev2.go -d DESCRIPTION -i IMAGE-ID -n IMAGE-NAME`

- _DESCRIPTION_ is the description of the image.
- _IMAGE-ID_ is the ID of the instance.
- _IMAGE-NAME_ is the name of the image.

The unit test accepts similar values in _config.json_.

### CreateInstancev2.go

This example creates a T2-Micro instance from the Amazon EC2 image ami-e7527ed7 and attaches a tag to the instance.

`go run CreateInstancev2.go -n TAG-NAME -v TAG-VALUE`

- _TAG-NAME_ is the name of the tag to attach to the instance.
- _TAG-VALUE_ is the value of the tag to attach to the instance.

The unit test accepts similar values in _config.json_.

### DescribeVpcEndpoints/DescribeVpcEndpointsv2.go

This example retrieves information about your VPC endpoint connections.

`go run DescribeVpcEndpointsv2.go [-r REGION]`

- _REGION_ is the region to get the endpoints from.
  This value is **us-west-2** by default.

### DescribeInstancesv2.go

This example retrieves information about your Amazon EC2 instances.

`go run DescribeInstancesv2.go`

### MonitorInstancesv2.go

This example enables or disables monitoring for an Amazon EC2 instance.

`go run MonitorInstancesv2.go -m MODE -i INSTANCE-ID`

- _MODE_ is either "OFF" to disable monitoring or "ON" to enable monitoring.
- _INSTANCE-ID_ is the ID of the instance.

The unit test accepts similar values in _config.json_.

### RebootInstancesv2.go

This example reboots an Amazon EC2 instance.

`go run RebootInstancesv2.go -i INSTANCE-ID`

- _INSTANCE-ID_ is the ID of the instance to reboot.

The unit test accepts a similar value in _config.json_.

### StartInstancesv2.go

This example starts an Amazon EC2 instance.

`go run StartInstancesv2.go -i INSTANCE-ID`

- _INSTANCE-ID_ is the ID of the instance to start.

The unit test accepts a similar value in _config.json_.

### StopInstancesv2.go

This example stops an Amazon EC2 instance.

`go run StopInstancesv2.go -i INSTANCE-ID`

- _INSTANCE-ID_ is the ID of the instance to stop.

The unit test accepts a similar value in _config.json_.

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
