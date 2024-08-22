# Amazon SQS code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SQS](../cross-service/TopicsAndQueues/Actions/SQSActions/HelloSQS.cs#L4) (`ListQueues`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateQueue](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L28)
- [DeleteMessage](ReceiveDeleteExample/ReceiveDeleteExample/ReceiveDeleteExample.cs#L17)
- [DeleteMessageBatch](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L154)
- [DeleteQueue](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L182)
- [GetQueueAttributes](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L74)
- [GetQueueUrl](GetQueueUrlExample/GetQueueUrlExample/GetQueueUrl.cs#L10)
- [ReceiveMessage](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L132)
- [SendMessage](CreateSendExample/CreateSendExample/CreateSendExample.cs#L8)
- [SetQueueAttributes](../cross-service/TopicsAndQueues/Actions/SQSActions/SQSWrapper.cs#L95)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Publish messages to queues](../cross-service/TopicsAndQueues/Scenarios/TopicsAndQueuesScenario/TopicsAndQueues.cs)
- [Use the AWS Message Processing Framework for .NET with Amazon SQS](../cross-service/MessageProcessingFramework)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


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

#### Use the AWS Message Processing Framework for .NET with Amazon SQS

This example shows you how to create applications that publish and receive Amazon SQS messages using the AWS Message Processing Framework for .NET.


<!--custom.scenario_prereqs.cross_MessageProcessingFrameworkTutorial.start-->
<!--custom.scenario_prereqs.cross_MessageProcessingFrameworkTutorial.end-->


<!--custom.scenarios.cross_MessageProcessingFrameworkTutorial.start-->
<!--custom.scenarios.cross_MessageProcessingFrameworkTutorial.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon SQS reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SQS/NSQS.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0