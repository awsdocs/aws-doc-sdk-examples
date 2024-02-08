# Amazon SQS code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Simple Queue Service (Amazon SQS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SQS is a fully managed message queuing service that makes it easy to decouple and scale microservices, distributed systems, and serverless applications._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
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

- [Hello Amazon SQS](src/main/java/com/example/sqs/HelloSQS.java#L6) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a queue](src/main/java/com/example/sqs/SQSExample.java#L6) (`CreateQueue`)
- [Delete a message from a queue](src/main/java/com/example/sqs/SQSExample.java#L189) (`DeleteMessage`)
- [Delete a queue](src/main/java/com/example/sqs/DeleteQueue.java#L6) (`DeleteQueue`)
- [Get the URL of a queue](src/main/java/com/example/sqs/SQSExample.java#L66) (`GetQueueUrl`)
- [List queues](src/main/java/com/example/sqs/SQSExample.java#L82) (`ListQueues`)
- [Receive messages from a queue](src/main/java/com/example/sqs/SQSExample.java#L152) (`ReceiveMessage`)
- [Send a batch of messages to a queue](src/main/java/com/example/sqs/SQSExample.java#L132) (`SendMessageBatch`)
- [Send a message to a queue](src/main/java/com/example/sqs/SendMessages.java#L7) (`SendMessage`)


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
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon SQS reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sqs/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0