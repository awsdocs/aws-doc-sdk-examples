# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for prepare_application in scheduler_scenario.py.
"""

import pytest
from botocore.exceptions import ClientError
from scheduler_scenario import SchedulerScenario
from botocore import waiter

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.email_address = "carlos@example.com"
        self.stack_name = "python-tests"
        self.parameters = [{"ParameterKey": "email", "ParameterValue": self.email_address}]
        self.capabilities = ['CAPABILITY_NAMED_IAM']
        self.cfn_template =  SchedulerScenario.get_template_as_string()
        self.stack_id = "arn:aws:cloudformation:us-east-1:123456789012:stack/myteststack/466df9e0-0dff-08e3-8e2f-5088487c4896"
        self.outputs = [
            {
                'OutputKey': 'RoleARN',
                'OutputValue': 'arn:aws:iam::123456789012:role/Test-Role'
            },
            {
                'OutputKey': 'SNStopicARN',
                'OutputValue': 'arn:aws:sns:us-west-2:123456789012:my-topic'
            }
        ]
        self.schedule_group_name = "scenario-schedules-group"
        self.schedule_group_arn = "arn:aws:scheduler:us-east-1:123456789012:schedule-group/tests"
        answers = [
            self.email_address,
            self.stack_name
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, scheduler_stubber, cloud_formation_stubber, monkeypatch):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(cloud_formation_stubber.stub_create_stack, self.stack_name, self.cfn_template,
                        self.capabilities, self.stack_id, self.parameters)
            runner.add(cloud_formation_stubber.stub_describe_stacks, self.stack_name, "CREATE_COMPLETE", self.outputs)
            runner.add(scheduler_stubber.stub_create_schedule_group, self.schedule_group_name, self.schedule_group_arn)

        def mock_wait(self, **kwargs):
            return

        monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)



@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_scenario_prepare_application(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.scheduler_stubber,
                          mock_mgr.scenario_data.cloud_formation_stubber,  monkeypatch)

    mock_mgr.scenario_data.scenario.prepare_application()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_stack", 0),
        ("TESTERROR-stub_describe_stacks", 1),
        ("TESTERROR-stub_create_schedule_group", 2),
    ],
)
@pytest.mark.integ
def test_scenario_prepare_application_error(mock_mgr, caplog, error, stop_on_index, monkeypatch):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.scheduler_stubber, mock_mgr.scenario_data.cloud_formation_stubber, monkeypatch)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.prepare_application()
