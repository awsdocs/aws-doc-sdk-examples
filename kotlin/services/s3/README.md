# Amazon S3 code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Simple Storage Service (Amazon S3).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/s3/S3Operations.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](src/main/kotlin/com/kotlin/s3/CopyObject.kt#L45)
- [CreateBucket](src/main/kotlin/com/kotlin/s3/CreateBucket.kt#L36)
- [CreateMultiRegionAccessPoint](src/main/kotlin/com/kotlin/s3/MrapExample.kt#L216)
- [DeleteBucketPolicy](src/main/kotlin/com/kotlin/s3/DeleteBucketPolicy.kt#L38)
- [DeleteObjects](src/main/kotlin/com/kotlin/s3/DeleteObjects.kt#L41)
- [GetBucketPolicy](src/main/kotlin/com/kotlin/s3/GetBucketPolicy.kt#L39)
- [GetObject](src/main/kotlin/com/kotlin/s3/GetObjectData.kt#L43)
- [GetObjectAcl](src/main/kotlin/com/kotlin/s3/GetAcl.kt#L39)
- [ListObjectsV2](src/main/kotlin/com/kotlin/s3/ListObjects.kt#L37)
- [PutBucketAcl](src/main/kotlin/com/kotlin/s3/SetAcl.kt#L45)
- [PutObject](src/main/kotlin/com/kotlin/s3/PutObject.kt#L43)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](src/main/kotlin/com/kotlin/s3/PresigningExamples.kt)
- [Get an object from a Multi-Region Access Point](src/main/kotlin/com/kotlin/s3/MrapExample.kt)


<!--custom.examples.start-->

### Custom Examples

- **DeleteBucket** - Demonstrates how to delete an Amazon S3 bucket.
- **SetBucketPolicy** - Demonstrates how to add a bucket policy to an existing Amazon S3 bucket.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


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

#### Get an object from a Multi-Region Access Point

This example shows you how to get an object from a Multi-Region Access Point.


<!--custom.scenario_prereqs.s3_GetObject_MRAP.start-->
<!--custom.scenario_prereqs.s3_GetObject_MRAP.end-->


<!--custom.scenarios.s3_GetObject_MRAP.start-->
<!--custom.scenarios.s3_GetObject_MRAP.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Kotlin Amazon S3 reference](https://sdk.amazonaws.com/kotlin/api/latest/s3/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0