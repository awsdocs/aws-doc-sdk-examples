# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.sm_name = "test-sm"
        self.sm_arn = (
            f"arn:aws:states:test-region:111122223333:/statemachine/{self.sm_name}"
        )
        self.act_name = "test-act"
        self.act_arn = (
            f"arn:aws:states:test-region:111122223333:/activity/{self.act_name}"
        )
        self.sm_role = "test-role"
        self.scenario_args = [
            self.sm_name,
            self.sm_arn,
            self.act_name,
            self.act_arn,
            self.sm_role,
        ]
        answers = ["y"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_delete_state_machine, self.sm_arn)
            runner.add(stubber.stub_delete_activity, self.act_arn)
            runner.add(self.scenario_data.iam_stubber.stub_delete_role, self.sm_role)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_cleanup(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.cleanup(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert mock_mgr.sm_name in capt.out
    assert mock_mgr.act_name in capt.out
    assert mock_mgr.sm_role in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_delete_state_machine", 0),
        ("TESTERROR-stub_delete_activity", 1),
    ],
)
def test_cleanup_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.cleanup(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
