# Amazon SQS code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Simple Queue Service (Amazon SQS).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a queue](zcl_aws1_sqs_actions.clas.abap#L70) (`CreateQueue`)
- [Delete a queue](zcl_aws1_sqs_actions.clas.abap#L89) (`DeleteQueue`)
- [Get the URL of a queue](zcl_aws1_sqs_actions.clas.abap#L104) (`GetQueueUrl`)
- [List queues](zcl_aws1_sqs_actions.clas.abap#L121) (`ListQueues`)
- [Receive messages from a queue](zcl_aws1_sqs_actions.clas.abap#L184) (`ReceiveMessage`)
- [Send a message to a queue](zcl_aws1_sqs_actions.clas.abap#L202) (`SendMessage`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP Amazon SQS reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/sqs/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0