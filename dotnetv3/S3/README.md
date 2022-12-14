# Amazon S3 code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Simple Storage Service (Amazon S3) features.

Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

- [Add CORS rules to a bucket](s3CORSExample/S3CORS.cs) (`PutCORSConfigurationAsync`)
- [Add a lifecyle configuration to a bucket](LifecycleExample/Lifecycle.cs) (`PutLifecycleConfigurationAsync`)
- [Cancel multi-part uploads](AbortMPUExample/AbortMPU.cs) (`AbortMultipartUploadsAsync`)
- [Copy an object from one bucket to another](CopyObjectExample/CopyObject.cs) (`CopyObjectAsync`)
- [Create a bucket](S3_Basics/S3_Basics.cs) (`CreateBucketAsync`)
- [Delete CORS rules from a bucket](s3CORSExample/S3CORS.cs) (`DeleteCORSConfigurationAsync`)
- [Delete an empty bucket](S3_Basics/S3_Basics.cs) (`DeleteBucketAsync`)
- [Delete an object](non-versioned-examples/DeleteObjectExample/DeleteObject.cs) (`DeleteObjectAsync`)
- [Delete multiple objects](non-versioned-examples/DeleteMultipleObjectsExample/DeleteMultipleObjects.cs) (`DeleteObjectsAsync`)
- [Delete the lifecycle configuration of a bucket](LifecycleExample/Lifecycle.cs) (`DeleteLifecycleConfigurationAsync`)
- [Enable logging](ServerAccessLoggingExample/ServerAccessLogging.cs) (`PutBucketLoggingAsync`)
- [Enable notifications](EnableNotificationsExample/EnableNotifications.cs) (`PutBucketNotificationAsync`)
- [Enable transfer acceleration](TransferAccelerationExample/TransferAcceleration.cs) (`PutBucketAccelerateConfigurationAsync`)
- [Get CORS rules for a bucket](s3CORSExample/S3CORS.cs) (`GetCORSConfigurationAsync`)
- [Get an object from a bucket](S3_Basics/S3_Basics.cs) (`GetObjectAsync`)
- [Get the ACL of a bucket](BucketACLExample/BucketACL.cs) (`GetBucketAclAsync` )
- [Get the lifecycle configuration of a bucket](LifecycleExample/Lifecycle.cs) (`GetLifecycleConfigurationAsync`)
- [Get the website configuration for a bucket](WebsiteConfigExample/WebsiteConfig.cs) (`GetBucketWebsiteAsync`)
- [List buckets](ListBucketsExample/ListBuckets.cs) (`ListBucketsAsync`)
- [List object versions in a bucket](versioned-examples/ListObjectVersionsExample/ListObjectVersions.cs) (`ListObjectVersionsAsync`)
- [List objects in a bucket](S3_Basics/S3_Basics.cs) (`ListObjectsV2Async`)
- [List objects in a bucket using a paginator](ListObjectsPaginatorExample/ListObjectsPaginator.cs) (`ListObjectsV2Async`)
- [Restore an archived copy of an object](RestoreArchivedObjectExample/RestoreArchivedObject.cs) (`RestoreObjectAsync`)
- [Set a new ACL for a bucket](BucketACLExample/BucketACL.cs) (`PutBucketACLAsync`)
- [Set the website configuration of a bucket](WebsiteConfigExample/WebsiteConfig.cs) (`PutBucketWebsiteAsync`)
- [Upload an object to a bucket](S3_Basics/S3_Basics.cs) (`PutObjectAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Create a presigned URL](UploadUsingPresignedURLExample)
- [Get started with buckets and objects](S3_Basics)
- [Get started with encryption](SSEClientEncryptionExample)
- [Get started with tags](ObjectTagExample)
- [Manage access control lists (ACLs)](ManageACLsExample)
- [Perform a multipart copy](MPUapiCopyObjExample)
- [Upload or download large files](scenarios/TransferUtilityBasics)

### Cross-service examples
Sample applications that work across multiple AWS services.

- [Create a web application that analyzes photos in a bucket using the Amazon Rekognition service](../cross-service/PhotoAnalyzerApp)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/index.html)
* [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
* [AWS SDK for .NET Amazon S3](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/S3/NS3.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

