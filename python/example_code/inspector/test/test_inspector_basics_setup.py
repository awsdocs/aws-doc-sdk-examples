# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest

ALL_TYPES = ["EC2", "ECR", "LAMBDA", "LAMBDA_CODE"]


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.initial_states = {
            "ec2": "DISABLED",
            "ecr": "DISABLED",
            "lambda": "DISABLED",
            "lambdaCode": "DISABLED",
        }
        self.to_enable = ALL_TYPES
        input_mocker.mock_answers([])
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber, enable_error=None):
        enabled_states = {key: "ENABLED" for key in self.initial_states}
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_batch_get_account_status,
                self.account_id,
                "DISABLED",
                self.initial_states,
            )
            if self.to_enable:
                runner.add(
                    stubber.stub_enable,
                    self.to_enable,
                    self.account_id,
                    error_code=enable_error,
                )
            runner.add(
                stubber.stub_batch_get_account_status,
                self.account_id,
                "ENABLED",
                enabled_states,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize(
    "initial_states, to_enable",
    [
        (
            {
                "ec2": "DISABLED",
                "ecr": "DISABLED",
                "lambda": "DISABLED",
                "lambdaCode": "DISABLED",
            },
            ALL_TYPES,
        ),
        (
            {
                "ec2": "ENABLED",
                "ecr": "ENABLED",
                "lambda": "DISABLED",
                "lambdaCode": "DISABLED",
            },
            ["LAMBDA", "LAMBDA_CODE"],
        ),
        (
            {
                "ec2": "ENABLED",
                "ecr": "ENABLED",
                "lambda": "ENABLED",
                "lambdaCode": "ENABLED",
            },
            [],
        ),
    ],
)
def test_setup(mock_mgr, capsys, initial_states, to_enable):
    mock_mgr.initial_states = initial_states
    mock_mgr.to_enable = to_enable
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.setup()

    assert mock_mgr.scenario_data.scenario.enabled_types == to_enable
    capt = capsys.readouterr()
    assert mock_mgr.account_id in capt.out
    if to_enable:
        assert f"Enabling Inspector for: {', '.join(to_enable)}" in capt.out
    else:
        assert "already enabled for all resource types" in capt.out


def test_setup_enable_validation_error(mock_mgr, caplog):
    """Spec: Enable ValidationException prints that Inspector may already be enabled."""
    mock_mgr.setup_stubs(
        None, None, mock_mgr.scenario_data.stubber, enable_error="ValidationException"
    )

    mock_mgr.scenario_data.scenario.setup()

    assert mock_mgr.scenario_data.scenario.enabled_types == []
    assert "Inspector may already be enabled" in caplog.text


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_batch_get_account_status", 0),
        ("AccessDeniedException", 0),
        ("TESTERROR-stub_enable", 1),
        ("TESTERROR-stub_batch_get_account_status", 2),
    ],
)
def test_setup_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.setup()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
