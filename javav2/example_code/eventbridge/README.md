# EventBridge code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon EventBridge.

<!--custom.overview.start-->
<!--custom.overview.end-->

_EventBridge is a serverless event bus service that makes it easy to connect your applications with data from a variety of sources._

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

- [Hello EventBridge](src/main/java/com/example/eventbridge/HelloEventBridge.java#L16) (`ListEventBuses`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteRule](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L374)
- [DescribeRule](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L509)
- [DisableRule](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L526)
- [EnableRule](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L526)
- [ListRuleNamesByTarget](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L577)
- [ListRules](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L649)
- [ListTargetsByRule](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L591)
- [PutEvents](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L408)
- [PutRule](src/main/java/com/example/eventbridge/CreateRuleSchedule.java#L60)
- [PutTargets](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L605)
- [RemoveTargets](src/main/java/com/example/eventbridge/EventbridgeMVP.java#L385)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with rules and targets](src/main/java/com/example/eventbridge/EventbridgeMVP.java)
- [Send event notifications to EventBridge](../s3/src/main/java/com/example/s3/PutBucketS3EventNotificationEventBridge.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello EventBridge

This example shows you how to get started using EventBridge.



#### Get started with rules and targets

This example shows you how to do the following:

- Create a rule and add a target to it.
- Enable and disable rules.
- List and update rules and targets.
- Send events, then clean up resources.

<!--custom.scenario_prereqs.eventbridge_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.eventbridge_Scenario_GettingStarted.end-->


<!--custom.scenarios.eventbridge_Scenario_GettingStarted.start-->
<!--custom.scenarios.eventbridge_Scenario_GettingStarted.end-->

#### Send event notifications to EventBridge

This example shows you how to enable a bucket to send S3 event notifications to EventBridge and route notifications to an Amazon SNS topic and Amazon SQS queue.


<!--custom.scenario_prereqs.s3_Scenario_PutBucketNotificationConfiguration.start-->
<!--custom.scenario_prereqs.s3_Scenario_PutBucketNotificationConfiguration.end-->


<!--custom.scenarios.s3_Scenario_PutBucketNotificationConfiguration.start-->
<!--custom.scenarios.s3_Scenario_PutBucketNotificationConfiguration.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge User Guide](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-what-is.html)
- [EventBridge API Reference](https://docs.aws.amazon.com/eventbridge/latest/APIReference/Welcome.html)
- [SDK for Java 2.x EventBridge reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/eventbridge/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0