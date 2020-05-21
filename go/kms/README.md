# AWS SDK for Go code examples for AWS Key Management Service (AWS KMS)

## Purpose

These examples demonstrate how to perform several AWS KMS operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateCMK/CreateCMK.go

This example creates an AWS KMS customer master key (CMK).

`go run CreateCMK.go -k KEY-NAME -v KEY-VALUE`

- _KEY-NAME_ is the name of the key.
- _KEY-VALUE_ is the value of the key.

The unit test mocks the service client and the `CreateKey` function.

### DecryptData/DecryptData.go

This example decrypts some text that was encrypted with an AWS Key Management Service (AWS KMS) customer master key (CMK).

`go run DecryptData.go -d DATA`

- _DATA_ is the encrypted data, as a string.

The unit test mocks the service client and the `Decrypt` function.

### EncryptData/EncryptData.go

This example encrypts some text using an AWS KMS customer master key (CMK).

`go run EncryptData.go -k KEY-ID -t "text"`

- _KEY-ID_ is the ID of a CMK.
- _text_ is the text to encrypt.

The unit test mocks the service client and the `Encrypt` function.

### ReEncryptData/ReEncryptData.go

This example reencrypts some text using a new AWS KMS customer master key (CMK).

`go run ReEncryptData.go -k KEY-ID -d DATA`

- _KEY-ID_ is the ID of a CMK.
- _DATA_ is the data to reencrypt, as a string.

The unit test mocks the service client and the `ReEncrypt` function.

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
