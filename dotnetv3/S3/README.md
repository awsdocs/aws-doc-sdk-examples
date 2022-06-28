# Amazon S3 code examples for the AWS SDK for .NET v3

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

### Single actions

- [Aborting a multi-part upload](AbortMPUExample/)
- [Creating a bucket using a canned access control list (ACL)](BucketACLExample/)
- [Coping an object](CopyObjectExample/)
- [Creating a bucket](CreateBucketExample/)
- [Replicating an object across regions](CrossRegionReplicationExample/)
- [Deleting a bucket](DeleteBucketExample/)
- [Listing objects in a bucket using a dual stack endpoint](DualStackEndpointExample/)
- [Enabling notifications](EnableNotificationsExample/)
- [Generating a presigned URL](GenPresignedURLExample/)
- [Getting an Object](GetObjectExample/)
- [Configuring an object lifecycle](LifecycleExample/)
- [Listing buckets](ListBucketsExample/)
- [Listing objects](ListObjectsExample/)
- [Listing objects using a paginator](ListObjectsPaginatorExample/)
- [Managing an access control list (ACL)](ManageACLsExample/)
- [Manageing an object access control list (ACL)](ManageObjectACLExample/)
- [Copying an object using a multi-part upload](MPUapiCopyObjExample/)
- [Deleting multiple objects](non-versioned-examples/DeleteMultipleObjectsExample/)
- [Deleting an object](non-versioned-examples/DeleteObjectExample/)
- [Tagging an object](ObjectTagExample/)
- [Restoring an archived object](RestoreArchivedObjectExample/)
- [Configuring cross-origin requests (CORS) configuration](s3CORSExample/)
- [Logging server access](ServerAccessLoggingExample/)
- [Using server-side encryption](ServerSideEncryptionExample/)
- [Applying client encryption to an object in a bucket](SSEClientEncryptionExample/)
- [Performing a multi-part upload using the low-level API](SSECLowLevelMPUcopyObjectExample/)
- [TUsig temporary credentials to start a session](TempCredExplicitSessionStartExample/)
- [Getting a security token to access a bucket](TempFederatedCredentialsExample/)
- [Tracking a multi-part upload](TrackMPUUsingHighLevelAPIExample/)
- [Accelerating transfers to and from a bucket](TransferAccelerationExample/)
- [Using a multi-part upload to pload a directory](UploadDirMPUHighLevelAPIExample/)
- [Performing a multi-part upload of a file using the high-level API](UploadFileMPUHighLevelAPIExample/)
- [Performing a multi-part upload of a file using the low-level API](UploadFileMPULowLevelAPIExample/)
- [Uploading an object](UploadObjectExample/)
- [Uploading an object using a presigned URL](UploadUsingPresignedURLExample/)
- [Deleting multiple objects](versioned-examples/DeleteMultipleObjectsExample/)
- [Deleting an object version](versioned-examples/DeleteObjectVersionExample/)
- [Listing object versions](versioned-examples/ListObjectVersionsExample/)
- [Configuring a website](WebsiteConfigExample/)

### Scenarios

 - [S3 Basics](S3_Basics)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Resources and documentation

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
