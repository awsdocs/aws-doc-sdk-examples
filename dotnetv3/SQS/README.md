# Amazon  SQS code examples for the AWS SDK for .NET v3

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET v3 to get started using queue and message operations in Amazon Simple Queue Service (Amazon SQS).

## ⚠️ Important

- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account.

## Code examples

- [Authorizing an Amazon S3 bucket to send messages to an Amazon SQS queue](AuthorizeS2ToSendMessageExample/)
- [Creating a queue](CreateQueueExample/)
- [Creating and send a message](CreateSendExample/)
- [Deleting a message](DeleteMessageExample/)
- [Deleting a queue](DeleteQueueExample/)
- [Getting queue attributes](GetQueueAttributesExample/)
- [Getting the URL for a queue](GetQueueUrlExample/)
- [Receiving and deleting a message](ReceivDeleteExample/)
- [Receive a message from a queue](ReceiveFromQueueExample/)
- [Sending a message to a queue](SendMessageToQueueExample/)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional resources
[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
