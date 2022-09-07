# Amazon SQS code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon Simple Queue Service (Amazon SQS).

Amazon SQS is a fully managed message queuing service that makes it easy to decouple and scale microservices, distributed systems, and serverless applications. Amazon SQS moves data between distributed application components and helps you decouple these components.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
- [Authorize a bucket to send messages to a queue](AuthorizeS3ToSendMessageExample/AuthorizeS3ToSendMessageExample/AuthorizeS3ToSendMessage.cs) (`AuthorizeS3ToSendMessageAsync`)
- [Create a queue](CreateQueueExample/CreateQueueExample/CreateQueue.cs) (`CreateQueueAsync`)
- [Create a queue and set a message](CreateSendExample/CreateSendExample/CreateSendExample.cs) (`CreateQueueAsync`, `SendMessageAsync`)
- [Delete a message from a queue](DeleteMessageExample/DeleteMessageExample/DeleteMessage.cs) (`DeleteMessageAsync`)
- [Delete a queue](DeleteQueueExample/DeleteQueueExample/DeleteQueue.cs) (`DeleteQueueAsync`)
- [Get attributes for a queue](GetQueueAttributesExample/GetQueueAttributesExample/GetQueueAttributes.cs) (`GetQueueAttributesAsync`)
- [Get the URL of a queue](GetQueueUrlExample/GetQueueUrlExample/GetQueueUrl.cs) (`GetQueueUrlAsync`)
- [Receive messages from a queue](ReceiveFromQueueExample/ReceiveFromQueueExample/ReceiveFromQueue.cs) (`ReceiveMessageAsync`)
- [Receive messages from a queue and delete](ReceiveDeleteExample/ReceiveDeleteExample/ReceiveDeleteExample.cs) (`ReceiveMessageAsync`, `DeleteMessageAsync`)
- [Send a message to a queue](SendMessageToQueueExample/SendMessageToQueueExample/SendMessageToQueue.cs) (`SendMessageAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then
issue the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon Simple Queue Service Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
* [Amazon Simple Queue Service API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/index.html)
* [AWS SDK for .NET Amazon SQS](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SQS/NSQS.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0