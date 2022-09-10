# Amazon SNS code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon Simple Notification Service (Amazon SNS).

Amazon SNS is a web service that enables applications, end users, and devices to instantly send and receive notifications from the cloud.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
- [Check whether a phone number is opted out](IsPhoneNumOptedOutExample/IsPhoneNumOptedOutExample/IsPhoneNumOptedOut.cs) (`CheckIfPhoneNumberIsOptedOutAsync`)
- [Create a topic](CreateSNSTopicExample/CreateSNSTopicExample/CreateSNSTopic.cs) (`CreateTopicAsync`)
- [Delete a topic](DeleteSNSTopicExample/DeleteSNSTopicExample/DeleteSNSTopic.cs) (`DeleteTopicAsync`)
- [Get the properties of a topic](GetTopicAttributesExample/GetTopicAttributesExample/GetTopicAttributes.cs) (`GetTopicAttributesAsync`)
- [List the subscribers of a topic](ListSNSSubscriptionsExample/ListSNSSubscriptionsExample/ListSubscriptions.cs) (`ListSubscriptionsAsync`)
- [List topics](ListSNSTopicsExample/ListSNSTopicsExample/ListSNSTopics.cs) (`ListTopicsAsync`)
- [Manage topic subscription](ManageTopicSubscriptionExample/ManageTopicSubscriptionExample/ManageTopicSubscription.cs) (`SubscribeAsync`, `UnsubscribeAsync`)
- [Publish to a topic](PublishToSNSTopicExample/PublishToSNSTopicExample/PublishToSNSTopic.cs) (`PublishAsync`)
- [Publish an SMS text message](SNSMessageExample/SNSMessageExample/SNSMessage.cs) (`PublishAsync`)

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
* [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/index.html)
* [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/index.html)
* [AWS SDK for .NET SNS](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SNS/NSNS.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
