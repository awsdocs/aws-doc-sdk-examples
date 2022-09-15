# Amazon S3 Glacier code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Simple Storage Service Glacier (Amazon S3 Glacier) to work with vaults and archives.

Amazon S3 Glacier is a secure and durable service for low-cost data archiving and long-term backup.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Add tags](AddTagsToVaultExample/AddTagsToVault.cs) (`AddTagsToVaultAsync`)
* [Create a vault](CreateVaultExample/CreateVault.cs) (`CreateVaultAsync`)
* [Describe a job](DescribeVaultExample/DescribeVault.cs) (`DescribeJobAsync`)
* [Download an archive](DownloadArchiveHighLevelExample/DownloadArchiveHighLevel.cs) (`DownloadAsync`)
* [List jobs](ListJobsExample/ListJobs.cs) (`ListJobsAsync`)
* [List tags](ListTagsForVaultExample/ListTagsForVault.cs) (`ListTagsForVaultAsync`)
* [List vaults](ListVaultsExample/ListVaults.cs) (`ListVaultsAsync`)
* [Upload an archive to a vault](UploadArchiveHighLevelExample/UploadArchiveHighLevel.cs) (`UploadArchiveAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS Region. The following
example shows how to supply the AWS Region to match your own as a
parameter to the client constructor:

```
var client = new AmazonGlacierClient(Amazon.RegionEndpoint.USWest2);
```

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon S3 Glacier Developer Guide](https://docs.aws.amazon.com/amazonglacier/latest/dev/index.html)
* [Amazon S3 Glacier API Reference](https://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-api.html)
* [AWS SDK for .NET Amazon S3 Glacier](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glacier/NGlacier.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0