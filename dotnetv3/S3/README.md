# Amazon S3 code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Simple Storage Service (Amazon S3).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](S3_Basics/S3_Basics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](CopyObjectExample/CopyObject.cs#L11)
- [CreateBucket](S3_Basics/S3Bucket.cs#L12)
- [DeleteBucket](S3_Basics/S3Bucket.cs#L266)
- [DeleteBucketCors](s3CORSExample/S3CORS.cs#L147)
- [DeleteBucketLifecycle](LifecycleExample/Lifecycle.cs#L192)
- [DeleteObject](non-versioned-examples/DeleteObjectExample/DeleteObject.cs#L6)
- [DeleteObjects](S3_Basics/S3Bucket.cs#L221)
- [GetBucketAcl](BucketACLExample/BucketACL.cs#L75)
- [GetBucketCors](s3CORSExample/S3CORS.cs#L125)
- [GetBucketEncryption](PutBucketEncryption/ServerSideEncryption/ServerSideEncryption.cs#L107)
- [GetBucketLifecycleConfiguration](LifecycleExample/Lifecycle.cs#L169)
- [GetBucketWebsite](WebsiteConfigExample/WebsiteConfig.cs#L72)
- [GetObject](S3_Basics/S3Bucket.cs#L85)
- [GetObjectLegalHold](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L259)
- [GetObjectLockConfiguration](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L290)
- [GetObjectRetention](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L193)
- [ListBuckets](ListBucketsExample/ListBuckets.cs#L4)
- [ListObjectVersions](versioned-examples/ListObjectVersionsExample/ListObjectVersions.cs#L6)
- [ListObjectsV2](S3_Basics/S3Bucket.cs#L171)
- [PutBucketAccelerateConfiguration](TransferAccelerationExample/TransferAcceleration.cs#L6)
- [PutBucketAcl](BucketACLExample/BucketACL.cs#L37)
- [PutBucketCors](s3CORSExample/S3CORS.cs#L104)
- [PutBucketEncryption](PutBucketEncryption/ServerSideEncryption/ServerSideEncryption.cs#L64)
- [PutBucketLifecycleConfiguration](LifecycleExample/Lifecycle.cs#L145)
- [PutBucketLogging](ServerAccessLoggingExample/ServerAccessLogging.cs#L6)
- [PutBucketNotificationConfiguration](EnableNotificationsExample/EnableNotifications.cs#L6)
- [PutBucketWebsite](WebsiteConfigExample/WebsiteConfig.cs#L57)
- [PutObject](S3_Basics/S3Bucket.cs#L43)
- [PutObjectLegalHold](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L224)
- [PutObjectLockConfiguration](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L60)
- [PutObjectRetention](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ActionsWrapper.cs#L102)
- [RestoreObject](RestoreArchivedObjectExample/RestoreArchivedObject.cs#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](GenPresignedURLExample/GenPresignedUrl.cs)
- [Create a serverless application to manage photos](../cross-service/PhotoAssetManager)
- [Detect objects in images](../cross-service/PhotoAnalyzerApp)
- [Get started with encryption](SSEClientEncryptionExample/SSEClientEncryption.cs)
- [Get started with tags](ObjectTagExample/ObjectTag.cs)
- [Lock Amazon S3 objects](scenarios/S3ObjectLockScenario/S3ObjectLockWorkflow/S3ObjectLockWorkflow.cs)
- [Make conditional requests](scenarios/S3ConditionalRequestsScenario/S3ConditionalRequests/S3ConditionalRequestsScenario.cs)
- [Manage access control lists (ACLs)](ManageACLsExample/ManageACLs.cs)
- [Perform a multipart copy](MPUapiCopyObjExample/MPUapiCopyObj.cs)
- [Transform data with S3 Object Lambda](../cross-service/S3ObjectLambdaFunction)
- [Upload or download large files](scenarios/TransferUtilityBasics/TransferUtilityBasics/GlobalUsings.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


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

#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Detect objects in images

This example shows you how to build an app that uses Amazon Rekognition to detect objects by category in images.


<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.end-->


<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.end-->

#### Get started with encryption

This example shows you how to get started with encryption for Amazon S3 objects.


<!--custom.scenario_prereqs.s3_Encryption.start-->
<!--custom.scenario_prereqs.s3_Encryption.end-->


<!--custom.scenarios.s3_Encryption.start-->
<!--custom.scenarios.s3_Encryption.end-->

#### Get started with tags

This example shows you how to get started with tags for Amazon S3 objects.


<!--custom.scenario_prereqs.s3_Scenario_Tagging.start-->
<!--custom.scenario_prereqs.s3_Scenario_Tagging.end-->


<!--custom.scenarios.s3_Scenario_Tagging.start-->
<!--custom.scenarios.s3_Scenario_Tagging.end-->

#### Lock Amazon S3 objects

This example shows you how to work with S3 object lock features.


<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.start-->
<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.end-->


<!--custom.scenarios.s3_Scenario_ObjectLock.start-->
<!--custom.scenarios.s3_Scenario_ObjectLock.end-->

#### Make conditional requests

This example shows you how to add preconditions to Amazon S3 requests.


<!--custom.scenario_prereqs.s3_Scenario_ConditionalRequests.start-->
<!--custom.scenario_prereqs.s3_Scenario_ConditionalRequests.end-->


<!--custom.scenarios.s3_Scenario_ConditionalRequests.start-->
<!--custom.scenarios.s3_Scenario_ConditionalRequests.end-->

#### Manage access control lists (ACLs)

This example shows you how to manage access control lists (ACLs) for Amazon S3 buckets.


<!--custom.scenario_prereqs.s3_Scenario_ManageACLs.start-->
<!--custom.scenario_prereqs.s3_Scenario_ManageACLs.end-->


<!--custom.scenarios.s3_Scenario_ManageACLs.start-->
<!--custom.scenarios.s3_Scenario_ManageACLs.end-->

#### Perform a multipart copy

This example shows you how to perform a multipart copy of an Amazon S3 object.


<!--custom.scenario_prereqs.s3_MultipartCopy.start-->
<!--custom.scenario_prereqs.s3_MultipartCopy.end-->


<!--custom.scenarios.s3_MultipartCopy.start-->
<!--custom.scenarios.s3_MultipartCopy.end-->

#### Transform data with S3 Object Lambda

This example shows you how to transform data for your application with S3 Object Lambda.


<!--custom.scenario_prereqs.cross_ServerlessS3DataTransformation.start-->
<!--custom.scenario_prereqs.cross_ServerlessS3DataTransformation.end-->


<!--custom.scenarios.cross_ServerlessS3DataTransformation.start-->
<!--custom.scenarios.cross_ServerlessS3DataTransformation.end-->

#### Upload or download large files

This example shows you how to upload or download large files to and from Amazon S3.


<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.end-->


<!--custom.scenarios.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenarios.s3_Scenario_UsingLargeFiles.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for .NET Amazon S3 reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/S3/NS3.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
