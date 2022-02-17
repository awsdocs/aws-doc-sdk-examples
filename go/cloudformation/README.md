# AWS SDK for Go code examples for AWS CloudFormation

## Purpose

These examples demonstrate how to perform several AWS CloudFormation operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateStack/CreateStack.go

This example creates an AWS CloudFormation stack.

`go run CreateStack -s STACK-NAME -t TEMPLATE-FILE`

- _STACK-NAME_ is the name of the stack to create.
- _TEMPLATE-FILE_ is the name of the file containing the AWS CloudFormation template.

The unit test mocks the service client and the `CreateStack` function.

### DeleteStack/DeleteStack.go

This example deletes an AWS CloudFormation stack.

`go run DeleteStack -s STACK-NAME`

- _STACK-NAME_ is the name of the stack to create.

The unit test mocks the service client and the `DeleteStack` function.

### ListStacks/ListStacks.go

This example lists your AWS CloudFormation stacks.

`go run ListStacks -s STATUS`

- _STATUS_ is the status of the stacks to list.
  By default it lists all stacks.

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
