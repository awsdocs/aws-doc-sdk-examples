# Amazon SNS code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Simple Notification Service (Amazon SNS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](hello.js#L8) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Check whether a phone number is opted out](libs/snsClient.js#L6) (`CheckIfPhoneNumberIsOptedOut`)
- [Confirm an endpoint owner wants to receive messages](libs/snsClient.js#L6) (`ConfirmSubscription`)
- [Create a topic](libs/snsClient.js#L6) (`CreateTopic`)
- [Delete a subscription](libs/snsClient.js#L6) (`Unsubscribe`)
- [Delete a topic](libs/snsClient.js#L6) (`DeleteTopic`)
- [Get the properties of a topic](libs/snsClient.js#L6) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](libs/snsClient.js#L6) (`GetSMSAttributes`)
- [List the subscribers of a topic](libs/snsClient.js#L6) (`ListSubscriptions`)
- [List topics](libs/snsClient.js#L6) (`ListTopics`)
- [Publish a message with an attribute](../cross-services/wkflw-topics-queues/TopicsQueuesWkflw.js#L269) (`Publish`)
- [Publish to a topic](libs/snsClient.js#L6) (`Publish`)
- [Set the default settings for sending SMS messages](libs/snsClient.js#L6) (`SetSMSAttributes`)
- [Set topic attributes](libs/snsClient.js#L6) (`SetTopicAttributes`)
- [Subscribe a Lambda function to a topic](libs/snsClient.js#L6) (`Subscribe`)
- [Subscribe a mobile application to a topic](libs/snsClient.js#L6) (`Subscribe`)
- [Subscribe an SQS queue to a topic](actions/subscribe-queue.js#L8) (`Subscribe`)
- [Subscribe an email address to a topic](libs/snsClient.js#L6) (`Subscribe`)
- [Subscribe with a filter to a topic](actions/subscribe-queue-filtered.js#L8) (`Subscribe`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**
Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.

```bash
node ./hello.js
```


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for JavaScript (v3) Amazon SNS reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/sns)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0