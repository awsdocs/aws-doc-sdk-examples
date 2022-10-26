# Amazon S3 code examples for the AWS SDK for Go (v2)

## Overview

These examples in this directory demonstrate how to perform several Amazon S3
actions using AWS SDK for Go (v2).

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single Actions

Code excerpts that show you how to call individual service functions.

- [Copy an object from one bucket to another](common/main.go) (`CopyObject`)
- [Create a presign client](common/main.go) (`NewPresignClient`)
- [Delete a bucket](common/main.go) (`DeleteBucket`)
- [Delete an object](common/main.go) (`DeleteObject`)
- [Get an object from a bucket](common/main.go) (`GetObject`)
- [Get the ACL of a bucket](GetBucketAcl/GetBucketAclv2.go) (`GetBucketAclInput`)
- [Gat the ACL of an object](GetBucketAcl/GetBucketAclv2.go) (`GetObjectAcl`)
- [List buckets](common/main.go) (`ListBuckets`)
- [List objects in a bucket](common/main.go) (`ListObjectsV2`)
- [Upload an object to a bucket](common/main.go) (`PubObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with buckets and objects](common/main.go)

## Run the examples

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the *AWS SDK for Go Developer Guide*.

You must have Go 1.17 or later installed.

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

From a directory containing `go.mod`, use `go test` to run all unit tests:

```
go test ./...
```

This tests all modules in the current folder and any submodules.

## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/amazonglacier/amazons3/dev/introduction.html)
- [Amazon S3 API reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [AWS SDK for Go (v2) API reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
