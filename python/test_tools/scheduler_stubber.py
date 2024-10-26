# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon EventBridge Scheduler unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import io
import json
from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber

from datetime import timedelta, timezone, datetime



class SchedulerStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon EventBridge Scheduler unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, scheduler_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param scheduler_client: A Boto 3 Amazon EventBridge Scheduler client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(scheduler_client, use_stubs)

    def stub_create_schedule(self,
                                   schedule_arn: str,
                                   name: str,
        schedule_expression: str,
        schedule_group_name: str,
        target_arn: str,
        role_arn: str,
        input: str,
        delete_after_completion: bool = False,
        use_flexible_time_window: bool = False,
        error_code: str=None) -> None:
        """
        Stub the create_schedule function.

        :param schedule_arn: The ARN of the created schedule.
        :param name: The name of the schedule.
        :param schedule_expression: The expression that defines when the schedule runs.
        :param schedule_group_name: The name of the schedule group.
        :param target_arn: The Amazon Resource Name (ARN) of the target.
        :param role_arn: The Amazon Resource Name (ARN) of the execution IAM role.
        :param input: The input for the target.
        :param delete_after_completion: Whether to delete the schedule after it completes.
        :param use_flexible_time_window: Whether to use a flexible time window.
        :param error_code: Simulated error code to raise.
        :return: None
        """
        flexible_time_window_minutes = 10
        expected_params = {"Name": name,
                           "ScheduleExpression": ANY,
                           "GroupName": schedule_group_name,
                           "Target": {"Arn": target_arn, "RoleArn": role_arn, "Input": input},
                           "StartDate":ANY,
                           "EndDate": ANY,
        }
        if delete_after_completion:
            expected_params["ActionAfterCompletion"] = "DELETE"

        if use_flexible_time_window:
            expected_params["FlexibleTimeWindow"] = {
                "Mode": "FLEXIBLE",
                "MaximumWindowInMinutes": flexible_time_window_minutes,
            }
        else:
            expected_params["FlexibleTimeWindow"] = {"Mode": "OFF"}

        response = {"ScheduleArn": schedule_arn}
        self._stub_bifurcator(
            "create_schedule", expected_params, response, error_code=error_code
        )

    def stub_create_schedule_group(self, group_name: str, schedule_group_arn: str, error_code: str =None) -> None:
        """
        Stub the create_schedule_group function.

        :param group_name: The name of the schedule group.
        :param schedule_group_arn: The ARN of the created schedule group.
        :param error_code: Simulated error code to raise.
        :return: None
        """
        expected_params = {"Name": group_name}
        response = {"ScheduleGroupArn": schedule_group_arn}
        self._stub_bifurcator(
            "create_schedule_group", expected_params, response, error_code=error_code
        )

    def stub_delete_schedule(self,  name: str, schedule_group_name: str, error_code: str =None) -> None:
        """
        Stub the delete_schedule function.

        :param name: The name of the schedule.
        :param schedule_group_name: The name of the schedule group.
        :param error_code: Simulated error code to raise.
        :return: None
        """
        expected_params = {"Name": name,
                            "GroupName": schedule_group_name}
        response = {}
        self._stub_bifurcator(
            "delete_schedule", expected_params, response, error_code=error_code
        )

    def stub_delete_schedule_group(self,  schedule_group_name: str, error_code: str =None) -> None:
        """
        Stub the delete_schedule_group function.

        :param schedule_group_name: The name of the schedule group.
        :param error_code: Simulated error code to raise.
        :return: None
        """
        expected_params = {"Name": schedule_group_name}
        response = {}
        self._stub_bifurcator(
            "delete_schedule_group", expected_params, response, error_code=error_code
        )

