# Amazon SQS code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a queue](queue_wrapper.py#L23) (`CreateQueue`)
- [Delete a batch of messages from a queue](message_wrapper.py#L153) (`DeleteMessageBatch`)
- [Delete a message from a queue](message_wrapper.py#L132) (`DeleteMessage`)
- [Delete a queue](queue_wrapper.py#L95) (`DeleteQueue`)
- [Get the URL of a queue](queue_wrapper.py#L50) (`GetQueueUrl`)
- [List queues](queue_wrapper.py#L71) (`ListQueues`)
- [Receive messages from a queue](message_wrapper.py#L100) (`ReceiveMessage`)
- [Send a batch of messages to a queue](message_wrapper.py#L52) (`SendMessageBatch`)
- [Send a message to a queue](message_wrapper.py#L24) (`SendMessage`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Send and receive batches of messages](message_wrapper.py)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a messenger application](../../cross_service/stepfunctions_messenger)
- [Create an Amazon Textract explorer application](../../cross_service/textract_explorer)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Send and receive batches of messages

This example shows you how to do the following:

- Create an Amazon SQS queue.
- Send batches of messages to the queue.
- Receive batches of messages from the queue.
- Delete batches of messages from the queue.

<!--custom.scenario_prereqs.sqs_Scenario_SendReceiveBatch.start-->
<!--custom.scenario_prereqs.sqs_Scenario_SendReceiveBatch.end-->

Start the example by running the following at a command prompt:

```
python message_wrapper.py
```


<!--custom.scenarios.sqs_Scenario_SendReceiveBatch.start-->
<!--custom.scenarios.sqs_Scenario_SendReceiveBatch.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for Python Amazon SQS reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sqs.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0