# Amazon Simple Queue Service (Amazon SQS) code examples in C\#

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET 3.x to
get started using queue and message operations in Amazon Simple Queue Service
(Amazon SQS). The examples will show you how to create a queue and send, receive,
and delete messages from it.

## Code examples

- [AuthorizeS3ToSendMessageExample](AuthorizeS2ToSendMessageExample/) - Authorizes
  an Amazon Simple Storage Service (Amazon S3) bucket to post message to an
  Amazon SQS queue.
- [CreateQueueExample](CreateQueueExample/) - Creates an Amazon SQS queue.
- [CreateSendExample](CreateSendExample/) - Create an SQS queue and sends a
  sample message.
- [DeleteMessageExample](DeleteMessageExample/) | Deletes messages from an
  Amazon SQS queue.
- [DeleteQueueExample](DeleteQueueExample/) | Deletes an Amazon SQS queue. Run
  this example with caution as when you run it, any existing messages in the
  queue will also be removed.
- [GetQueueAttributesExample](GetQueueAttributesExample/) | Retrieves and
  displays the attributes of an Amazon SQS queue.
- [GetQueueUrlExample](GetQueueUrlExample/) | Retrieves the URL for an Amazon
  SQS queue.
- [ReceiveDeleteExample](ReceivDeleteExample/) | Receives a single message from
  the queue and then deletes it from the queue.
- [ReceiveFromQueueExample](ReceiveFromQueueExample/) | Receives a message from
  an Amazon SQS queue.
- [SendMessageToQueueExample](SendMessageToQueueExample/) | Sends a message to
  an Amazon SQS queue.
- [Send messages to an existing SQS queue.](from-developer-guide/SQSSendMessages.cs) (`SQSSendMessages.cs`, additional guidance in [developer guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/sqs-apis-intro.html))
- [Receive messages from an existing SQS queue.](from-developer-guide/SQSReceiveMessages.cs) (`SQSReceiveMessages.cs`, additional guidance in [developer guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/sqs-apis-intro.html))

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
initialize the Rekognition client does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonRekognitionClient(Amazon.RegionEndpoint.USWest2);
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

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
