# EventBridge Scheduler Scenario - Technical Specification

This document contains the technical specifications for _Amazon Eventbridge Scheduler Scenario_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Sample reference output.
- Suggested error handling.
- Metadata information for the scenario.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Errors](#errors)
- [Metadata](#metadata)

## Resources and User Input

- The scenario deploys an AWS CloudFormation stack with the following resources:
    - An Amazon Simple Notification Service (Amazon SNS) topic.
    - An email subscription to the topic. The user must confirm their subscription to receive email notifications for the scheduled events.
    - An AWS Identity and Access Management (IAM) Role with permission for the scheduler service to assume the role and publish to SNS.
- The scenario should prompt the user for a stack name.
- The scenario should check that the stack name does not already exist.
- The scenario should wait for the stack CREATE_COMPLETE status.
  - After a successful deploy, the scenario should retrieve and print the output values for `RoleARN` and `SNStopicARN`, which will be used for the EventBridge Scheduler operations.
- If the stack has any failed status, notify the user and end the scenario.
 
Example:
```
--------------------------------------------------------------------------------
Welcome to the Amazon EventBridge Scheduler Scenario.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Preparing the application...

This example creates resources in a CloudFormation stack, including an SNS topic
that will be subscribed to the EventBridge Scheduler events.

You will need to confirm the subscription in order to receive event emails.
Enter an email address to use for event subscriptions:
test@example.com
Enter a name for the AWS Cloud Formation Stack:
teststack10

Deploying CloudFormation stack: teststack10
Enter a name for the AWS Cloud Formation Stack:
warn: SchedulerScenario.SchedulerWorkflow[0]
      CloudFormation stack 'teststack10' already exists. Please provide a unique name.
stackAB

Deploying CloudFormation stack: stackAB
CloudFormation stack creation started: stackAB
Waiting for CloudFormation stack creation to complete...
CloudFormation stack creation complete.
Stack output RoleARN: arn:aws:iam::123456789123:role/example_scheduler_role
Stack output SNStopicARN: arn:aws:sns:us-east-1:123456789123:stackAB-SchedulerSnsTopic-UORtMkZypo3x
--------------------------------------------------------------------------------

```
- The scenario creates the following EventBridge Scheduler resources:
    - A Schedule Group, for organizing schedules.
      - The group can use a default name, but should be unique. If the user enters and invalid name, they should be prompted again.
    - A One-Time Schedule, with a flexible time window that will delete after completion.
      - The user should be prompted for the schedule name, which will appear in notification emails.
      - The schedule should use a flexible time window (for example, 5 minutes) and be set to delete after completion.
    - A Recurring Schedule, which will run for one hour at a specified rate.
      - The schedule should start at the current time and run for one hour.
      - The user should be prompted for the schedule name, which will appear in notification emails.
      - The program should wait to allow time for the schedule to run, and then prompt the user to delete the schedule.

Example:

```
Successfully created schedule group 'scenario-schedules-group'.
Application preparation complete.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Enter a name for the one-time schedule:
one-time-test
Creating a one-time schedule named 'one-time-test' to send an initial event in 1 minute...
Successfully created schedule 'one-time-test' in schedule group 'scenario-schedules-group'.
Subscription email will receive an email from this event.
You must confirm your subscription to receive event emails.
One-time schedule 'one-time-test' created successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Creating a recurring schedule to send events for one hour...
Enter a name for the recurring schedule:
recurring-test
Enter the desired schedule rate (in minutes):
2
Successfully created schedule 'recurring-test' in schedule group 'scenario-schedules-group'.
Subscription email will receive an email from this event.
You must confirm your subscription to receive event emails.
Are you ready to delete the 'recurring-test' schedule? (y/n)
y
```

- Subscription Emails
    - After confirming their subscription, the user should receive one email from the one-time schedule, and a recurring email from the recurring schedule. 
    - The email text should contain the user's custom name for each schedule.

Example

![Emails](/resources/emails.png)

- Cleanup
    - The scenario should prompt the user for cleanup:
      - Delete the schedule and schedule group.
      - Delete the CloudFormation stack.
        - If the stack fails to delete, it should be force-deleted.
    - The cleanup operation should attempt to run if there are any errors in the scenario.
        - The user should be notified if the delete operation cannot occur.

Example:

```
Are you ready to delete the 'recurring-test' schedule? (y/n)
y
Successfully deleted schedule with name 'recurring-test'.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Do you want to delete all resources created by this workflow? (y/n)
y
Successfully deleted schedule group 'scenario-schedules-group'.
CloudFormation stack 'teststack10' is being deleted. This may take a few minutes.
Waiting for CloudFormation stack 'teststack10' to be deleted...
Waiting for CloudFormation stack 'teststack10' to be deleted...
CloudFormation stack 'teststack10' has been deleted.
--------------------------------------------------------------------------------
Amazon EventBridge Scheduler workflow completed.
```

---
## Errors

| action                       | Error                     | Handling                                         |
|------------------------------|---------------------------|--------------------------------------------------|
| `ListSchedules`              | none                      | Not required for Hello Service                   |
| `CreateSchedule`             | ConflictException         | Notify the user to use a unique name.            |
| `CreateScheduleGroup`        | ConflictException         | Notify the user to use a unique name.            |
| `DeleteSchedule`             | ResourceNotFoundException | Notify the user the schedule is already deleted. |
| `DeleteScheduleGroup`        | ResourceNotFoundException | Notify the user the group is already deleted.    |

---

## Metadata

| action / scenario                | metadata file           | metadata key                      |
|----------------------------------|-------------------------|-----------------------------------|
| `ListSchedules`                  | scheduler_metadata.yaml | scheduler_hello                   |
| `CreateSchedule`                 | scheduler_metadata.yaml | scheduler_CreateSchedule          |
| `CreateScheduleGroup`            | scheduler_metadata.yaml | scheduler_CreateScheduleGroup     |
| `DeleteSchedule`                 | scheduler_metadata.yaml | scheduler_DeleteSchedule          |
| `DeleteScheduleGroup`            | scheduler_metadata.yaml | scheduler_DeleteScheduleGroup     |
| `EventBridge Scheduler Scenario` | scheduler_metadata.yaml | scheduler_ScheduledEventsScenario |

