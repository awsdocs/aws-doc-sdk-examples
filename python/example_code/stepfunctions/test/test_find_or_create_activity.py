# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.act_exists = False
        self.act_name = "test-act"
        self.activities = [
            {
                "name": self.act_name,
                "activityArn": f"arn:aws:states:test-region:111122223333:/activity/{self.act_name}",
                "creationDate": datetime.now(),
            }
        ]
        self.scenario_args = [self.act_name]
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            if self.act_exists:
                runner.add(stubber.stub_list_activities, self.activities)
            else:
                runner.add(stubber.stub_list_activities, [])
                runner.add(
                    stubber.stub_create_activity,
                    self.activities[0]["name"],
                    self.activities[0]["activityArn"],
                )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize("act_exists", [True, False])
def test_find_or_create_activity(mock_mgr, capsys, act_exists):
    mock_mgr.act_exists = act_exists
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    got_output = mock_mgr.scenario_data.scenario.find_or_create_activity(
        *mock_mgr.scenario_args
    )

    capt = capsys.readouterr()
    assert mock_mgr.act_name in capt.out
    assert got_output == mock_mgr.activities[0]["activityArn"]


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_activities", 0),
        ("TESTERROR-stub_create_activity", 1),
    ],
)
def test_find_or_create_activity_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.find_or_create_activity(*mock_mgr.scenario_args)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
