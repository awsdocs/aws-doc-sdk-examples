# AWS SDK for Go code examples for Amazon S3 Glacier

## Purpose

These examples demonstrate how to perform several Amazon Simple Storage Service Glacier (Amazon S3 Glacier) operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the *AWS SDK for Go Developer Guide*.

## Running the code

### CreateVault/CreateVault.go

This example creates an Amazon S3 Glacier vault.

`go run CreateVault -v VAULT-NAME`

- _VAULT-NAME_ is the name of the vault to create.

The unit test mocks the service client and the `CreateVault` function.

### UploadArchive/UploadArchive.go

This example uploads a file to an Amazon S3 Glacier vault.

`go run UploadArchive -v VAULT-NAME -f FILE-NAME`

- _VAULT-NAME_ is the name of the vault to which the file is uploaded.
- _FILE-NAME_ is the name of the file to upload.

The unit test mocks the service client and the `UploadArchive` function.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
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
