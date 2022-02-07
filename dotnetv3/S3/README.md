# Amazon S3 code examples for .NET

## Purpose

The code examples in this directory demonstrate how to work with Amazon Simple
Storage Service (Amazon S3) features using the AWS SDK for .NET v3.5 or later.
Amazon S3 is storage for the internet. You can use Amazon S3 to store and
retrieve any amount of data at any time, from anywhere on the web.

## Code examples

### Scenario Examples

 - [S3 Basics](S3_Basics) - Shows the basic procedures used to work with Amazon
   S3 buckets. The example creates a bucket, uploads and downloads objects
   to that bucket and then deletes the objects and the bucket. This example uses
   the following API methods:
   
   - CreateBucketAsync
   - PutObjectAsync
   - GetObjectAsync
   - CopyObjectAsync
   - ListObjectsAsync
   - DeleteObjectsAsync
   - DeleteBucketAsync

### API examples

- [AbortMPUExample](AbortMPUExample/) (`AbortMultipartUploadsAsync`)
- [BucketACLExample](BucketACLExample/) (`PutBucketACLAsync`, `GetBucketAclAsync` )
- [CopyObjectExample](CopyObjectExample/) (`CopyObjectAsync`)
- [CreateBucketExample](CreateBucketExample/) (`CreateBucketAsync`)
- [CrossRegionReplicationExample](CrossRegionReplicationExample/)
(`PutBucketReplicationAsync`, `GetBucketReplicationAsync`)
- [DeleteBucketExample](DeleteBucketExample/) (`DeleteBucketAsync`)
- [DualStackEndpointExample](DualStackEndpointExample/) (`ListObjectsV2Async`)
- [EnableNotificationsExample](EnableNotificationsExample/) (`PutBucketNotificationAsync`)
- [GenPresignedURLExample](GenPresignedURLExample/) (`GetPreSignedURLAsync`)
- [GetObjectExample](GetObjectExample/) (`GetObjectAsync`)
- [LifecycleExample](LifecycleExample/)
(`GetBucketLifecycleConfigurationAsync`, `PutBucketLifecycleConfigurationAsync`, `DeleteLifecycleConfigurationAsync`)
- [ListBucketsExample](ListBucketsExample/) (`ListBucketsAsync`)
- [ListObjectsExample](ListObjectsExample/) (`ListObjectsV2Async`)
- [ListObjectsPaginatorExample](ListObjectsPaginatorExample/) (`ListObjectsV2Async`)
- [ManageACLsExample](ManageACLsExample/) (`PutBucketAsync`, `GetACLAsync`, `PutACLAsync`)
- [ManageObjectACLExample](ManageObjectACLExample/) (`PubtObjectACLAsync`, `GetObjectAclAsync`)
- [MPUapiCopyObjExample](MPUapiCopyObjExample/)
(`InitiateMultipartUploadAsync`, `CopyPartAsync`, `CompleteMultipartUploadAsync`)
- [DeleteMultipleObjectsExample](non-versioned-examples/DeleteMultipleObjectsExample/) (`DeleteObjectsAsync`)
- [DeleteObjectExample](non-versioned-examples/DeleteObjectExample/) (`DeleteObjectAsync`)
- [ObjectTagExample](ObjectTagExample/) (`PutOjectAsync`, `PutObjectTaggingAsync`)
- [RestoreArchivedObjectExample](RestoreArchivedObjectExample/) (`RestoreObjectAsync`)
- [S3CORSExample](s3CORSExample/) (`GetBucketCorsAsync`, `PutBucketCorsAsync`, `DeleteBucketCorsAsync`)
- [ServerAccessLoggingExample](ServerAccessLoggingExample/) (`PutACLAsync`, `PutBucketLoggingAsync`)
- [ServerSideEncryptionExample](ServerSideEncryptionExample/) (`PutObjectAsync`, `GetObjectMetadataAsync`)
- [SSEClientEncryptionExample](SSEClientEncryptionExample/) (`PutObjectAsync`, `GetObjectAsync`, `GetObjectMetadataAsync`)
- [SSECLowLevelMPUcopyObjectExample](SSECLowLevelMPUcopyObjectExample/)
(`InitiateMultipartUploadAsync`, `UploadPartAsync`, `CompleteMultipartUploadAsync`)
- [TempCredExplicitSessionStartExample](TempCredExplicitSessionStartExample/) (`GetSessionTokenAsync`)
- [TempFederatedCredentialsExample](TempFederatedCredentialsExample/)
(`GetTemporaryFederatedCredentialsAsync`, `GetFederationTokenAsync`)
- [TrackMPUUsingHighLevelAPIExample](TrackMPUUsingHighLevelAPIExample/) (`FileTransferUtility.UploadAsync`)
- [TransferAccelerationExample](TransferAccelerationExample/) (`GetBucketAccelerateConfigurationAsync`)
- [UploadDirMPUHighLevelAPIExample](UploadDirMPUHighLevelAPIExample/) (`UploadDirectoryAsync`)
- [UploadFileMPUHighLevelAPIExample](UploadFileMPUHighLevelAPIExample/) (`UploadAsync`)
- [UploadFileMPULowLevelAPIExample](UploadFileMPULowLevelAPIExample/) (`UploadPartAsync`)
- [UploadObjectExample](UploadObjectExample/) (`PutObjectAsync`)
- [UploadUsingPresignedURLExample](UploadUsingPresignedURLExample/) (`GetPreSignedURLAsync`)
- [DeleteMultipleObjectsExample](versioned-examples/DeleteMultipleObjectsExample/) (`DeleteObjectsAsync`)
- [DeleteObjectVersionExample](versioned-examples/DeleteObjectVersionExample/)
(`DeleteObjectVersionAsync`)
- [ListObjectVersionsExample](versioned-examples/ListObjectVersionsExample/) (`ListObjectVersionsAsync`)
- [WebsiteConfigExample](WebsiteConfigExample/) (`PutBucketWebsiteAsync`)

## ⚠️ Important

- We recommend that grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 

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

Or you can execute the example from within your IDE.

## Resources and documentation

[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

