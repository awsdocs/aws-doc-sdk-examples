# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for create_recurring_schedule in scheduler_scenario.py.
"""

import pytest
from botocore.exceptions import ClientError
from scheduler_scenario import SchedulerScenario
from botocore import waiter

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.schedule_name = "python-test2"
        self.schedule_group_name = "scenario-schedules-group"
        self.role_arn = "arn:aws:iam::123456789012:role/Test-Role"
        self.sns_topic_arn = "arn:aws:sns:us-west-2:123456789012:my-topic"
        self.schedule_rate_in_minutes = 5
        self.schedule_expression = f"rate({self.schedule_rate_in_minutes} minutes)"
        self.schedule_input = f"Recurrent event test from schedule {self.schedule_name}."
        self.schedule_arn = f"arn:aws:scheduler:us-east-1:123456789012:schedule/{self.schedule_group_name}/{self.schedule_name}"
        scenario_data.scenario.sns_topic_arn = self.sns_topic_arn
        scenario_data.scenario.role_arn = self.role_arn
        scenario_data.scenario.schedule_group_name = "scenario-schedules-group"
        answers = [
            self.schedule_name,
            str(self.schedule_rate_in_minutes),
            "y"
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, scheduler_stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(scheduler_stubber.stub_create_schedule, self.schedule_arn, self.schedule_name,
                       self.schedule_expression, self.schedule_group_name, self.sns_topic_arn, self.role_arn,
                       self.schedule_input)
            runner.add(scheduler_stubber.stub_delete_schedule, self.schedule_name, self.schedule_group_name)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_scenario_create_recurring_schedule(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.scheduler_stubber)

    mock_mgr.scenario_data.scenario.create_recurring_schedule()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_schedule", 0),
("TESTERROR-stub_delete_schedule", 1),
    ],
)
@pytest.mark.integ
def test_scenario_create_recurring_schedule_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.scheduler_stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_recurring_schedule()
