# AWS SDK for Go code examples for Amazon Elastic Compute Cloud (Amazon EC2)

## Purpose

These examples demonstrate how to perform several Amazon EC2 operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateImage/CreateImage.go

This example creates an Amazon EC2 image.

`go run CreateImage -d IMAGE-DESCRIPTION -i INSTANCE-ID -n IMAGE-NAME`

- _IMAGE-DESCRIPTION_ is the description of the image.
- _INSTANCE-ID_ is the ID of the instance.
- _IMAGE-NAME_ is the name of the image.

The unit test mocks the service client and the `CreateImage` function.

### CreateInstance/CreateInstance.go

This example creates an Amazon EC2 instance.

`go run CreateInstance -n NAME -v VALUE`

- _NAME_ is the name of the tag to attach to the instance.
- _VALUE_ is the value of the tag to attach to the instance.

The unit test mocks the service client and the `RunInstances` and `CreateTags` functions.

### DescribeInstances/DescribeInstances.go

This example lists your reservation IDs and instance IDs.

`go run DescribeInstances.go`

### MonitorInstances/MonitorInstances.go

This example enables or disables monitoring for an instance.

`go run MonitorInstances.go -i INSTANCE-ID -m STATE`

- _INSTANCE-ID_ is the ID of the instance to monitor or stop monitoring.
- _STATE_ is **ON** or **OFF**.

The unit test mocks the service client and the `MonitorInstances` and `UnmonitorInstances` functions.

### StartStopInstances/StartStopInstances.go

This example starts or stops an Amazon EC2 instance.

`go run StartStopInstances.go -i INSTANCE-ID -s STATE`

- _INSTANCE-ID_ is the ID of an instance.
- _STATE_ is either **START** or **STOP**.

The unit test mocks the service client and the `StartInstances` and `StopInstances` functions.

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

To run a unit test, enter the following.

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files.

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter the following.

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
