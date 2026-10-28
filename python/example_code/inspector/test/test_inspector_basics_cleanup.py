# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest

ALL_TYPES = ["EC2", "ECR", "LAMBDA", "LAMBDA_CODE"]


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.filter_arn = (
            f"arn:aws:inspector2:us-east-1:{self.account_id}:owner/"
            f"{self.account_id}/filter/test-filter"
        )
        self.enabled_types = ["LAMBDA", "LAMBDA_CODE"]
        self.answers = ["y"]
        self.input_mocker = input_mocker
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber, disable_types=None):
        scenario = self.scenario_data.scenario
        scenario.filter_arn = self.filter_arn
        scenario.enabled_types = list(self.enabled_types)
        self.input_mocker.mock_answers(self.answers)
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_delete_filter, self.filter_arn)
            if disable_types:
                runner.add(stubber.stub_disable, disable_types, self.account_id)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_cleanup_disables_types_enabled_by_scenario(mock_mgr, capsys):
    mock_mgr.setup_stubs(
        None, None, mock_mgr.scenario_data.stubber, mock_mgr.enabled_types
    )

    mock_mgr.scenario_data.scenario.cleanup()

    scenario = mock_mgr.scenario_data.scenario
    assert scenario.filter_arn is None
    assert scenario.enabled_types == []
    capt = capsys.readouterr()
    assert "Filter deleted." in capt.out
    assert "Inspector disabled for: LAMBDA, LAMBDA_CODE" in capt.out


@pytest.mark.parametrize(
    "disable_anyway, disable_types",
    [("y", ALL_TYPES), ("n", None)],
)
def test_cleanup_previously_enabled(mock_mgr, capsys, disable_anyway, disable_types):
    mock_mgr.enabled_types = []
    mock_mgr.answers = ["y", disable_anyway]
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber, disable_types)

    mock_mgr.scenario_data.scenario.cleanup()

    capt = capsys.readouterr()
    assert "Filter deleted." in capt.out
    if disable_types:
        assert f"Inspector disabled for: {', '.join(ALL_TYPES)}" in capt.out
    else:
        assert "Inspector was left enabled." in capt.out


def test_cleanup_declined(mock_mgr, capsys):
    """When the user answers 'n', no cleanup calls are made."""
    mock_mgr.answers = ["n"]
    mock_mgr.scenario_data.scenario.filter_arn = mock_mgr.filter_arn
    mock_mgr.input_mocker.mock_answers(mock_mgr.answers)

    mock_mgr.scenario_data.scenario.cleanup()

    assert "Leaving the filter and Inspector settings in place." in (
        capsys.readouterr().out
    )


def test_cleanup_disable_validation_error(mock_mgr, caplog):
    """Spec: Disable ValidationException prints that Inspector may already be disabled."""
    mock_mgr.setup_stubs(
        "ValidationException",
        "stub_disable",
        mock_mgr.scenario_data.stubber,
        mock_mgr.enabled_types,
    )

    mock_mgr.scenario_data.scenario.cleanup()

    assert "Inspector may already be disabled" in caplog.text


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_delete_filter", 0),
        ("ResourceNotFoundException", 0),
        ("TESTERROR-stub_disable", 1),
    ],
)
def test_cleanup_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(
        error, stop_on_index, mock_mgr.scenario_data.stubber, mock_mgr.enabled_types
    )

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.cleanup()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
