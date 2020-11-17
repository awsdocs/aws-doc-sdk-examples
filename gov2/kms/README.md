# AWS SDK for Go V2 code examples for AWS KMS 

## Purpose

These examples demonstrate how to perform several AWS Key Management Service (AWS KMS) 
operations using version 2 of the AWS SDK for Go.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CreateKey/CreateKeyv2.go

This example creates an AWS KMS customer master key (CMK).

`go run CreateKeyv2.go -k KEY -v VALUE`

- _KEY_ is the name of the key to create.

The unit test accepts similar values in _config.json_.

### DecryptData/DecryptDatav2.go

This example decrypts some text that was encrypted with an AWS KMS customer master key (CMK).

`go run DecryptDatav2.go -d DATA`

- _DATA_ is the encrypted data, as a string.

The unit test accepts a similar value in _config.json_.

### EncryptData/EncryptDatav2.go

This example encrypts some text using an AWS KMS customer master key (CMK).

`go run EncryptDatav2.go -k KEYID -t TEXT`

- _KEYID_ is the ID for the AWS KMS key to use for encrypting the text.
- _TEXT_ is the text to encrypt.

The unit test accepts similar values in _config.json_.

### ReEncryptData/ReEncryptDatav2.go

This example reencrypts some text using an AWS KMS customer master key (CMK).

`go run ReEncryptDatav2.go -k KeyID -d DATA`

- _KeyID_ is the ID of the AWS KMS key to use for reencrypting the data.
- _DATA_ is the data to reencrypt, as a string.

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
