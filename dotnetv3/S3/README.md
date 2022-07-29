# Amazon S3 code examples for .NET

## Purpose

The code examples in this directory demonstrate how to work with Amazon Simple
Storage Service (Amazon S3) features using the AWS SDK for .NET v3.5 or later.

Amazon S3 is storage for the internet. You can use Amazon S3 to store and
retrieve any amount of data at any time, from anywhere on the web.

## ⚠️ Important

- We recommend that grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 

## Code examples

### Scenario Examples

 - [S3 basics](S3_Basics)
 - [Transfer Utility basics](scenarios/TransferUtilityBasics/)

### API examples

- [Abort multi-part upload](AbortMPUExample/) (`AbortMultipartUploadsAsync`)
- [Get the ACL of a bucket](BucketACLExample/) (`GetBucketAclAsync` )
- [Set a new ACL for a bucket](BucketACLExample/) (`PutBucketACLAsync`)
- [Copy an object from one bucket to another](CopyObjectExample/) (`CopyObjectAsync`)
- [Create a bucket](CreateBucketExample/) (`CreateBucketAsync`)
- [Cross-region replication](CrossRegionReplicationExample/)
(`PutBucketReplicationAsync`, `GetBucketReplicationAsync`)
- [Delete an empty bucket](DeleteBucketExample/) (`DeleteBucketAsync`)
- [List objects in a bucket](DualStackEndpointExample/) (`ListObjectsV2Async`)
- [Enable notifications](EnableNotificationsExample/) (`PutBucketNotificationAsync`)
- [Get a presigned URL](GenPresignedURLExample/) (`GetPreSignedURLAsync`)
- [Get an object from a bucket](GetObjectExample/) (`GetObjectAsync`)
- [Manage the lifecycle of a bucket](LifecycleExample/)
(`GetBucketLifecycleConfigurationAsync`, `PutBucketLifecycleConfigurationAsync`, `DeleteLifecycleConfigurationAsync`)
- [List buckets](ListBucketsExample/) (`ListBucketsAsync`)
- [List objects in a bucket](ListObjectsExample/) (`ListObjectsV2Async`)
- [List objects in a bucket using a paginator](ListObjectsPaginatorExample/) (`ListObjectsV2Async`)
- [Manage a bucket's ACL](ManageACLsExample/) (`PutBucketAsync`, `GetACLAsync`, `PutACLAsync`)
- [Manage an object's ACL](ManageObjectACLExample/) (`PubtObjectACLAsync`, `GetObjectAclAsync`)
- [Perform a multi-part upload](MPUapiCopyObjExample/)
(`InitiateMultipartUploadAsync`, `CopyPartAsync`, `CompleteMultipartUploadAsync`)
- [Delete multiple objects](non-versioned-examples/DeleteMultipleObjectsExample/) (`DeleteObjectsAsync`)
- [Delete an object from a bucket](non-versioned-examples/DeleteObjectExample/) (`DeleteObjectAsync`)
- [Tag an object in a bucket](ObjectTagExample/) (`PutOjectAsync`, `PutObjectTaggingAsync`)
- [Restore an archived object](RestoreArchivedObjectExample/) (`RestoreObjectAsync`)
- [Manage bucket CORS rules](s3CORSExample/) (`GetBucketCorsAsync`, `PutBucketCorsAsync`, `DeleteBucketCorsAsync`)
- [Log server access](ServerAccessLoggingExample/) (`PutACLAsync`, `PutBucketLoggingAsync`)
- [Encrypt an object using server side encryption](ServerSideEncryptionExample/) (`PutObjectAsync`, `GetObjectMetadataAsync`)
- [Encrypt an object using client side encryption](SSEClientEncryptionExample/) (`PutObjectAsync`, `GetObjectAsync`, `GetObjectMetadataAsync`)
- [Perform a multi-part copy](SSECLowLevelMPUcopyObjectExample/)
(`InitiateMultipartUploadAsync`, `UploadPartAsync`, `CompleteMultipartUploadAsync`)
- [Start a session using temporary credentials](TempCredExplicitSessionStartExample/) (`GetSessionTokenAsync`)
- [TempFederatedCredentialsExample](TempFederatedCredentialsExample/)
(`GetTemporaryFederatedCredentialsAsync`, `GetFederationTokenAsync`)
- [TrackMPUUsingHighLevelAPIExample](TrackMPUUsingHighLevelAPIExample/) (`FileTransferUtility.UploadAsync`)
- [Get a bucket's transfer acceleration configuration](TransferAccelerationExample/) (`GetBucketAccelerateConfigurationAsync`)
- [Perform a multi-part upload of a directory](UploadDirMPUHighLevelAPIExample/) (`UploadDirectoryAsync`)
- [Perform a multi-part upload of a file](UploadFileMPUHighLevelAPIExample/) (`UploadAsync`)
- [Perform a multi-part upload of a file](UploadFileMPULowLevelAPIExample/) (`UploadPartAsync`)
- [Upload an object to a bucket](UploadObjectExample/) (`PutObjectAsync`)
- [Upload objects using a presigned URL](UploadUsingPresignedURLExample/) (`GetPreSignedURLAsync`)
- [Delete multiple objects](versioned-examples/DeleteMultipleObjectsExample/) (`DeleteObjectsAsync`)
- [Delete versions of an object](versioned-examples/DeleteObjectVersionExample/)
(`DeleteObjectVersionAsync`)
- [List the versions of an object](versioned-examples/ListObjectVersionsExample/) (`ListObjectVersionsAsync`)
- [Set the website configuration of a bucket](WebsiteConfigExample/) (`PutBucketWebsiteAsync`)

### Cross-service examples

- [Create a web application that analyzes photos in a bucket using the Amazon Rekognition service](dotnetv3/cross-service/PhotoAnalyzerApp)

## Running the Examples

The examples in this folder use the default user account. The call to
initialize the Amazon S3 client supplies the region. Change the region to
match your own before running the example.

Once the example has been compiled, you can run it from the commandline by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Alternatively you can execute the example from within your IDE.

## Resources and documentation

- [Amazon Simple Storage Service documentation](https://docs.aws.amazon.com/s3/)
- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

