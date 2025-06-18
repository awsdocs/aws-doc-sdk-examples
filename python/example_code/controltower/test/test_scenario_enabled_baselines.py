# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the list_enabled_baselines method in controltower_wrapper.py.
"""

import pytest
from botocore.exceptions import ClientError

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.ou_arn = "arn:aws:organizations::123456789012:ou/o-exampleorgid/ou-exampleouid"
        
        self.enabled_baselines = [
            {
                "baselineArn": "arn:aws:controltower:us-east-1:123456789012:baseline/AWSControlTowerBaseline/enabled",
                "baselineVersion": "4.0",
                "baselineName": "AWSControlTowerBaseline"
            },
            {
                "baselineArn": "arn:aws:controltower:us-east-1:123456789012:baseline/OtherBaseline/enabled",
                "baselineVersion": "2.0",
                "baselineName": "OtherBaseline"
            }
        ]
        
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, controltower_stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                controltower_stubber.stub_list_enabled_baselines,
                self.ou_arn,
                self.enabled_baselines
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_list_enabled_baselines(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.controltower_stubber)
    
    # Test listing enabled baselines
    enabled_baselines = mock_mgr.scenario_data.scenario.controltower_wrapper.list_enabled_baselines(
        mock_mgr.ou_arn
    )
    
    # Verify the results
    assert len(enabled_baselines) == 2
    assert enabled_baselines[0]["baselineName"] == "AWSControlTowerBaseline"
    assert enabled_baselines[0]["baselineVersion"] == "4.0"
    assert enabled_baselines[1]["baselineName"] == "OtherBaseline"


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_enabled_baselines", 0),
    ],
)
@pytest.mark.integ
def test_list_enabled_baselines_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.controltower_stubber)
    
    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.controltower_wrapper.list_enabled_baselines(
            mock_mgr.ou_arn
        )