# Amazon SNS code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Simple Notification Service (Amazon SNS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](src/main/kotlin/com/kotlin/sns/HelloSNS.kt#L6) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateTopic](src/main/kotlin/com/kotlin/sns/CreateTopic.kt#L39)
- [DeleteTopic](src/main/kotlin/com/kotlin/sns/DeleteTopic.kt#L38)
- [GetTopicAttributes](src/main/kotlin/com/kotlin/sns/GetTopicAttributes.kt#L38)
- [ListSubscriptions](src/main/kotlin/com/kotlin/sns/ListSubscriptions.kt#L22)
- [ListTopics](src/main/kotlin/com/kotlin/sns/ListTopics.kt#L22)
- [Publish](src/main/kotlin/com/kotlin/sns/PublishTopic.kt#L39)
- [SetTopicAttributes](src/main/kotlin/com/kotlin/sns/SetTopicAttributes.kt#L41)
- [Subscribe](src/main/kotlin/com/kotlin/sns/SubscribeEmail.kt#L40)
- [TagResource](src/main/kotlin/com/kotlin/sns/AddTags.kt#L39)
- [Unsubscribe](src/main/kotlin/com/kotlin/sns/Unsubscribe.kt#L37)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish an SMS text message](src/main/kotlin/com/kotlin/sns/PublishTextSMS.kt)
- [Publish messages to queues](../../usecases/topics_and_queues/src/main/kotlin/com/example/sns/SNSWorkflow.kt)


<!--custom.examples.start-->

### Custom Examples

- **DeleteTag** - Demonstrates how to delete tags from an Amazon SNS topic.
- **ListTags** - Demonstrates how to retrieve tags from an Amazon SNS topic.
- **SubscribeTextSMS** - Demonstrates how to subscribe to an Amazon SNS text endpoint.
- **Unsubscribe** - Demonstrates how to remove an Amazon SNS subscription.
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.



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


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for Kotlin Amazon SNS reference](https://sdk.amazonaws.com/kotlin/api/latest/sns/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0