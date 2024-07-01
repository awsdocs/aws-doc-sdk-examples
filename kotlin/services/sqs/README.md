# Amazon SQS code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SQS](src/main/kotlin/com/kotlin/sqs/HelloSQS.kt#L4) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateQueue](src/main/kotlin/com/kotlin/sqs/CreateQueue.kt#L38)
- [DeleteMessage](src/main/kotlin/com/kotlin/sqs/DeleteMessages.kt#L38)
- [DeleteQueue](src/main/kotlin/com/kotlin/sqs/DeleteMessages.kt#L38)
- [ListQueues](src/main/kotlin/com/kotlin/sqs/ListQueues.kt#L22)
- [ReceiveMessage](src/main/kotlin/com/kotlin/sqs/ReceiveMessages.kt#L37)
- [SendMessage](src/main/kotlin/com/kotlin/sqs/SendMessages.kt#L43)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish messages to queues](../../usecases/topics_and_queues/src/main/kotlin/com/example/sns/SNSWorkflow.kt)


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