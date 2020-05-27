# AWS SDK for Go code examples for AWS Lambda

## Purpose

These examples demonstrate how to perform several Lambda operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the _AWS SDK for Go Developer Guide_.

## Running the code

### MakeFunction/MakeFunction.go

This example creates a Lambda function.

`go run MakeFunction.go -z ZIPFILE -b BUCKET -f FUNCTION -h HANDLER -a ROLE-ARN -r RUNTIME`

- _ZIPFILE_ is the name of the zip file, without the .zip extension, containing the Lambda function code.
- _BUCKET_ is the name of an Amazon S3 bucket in the same region as the Lambda function.
- _FUNCTION_ is the name of the Lambda function.
- _HANDLER_ is the name of the method within your code that Lambda calls.
- _ROLE-ARN_ is the ARN of the function's execution role.
- _RUNTIME_ is the identifier of the function's runtime.

The unit test mocks the service client and the `CreateFunction` function.

### ShowFunctions/ShowFunctions.go

This example displays a list of your Lambda functions.

`go run ShowFunctions.go`

The unit test mocks the service client and the `ListFunctions` function.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the _AWS Identity and Access Management User Guide_.
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

To see any log messages, enter the following.

`go test -test.v`

You should see additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
