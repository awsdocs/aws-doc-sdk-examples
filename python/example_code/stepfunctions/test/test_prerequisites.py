# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.role_exists = False
        self.role_name = "test-role"
        self.role_arn = f"arn:aws:iam:test-region:111122223333:/roles/{self.role_name}"
        self.scenario_args = [self.role_name]
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            if self.role_exists:
                runner.add(stubber.stub_get_role, self.role_name, self.role_arn)
            else:
                runner.add(
                    stubber.stub_get_role, self.role_name, error_code="NoSuchEntity"
                )
                runner.add(
                    stubber.stub_create_role, self.role_name, role_arn=self.role_arn
                )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize("role_exists", [True, False])
def test_prerequisites(mock_mgr, capsys, role_exists):
    mock_mgr.role_exists = role_exists
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.iam_stubber)

    mock_mgr.scenario_data.scenario.prerequisites(*mock_mgr.scenario_args)

    assert (
        mock_mgr.scenario_data.scenario.state_machine_role["RoleName"]
        == mock_mgr.role_name
    )
    assert (
        mock_mgr.scenario_data.scenario.state_machine_role["Arn"] == mock_mgr.role_arn
    )


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_get_role", 0),
        ("TESTERROR-stub_create_role", 1),
    ],
)
def test_prerequisites_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.iam_stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.prerequisites(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
