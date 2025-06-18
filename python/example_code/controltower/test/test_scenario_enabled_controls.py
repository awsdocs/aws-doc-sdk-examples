# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the list_enabled_controls method in controltower_wrapper.py.
"""

import pytest
from botocore.exceptions import ClientError

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.ou_arn = "arn:aws:organizations::123456789012:ou/o-exampleorgid/ou-exampleouid"
        
        self.enabled_controls = [
            {
                "controlIdentifier": "arn:aws:controlcatalog:us-east-1:123456789012:control/aws-control-1234",
                "controlName": "TestControl1",
                "controlStatus": "ENABLED"
            },
            {
                "controlIdentifier": "arn:aws:controlcatalog:us-east-1:123456789012:control/aws-control-5678",
                "controlName": "TestControl2",
                "controlStatus": "ENABLED"
            }
        ]
        
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, controltower_stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                controltower_stubber.stub_list_enabled_controls,
                self.ou_arn,
                self.enabled_controls
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)

@pytest.mark.integ
def test_list_enabled_controls(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.controltower_stubber)
    
    # Test listing enabled controls
    enabled_controls = mock_mgr.scenario_data.scenario.controltower_wrapper.list_enabled_controls(
        mock_mgr.ou_arn
    )
    
    # Verify the results
    assert len(enabled_controls) == 2
    assert enabled_controls[0]["controlName"] == "TestControl1"
    assert enabled_controls[0]["controlStatus"] == "ENABLED"
    assert enabled_controls[1]["controlName"] == "TestControl2"


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_enabled_controls", 0),
    ],
)
@pytest.mark.integ
def test_list_enabled_controls_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.controltower_stubber)
    
    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.controltower_wrapper.list_enabled_controls(
            mock_mgr.ou_arn
        )