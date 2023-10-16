# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.attachment_set_id = "test-attachment-set-id"
        self.case_id = "test-case-id"
        self.scenario_args = [self.case_id, self.attachment_set_id]
        answers = []
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_add_communication_to_case,
                self.attachment_set_id,
                self.case_id,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_add_communication(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.add_communication(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert mock_mgr.attachment_set_id in capt.out
    assert mock_mgr.case_id in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index", [("TESTERROR-stub_add_communication", 0)]
)
def test_add_communication_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.add_communication(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
