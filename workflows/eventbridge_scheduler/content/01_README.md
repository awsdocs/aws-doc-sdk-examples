---
prompt: The text of README.md
---

# Amazon EventBridge Scheduler Workflow

This workflow demonstrates how to use the Amazon EventBridge Scheduler service to schedule and manage tasks.

1. **Prepare the Application**

   - Prompt the user for an email address to use for the subscription for the SNS topic subscription.
   - Prompt the user for a name for the Cloud Formation stack.
   - Deploy the Cloud Formation template in resources/cfn_template.yaml for resource creation. 
   - Store the outputs of the stack into variables for use in the workflow.
   - Create a schedule group for all workflow schedules.

2. **Create One-Time Schedule**

   - Create a one-time schedule to send an initial event on the new Event Bus.
   - Use a Flexible Time Window and set the schedule to delete after completion.
   - Print a URL for the user to view logs for the Event Bus.

3. **Create a time-based schedule**

   - Prompt the user for how many X times per Y hours a recurring event should be scheduled.
   - Create the scheduled event for X times per hour for Y hours.
   - Print a URL for the user to view logs for the Event Bus.
   - Delete the schedule when the user is finished.

4. **Clean up**

   - Prompt the user for y/n answer if they want to destroy the stack and clean up all resources.
   - Delete the schedule group.
   - Destroy the Cloud Formation stack and wait until the stack has been removed.

## Prerequisites

Before running this workflow, ensure you have:

- An AWS account with proper permissions to use AAmazon EventBridge Scheduler and Amazon EventBridge.

## AWS Services Used

This workflow uses the following AWS services:

- Amazon EventBridge Scheduler
- Amazon EventBridge

## Amazon EventBridge Scheduler Actions

The workflow covers the following EventBridge Scheduler API actions:

- [`CreateSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateSchedule.html)
- [`CreateScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateScheduleGroup.html)
- [`DeleteSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteSchedule.html)
- [`DeleteScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteScheduleGroup.html)

## Implementations

This example is implemented in the following languages:

- [.NET](../../dotnetv3/EventBridge Scheduler/README.md)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
