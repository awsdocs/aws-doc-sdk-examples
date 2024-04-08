# Amazon SNS code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon Simple Notification Service (Amazon SNS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Check whether a phone number is opted out](CheckOptOut.php#L10) (`CheckIfPhoneNumberIsOptedOut`)
- [Confirm an endpoint owner wants to receive messages](ConfirmSubscription.php#L10) (`ConfirmSubscription`)
- [Create a topic](CreateTopic.php#L10) (`CreateTopic`)
- [Delete a subscription](Unsubscribe.php#L10) (`Unsubscribe`)
- [Delete a topic](DeleteTopic.php#L10) (`DeleteTopic`)
- [Get the properties of a topic](GetTopicAttributes.php#L26) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](GetSMSAtrributes.php#L10) (`GetSMSAttributes`)
- [List opted out phone numbers](ListOptOut.php#L10) (`ListPhoneNumbersOptedOut`)
- [List the subscribers of a topic](ListSubscriptions.php#L10) (`ListSubscriptions`)
- [List topics](ListTopics.php#L10) (`ListTopics`)
- [Publish to a topic](PublishTopic.php#L10) (`Publish`)
- [Set the default settings for sending SMS messages](SetSMSAttributes.php#L26) (`SetSMSAttributes`)
- [Set topic attributes](SetTopicAttributes.php#L10) (`SetTopicAttributes`)
- [Subscribe an email address to a topic](SubscribeEmail.php#L10) (`Subscribe`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish an SMS text message](PublishTextSMS.php)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a serverless application to manage photos](../../applications/photo_asset_manager)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Publish an SMS text message

This example shows you how to publish SMS messages using Amazon SNS.


<!--custom.scenario_prereqs.sns_PublishTextSMS.start-->
<!--custom.scenario_prereqs.sns_PublishTextSMS.end-->


<!--custom.scenarios.sns_PublishTextSMS.start-->
<!--custom.scenarios.sns_PublishTextSMS.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for PHP Amazon SNS reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Sns.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0