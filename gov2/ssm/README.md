# AWS Systems Manager code examples for AWS SDK for Go (v2)

## Purpose

These examples in this directory demonstrates how to perform several AWS
Systems Manager  (Systems Manager) operations using the AWS SDK for Go (v2).

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### DeleteParameter/DeleteParameterv2.go

This example deletes a Systems Manager string parameter.

`go run DeleteParameterv2.go -n NAME`

- _NAME_ is the name of the parameter to delete.

The unit test accepts a similar value in _config.json_.

### GetParameter/GetParameterv2.go

This example retrieves a Systems Manager string parameter.

`go run GetParameterv2.go -n NAME`

- _NAME_ is the name of the parameter to retrieve.

### PutParameter/PutParameterv2.go

This example creates a Systems Manager string parameter.

`go run PutParameterv2.go -n NAME -v VALUE`

- _NAME_ is the name of the parameter to create.
- _VALUE_ is the value of the parameter to create.

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
