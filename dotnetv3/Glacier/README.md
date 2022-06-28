# Amazon S3 Glacier examples for the AWS SDK for .NET v3

## Purpose

The examples in this section show to use the AWS SDK for .NET with Amazon
S3 Glacier to work with vaults and archives.

Amazon Simple Storage Service Glacier provides secure, durable Amazon Simple Storage Service
(Amazon S3) cloud storage classes for data archiving and long-term backup.

## ⚠️ Important

- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Adding tags to a vault](AddTagsToVaultExample/)
- [Creating a vault](CreateVaultExample/)
- [Describing a vault](DescribeVaultExample/)
- [Downloading an archive](DownloadArchiveHighLevelExample/)
- [Listing jobs](ListJobsExample/)
- [Listing tags for a vault](ListTagsForVaultExample/)
- [Listing vaults](ListVaultsExample/)
- [Uploading an archive](UploadArchiveHighLevelExample/)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

