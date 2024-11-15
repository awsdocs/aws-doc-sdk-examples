# EventBridge code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon EventBridge.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello EventBridge](Actions/HelloEventBridge.cs#L4) (`ListEventBuses`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/EventBridgeScenario.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteRule](Actions/EventBridgeWrapper.cs#L433)
- [DescribeRule](Actions/EventBridgeWrapper.cs#L34)
- [DisableRule](Actions/EventBridgeWrapper.cs#L70)
- [EnableRule](Actions/EventBridgeWrapper.cs#L53)
- [ListRuleNamesByTarget](Actions/EventBridgeWrapper.cs#L140)
- [ListRules](Actions/EventBridgeWrapper.cs#L87)
- [ListTargetsByRule](Actions/EventBridgeWrapper.cs#L114)
- [PutEvents](Actions/EventBridgeWrapper.cs#L290)
- [PutRule](Actions/EventBridgeWrapper.cs#L166)
- [PutTargets](Actions/EventBridgeWrapper.cs#L347)
- [RemoveTargets](Actions/EventBridgeWrapper.cs#L391)


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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge User Guide](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-what-is.html)
- [EventBridge API Reference](https://docs.aws.amazon.com/eventbridge/latest/APIReference/Welcome.html)
- [SDK for .NET EventBridge reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EventBridge/NEventBridge.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0