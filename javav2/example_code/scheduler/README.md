# EventBridge Scheduler code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon EventBridge Scheduler.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello EventBridge Scheduler](src/main/java/com/example/eventbrideschedule/HelloScheduler.java#L6) (`ListSchedules`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateSchedule](src/main/java/com/example/eventbrideschedule/scenario/EventbridgeSchedulerActions.java#L104)
- [CreateScheduleGroup](src/main/java/com/example/eventbrideschedule/scenario/EventbridgeSchedulerActions.java#L70)
- [DeleteSchedule](src/main/java/com/example/eventbrideschedule/scenario/EventbridgeSchedulerActions.java#L212)
- [DeleteScheduleGroup](src/main/java/com/example/eventbrideschedule/scenario/EventbridgeSchedulerActions.java#L182)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Scheduled Events](src/main/java/com/example/eventbrideschedule/scenario/EventbridgeSchedulerScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello EventBridge Scheduler

This example shows you how to get started using EventBridge Scheduler.



#### Scheduled Events

This example shows you how to do the following:

- Deploy a CloudFormation stack with required resources.
- Create a EventBridge Scheduler schedule group.
- Create a one-time EventBridge Scheduler schedule with a flexible time window.
- Create a recurring EventBridge Scheduler schedule with a specified rate.
- Delete EventBridge Scheduler the schedule and schedule group.
- Clean up resources and delete the stack.

<!--custom.scenario_prereqs.scheduler_ScheduledEventsScenario.start-->
<!--custom.scenario_prereqs.scheduler_ScheduledEventsScenario.end-->


<!--custom.scenarios.scheduler_ScheduledEventsScenario.start-->
<!--custom.scenarios.scheduler_ScheduledEventsScenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge Scheduler User Guide](https://docs.aws.amazon.com/scheduler/latest/userguide/intro.html)
- [EventBridge Scheduler API Reference](https://docs.aws.amazon.com/scheduler/latest/apireference/Welcome.html)
- [SDK for Java 2.x EventBridge Scheduler reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/firehose/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
