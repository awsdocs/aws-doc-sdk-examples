# Amazon SNS code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon Simple Notification Service (Amazon SNS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Check whether a phone number is opted out](CheckOptOut.php#L19) (`CheckIfPhoneNumberIsOptedOut`)
- [Confirm an endpoint owner wants to receive messages](ConfirmSubscription.php#L19) (`ConfirmSubscription`)
- [Create a topic](CreateTopic.php#L19) (`CreateTopic`)
- [Delete a subscription](Unsubscribe.php#L19) (`Unsubscribe`)
- [Delete a topic](DeleteTopic.php#L19) (`DeleteTopic`)
- [Get the properties of a topic](GetTopicAttributes.php#L34) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](GetSMSAtrributes.php#L19) (`GetSMSAttributes`)
- [List opted out phone numbers](ListOptOut.php#L19) (`ListPhoneNumbersOptedOut`)
- [List the subscribers of a topic](ListSubscriptions.php#L19) (`ListSubscriptions`)
- [List topics](ListTopics.php#L19) (`ListTopics`)
- [Publish an SMS text message](PublishTextSMS.php#L19) (`Publish`)
- [Publish to a topic](PublishTopic.php#L19) (`Publish`)
- [Set the default settings for sending SMS messages](SetSMSAttributes.php#L34) (`SetSMSAttributes`)
- [Set topic attributes](SetTopicAttributes.php#L19) (`SetTopicAttributes`)
- [Subscribe an HTTP endpoint to a topic](SubscribeHTTPS.php#L19) (`Subscribe`)
- [Subscribe an email address to a topic](SubscribeEmail.php#L19) (`Subscribe`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



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