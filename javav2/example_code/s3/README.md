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

- [Add CORS rules to a bucket](src/main/java/com/example/s3/S3Cors.java#L6) (`PutBucketCors`)
- [Add a lifecycle configuration to a bucket](src/main/java/com/example/s3/LifecycleConfiguration.java#L6) (`PutBucketLifecycleConfiguration`)
- [Add a policy to a bucket](src/main/java/com/example/s3/SetBucketPolicy.java#L6) (`PutBucketPolicy`)
- [Copy an object from one bucket to another](src/main/java/com/example/s3/CopyObject.java#L6) (`CopyObject`)
- [Create a bucket](src/main/java/com/example/s3/CreateBucket.java#L6) (`CreateBucket`)
- [Delete a policy from a bucket](src/main/java/com/example/s3/DeleteBucketPolicy.java#L6) (`DeleteBucketPolicy`)
- [Delete an empty bucket](src/main/java/com/example/s3/S3BucketOps.java#L79) (`DeleteBucket`)
- [Delete multiple objects](src/main/java/com/example/s3/DeleteMultiObjects.java#L6) (`DeleteObjects`)
- [Delete the website configuration from a bucket](src/main/java/com/example/s3/DeleteWebsiteConfiguration.java#L6) (`DeleteBucketWebsite`)
- [Determine the existence and content type of an object](src/main/java/com/example/s3/GetObjectContentType.java#L6) (`HeadObject`)
- [Download objects to a local directory](src/main/java/com/example/s3/transfermanager/DownloadToDirectory.java#L6) (`DownloadDirectory`)
- [Enable notifications](src/main/java/com/example/s3/SetBucketEventBridgeNotification.java#L6) (`PutBucketNotificationConfiguration`)
- [Get an object from a bucket](src/main/java/com/example/s3/GetObjectData.java#L6) (`GetObject`)
- [Get the ACL of a bucket](src/main/java/com/example/s3/GetAcl.java#L6) (`GetBucketAcl`)
- [Get the legal hold configuration of an object](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L94) (`GetObjectLegalHold`)
- [Get the object lock configuration of a bucket](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L345) (`GetObjectLockConfiguration`)
- [Get the policy for a bucket](src/main/java/com/example/s3/GetBucketPolicy.java#L6) (`GetBucketPolicy`)
- [Get the retention configuration of an object](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L309) (`GetObjectRetention`)
- [List buckets](src/main/java/com/example/s3/ListBuckets.java#L6) (`ListBuckets`)
- [List in-progress multipart uploads](src/main/java/com/example/s3/ListMultipartUploads.java#L6) (`ListMultipartUploads`)
- [List objects in a bucket](src/main/java/com/example/s3/ListObjects.java#L6) (`ListObjectsV2`)
- [Restore an archived copy of an object](src/main/java/com/example/s3/RestoreObject.java#L6) (`RestoreObject`)
- [Set a new ACL for a bucket](src/main/java/com/example/s3/SetAcl.java#L6) (`PutBucketAcl`)
- [Set the legal hold configuration of an object](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L255) (`PutObjectLegalHold`)
- [Set the object lock configuration of a bucket](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L206) (`PutObjectLockConfiguration`)
- [Set the retention period of an object](src/main/java/com/example/s3/lockscenario/S3LockActions.java#L61) (`PutObjectRetention`)
- [Set the website configuration for a bucket](src/main/java/com/example/s3/SetWebsiteConfiguration.java#L6) (`PutBucketWebsite`)
- [Upload an object to a bucket](src/main/java/com/example/s3/PutObject.java#L6) (`PutObject`)
- [Upload directory to a bucket](src/main/java/com/example/s3/transfermanager/UploadADirectory.java#L6) (`UploadDirectory`)
- [Use SQL with Amazon S3 Select](src/main/java/com/example/s3/async/SelectObjectContentExample.java#L5) (`SelectObjectContent`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with buckets and objects](src/main/java/com/example/s3/S3Scenario.java)
- [Lock Amazon S3 objects](src/main/java/com/example/s3/lockscenario/S3ObjectLockWorkflow.java)
- [Parse URIs](src/main/java/com/example/s3/ParseUri.java)
- [Perform a multipart upload](src/main/java/com/example/s3/PerformMultiPartUpload.java)
- [Track uploads and downloads](src/main/java/com/example/s3/transfermanager/UploadFile.java)
- [Upload or download large files](src/main/java/com/example/s3/transfermanager/DownloadToDirectory.java)
- [Upload stream of unknown size](src/main/java/com/example/s3/async/PutObjectFromStreamAsync.java)
- [Use checksums](src/main/java/com/example/s3/BasicOpsWithChecksums.java)


<!--custom.examples.start-->
- [Create a presigned URL for download](s3/src/main/java/com/example/s3/GeneratePresignedGetUrlAndRetrieve.java)
- [Create a presigned URL for upload](s3/src/main/java/com/example/s3/GeneratePresignedUrlAndPutFileWithMetadata.java)
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.



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

#### Track uploads and downloads

This example shows you how to track an Amazon S3 object upload or download.


<!--custom.scenario_prereqs.s3_Scenario_TrackUploadDownload.start-->
<!--custom.scenario_prereqs.s3_Scenario_TrackUploadDownload.end-->


<!--custom.scenarios.s3_Scenario_TrackUploadDownload.start-->
<!--custom.scenarios.s3_Scenario_TrackUploadDownload.end-->

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