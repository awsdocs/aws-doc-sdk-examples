# Step Functions code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Step Functions.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Step Functions makes it easy to coordinate the components of distributed applications as a series of steps in a visual workflow._

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

- [Hello Step Functions](Actions/HelloStepFunctions.cs#L4) (`ListStateMachines`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/StepFunctionsBasics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateActivity](Actions/StepFunctionsWrapper.cs#L28)
- [CreateStateMachine](Actions/StepFunctionsWrapper.cs#L42)
- [DeleteActivity](Actions/StepFunctionsWrapper.cs#L68)
- [DeleteStateMachine](Actions/StepFunctionsWrapper.cs#L83)
- [DescribeExecution](Actions/StepFunctionsWrapper.cs#L99)
- [DescribeStateMachine](Actions/StepFunctionsWrapper.cs#L114)
- [GetActivityTask](Actions/StepFunctionsWrapper.cs#L129)
- [ListActivities](Actions/StepFunctionsWrapper.cs#L147)
- [ListExecutions](Actions/StepFunctionsWrapper.cs#L175)
- [ListStateMachines](Actions/StepFunctionsWrapper.cs#L204)
- [SendTaskSuccess](Actions/StepFunctionsWrapper.cs#L225)
- [StartExecution](Actions/StepFunctionsWrapper.cs#L243)


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

#### Hello Step Functions

This example shows you how to get started using Step Functions.


#### Learn the basics

This example shows you how to do the following:

- Create an activity.
- Create a state machine from an Amazon States Language definition that contains the previously created activity as a step.
- Run the state machine and respond to the activity with user input.
- Get the final status and output after the run completes, then clean up resources.

<!--custom.basic_prereqs.sfn_Scenario_GetStartedStateMachines.start-->
<!--custom.basic_prereqs.sfn_Scenario_GetStartedStateMachines.end-->


<!--custom.basics.sfn_Scenario_GetStartedStateMachines.start-->
<!--custom.basics.sfn_Scenario_GetStartedStateMachines.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Step Functions Developer Guide](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html)
- [Step Functions API Reference](https://docs.aws.amazon.com/step-functions/latest/apireference/Welcome.html)
- [SDK for .NET Step Functions reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/StepFunctions/NStepFunctions.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0