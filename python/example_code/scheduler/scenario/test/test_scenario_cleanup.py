# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for cleanup in scheduler_scenario.py.
"""

import pytest
from botocore.exceptions import ClientError
from botocore import waiter

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.stack_name = "python-tests"
        self.schedule_group_name = "scenario-schedules-group"
        scenario_data.scenario.schedule_group_name = self.schedule_group_name
        scenario_data.scenario.stack = scenario_data.cloud_formation_resource.Stack(self.stack_name)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, scheduler_stubber, cloud_formation_stubber, monkeypatch):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(scheduler_stubber.stub_delete_schedule_group, self.schedule_group_name)
            runner.add(cloud_formation_stubber.stub_delete_stack, self.stack_name)

        def mock_wait(self, **kwargs):
            return

        monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)



@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_scenario_cleanup(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.scheduler_stubber,
                          mock_mgr.scenario_data.cloud_formation_stubber,  monkeypatch)

    mock_mgr.scenario_data.scenario.cleanup()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_delete_schedule_group", 0),
        ("TESTERROR-stub_delete_stack", 1),
    ],
)

@pytest.mark.integ
def test_scenario_cleanup_error(mock_mgr, caplog, error, stop_on_index, monkeypatch):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.scheduler_stubber, mock_mgr.scenario_data.cloud_formation_stubber, monkeypatch)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.cleanup()
