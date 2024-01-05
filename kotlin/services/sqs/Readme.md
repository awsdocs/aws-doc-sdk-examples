# Amazon SQS code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SQS](src/main/kotlin/com/kotlin/sqs/HelloSQS.kt#L9) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a queue](src/main/kotlin/com/kotlin/sqs/CreateQueue.kt#L45) (`CreateQueue`)
- [Delete a message from a queue](src/main/kotlin/com/kotlin/sqs/DeleteMessages.kt#L45) (`DeleteMessage`)
- [Delete a queue](src/main/kotlin/com/kotlin/sqs/DeleteMessages.kt#L45) (`DeleteQueue`)
- [List queues](src/main/kotlin/com/kotlin/sqs/ListQueues.kt#L28) (`ListQueues`)
- [Receive messages from a queue](src/main/kotlin/com/kotlin/sqs/ReceiveMessages.kt#L44) (`ReceiveMessage`)
- [Send a message to a queue](src/main/kotlin/com/kotlin/sqs/SendMessages.kt#L50) (`SendMessage`)


<!--custom.examples.start-->

### Custom Examples

- **AddQueueTags** - Demonstrates how to add tags to an Amazon SQS queue.
- **ListQueueTags** - Demonstrates how to retrieve tags from an Amazon SQS queue.
- **RemoveQueueTag** - Demonstrates how to remove a tag from an Amazon SQS queue.
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
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for Kotlin Amazon SQS reference](https://sdk.amazonaws.com/kotlin/api/latest/sqs/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0