# AWS Control Tower code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Control Tower.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Control Tower enables you to enforce and manage governance rules for security, operations, and compliance at scale across all your organizations and accounts._

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

- [Hello AWS Control Tower](src/main/java/com/example/controltower/HelloControlTower.java#L28) (`ListBaselines`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/controltower/ControlTowerActions.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DisableBaseline](src/main/java/com/example/controltower/ControlTowerActions.java#L241)
- [DisableControl](src/main/java/com/example/controltower/ControlTowerActions.java#L431)
- [EnableBaseline](src/main/java/com/example/controltower/ControlTowerActions.java#L188)
- [EnableControl](src/main/java/com/example/controltower/ControlTowerActions.java#L377)
- [GetBaselineOperation](src/main/java/com/example/controltower/ControlTowerActions.java#L38)
- [GetControlOperation](src/main/java/com/example/controltower/ControlTowerActions.java#L474)
- [ListBaselines](src/main/java/com/example/controltower/ControlTowerActions.java#L88)
- [ListEnabledBaselines](src/main/java/com/example/controltower/ControlTowerActions.java#L138)
- [ListEnabledControls](src/main/java/com/example/controltower/ControlTowerActions.java#L324)
- [ListLandingZones](src/main/java/com/example/controltower/ControlTowerActions.java#L38)
- [ResetEnabledBaseline](src/main/java/com/example/controltower/ControlTowerActions.java#L548)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Control Tower

This example shows you how to get started using AWS Control Tower.


#### Learn the basics

This example shows you how to do the following:

- List landing zones.
- List, enable, get, reset, and disable baselines.
- List, enable, get, and disable controls.

<!--custom.basic_prereqs.controltower_Scenario.start-->
<!--custom.basic_prereqs.controltower_Scenario.end-->


<!--custom.basics.controltower_Scenario.start-->
<!--custom.basics.controltower_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Control Tower User Guide](https://docs.aws.amazon.com/controltower/latest/userguide/what-is-control-tower.html)
- [AWS Control Tower API Reference](https://docs.aws.amazon.com/controltower/latest/APIReference/Welcome.html)
- [SDK for Java 2.x AWS Control Tower reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/controltower/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
