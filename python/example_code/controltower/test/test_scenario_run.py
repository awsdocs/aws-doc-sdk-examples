# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the run_scenario method in scenario_controltower.py.
"""

import pytest
from botocore.exceptions import ClientError
import datetime
import boto3

from example_code.controltower.controltower_wrapper import ControlTowerWrapper
from example_code.controltower.scenario_controltower import ControlTowerScenario


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.org_id = "o-exampleorgid"
        self.root_id = "r-examplerootid"
        self.sandbox_ou_id = "ou-exampleouid123456"
        self.sandbox_ou_arn = (
            "arn:aws:organizations::123456789012:ou/o-exampleorgid/ou-exampleouid"
        )
        self.landing_zone_arn = (
            "arn:aws:controltower:us-east-1:123456789012:landingzone/lz-example"
        )
        self.operation_id = "op-1234567890abcdef01234567890abcdef"
        self.baseline_operation_id = "op-1234567890abcdef01234567890abcdef"
        self.stack_id = (
            "arn:aws:cloudformation:us-east-1:123456789012:stack/test-stack/abcdef"
        )
        self.baseline_arn = "arn:aws:controltower:us-east-1:123456789012:baseline/AWSControlTowerBaseline"
        self.enabled_baseline_arn = "arn:aws:controltower:us-east-1:123456789012:baseline/AWSControlTowerBaseline/isenabled"
        self.control_arn = (
            "arn:aws:controlcatalog:us-east-1:123456789012:control/aws-control-1234"
        )
        self.control_arn_enabled = (
            "arn:aws:controlcatalog:us-east-1:123456789012:control/aws-control-5678"
        )

        self.landing_zones = [{"arn": self.landing_zone_arn}]

        self.baselines = [{"name": "AWSControlTowerBaseline", "arn": self.baseline_arn}]

        self.enabled_baselines = [
            {
                "targetIdentifier": self.sandbox_ou_arn,
                "baselineIdentifier": self.enabled_baseline_arn,
                "arn": self.baseline_arn,
                "statusSummary": {
                    "status": "SUCCEEDED",
                    "lastOperationIdentifier": self.baseline_operation_id,
                },
            }
        ]

        self.controls = [
            {
                "Arn": self.control_arn,
                "Name": "TestControl1",
                "Description": "Test control description",
            }
        ]

        self.enabled_controls = [
            {
                "arn": self.control_arn_enabled,
                "controlIdentifier": self.control_arn_enabled,
                "statusSummary": {
                    "status": "SUCCEEDED",
                    "lastOperationIdentifier": self.baseline_operation_id,
                },
                "targetIdentifier": self.sandbox_ou_id,
            }
        ]

        self.stub_runner = stub_runner
        self.input_mocker = input_mocker

    def setup_stubs(self, error, stop_on, monkeypatch):
        """Setup stubs for the scenario"""
        # Mock user inputs
        answers = [
            "y",  # Use first landing zone in the list.
            "y",  # Enable baseline.
            "y",  # Reset baseline.
            "y",  # Disable baseline.
            "y",  # Enable control.
            "y",  # Disable control.
        ]
        self.input_mocker.mock_answers(answers)

        # Mock STS get_caller_identity
        def mock_get_caller_identity():
            return {"Account": self.account_id}

        monkeypatch.setattr(
            boto3.client("sts"), "get_caller_identity", mock_get_caller_identity
        )

        with self.stub_runner(error, stop_on) as runner:
            # List landing zones
            runner.add(
                self.scenario_data.controltower_stubber.stub_list_landing_zones,
                self.landing_zones,
            )

            # Organization setup
            runner.add(
                self.scenario_data.organizations_stubber.stub_describe_organization,
                self.org_id,
            )
            runner.add(
                self.scenario_data.organizations_stubber.stub_list_roots,
                [{"Id": self.root_id, "Name": "Root"}],
            )
            runner.add(
                self.scenario_data.organizations_stubber.stub_list_organizational_units_for_parent,
                self.root_id,
                [
                    {
                        "Id": self.sandbox_ou_id,
                        "Name": "Sandbox",
                        "Arn": self.sandbox_ou_arn,
                    }
                ],
            )

            # List and enable baselines
            runner.add(
                self.scenario_data.controltower_stubber.stub_list_baselines,
                self.baselines,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_list_enabled_baselines,
                self.enabled_baselines,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_enable_baseline,
                self.baseline_arn,
                "4.0",
                self.sandbox_ou_arn,
                self.enabled_baseline_arn,
                self.baseline_operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_baseline_operation,
                self.baseline_operation_id,
                "SUCCEEDED",
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_reset_enabled_baseline,
                self.enabled_baseline_arn,
                self.baseline_operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_baseline_operation,
                self.baseline_operation_id,
                "SUCCEEDED",
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_disable_baseline,
                self.enabled_baseline_arn,
                self.baseline_operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_baseline_operation,
                self.baseline_operation_id,
                "SUCCEEDED",
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_enable_baseline,
                self.baseline_arn,
                "4.0",
                self.sandbox_ou_arn,
                self.enabled_baseline_arn,
                self.baseline_operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_baseline_operation,
                self.baseline_operation_id,
                "SUCCEEDED",
            )

            # List and enable controls
            runner.add(
                self.scenario_data.controlcatalog_stubber.stub_list_controls,
                self.controls,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_list_enabled_controls,
                self.sandbox_ou_arn,
                self.enabled_controls,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_enable_control,
                self.control_arn,
                self.sandbox_ou_arn,
                self.operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_control_operation,
                self.operation_id,
                "SUCCEEDED",
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_disable_control,
                self.control_arn,
                self.sandbox_ou_arn,
                self.operation_id,
            )
            runner.add(
                self.scenario_data.controltower_stubber.stub_get_control_operation,
                self.operation_id,
                "SUCCEEDED",
            )

    def setup_integ(self, error, stop_on):
        """Set up the scenario for an integration test."""
        # Mock user inputs for using the suggested landing zone
        answers = [
            "n",  # Use first landing zone in the list.
            "n",  # Enable baseline.
        ]
        self.stub_runner = None
        self.input_mocker.mock_answers(answers)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


# Define ANY constant for template body matching
ANY = object()


def test_run_scenario(mock_mgr, capsys, monkeypatch):
    """Test the scenario that uses the suggested landing zone."""
    mock_mgr.setup_stubs(None, None, monkeypatch)

    # Run the scenario
    mock_mgr.scenario_data
    mock_mgr.scenario_data.scenario.run_scenario()

    # Verify the scenario completed successfully
    captured = capsys.readouterr()
    assert "This concludes the example scenario." in captured.out


@pytest.mark.integ
def test_run_scenario_integ(input_mocker, capsys):
    """Test the scenario with an integration test."""
    answers = [
        "n",  # Run the sections that don't require a landing zone.
        "n",
    ]

    input_mocker.mock_answers(answers)
    controltower_client = boto3.client("controltower")
    controlcatalog_client = boto3.client("controlcatalog")
    organizations_client = boto3.client("organizations")

    scenario = ControlTowerScenario(
        controltower_wrapper=ControlTowerWrapper(
            controltower_client, controlcatalog_client
        ),
        org_client=organizations_client,
    )

    # Run the scenario
    scenario.run_scenario()

    # Verify the scenario completed successfully
    captured = capsys.readouterr()
    assert "This concludes the example scenario." in captured.out
