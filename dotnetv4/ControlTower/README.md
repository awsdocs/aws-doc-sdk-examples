# AWS Control Tower code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with AWS Control Tower.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Control Tower](Actions/HelloControlTower.cs#L4) (`ListBaselines`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/ControlTower_Basics/ControlTowerBasics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DisableBaseline](Actions/ControlTowerWrapper.cs#L185)
- [DisableControl](Actions/ControlTowerWrapper.cs#L405)
- [EnableBaseline](Actions/ControlTowerWrapper.cs#L120)
- [EnableControl](Actions/ControlTowerWrapper.cs#L349)
- [GetBaselineOperation](Actions/ControlTowerWrapper.cs#L277)
- [GetControlOperation](Actions/ControlTowerWrapper.cs#L453)
- [ListBaselines](Actions/ControlTowerWrapper.cs#L62)
- [ListEnabledBaselines](Actions/ControlTowerWrapper.cs#L91)
- [ListEnabledControls](Actions/ControlTowerWrapper.cs#L309)
- [ListLandingZones](Actions/ControlTowerWrapper.cs#L33)
- [ResetEnabledBaseline](Actions/ControlTowerWrapper.cs#L231)


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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Control Tower User Guide](https://docs.aws.amazon.com/controltower/latest/userguide/what-is-control-tower.html)
- [AWS Control Tower API Reference](https://docs.aws.amazon.com/controltower/latest/APIReference/Welcome.html)
- [SDK for .NET (v4) AWS Control Tower reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/Controltower/NControltower.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
