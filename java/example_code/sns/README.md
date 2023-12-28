# Amazon SNS code examples for the SDK for Java 1.x

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for Java 1.x to work with Amazon Simple Notification Service (Amazon SNS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `java` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](None) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Add tags to a topic](None) (`TagResource`)
- [Check whether a phone number is opted out](None) (`CheckIfPhoneNumberIsOptedOut`)
- [Confirm an endpoint owner wants to receive messages](None) (`ConfirmSubscription`)
- [Create a topic](None) (`CreateTopic`)
- [Delete a subscription](None) (`Unsubscribe`)
- [Delete a topic](None) (`DeleteTopic`)
- [Get the properties of a topic](None) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](None) (`GetSMSAttributes`)
- [List opted out phone numbers](None) (`ListPhoneNumbersOptedOut`)
- [List the subscribers of a topic](None) (`ListSubscriptions`)
- [List topics](None) (`ListTopics`)
- [Publish an SMS text message](None) (`Publish`)
- [Publish to a topic](None) (`Publish`)
- [Set a dead-letter queue for a subscription](RedrivePolicy.java#L5) (`SetSubscriptionAttributesRedrivePolicy`)
- [Set a filter policy](None) (`SetSubscriptionAttributes`)
- [Set the default settings for sending SMS messages](None) (`SetSMSAttributes`)
- [Set topic attributes](None) (`SetTopicAttributes`)
- [Subscribe a Lambda function to a topic](None) (`Subscribe`)
- [Subscribe an HTTP endpoint to a topic](None) (`Subscribe`)
- [Subscribe an email address to a topic](None) (`Subscribe`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish a large message](PublishLargeFile.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.



#### Publish a large message

This example shows you how to publish a large message to Amazon SNS using Amazon S3 to store the message payload.


<!--custom.scenario_prereqs.sns_PublishLargeMessage.start-->
<!--custom.scenario_prereqs.sns_PublishLargeMessage.end-->


<!--custom.scenarios.sns_PublishLargeMessage.start-->
<!--custom.scenarios.sns_PublishLargeMessage.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `java` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for Java 1.x Amazon SNS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sns/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0