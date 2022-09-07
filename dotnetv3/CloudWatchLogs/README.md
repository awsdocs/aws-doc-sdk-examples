# Amazon CloudWatch Logs code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon CloudWatch Logs (CloudWatch Logs)
to request, import, and manage certificates.

Use CloudWatch Logs to monitor, store, and access your log files from Amazon Elastic Compute Cloud instances, AWS CloudTrail, or other sources.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Associate an AWS KMS key to log group](AssociateKmsKeyExample/AssociateKmsKey.cs) (`DescribeCertificateAsync`)
* [Cancel an export task](CancelExportTaskExample/CancelExportTask.cs) (`CancelExportTaskAsync`)
* [Create a log group](CreateLogGroupExample/CreateLogGroup.cs) (`CreateLogGroupAsync`)
* [Create a new log stream](CreateLogStreamExample/CreateLogStream.cs) (`CreateLogStreamAsync`)
* [Create an export task](CreateExportTaskExample/CreateExportTask.cs) (`CreateExportTaskAsync`)
* [Delete a log group](DeleteLogGroupExample/DeleteLogGroup.cs) (`DeleteLogGroupAsync`)
* [Describe export tasks](DescribeExportTasksExample/DescribeExportTasks.cs) (`DescribeExportTasksAsync`)
* [Describe log groups](DescribeLogGroupsExample/DescribeLogGroups.cs) (`DescribeLogGroupsAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonCloudWatchLogsClient(Amazon.RegionEndpoint.USWest2);
```

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [CloudWatch Logs User Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CloudWatchLogs/NCloudWatchLogs.html)
* [CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/index.html)
* [AWS SDK for .NET CloudWatch Logs](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CertificateManager/NCertificateManager.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0