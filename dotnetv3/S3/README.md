# Amazon Simple Storage Service (Amazon S3) code examples in C#

## Purpose

Customers of all sizes and industries can use Amazon S3 to store and protect any amount of data for a range of use cases, such as data lakes, websites, mobile applications, backup and restore, archive, enterprise applications, IoT devices, and big data analytics. Amazon S3 provides management features so that you can optimize, organize, and configure access to your data to meet your specific business, organizational, and compliance requirements.

The code examples in this directory demonstrate how to work with Amazon S3 features using the AWS SDK for .NET v3.5 or later.

## Code examples

- [AbortMPUExample](AbortMPUExample/)
- [BucketACLExample](BucketACLExample/)
- [CopyObjectExample](CopyObjectExample/)
- [CreateBucketExample](CreateBucketExample/)
- [CrossRegionReplicationExample](CrossRegionReplicationExample/)
- [DeleteBucketExample](DeleteBucketExample/)
- [DualStackEndpointExample](DualStackEndpointExample/)
- [EnableNotificationsExample](EnableNotificationsExample/)
- [GenPresignedUrlExample](GenPresignedUrlExample/)
- [GetObjectExample](GetObjectExample/)
- [LifecycleExample](LifecycleExample/)
- [ListBucketsExample](ListBucketsExample/)
- [ListObjectsExample](ListObjectsExample/)
- [ListObjectsPaginatorExample](ListObjectsPaginatorExample/)
- [ManageACLsExample](ManageACLsExample/)
- [ManageObjectACLExample](ManageObjectACLExample/)
- [MPUapiCopyObjExample](MPUapiCopyObjExample/)
- [DeleteMultipleObjectsExample](non-versioned-examples/DeleteMultipleObjectsExample/)
- [DeleteObjectExample](non-versioned-examples/DeleteObjectExample/)
- [ObjectTagExample](ObjectTagExample/)
- [RestoreArchivedObjectExample](RestoreArchivedObjectExample/)
- [S3CORSExample](S3CORSExample/) - 
- [ServerAccessLoggingExample](ServerAccessLoggingExample/)
- [ServerSideEncryptionExample](ServerSideEncryptionExample/)
- [SSEClientEncryptionExample](SSEClientEncryptionExample/) 
- [SSECLowLevelMPUcopyObjectExample](SSECLowLevelMPUcopyObjectExample/)
- [TempCredExplicitSessionStartExample](TempCredExplicitSessionStartExample/)
- [TempFederatedCredentialsExample](TempFederatedCredentialsExample/)
- [TrackMPUUsingHighLevelAPIExample](TrackMPUUsingHighLevelAPIExample/)
- [TransferAccelerationExample](TransferAccelerationExample/)
- [UploadDirMPUHighLevelAPIExample](UploadDirMPUHighLevelAPIExample/)
- [UploadFileMPUHighLevelAPIExample](UploadFileMPUHighLevelAPIExample/)
- [UploadFileMPULowLevelAPIExample](UploadFileMPULowLevelAPIExample/)
- [UploadObjectExample](UploadObjectExample/)
- [UploadUsingPresignedURLExample](UploadUsingPresignedURLExample/)
- [versioned-examples/DeleteMultipleObjectsExample](versioned-examples/DeleteMultipleObjectsExample/)
- [versioned-examples/DeleteObjectVersionExample](versioned-examples/DeleteObjectVersionExample/)
- [versioned-examples/ListObjectVersionsExample](versioned-examples/ListObjectVersionsExample/)
- [WebsiteConfigExample](WebsiteConfigExample/)


## ⚠️ Important

- We recommend that grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

The examples in this folder use the default user account. The call to
initialize the Amazon SQS client supplies the region. Change the region to
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

