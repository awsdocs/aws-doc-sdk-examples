# S3 Directory Buckets code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon S3 Directory Buckets.

<!--custom.overview.start-->
<!--custom.overview.end-->

_S3 Directory Buckets are designed to store data within a single AWS Zone. Directory buckets organize data hierarchically into directories, providing a structure similar to a file system._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../../../../../../../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello S3 Directory Buckets](HelloS3DirectoryBuckets.java#L4) (`CreateBucket`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AbortMultipartUpload](AbortDirectoryBucketMultipartUploads.java#L6)
- [CompleteMultipartUpload](CompleteDirectoryBucketMultipartUpload.java#L5)
- [CopyObject](CopyDirectoryBucketObject.java#L6)
- [CreateBucket](CreateDirectoryBucket.java#L6)
- [CreateMultipartUpload](CreateDirectoryBucketMultipartUpload.java#L6)
- [DeleteBucket](DeleteDirectoryBucket.java#L6)
- [DeleteBucketEncryption](DeleteDirectoryBucketEncryption.java#L6)
- [DeleteBucketPolicy](DeleteDirectoryBucketPolicy.java#L6)
- [DeleteObject](DeleteDirectoryBucketObject.java#L6)
- [DeleteObjects](DeleteDirectoryBucketObjects.java#L6)
- [GetBucketEncryption](GetDirectoryBucketEncryption.java#L6)
- [GetBucketPolicy](GetDirectoryBucketPolicy.java#L51)
- [GetObject](GetDirectoryBucketObject.java#L6)
- [GetObjectAttributes](GetDirectoryBucketObjectAttributes.java#L6)
- [HeadBucket](HeadDirectoryBucket.java#L6)
- [HeadObject](HeadDirectoryBucketObject.java#L6)
- [ListDirectoryBuckets](ListDirectoryBuckets.java#L6)
- [ListMultipartUploads](ListDirectoryBucketMultipartUpload.java#L6)
- [ListObjectsV2](ListDirectoryBucketObjectsV2.java#L6)
- [ListParts](ListDirectoryBucketParts.java#L6)
- [PutBucketEncryption](PutDirectoryBucketEncryption.java#L6)
- [PutBucketPolicy](PutDirectoryBucketPolicy.java#L49)
- [PutObject](PutDirectoryBucketObject.java#L6)
- [UploadPart](UploadPartForDirectoryBucket.java#L6)
- [UploadPartCopy](UploadPartCopyForDirectoryBucket.java#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL to get an object](GeneratePresignedGetURLForDirectoryBucket.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello S3 Directory Buckets

This example shows you how to get started using S3 Directory Buckets.



#### Create a presigned URL to get an object

This example shows you how to create a presigned URL for S3 directory buckets and get an object.


<!--custom.scenario_prereqs.s3-directory-buckets_GeneratePresignedGetURLForDirectoryBucket.start-->
<!--custom.scenario_prereqs.s3-directory-buckets_GeneratePresignedGetURLForDirectoryBucket.end-->


<!--custom.scenarios.s3-directory-buckets_GeneratePresignedGetURLForDirectoryBucket.start-->
<!--custom.scenarios.s3-directory-buckets_GeneratePresignedGetURLForDirectoryBucket.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../../../../../../../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [S3 Directory Buckets User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/directory-buckets-overview.html)
- [S3 Directory Buckets API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Java 2.x S3 Directory Buckets reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3-directory-buckets/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
