# Amazon S3 code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Simple Storage Service (Amazon S3).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon S3](src/main/java/com/example/s3/HelloS3.java#L6) (`ListBuckets`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](src/main/java/com/example/s3/CopyObject.java#L6)
- [CreateBucket](src/main/java/com/example/s3/CreateBucket.java#L6)
- [DeleteBucket](src/main/java/com/example/s3/S3BucketOps.java#L79)
- [DeleteBucketPolicy](src/main/java/com/example/s3/DeleteBucketPolicy.java#L6)
- [DeleteBucketWebsite](src/main/java/com/example/s3/DeleteWebsiteConfiguration.java#L6)
- [DeleteObjects](src/main/java/com/example/s3/DeleteMultiObjects.java#L6)
- [GetBucketAcl](src/main/java/com/example/s3/GetAcl.java#L6)
- [GetBucketPolicy](src/main/java/com/example/s3/GetBucketPolicy.java#L6)
- [GetObject](src/main/java/com/example/s3/GetObjectData.java#L6)
- [GetObjectLegalHold](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L94)
- [GetObjectLockConfiguration](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L345)
- [GetObjectRetention](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L309)
- [HeadObject](src/main/java/com/example/s3/GetObjectContentType.java#L6)
- [ListBuckets](src/main/java/com/example/s3/ListBuckets.java#L6)
- [ListMultipartUploads](src/main/java/com/example/s3/ListMultipartUploads.java#L6)
- [ListObjectsV2](src/main/java/com/example/s3/ListObjects.java#L6)
- [PutBucketAcl](src/main/java/com/example/s3/SetAcl.java#L6)
- [PutBucketCors](src/main/java/com/example/s3/S3Cors.java#L6)
- [PutBucketLifecycleConfiguration](src/main/java/com/example/s3/LifecycleConfiguration.java#L6)
- [PutBucketPolicy](src/main/java/com/example/s3/SetBucketPolicy.java#L6)
- [PutBucketWebsite](src/main/java/com/example/s3/SetWebsiteConfiguration.java#L6)
- [PutObject](src/main/java/com/example/s3/PutObject.java#L6)
- [PutObjectLegalHold](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L255)
- [PutObjectLockConfiguration](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L206)
- [PutObjectRetention](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L61)
- [RestoreObject](src/main/java/com/example/s3/RestoreObject.java#L6)
- [SelectObjectContent](src/main/java/com/example/s3/async/SelectObjectContentExample.java#L5)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Delete incomplete multipart uploads](src/main/java/com/example/s3/AbortMultipartUploadExamples.java)
- [Download objects to a local directory](src/main/java/com/example/s3/transfermanager/DownloadToDirectory.java)
- [Get started with buckets and objects](src/main/java/com/example/s3/S3Scenario.java)
- [Get the legal hold configuration of an object](src/main/java/com/example/s3/lockscenario/S3LockActions.java)
- [Lock Amazon S3 objects](src/main/java/com/example/s3/lockscenario/S3ObjectLockWorkflow.java)
- [Parse URIs](src/main/java/com/example/s3/ParseUri.java)
- [Perform a multipart upload](src/main/java/com/example/s3/PerformMultiPartUpload.java)
- [Process S3 event notifications](src/main/java/com/example/s3/ProcessS3EventNotification.java)
- [Send event notifications to EventBridge](src/main/java/com/example/s3/PutBucketS3EventNotificationEventBridge.java)
- [Track uploads and downloads](src/main/java/com/example/s3/transfermanager/UploadFile.java)
- [Upload directory to a bucket](src/main/java/com/example/s3/transfermanager/UploadADirectory.java)
- [Upload or download large files](src/main/java/com/example/s3/transfermanager/DownloadToDirectory.java)
- [Upload stream of unknown size](src/main/java/com/example/s3/async/PutObjectFromStreamAsync.java)
- [Use checksums](src/main/java/com/example/s3/BasicOpsWithChecksums.java)


<!--custom.examples.start-->
- [Create a presigned URL for download](src/main/java/com/example/s3/GeneratePresignedGetUrlAndRetrieve.java)
- [Create a presigned URL for upload](src/main/java/com/example/s3/GeneratePresignedUrlAndPutFileWithMetadata.java)
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.



#### Delete incomplete multipart uploads

This example shows you how to how to delete or stop incomplete Amazon S3 multipart uploads.


<!--custom.scenario_prereqs.s3_Scenario_AbortMultipartUpload.start-->
<!--custom.scenario_prereqs.s3_Scenario_AbortMultipartUpload.end-->


<!--custom.scenarios.s3_Scenario_AbortMultipartUpload.start-->
<!--custom.scenarios.s3_Scenario_AbortMultipartUpload.end-->

#### Download objects to a local directory

This example shows you how to download all objects in an Amazon Simple Storage Service (Amazon S3) bucket to a local directory.


<!--custom.scenario_prereqs.s3_DownloadBucketToDirectory.start-->
<!--custom.scenario_prereqs.s3_DownloadBucketToDirectory.end-->


<!--custom.scenarios.s3_DownloadBucketToDirectory.start-->
<!--custom.scenarios.s3_DownloadBucketToDirectory.end-->

#### Get started with buckets and objects

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.scenarios.s3_Scenario_GettingStarted.start-->
<!--custom.scenarios.s3_Scenario_GettingStarted.end-->

#### Get the legal hold configuration of an object

This example shows you how to get the legal hold configuration of an S3 bucket.


<!--custom.scenario_prereqs.s3_GetObjectLegalHoldConfiguration.start-->
<!--custom.scenario_prereqs.s3_GetObjectLegalHoldConfiguration.end-->


<!--custom.scenarios.s3_GetObjectLegalHoldConfiguration.start-->
<!--custom.scenarios.s3_GetObjectLegalHoldConfiguration.end-->

#### Lock Amazon S3 objects

This example shows you how to work with S3 object lock features.


<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.start-->
<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.end-->


<!--custom.scenarios.s3_Scenario_ObjectLock.start-->
<!--custom.scenarios.s3_Scenario_ObjectLock.end-->

#### Parse URIs

This example shows you how to parse Amazon S3 URIs to extract important components like the bucket name and object key.


<!--custom.scenario_prereqs.s3_Scenario_URIParsing.start-->
<!--custom.scenario_prereqs.s3_Scenario_URIParsing.end-->


<!--custom.scenarios.s3_Scenario_URIParsing.start-->
<!--custom.scenarios.s3_Scenario_URIParsing.end-->

#### Perform a multipart upload

This example shows you how to perform a multipart upload to an Amazon S3 object.


<!--custom.scenario_prereqs.s3_Scenario_MultipartUpload.start-->
<!--custom.scenario_prereqs.s3_Scenario_MultipartUpload.end-->


<!--custom.scenarios.s3_Scenario_MultipartUpload.start-->
<!--custom.scenarios.s3_Scenario_MultipartUpload.end-->

#### Process S3 event notifications

This example shows you how to work with S3 event notifications in an object-oriented way.


<!--custom.scenario_prereqs.s3_Scenario_ProcessS3EventNotification.start-->
<!--custom.scenario_prereqs.s3_Scenario_ProcessS3EventNotification.end-->


<!--custom.scenarios.s3_Scenario_ProcessS3EventNotification.start-->
<!--custom.scenarios.s3_Scenario_ProcessS3EventNotification.end-->

#### Send event notifications to EventBridge

This example shows you how to enable a bucket to send S3 event notifications to EventBridge and route notifications to an Amazon SNS topic and Amazon SQS queue.


<!--custom.scenario_prereqs.s3_Scenario_PutBucketNotificationConfiguration.start-->
<!--custom.scenario_prereqs.s3_Scenario_PutBucketNotificationConfiguration.end-->


<!--custom.scenarios.s3_Scenario_PutBucketNotificationConfiguration.start-->
<!--custom.scenarios.s3_Scenario_PutBucketNotificationConfiguration.end-->

#### Track uploads and downloads

This example shows you how to track an Amazon S3 object upload or download.


<!--custom.scenario_prereqs.s3_Scenario_TrackUploadDownload.start-->
<!--custom.scenario_prereqs.s3_Scenario_TrackUploadDownload.end-->


<!--custom.scenarios.s3_Scenario_TrackUploadDownload.start-->
<!--custom.scenarios.s3_Scenario_TrackUploadDownload.end-->

#### Upload directory to a bucket

This example shows you how to upload a local directory recursively to an Amazon Simple Storage Service (Amazon S3) bucket.


<!--custom.scenario_prereqs.s3_UploadDirectoryToBucket.start-->
<!--custom.scenario_prereqs.s3_UploadDirectoryToBucket.end-->


<!--custom.scenarios.s3_UploadDirectoryToBucket.start-->
<!--custom.scenarios.s3_UploadDirectoryToBucket.end-->

#### Upload or download large files

This example shows you how to upload or download large files to and from Amazon S3.


<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.end-->


<!--custom.scenarios.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenarios.s3_Scenario_UsingLargeFiles.end-->

#### Upload stream of unknown size

This example shows you how to upload a stream of unknown size to an Amazon S3 object.


<!--custom.scenario_prereqs.s3_Scenario_UploadStream.start-->
<!--custom.scenario_prereqs.s3_Scenario_UploadStream.end-->


<!--custom.scenarios.s3_Scenario_UploadStream.start-->
<!--custom.scenarios.s3_Scenario_UploadStream.end-->

#### Use checksums

This example shows you how to use checksums to work with an Amazon S3 object.


<!--custom.scenario_prereqs.s3_Scenario_UseChecksums.start-->
<!--custom.scenario_prereqs.s3_Scenario_UseChecksums.end-->


<!--custom.scenarios.s3_Scenario_UseChecksums.start-->
<!--custom.scenarios.s3_Scenario_UseChecksums.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Java 2.x Amazon S3 reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0