# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.sm_arn = f"arn:aws:states:test-region:111122223333:/statemachine/test-sm"
        self.act_arn = "arn:aws:states:test-region:111122223333:/activity/test-act"
        self.run_arn = f"arn:aws:states:test-region:111122223333:/execution/test-run"
        self.token = "test-token"
        self.scenario_args = [self.sm_arn, self.act_arn]
        self.scenario_out = self.run_arn
        self.username = "Testerson"
        answers = [self.username, 2]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_start_execution,
                self.sm_arn,
                self.run_arn,
                run_input={"name": self.username},
            )
            runner.add(
                stubber.stub_get_activity_task,
                self.act_arn,
                self.token,
                json.dumps(
                    {"message": f"Hello, {self.username}", "actions": ["test", "done"]}
                ),
            )
            runner.add(
                stubber.stub_send_task_success,
                self.token,
                json.dumps({"action": "done"}),
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_run_state_machine(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    got_output = mock_mgr.scenario_data.scenario.run_state_machine(
        *mock_mgr.scenario_args
    )

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"ChatSFN: Hello, {mock_mgr.username}" in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_start_execution", 0),
        ("TESTERROR-stub_get_activity_task", 1),
        ("TESTERROR-stub_send_task_success", 2),
    ],
)
def test_run_state_machine_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run_state_machine(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
