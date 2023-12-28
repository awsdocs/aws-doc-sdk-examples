# Amazon S3 code examples for the SDK for Java 1.x

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for Java 1.x to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `java` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon S3](None) (`ListBuckets`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Add CORS rules to a bucket](None) (`PutBucketCors`)
- [Add a lifecycle configuration to a bucket](None) (`PutBucketLifecycleConfiguration`)
- [Add a policy to a bucket](None) (`PutBucketPolicy`)
- [Copy an object from one bucket to another](None) (`CopyObject`)
- [Create a bucket](None) (`CreateBucket`)
- [Delete a policy from a bucket](None) (`DeleteBucketPolicy`)
- [Delete an empty bucket](None) (`DeleteBucket`)
- [Delete multiple objects](None) (`DeleteObjects`)
- [Delete the website configuration from a bucket](None) (`DeleteBucketWebsite`)
- [Determine the existence and content type of an object](None) (`HeadObject`)
- [Download objects to a local directory](None) (`DownloadDirectory`)
- [Enable notifications](None) (`PutBucketNotificationConfiguration`)
- [Get an object from a bucket](None) (`GetObject`)
- [Get the ACL of a bucket](None) (`GetBucketAcl`)
- [Get the policy for a bucket](None) (`GetBucketPolicy`)
- [List in-progress multipart uploads](None) (`ListMultipartUploads`)
- [List objects in a bucket](None) (`ListObjectsV2`)
- [Restore an archived copy of an object](None) (`RestoreObject`)
- [Set a new ACL for a bucket](None) (`PutBucketAcl`)
- [Set the website configuration for a bucket](None) (`PutBucketWebsite`)
- [Upload an object to a bucket](None) (`PutObject`)
- [Upload directory to a bucket](None) (`UploadDirectory`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `java` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Java 1.x Amazon S3 reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0