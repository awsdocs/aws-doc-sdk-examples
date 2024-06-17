---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_019EaAEh3h4WrZcxj1mknWpr
  lastRun: 2024-06-10T21:59:25.32Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 334
    inputTokenCount: 1470
    invocationLatency: 7070
    outputTokenCount: 797
prompt: The text of SPECIFICATION.md
---
# Workflow Specification: EventBridge Scheduler

This document outlines the specification for the EventBridge Scheduler Workflow example.

## Objectives

The main objectives of this workflow are:

1. Demonstrate how to use the Amazon EventBridge Scheduler service to create, manage, and schedule tasks.
2. Show how to integrate EventBridge Scheduler with other AWS services, such as Amazon EventBridge, to trigger events.
3. Provide a reusable example that can be adapted and extended by developers to fit their specific use cases.

## Workflow Steps

The workflow consists of the following steps:

1. **Prepare the Application**:
   - Prompt the user for an email address to use for the subscription for the SNS topic subscription.
   - Prompt the user for a name for the Cloud Formation stack.
   - Deploy the Cloud Formation template in resources/cfn_template.yaml for resource creation. 
   - Store the outputs of the stack into variables for use in the workflow.
   - Create a schedule group for all workflow schedules.

2. **Create One-Time Schedule**:
   - Create a one-time schedule to send an initial event on the new Event Bus.
   - Use a Flexible Time Window and set the schedule to delete after completion.
   - Print a URL for the user to view logs for the Event Bus.

3. **Create a time-based schedule**:
   - Prompt the user for how many X times per Y hours a recurring event should be scheduled.
   - Create the scheduled event for X times per hour for Y hours.
   - Print a URL for the user to view logs for the Event Bus.
   - Delete the schedule when the user is finished.

4. **Clean up**:
   - Prompt the user for y/n answer if they want to destroy the stack and clean up all resources.
   - Delete the schedule group.
   - Destroy the Cloud Formation stack and wait until the stack has been removed.

## AWS Services Used

This workflow utilizes the following AWS services:

- **Amazon EventBridge Scheduler**: Used to create, manage, and schedule tasks.
- **Amazon EventBridge**: Used to trigger events based on the schedules created by EventBridge Scheduler.
- **AWS CloudFormation**: Used to deploy the necessary resources for the workflow.

## EventBridge Scheduler Actions

The workflow covers the following EventBridge Scheduler API actions:

- [`CreateSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateSchedule.html)
- [`CreateScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateScheduleGroup.html)
- [`DeleteSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteSchedule.html)
- [`DeleteScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteScheduleGroup.html)

## Workflow Outputs

The workflow should provide the following outputs:

1. URLs for the user to view logs for the events triggered by the EventBridge Scheduler schedules.
2. Confirmation of successful creation and deletion of schedules and schedule groups.
3. Confirmation of successful destruction of the CloudFormation stack and cleanup of resources.

## Implementation

The workflow should be implemented in the following programming language:

- C#

## Testing

The workflow should be thoroughly tested to ensure it meets the specified requirements and handles various error scenarios gracefully.