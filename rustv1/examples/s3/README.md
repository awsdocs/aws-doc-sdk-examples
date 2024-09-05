# Amazon S3 code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon S3](src/bin/s3-helloworld.rs#L35) (`ListBuckets`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/bin/s3-getting-started.rs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CompleteMultipartUpload](src/bin/s3-multipart-upload.rs#L139)
- [CopyObject](src/lib.rs#L7)
- [CreateBucket](src/lib.rs#L192)
- [CreateMultipartUpload](src/bin/s3-multipart-upload.rs#L48)
- [DeleteBucket](src/lib.rs#L224)
- [DeleteObject](src/lib.rs#L37)
- [DeleteObjects](src/lib.rs#L156)
- [GetBucketLocation](src/bin/list-buckets.rs#L27)
- [GetObject](src/bin/get-object.rs#L21)
- [ListBuckets](src/bin/list-buckets.rs#L27)
- [ListObjectVersions](src/bin/list-object-versions.rs#L26)
- [ListObjectsV2](src/lib.rs#L92)
- [PutObject](src/lib.rs#L73)
- [UploadPart](src/bin/s3-multipart-upload.rs#L101)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](src/bin/get-object-presigned.rs)
- [Get an object from a bucket if it has been modified](src/bin/if-modified-since.rs)
- [Unit and integration test with an SDK](Cargo.toml)
- [Upload or download large files](src/bin/s3-multipart-upload.rs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.


#### Learn the basics

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.basic_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.basics.s3_Scenario_GettingStarted.start-->
<!--custom.basics.s3_Scenario_GettingStarted.end-->


#### Create a presigned URL

This example shows you how to create a presigned URL for Amazon S3 and upload an object.


<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.start-->
<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.end-->


<!--custom.scenarios.s3_Scenario_PresignedUrl.start-->
<!--custom.scenarios.s3_Scenario_PresignedUrl.end-->

#### Get an object from a bucket if it has been modified

This example shows you how to read data from an object in an S3 bucket, but only if that bucket has not been modified since the last retrieval time.


<!--custom.scenario_prereqs.s3_GetObject_IfModifiedSince.start-->
<!--custom.scenario_prereqs.s3_GetObject_IfModifiedSince.end-->


<!--custom.scenarios.s3_GetObject_IfModifiedSince.start-->
<!--custom.scenarios.s3_GetObject_IfModifiedSince.end-->

#### Unit and integration test with an SDK

This example shows you how to examples for best-practice techniques when writing unit and integration tests using an AWS SDK.


<!--custom.scenario_prereqs.cross_Testing.start-->
<!--custom.scenario_prereqs.cross_Testing.end-->


<!--custom.scenarios.cross_Testing.start-->
<!--custom.scenarios.cross_Testing.end-->

#### Upload or download large files

This example shows you how to upload or download large files to and from Amazon S3.


<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.end-->


<!--custom.scenarios.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenarios.s3_Scenario_UsingLargeFiles.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Rust Amazon S3 reference](https://docs.rs/aws-sdk-s3/latest/aws_sdk_s3/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0