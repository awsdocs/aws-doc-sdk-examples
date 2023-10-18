# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.case_id = "test-case-id"
        self.communications = [
            {
                "body": f"Communication-{i}",
                "caseId": f"test-case-{i}",
                "timeCreated": f"test-time{i}",
                "attachmentSet": [],
            }
            for i in range(1, 4)
        ]
        self.scenario_args = [self.case_id]
        answers = []
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_describe_communications, self.case_id, self.communications
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_list_communications(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.list_communications(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    for communication in mock_mgr.communications:
        assert f"{communication['timeCreated']}" in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index", [("TESTERROR-stub_describe_communications", 0)]
)
def test_list_communications_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.list_communications(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
