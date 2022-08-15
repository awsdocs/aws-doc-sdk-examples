# AWS SDK for .NET Amazon Simple Storage Service Glacier examples in C#

## Purpose

Amazon S3 Glacier provides secure, durable Amazon Simple Storage Service
(Amazon S3) cloud storage classes for data archiving and long-term backup. They
are designed to deliver 99.999999999% durability, and provide comprehensive
security and compliance capabilities that can help meet even the most stringent
regulatory requirements.

The examples in this section show to use the AWS SDK for .NET with Amazon
S3 Glacier to work with vaults and archives.

## Code examples

- [AddTagsToVaultExample](AddTagsToVaultExample/) - Add tags to an S3 Glacier vault.
- [CreateVaultExample](CreateVaultExample/) - Create a new S3 Glacier vault.
- [DescribeVaultExample](DescribeVaultExample/) - Describe an S3 Glacier vault.
- [DownloadArchiveHighLevelExample](DownloadArchiveHighLevelExample/) - Shows how to use the S3 Glacier ArchiveTransferManager to download an object by using the high level API.
- [ListJobsExample](ListJobsExample/) - List information about current S3 Glacier jobs.
- [ListTagsForVaultExample](ListTagsForVaultExample/) - List the tags that are associated with an S3 Glacier vault.
- [ListVaultsExample](ListVaultsExample/) - List the S3 Glacier vaults for an AWS Region.
- [UploadArchiveHighLevelExample](UploadArchiveHighLevelExample/) - Uses the S3 Glacier TransferManager to upload an archive.

## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the examples

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS Region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonGlacierClient(Amazon.RegionEndpoint.USWest2);
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.0 or later

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information
[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

