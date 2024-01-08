# Amazon SNS code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Simple Notification Service (Amazon SNS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](src/main/java/com/example/sns/HelloSNS.java#L8) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Add tags to a topic](src/main/java/com/example/sns/AddTags.java#L11) (`TagResource`)
- [Check whether a phone number is opted out](src/main/java/com/example/sns/CheckOptOut.java#L10) (`CheckIfPhoneNumberIsOptedOut`)
- [Confirm an endpoint owner wants to receive messages](src/main/java/com/example/sns/ConfirmSubscription.java#L12) (`ConfirmSubscription`)
- [Create a topic](src/main/java/com/example/sns/CreateTopic.java#L12) (`CreateTopic`)
- [Delete a subscription](src/main/java/com/example/sns/Unsubscribe.java#L12) (`Unsubscribe`)
- [Delete a topic](src/main/java/com/example/sns/DeleteTopic.java#L11) (`DeleteTopic`)
- [Get the properties of a topic](src/main/java/com/example/sns/GetTopicAttributes.java#L12) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](src/main/java/com/example/sns/GetSMSAtrributes.java#L12) (`GetSMSAttributes`)
- [List opted out phone numbers](src/main/java/com/example/sns/ListOptOut.java#L11) (`ListPhoneNumbersOptedOut`)
- [List the subscribers of a topic](src/main/java/com/example/sns/ListSubscriptions.java#L12) (`ListSubscriptions`)
- [List topics](src/main/java/com/example/sns/ListTopics.java#L12) (`ListTopics`)
- [Publish an SMS text message](src/main/java/com/example/sns/PublishTextSMS.java#L11) (`Publish`)
- [Publish to a topic](src/main/java/com/example/sns/PublishTopic.java#L12) (`Publish`)
- [Set a dead-letter queue for a subscription](None) (`SetSubscriptionAttributesRedrivePolicy`)
- [Set a filter policy](src/main/java/com/example/sns/UseMessageFilterPolicy.java#L12) (`SetSubscriptionAttributes`)
- [Set the default settings for sending SMS messages](src/main/java/com/example/sns/SetSMSAttributes.java#L11) (`SetSMSAttributes`)
- [Set topic attributes](src/main/java/com/example/sns/SetTopicAttributes.java#L12) (`SetTopicAttributes`)
- [Subscribe a Lambda function to a topic](src/main/java/com/example/sns/SubscribeLambda.java#L12) (`Subscribe`)
- [Subscribe an HTTP endpoint to a topic](src/main/java/com/example/sns/SubscribeHTTPS.java#L12) (`Subscribe`)
- [Subscribe an email address to a topic](src/main/java/com/example/sns/SubscribeEmail.java#L12) (`Subscribe`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a platform endpoint for push notifications](src/main/java/com/example/sns/RegistrationExample.java)
- [Create and publish to a FIFO topic](src/main/java/com/example/sns/PriceUpdateExample.java)
- [Publish SMS messages to a topic](src/main/java/com/example/sns/CreateTopic.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.



#### Create a platform endpoint for push notifications

This example shows you how to create a platform endpoint for Amazon SNS push notifications.


<!--custom.scenario_prereqs.sns_CreatePlatformEndpoint.start-->
<!--custom.scenario_prereqs.sns_CreatePlatformEndpoint.end-->


<!--custom.scenarios.sns_CreatePlatformEndpoint.start-->
<!--custom.scenarios.sns_CreatePlatformEndpoint.end-->

#### Create and publish to a FIFO topic

This example shows you how to create and publish to a FIFO Amazon SNS topic.


<!--custom.scenario_prereqs.sns_PublishFifoTopic.start-->
<!--custom.scenario_prereqs.sns_PublishFifoTopic.end-->


<!--custom.scenarios.sns_PublishFifoTopic.start-->
<!--custom.scenarios.sns_PublishFifoTopic.end-->

#### Publish SMS messages to a topic

This example shows you how to do the following:

- Create an Amazon SNS topic.
- Subscribe phone numbers to the topic.
- Publish SMS messages to the topic so that all subscribed phone numbers receive the message at once.

<!--custom.scenario_prereqs.sns_UsageSmsTopic.start-->
<!--custom.scenario_prereqs.sns_UsageSmsTopic.end-->


<!--custom.scenarios.sns_UsageSmsTopic.start-->
<!--custom.scenarios.sns_UsageSmsTopic.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for Java 2.x Amazon SNS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sns/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0