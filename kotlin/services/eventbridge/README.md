# EventBridge code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon EventBridge.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello EventBridge](src/main/kotlin/com/kotlin/eventbridge/HelloEventBridge.kt#L5) (`ListEventBuses`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteRule](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L339)
- [DescribeRule](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L493)
- [DisableRule](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L507)
- [EnableRule](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L507)
- [ListRuleNamesByTarget](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L557)
- [ListRules](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L671)
- [ListTargetsByRule](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L573)
- [PutEvents](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L380)
- [PutRule](src/main/kotlin/com/kotlin/eventbridge/CreateRuleSchedule.kt#L49)
- [PutTargets](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L589)
- [RemoveTargets](src/main/kotlin/com/kotlin/eventbridge/EventbridgeMVP.kt#L352)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello EventBridge

This example shows you how to get started using EventBridge.


#### Learn the basics

This example shows you how to do the following:

- Create a rule and add a target to it.
- Enable and disable rules.
- List and update rules and targets.
- Send events, then clean up resources.

<!--custom.basic_prereqs.eventbridge_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.eventbridge_Scenario_GettingStarted.end-->


<!--custom.basics.eventbridge_Scenario_GettingStarted.start-->
<!--custom.basics.eventbridge_Scenario_GettingStarted.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge User Guide](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-what-is.html)
- [EventBridge API Reference](https://docs.aws.amazon.com/eventbridge/latest/APIReference/Welcome.html)
- [SDK for Kotlin EventBridge reference](https://sdk.amazonaws.com/kotlin/api/latest/eventbridge/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0