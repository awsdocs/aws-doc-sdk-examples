# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon EventBridge Scheduler API to schedule
and receive events.
"""

import logging
import datetime
import boto3
from boto3 import client
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.eventbridge.EventSchedulerWrapper.class]
# snippet-start:[python.example_code.eventbridge.EventSchedulerWrapper.decl]
class EventBridgeSchedulerWrapper:
    def __init__(self, eventbridge_scheduler_client: client):
        self.eventbridge_scheduler_client = eventbridge_scheduler_client

    @classmethod
    def from_client(cls) -> "EventBridgeSchedulerWrapper":
        """
        Creates a EventBridgeSchedulerWrapper instance with a default EventBridge client.

        :return: An instance of EventBridgeSchedulerWrapper initialized with the default EventBridge client.
        """
        eventbridge_scheduler_client = boto3.client("scheduler")
        return cls(eventbridge_scheduler_client)

    # snippet-end:[python.example_code.eventbridge.EventSchedulerWrapper.decl]

    # snippet-start:[python.example_code.eventbridge.CreateSchedule]
    def create_schedule(
        self,
        name: str,
        schedule_expression: str,
        schedule_group_name: str,
        target_arn: str,
        role_arn: str,
        input: str,
        delete_after_completion: bool = False,
        use_flexible_time_window: bool = false,
    ):
        """
        Creates a new schedule with the specified parameters.

        :param name: The name of the schedule.
        :param schedule_expression: The expression that defines when the schedule runs.
        :param schedule_group_name: The name of the schedule group.
        :param target_arn: The Amazon Resource Name (ARN) of the target.
        :param role_arn: The Amazon Resource Name (ARN) of the execution IAM role.
        :param input: The input for the target.
        :param delete_after_completion: Whether to delete the schedule after it completes.
        :param use_flexible_time_window: Whether to use a flexible time window.
        """
        try:
            hours_to_run = 1
            flexible_time_window_minutes = 10
            self.eventbridge_scheduler_client.create_schedule(
                Name=name,
                ScheduleExpression=schedule_expression,
                GroupName=schedule_group_name,
                Target={"Arn": target_arn, "RoleArn": role_arn, "Input": input},
                ActionAfterCompletion="DELETE" if delete_after_completion else "None",
                FlexibleTimeWindow={
                    "Mode": "FLEXIBLE" if use_flexible_time_window else "OFF",
                    "MaximumWindowInMinutes": flexible_time_window_minutes
                    if use_flexible_time_window
                    else None,
                },
                StartDate=datetime.today(),
                EndDate=datetime.today() + datetime.timedelta(hours=hours_to_run),
            )
        except ClientError as e:
            logger.error("Error creating schedule: %s", e.response["Error"]["Message"])
            raise

    # snippet-start:[python.example_code.eventbridge.CreateScheduleGroup]
    def create_schedule_group(self, name: str):
        """
        Creates a new schedule group with the specified name and description.

        :param name: The name of the schedule group.
        :param description: The description of the schedule group.
        """
        try:
            self.eventbridge_scheduler_client.create_schedule_group(Name=name)
            logger.info("Schedule group %s created successfully.", name)
        except ClientError as e:
            logger.error(
                "Error creating schedule group: %s", e.response["Error"]["Message"]
            )
            raise

    # snippet-end:[python.example_code.eventbridge.CreateScheduleGroup]

    # snippet-start:[python.example_code.eventbridge.DeleteScheduleGroup]
    def delete_schedule_group(self, name: str):
        """
        Deletes the schedule group with the specified name.

        :param name: The name of the schedule group.
        """
        try:
            self.eventbridge_scheduler_client.delete_schedule_group(Name=name)
            logger.info("Schedule group %s deleted successfully.", name)
        except ClientError as e:
            logger.error(
                "Error deleting schedule group: %s", e.response["Error"]["Message"]
            )
            raise
        # snippet-end:[python.example_code.eventbridge.DeleteScheduleGroup]


# snippet-end:[python.example_code.eventbridge.EventSchedulerWrapper.class]

if __name__ == "__main__":
    try:
        eventbridge = EventBridgeSchedulerWrapper.from_client()
    except Exception:
        logging.exception("Something went wrong with the demo!")
