# EventBridge Scheduler code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon EventBridge Scheduler.

<!--custom.overview.start-->
<!--custom.overview.end-->

_EventBridge Scheduler allows you to create, run, and manage tasks on a schedule from one central, managed service._

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

- [Hello EventBridge Scheduler](Actions/HelloScheduler.cs#L11) (`ListSchedules`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateSchedule](Actions/SchedulerWrapper.cs#L30)
- [CreateScheduleGroup](Actions/SchedulerWrapper.cs#L103)
- [DeleteSchedule](Actions/SchedulerWrapper.cs#L136)
- [DeleteScheduleGroup](Actions/SchedulerWrapper.cs#L174)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Scheduled Events workflow](Scenarios/SchedulerWorkflow.cs)


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

#### Hello EventBridge Scheduler

This example shows you how to get started using EventBridge Scheduler.



#### Scheduled Events workflow

This example shows you how to do the following:

- Deploy a CloudFormation stack with required resources.
- Create a EventBridge Scheduler schedule group.
- Create a one-time EventBridge Scheduler schedule with a flexible time window.
- Create a recurring EventBridge Scheduler schedule with a specified rate.
- Delete EventBridge Scheduler the schedule and schedule group.
- Clean up resources and delete the stack.

<!--custom.scenario_prereqs.scheduler_ScheduledEventsWorkflow.start-->
<!--custom.scenario_prereqs.scheduler_ScheduledEventsWorkflow.end-->


<!--custom.scenarios.scheduler_ScheduledEventsWorkflow.start-->
<!--custom.scenarios.scheduler_ScheduledEventsWorkflow.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge Scheduler User Guide](https://docs.aws.amazon.com/scheduler/latest/userguide/intro.html)
- [EventBridge Scheduler API Reference](https://docs.aws.amazon.com/scheduler/latest/apireference/Welcome.html)
- [SDK for .NET EventBridge Scheduler reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Scheduler/NScheduler.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0