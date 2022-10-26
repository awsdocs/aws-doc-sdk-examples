# AWS KMS code examples the AWS SDK for Go (v2) 

## Overview

These examples demonstrate how to perform AWS Key Management Service (AWS KMS) 
actions using the AWS SDK for Go (v2).


## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a key](CreateKey/CreateKeyv2.go) (`CreateKey`)
- [Decrypt cyphertext](DecryptData/DecryptDatav2.go) (`DecryptData`)
- [Encrypt text using a key](EncryptData/EncryptDatav2.go) (`Encrypt`)
- [Reencrypt ciphertext from one key to another](ReEncryptData/ReEncryptDatav2.go) (`Reencrypt`)

## Run the examples

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

### Instructions

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

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

## Additional resources

- [AWS KMS developer guide](https://docs.aws.amazon.com/kms/latest/developerguide/overview.html)
- [AWS KMS API reference](https://docs.aws.amazon.com/kms/latest/APIReference/Welcome.html)
- [AWS SDK for Go (v2) API reference](https://docs.aws.amazon.com/sdk-for-go/api/service/kms/)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
