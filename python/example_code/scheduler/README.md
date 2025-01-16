# EventBridge Scheduler code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon EventBridge Scheduler.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello EventBridge Scheduler](hello/hello_scheduler.py#L4) (`ListSchedules`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateSchedule](scheduler_wrapper.py#L38)
- [CreateScheduleGroup](scheduler_wrapper.py#L131)
- [DeleteSchedule](scheduler_wrapper.py#L104)
- [DeleteScheduleGroup](scheduler_wrapper.py#L160)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Scheduled Events](scenario/scheduler_scenario.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello EventBridge Scheduler

This example shows you how to get started using EventBridge Scheduler.

```
python hello/hello_scheduler.py
```


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

Start the example by running the following at a command prompt:

```
python scenario/scheduler_scenario.py
```


<!--custom.scenarios.scheduler_ScheduledEventsScenario.start-->
<!--custom.scenarios.scheduler_ScheduledEventsScenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [EventBridge Scheduler User Guide](https://docs.aws.amazon.com/scheduler/latest/userguide/intro.html)
- [EventBridge Scheduler API Reference](https://docs.aws.amazon.com/scheduler/latest/apireference/Welcome.html)
- [SDK for Python EventBridge Scheduler reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/scheduler.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
