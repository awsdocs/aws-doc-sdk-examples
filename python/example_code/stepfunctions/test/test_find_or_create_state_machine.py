# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
from unittest.mock import patch, mock_open
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.sm_exists = False
        self.sm_name = "test-state-machine"
        self.sm_def = "test-definition"
        self.sm_arn = (
            f"arn:aws:states:test-region:111122223333:/statemachine/{self.sm_name}"
        )
        self.act_arn = "arn:aws:states:test-region:111122223333:/activity/test-act"
        self.sm_role_arn = "arn:aws:iam:test-region:111122223333:/roles/test-role"
        self.state_machine_file = (
            "../../../resources/sample_files/chat_sfn_state_machine.json"
        )
        scenario_data.scenario.state_machine_role = {"Arn": self.sm_role_arn}
        self.scenario_args = [self.sm_name, self.act_arn, self.state_machine_file]
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            if self.sm_exists:
                runner.add(
                    stubber.stub_list_state_machines,
                    [{"name": self.sm_name, "stateMachineArn": self.sm_arn}],
                )
            else:
                runner.add(stubber.stub_list_state_machines, [])
                runner.add(
                    stubber.stub_create_state_machine,
                    self.sm_name,
                    self.sm_def,
                    self.sm_role_arn,
                    self.sm_arn,
                )
            runner.add(
                stubber.stub_describe_state_machine,
                self.sm_arn,
                self.sm_name,
                self.sm_def,
                "ACTIVE",
                self.sm_role_arn,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize("sm_exists", [True, False])
def test_find_or_create_state_machine(mock_mgr, capsys, sm_exists):
    mock_mgr.sm_exists = sm_exists
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    with patch("builtins.open", mock_open(read_data=mock_mgr.sm_def)) as mock_file:
        got_output = mock_mgr.scenario_data.scenario.find_or_create_state_machine(
            *mock_mgr.scenario_args
        )
        if not sm_exists:
            mock_file.assert_called_with(mock_mgr.state_machine_file)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.sm_arn
    assert f"name: {mock_mgr.sm_name}" in capt.out
    assert f"stateMachineArn: {mock_mgr.sm_arn}" in capt.out
    assert f"roleArn: {mock_mgr.sm_role_arn}" in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_state_machines", 0),
        ("TESTERROR-stub_create_state_machine", 1),
        ("TESTERROR-stub_describe_state_machine", 2),
    ],
)
def test_find_or_create_state_machine_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        with patch("builtins.open", mock_open(read_data=mock_mgr.sm_def)) as mock_file:
            mock_mgr.scenario_data.scenario.find_or_create_state_machine(
                *mock_mgr.scenario_args
            )
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
