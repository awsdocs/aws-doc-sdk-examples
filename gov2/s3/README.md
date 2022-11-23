# Amazon S3 code examples for the SDK for Go
## Overview

Shows how to use the AWS SDK for Go V2 to get started with bucket and
object actions in Amazon Simple Storage Service (Amazon S3).

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any
amount of data at any time, from anywhere on the web.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Amazon S3](hello/hello.go)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Copy an object from one bucket to another](actions/bucket_basics.go)
  (`CopyObject`)
* [Create a bucket](actions/bucket_basics.go)
  (`CreateBucket`)
* [Delete an empty bucket](actions/bucket_basics.go)
  (`DeleteBucket`)
* [Delete multiple objects](actions/bucket_basics.go)
  (`DeleteObjects`)
* [Determine the existence of a bucket](actions/bucket_basics.go)
  (`HeadBucket`)
* [Get an object from a bucket](actions/bucket_basics.go)
  (`GetObject`)
* [List buckets](actions/bucket_basics.go)
  (`ListBuckets`)
* [List objects in a bucket](actions/bucket_basics.go)
  (`ListObjects`)
* [Upload an object to a bucket](actions/bucket_basics.go)
  (`PutObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Create a presigned URL](scenarios/scenario_presigning.go)
* [Get started with buckets and objects](scenarios/scenario_get_started.go)
* [Upload or download large files](actions/bucket_basics.go)

## Run the examples

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../README.md#Prerequisites) in the GoV2 folder.

### Instructions

#### Hello Amazon S3

Get started using the SDK for Go with Amazon S3 by listing buckets in your account.

```
go run ./hello
```

#### Get started with buckets and objects

Interactively shows how to create a bucket and upload and download objects.

```
go run ./cmd -scenario getstarted
```

#### Create a presigned URL

Interactively shows how to generate a presigned URL that contains temporary credentials. 
Shows how to use the net/http package to use the presigned requests to upload, download, 
and delete an object.

```
go run ./cmd -scenario presigning
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

Instructions for running the tests for this service can be found in the
[README](../README.md#Tests) in the GoV2 folder.

## Additional resources

* [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
* [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
* [AWS SDK for Go S3 Client](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/s3)
* [AWS SDK for Go S3 manager package](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/feature/s3/manager)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
