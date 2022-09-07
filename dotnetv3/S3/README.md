# Amazon S3 code examples for .NET

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

- [Abort multi-part upload](AbortMPUExample/AbortMPU.cs) (`AbortMultipartUploadsAsync`)
- [Copy an object from one bucket to another](CopyObjectExample/CopyObject.cs) (`CopyObjectAsync`)
- [Create a bucket](CreateBucketExample/CreateBucket.cs) (`CreateBucketAsync`)
- [Cross-region replication](CrossRegionReplicationExample/CrossRegionReplication.cs) (`PutBucketReplicationAsync`, `GetBucketReplicationAsync`)
- [Delete an empty bucket](DeleteBucketExample/DeleteBucket.cs) (`DeleteBucketAsync`)
- [Delete an object from a bucket](non-versioned-examples/DeleteObjectExample/DeleteObject.cs) (`DeleteObjectAsync`)
- [Delete multiple objects](non-versioned-examples/DeleteMultipleObjectsExample/DeleteMultipleObjects.cs) (`DeleteObjectsAsync`)
- [Delete versions of an object](versioned-examples/DeleteObjectVersionExample/DeleteObjectVersion.cs) (`DeleteObjectVersionAsync`)
- [Enable notifications](EnableNotificationsExample/EnableNotifications.cs) (`PutBucketNotificationAsync`)
- [Encrypt an object using server side encryption](ServerSideEncryptionExample/ServerSideEncryption.cs) (`PutObjectAsync`, `GetObjectMetadataAsync`)
- [Encrypt an object using client side encryption](SSEClientEncryptionExample/SSEClientEncryption.cs) (`PutObjectAsync`, `GetObjectAsync`, `GetObjectMetadataAsync`)
- [Get a bucket's transfer acceleration configuration](TransferAccelerationExample/TransferAcceleration.cs) (`GetBucketAccelerateConfigurationAsync`)
- [Get a presigned URL](GenPresignedURLExample/GenPresignedUrl.cs) (`GetPreSignedURLAsync`)
- [Get an object from a bucket](GetObjectExample/GetObject.cs) (`GetObjectAsync`)
- [Get temporary federated credentials](TempFederatedCredentialsExample/TempFederatedCredentials.cs) (`GetTemporaryFederatedCredentialsAsync`, `GetFederationTokenAsync`)
- [Get the ACL of a bucket](BucketACLExample/BucketACL.cs) (`GetBucketAclAsync` )
- [List buckets](ListBucketsExample/ListBuckets.cs) (`ListBucketsAsync`)
- [List objects in a bucket](ListObjectsExample/ListObjects.cs) (`ListObjectsV2Async`)
- [List objects in a bucket using a dual stack endpoint](DualStackEndpointExample/DualStackEndpoint.cs) (`ListObjectsV2Async`)
- [List objects in a bucket using a paginator](ListObjectsPaginatorExample/ListObjectsPaginator.cs) (`ListObjectsV2Async`)
- [List the versions of an object](versioned-examples/ListObjectVersionsExample/ListObjectVersions.cs) (`ListObjectVersionsAsync`)
- [Log server access](ServerAccessLoggingExample/ServerAccessLogging.cs) (`PutACLAsync`, `PutBucketLoggingAsync`)
- [Manage a bucket's ACL](ManageACLsExample/ManageACLs.cs) (`PutBucketAsync`, `GetACLAsync`, `PutACLAsync`)
- [Manage an object's ACL](ManageObjectACLExample/ManageObjectACL.cs) (`PubtObjectACLAsync`, `GetObjectAclAsync`)
- [Manage bucket CORS rules](s3CORSExample/S3CORS.cs) (`GetBucketCorsAsync`, `PutBucketCorsAsync`, `DeleteBucketCorsAsync`)
- [Manage the lifecycle of a bucket](LifecycleExample/Lifecycle.cs) (`GetBucketLifecycleConfigurationAsync`, `PutBucketLifecycleConfigurationAsync`, `DeleteLifecycleConfigurationAsync`)
- [Perform a multi-part copy](SSECLowLevelMPUcopyObjectExample/SSECLowLevelMPUcopyObject.cs) (`InitiateMultipartUploadAsync`, `UploadPartAsync`, `CompleteMultipartUploadAsync`)
- [Perform a multi-part upload](MPUapiCopyObjExample/MPUapiCopyObj.cs) (`InitiateMultipartUploadAsync`, `CopyPartAsync`, `CompleteMultipartUploadAsync`)
- [Perform a multi-part upload of a directory](UploadDirMPUHighLevelAPIExample/UploadDirMPUHighLevelAPI.cs) (`UploadDirectoryAsync`)
- [Perform a multi-part upload of a file](UploadFileMPUHighLevelAPIExample/UploadFileMPUHighLevelAPI.cs) (`UploadAsync`)
- [Perform a multi-part upload of a file](UploadFileMPULowLevelAPIExample/UploadFileMPULowLevelAPI.cs) (`UploadPartAsync`)
- [Restore an archived object](RestoreArchivedObjectExample/RestoreArchivedObject.cs) (`RestoreObjectAsync`)
- [Set a new ACL for a bucket](BucketACLExample/BucketACL.cs) (`PutBucketACLAsync`)
- [Set the website configuration of a bucket](WebsiteConfigExample/WebsiteConfig.cs) (`PutBucketWebsiteAsync`)
- [Start a session using temporary credentials](TempCredExplicitSessionStartExample/TempCredExplicitSessionStart.cs) (`GetSessionTokenAsync`)
- [Tag an object in a bucket](ObjectTagExample/ObjectTag.cs) (`PutOjectAsync`, `PutObjectTaggingAsync`)
- [Track progress of a multi-part upload](TrackMPUUsingHighLevelAPIExample/TrackMPUUsingHighLevelAPI.cs) (`FileTransferUtility.UploadAsync`)
- [Upload an object to a bucket](UploadObjectExample/UploadObject.cs) (`PutObjectAsync`)
- [Upload objects using a presigned URL](UploadUsingPresignedURLExample/UploadUsingPresignedURL.cs) (`GetPreSignedURLAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

 - [S3 basics](S3_Basics)
 - [Transfer Utility basics](scenarios/TransferUtilityBasics/)

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

