# Amazon S3 Glacier code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Simple Storage Service Glacier (Amazon S3 Glacier) to work with vaults and archives.

Amazon S3 Glacier is a secure and durable service for low-cost data archiving and long-term backup.

## ⚠ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Add tags](AddTagsToVaultExample/AddTagsToVault.cs) (AddTagsToVault)
* [Create a vault](CreateVaultExample/CreateVault.cs) (CreateVault)
* [Describe a job](DescribeVaultExample/DescribeVault.cs) (DescribeJob)
* [Download an archive](DownloadArchiveHighLevelExample/DownloadArchiveHighLevel.cs)
* [List jobs](ListJobsExample/ListJobs.cs) (ListJobs)
* [List tags](ListTagsForVaultExample/ListTagsForVault.cs) (ListTagsForVault)
* [List vaults](ListVaultsExample/ListVaults.cs) (ListVaults)
* [Upload an archive to a vault](UploadArchiveHighLevelExample/UploadArchiveHighLevel.cs) (UploadArchive)

## Run the examples

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS Region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonGlacierClient(Amazon.RegionEndpoint.USWest2);
```

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

### Prerequisites
* You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
* .NET Core 5.0 or later
* AWS SDK for .NET (v3) or later

## Additional resources
* [Amazon S3 Glacier Developer Guide](https://docs.aws.amazon.com/amazonglacier/latest/dev/index.html)
* [AWS SDK for .NET Amazon S3 Glacier](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glacier/NGlacier.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

