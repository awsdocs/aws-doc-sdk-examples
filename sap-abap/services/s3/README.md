# Amazon S3 code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Simple Storage Service (Amazon S3).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](%23awsex%23cl_s3_scenarios.clas.abap)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](%23awsex%23cl_s3_actions.clas.abap#L194)
- [CreateBucket](%23awsex%23cl_s3_actions.clas.abap#L217)
- [DeleteBucket](%23awsex%23cl_s3_actions.clas.abap#L250)
- [DeleteBucketCors](%23awsex%23cl_s3_actions.clas.abap#L475)
- [DeleteBucketLifecycle](%23awsex%23cl_s3_actions.clas.abap#L590)
- [DeleteBucketPolicy](%23awsex%23cl_s3_actions.clas.abap#L533)
- [DeleteObject](%23awsex%23cl_s3_actions.clas.abap#L270)
- [DeleteObjects](%23awsex%23cl_s3_actions.clas.abap#L378)
- [GetBucketAcl](%23awsex%23cl_s3_actions.clas.abap#L397)
- [GetBucketCors](%23awsex%23cl_s3_actions.clas.abap#L436)
- [GetBucketLifecycleConfiguration](%23awsex%23cl_s3_actions.clas.abap#L551)
- [GetBucketPolicy](%23awsex%23cl_s3_actions.clas.abap#L493)
- [GetObject](%23awsex%23cl_s3_actions.clas.abap#L289)
- [GetObjectAcl](%23awsex%23cl_s3_actions.clas.abap#L608)
- [GetObjectLegalHold](%23awsex%23cl_s3_actions.clas.abap#L653)
- [GetObjectLockConfiguration](%23awsex%23cl_s3_actions.clas.abap#L727)
- [HeadBucket](%23awsex%23cl_s3_actions.clas.abap#L808)
- [ListObjectVersions](%23awsex%23cl_s3_actions.clas.abap#L789)
- [ListObjectsV2](%23awsex%23cl_s3_actions.clas.abap#L330)
- [PutBucketAcl](%23awsex%23cl_s3_actions.clas.abap#L415)
- [PutBucketCors](%23awsex%23cl_s3_actions.clas.abap#L454)
- [PutBucketLifecycleConfiguration](%23awsex%23cl_s3_actions.clas.abap#L569)
- [PutBucketPolicy](%23awsex%23cl_s3_actions.clas.abap#L512)
- [PutBucketVersioning](%23awsex%23cl_s3_actions.clas.abap#L767)
- [PutObject](%23awsex%23cl_s3_actions.clas.abap#L349)
- [PutObjectAcl](%23awsex%23cl_s3_actions.clas.abap#L629)
- [PutObjectLegalHold](%23awsex%23cl_s3_actions.clas.abap#L674)
- [PutObjectLockConfiguration](%23awsex%23cl_s3_actions.clas.abap#L745)
- [PutObjectRetention](%23awsex%23cl_s3_actions.clas.abap#L699)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](%23awsex%23cl_s3_scenarios.clas.abap)


<!--custom.examples.start-->
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

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for SAP ABAP Amazon S3 reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/s3/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
