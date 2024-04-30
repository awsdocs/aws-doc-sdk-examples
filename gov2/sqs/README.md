# Amazon SQS code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SQS](hello/hello.go#L4) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateQueue](../workflows/topics_and_queues/actions/sqs_actions.go#L28)
- [DeleteMessageBatch](../workflows/topics_and_queues/actions/sqs_actions.go#L148)
- [DeleteQueue](../workflows/topics_and_queues/actions/sqs_actions.go#L170)
- [GetQueueAttributes](../workflows/topics_and_queues/actions/sqs_actions.go#L53)
- [ListQueues](hello/hello.go#L4)
- [ReceiveMessage](../workflows/topics_and_queues/actions/sqs_actions.go#L128)
- [SetQueueAttributes](../workflows/topics_and_queues/actions/sqs_actions.go#L74)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish messages to queues](../workflows/topics_and_queues/workflows/scenario_topics_and_queues.go)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SQS

This example shows you how to get started using Amazon SQS.

```
go run ./hello
```

#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```

#### Publish messages to queues

This example shows you how to do the following:

- Create topic (FIFO or non-FIFO).
- Subscribe several queues to the topic with an option to apply a filter.
- Publish messages to the topic.
- Poll the queues for messages received.

<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.start-->
<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.end-->


<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.start-->
The code for this example is in the `gov2/workflows/topics_and_queues` folder.
Instructions for how to run are in the [README](../workflows/topics_and_queues/README.md) in the same folder.
<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for Go V2 Amazon SQS reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/sqs)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0