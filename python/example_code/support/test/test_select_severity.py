# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.severity_levels = [{"name": f"Level {i}"} for i in range(1, 4)]
        self.scenario_args = []
        answers = ["1"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_describe_severity_levels, "en", self.severity_levels
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_display_and_select_severity(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.display_and_select_severity(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    for severity in mock_mgr.severity_levels:
        assert severity["name"] in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_describe_severity", 0),
    ],
)
def test_severity_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.display_and_select_severity(
            *mock_mgr.scenario_args
        )
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
