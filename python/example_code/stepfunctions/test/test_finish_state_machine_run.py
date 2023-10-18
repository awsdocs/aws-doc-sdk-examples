# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.run_arn = f"arn:aws:states:test-region:111122223333:/execution/test-run"
        self.sm_arn = f"arn:aws:states:test-region:111122223333:/statemachine/test-sm"
        self.scenario_args = [self.run_arn]
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_describe_execution,
                self.run_arn,
                self.sm_arn,
                "RUNNING",
                "",
            )
            runner.add(
                stubber.stub_describe_execution,
                self.run_arn,
                self.sm_arn,
                "SUCCEEDED",
                json.dumps({"message": "test-message"}),
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_finish_state_machine_run(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.finish_state_machine_run(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert "running" in capt.out
    assert "ChatSFN: test-message" in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_describe_execution", 0),
        ("TESTERROR-stub_describe_execution", 1),
    ],
)
def test_finish_state_machine_run_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.finish_state_machine_run(
            *mock_mgr.scenario_args
        )
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
