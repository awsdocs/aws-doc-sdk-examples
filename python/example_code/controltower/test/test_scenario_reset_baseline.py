# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the reset_enabled_baseline method in controltower_wrapper.py.
"""

import pytest
from botocore.exceptions import ClientError

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.ou_arn = "arn:aws:organizations::123456789012:ou/o-exampleorgid/ou-exampleouid"
        self.baseline_arn = "arn:aws:controltower:us-east-1:123456789012:baseline/AWSControlTowerBaseline/enabled"
        self.operation_id = "op-1234567890abcdef0"
        
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, controltower_stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                controltower_stubber.stub_reset_enabled_baseline,
                self.ou_arn,
                self.baseline_arn,
                self.operation_id
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_reset_enabled_baseline(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.controltower_stubber)
    
    # Test resetting an enabled baseline
    operation_id = mock_mgr.scenario_data.scenario.controltower_wrapper.reset_enabled_baseline(
        mock_mgr.ou_arn,
        mock_mgr.baseline_arn
    )
    
    # Verify the results
    assert operation_id == mock_mgr.operation_id


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_reset_enabled_baseline", 0),
    ],
)
@pytest.mark.integ
def test_reset_enabled_baseline_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.controltower_stubber)
    
    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.controltower_wrapper.reset_enabled_baseline(
            mock_mgr.ou_arn,
            mock_mgr.baseline_arn
        )