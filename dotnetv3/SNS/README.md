# Amazon SNS code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Simple Notification Service (Amazon SNS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](../cross-service/TopicsAndQueues/Actions/SNSActions/HelloSNS.cs#L4) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CheckIfPhoneNumberIsOptedOut](IsPhoneNumOptedOutExample/IsPhoneNumOptedOutExample/IsPhoneNumOptedOut.cs#L6)
- [CreateTopic](CreateSNSTopicExample/CreateSNSTopicExample/CreateSNSTopic.cs#L6)
- [DeleteTopic](../cross-service/TopicsAndQueues/Actions/SNSActions/SNSWrapper.cs#L154)
- [GetTopicAttributes](GetTopicAttributesExample/GetTopicAttributesExample/GetTopicAttributes.cs#L6)
- [ListSubscriptions](ListSNSSubscriptionsExample/ListSNSSubscriptionsExample/ListSubscriptions.cs#L6)
- [ListTopics](ListSNSTopicsExample/ListSNSTopicsExample/ListSNSTopics.cs#L6)
- [Publish](PublishToSNSTopicExample/PublishToSNSTopicExample/PublishToSNSTopic.cs#L6)
- [Subscribe](ManageTopicSubscriptionExample/ManageTopicSubscriptionExample/ManageTopicSubscription.cs#L38)
- [Unsubscribe](../cross-service/TopicsAndQueues/Actions/SNSActions/SNSWrapper.cs#L137)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Building an Amazon SNS application](../cross-service/SubscribePublishTranslate)
- [Create a serverless application to manage photos](../cross-service/PhotoAssetManager)
- [Publish an SMS text message](SNSMessageExample/SNSMessageExample/SNSMessage.cs)
- [Publish messages to queues](../cross-service/TopicsAndQueues/Scenarios/TopicsAndQueuesScenario/TopicsAndQueues.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.



#### Building an Amazon SNS application

This example shows you how to create an application that has subscription and publish functionality and translates messages.


<!--custom.scenario_prereqs.cross_SnsPublishSubscription.start-->
<!--custom.scenario_prereqs.cross_SnsPublishSubscription.end-->


<!--custom.scenarios.cross_SnsPublishSubscription.start-->
<!--custom.scenarios.cross_SnsPublishSubscription.end-->

#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Publish an SMS text message

This example shows you how to publish SMS messages using Amazon SNS.


<!--custom.scenario_prereqs.sns_PublishTextSMS.start-->
<!--custom.scenario_prereqs.sns_PublishTextSMS.end-->


<!--custom.scenarios.sns_PublishTextSMS.start-->
<!--custom.scenarios.sns_PublishTextSMS.end-->

#### Publish messages to queues

This example shows you how to do the following:

- Create topic (FIFO or non-FIFO).
- Subscribe several queues to the topic with an option to apply a filter.
- Publish messages to the topic.
- Poll the queues for messages received.

<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.start-->
<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.end-->


<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.start-->
<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for .NET Amazon SNS reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SNS/NSNS.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0