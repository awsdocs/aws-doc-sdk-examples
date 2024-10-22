# Amazon EventBridge Scheduler Workflow

## Overview
This example shows how to use AWS SDKs to work with Amazon EventBridge Scheduler with schedules and schedule groups. The workflow demonstrates how to create and delete one-time and recurring schedules within a schedule group to generate events on a specified target, such as an Amazon Simple Notification Service (Amazon SNS) Topic.

The target SNS topic and the AWS Identity and Access Management (IAM) role used with the schedules are created as part of an AWS CloudFormation stack that is deployed at the start of the workflow, and deleted when the workflow is complete.

![Object Lock Features](../../../workflows/eventbridge_scheduler/resources/scheduler-workflow.png)

This workflow demonstrates the following steps and tasks:

1. **Prepare the Application**

   - Prompts the user for an email address to use for the subscription for the SNS topic.
   - Prompts the user for a name for the Cloud Formation stack. 
     - The user must confirm the email subscription to receive event emails.
   - Deploys the Cloud Formation template in resources/cfn_template.yaml for resource creation. 
     - Stores the outputs of the stack into variables for use in the workflow.
   - Creates a schedule group for all workflow schedules.

2. **Create a one-time Schedule**

   - Creates a one-time schedule to send an initial event. 
     - Prompts the user for a name for the one-time schedule.
     - The user must confirm the email subscription to receive an event email.
     - The content of the email should include the name of the newly created schedule.
   - Use a Flexible Time Window of 10 minutes and set the schedule to delete after completion.

3. **Create a time-based schedule**

   - Prompts the user for a rate per minutes (example: every 2 minutes) for a scheduled recurring event.
   - Creates the scheduled event for X times per hour for 1 hour.
   - Deletes the schedule when the user is finished.
     - Prompts the user to confirm when they are ready to delete the schedule.

4. **Clean up**

   - Prompts the user to confirm they want to destroy the stack and clean up all resources.
   - Deletes the schedule group.
   - Destroys the Cloud Formation stack and wait until the stack has been removed.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

Before running this workflow, ensure you have:

- An AWS account with proper permissions to use Amazon EventBridge Scheduler and Amazon EventBridge.

### AWS Services Used

This workflow uses the following AWS services:

- Amazon EventBridge Scheduler
- Amazon EventBridge
- Amazon Simple Notification Service (SNS)
- AWS CloudFormation

### Resources

The workflow scenario deploys the AWS CloudFormation stack with the required resources.

### Instructions

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .sln file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

This starts an interactive scenario that walks you through creating different types of schedules.

## Amazon EventBridge Scheduler Actions

The workflow covers the following EventBridge Scheduler API actions:

- [`CreateSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateSchedule.html)
- [`CreateScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_CreateScheduleGroup.html)
- [`DeleteSchedule`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteSchedule.html)
- [`DeleteScheduleGroup`](https://docs.aws.amazon.com/scheduler/latest/APIReference/API_DeleteScheduleGroup.html)

## Additional resources

* [EventBridge Scheduler User Guide](https://docs.aws.amazon.com/scheduler/latest/UserGuide/what-is-scheduler.html)
* [EventBridge Scheduler API Reference](https://docs.aws.amazon.com/scheduler/latest/APIReference/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
