# Amazon SQS code examples for the SDK for JavaScript (v2)

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for JavaScript (v2) to work with Amazon Simple Queue Service (Amazon SQS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SQS is a fully managed message queuing service that makes it easy to decouple and scale microservices, distributed systems, and serverless applications._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascript` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SQS](None) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Change message timeout visibility](sqs_changingvisibility.js#L28) (`ChangeMessageVisibility`)
- [Configure a dead-letter queue](None) (`SetQueueAttributes`)
- [Create a queue](sqs_createqueue.js#L28) (`CreateQueue`)
- [Delete a batch of messages from a queue](None) (`DeleteMessageBatch`)
- [Delete a message from a queue](sqs_receivemessage.js#L28) (`DeleteMessage`)
- [Delete a queue](sqs_deletequeue.js#L29) (`DeleteQueue`)
- [Get attributes for a queue](None) (`GetQueueAttributes`)
- [Get the URL of a queue](sqs_getqueueurl.js#L28) (`GetQueueUrl`)
- [List queues](sqs_listqueues.js#L28) (`ListQueues`)
- [Receive messages from a queue](sqs_longpolling_receivemessage.js#L28) (`ReceiveMessage`)
- [Send a message to a queue](sqs_sendmessage.js#L28) (`SendMessage`)
- [Set queue attributes](None) (`SetQueueAttributes`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SQS

This example shows you how to get started using Amazon SQS.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascript` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v2) Amazon SQS reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Sqs.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0