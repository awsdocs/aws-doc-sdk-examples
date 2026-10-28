# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the full run_scenario flow in scenario_inspector_basics.py.
"""

from datetime import datetime

from botocore.exceptions import ClientError
import pytest

from conftest import TEST_FILTER_NAME

ALL_TYPES = ["EC2", "ECR", "LAMBDA", "LAMBDA_CODE"]
DISABLED = {key: "DISABLED" for key in ("ec2", "ecr", "lambda", "lambdaCode")}
ENABLED = {key: "ENABLED" for key in ("ec2", "ecr", "lambda", "lambdaCode")}


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.finding_arn = (
            f"arn:aws:inspector2:us-east-1:{self.account_id}:finding/test-finding"
        )
        self.findings = [
            {
                "findingArn": self.finding_arn,
                "awsAccountId": self.account_id,
                "type": "PACKAGE_VULNERABILITY",
                "title": "CVE-2024-0001 - test-package",
                "description": "Test finding description.",
                "remediation": {},
                "severity": "HIGH",
                "firstObservedAt": datetime(2026, 1, 1),
                "lastObservedAt": datetime(2026, 1, 2),
                "status": "ACTIVE",
                "resources": [{"type": "AWS_EC2_INSTANCE", "id": "i-0123"}],
            }
        ]
        self.filter_arn = (
            f"arn:aws:inspector2:us-east-1:{self.account_id}:owner/"
            f"{self.account_id}/filter/test-filter"
        )
        # Five continue prompts and the cleanup confirmation.
        answers = ["", "", "", "", "", "y"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            # Setup
            runner.add(
                stubber.stub_batch_get_account_status,
                self.account_id,
                "DISABLED",
                DISABLED,
            )
            runner.add(stubber.stub_enable, ALL_TYPES, self.account_id)
            runner.add(
                stubber.stub_batch_get_account_status,
                self.account_id,
                "ENABLED",
                ENABLED,
            )
            # Coverage Assessment
            runner.add(stubber.stub_list_coverage, [])
            runner.add(stubber.stub_list_coverage_statistics, 0, [])
            # Findings Management
            runner.add(stubber.stub_list_findings, self.findings)
            runner.add(
                stubber.stub_batch_get_finding_details,
                [self.finding_arn],
                [{"findingArn": self.finding_arn, "riskScore": 10}],
            )
            runner.add(
                stubber.stub_create_filter,
                TEST_FILTER_NAME,
                "Suppress low severity findings",
                self.filter_arn,
            )
            runner.add(
                stubber.stub_list_filters,
                [{"arn": self.filter_arn, "name": TEST_FILTER_NAME}],
            )
            # Usage and Costs
            runner.add(stubber.stub_list_usage_totals, [])
            runner.add(
                stubber.stub_list_coverage_statistics,
                0,
                [],
                group_by="RESOURCE_TYPE",
            )
            # Cleanup
            runner.add(stubber.stub_delete_filter, self.filter_arn)
            runner.add(stubber.stub_disable, ALL_TYPES, self.account_id)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_run_scenario(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Filter deleted." in capt.out
    assert "Inspector disabled for: EC2, ECR, LAMBDA, LAMBDA_CODE" in capt.out
    assert "Scenario completed successfully!" in capt.out


def test_run_scenario_error_cleans_up(mock_mgr, caplog):
    """When a step fails, the filter is deleted and the types this run enabled are disabled."""
    error = "TESTERROR-stub_list_usage_totals"
    stubber = mock_mgr.scenario_data.stubber
    mock_mgr.setup_stubs(error, "stub_list_usage_totals", stubber)
    stubber.stub_delete_filter(mock_mgr.filter_arn)
    stubber.stub_disable(ALL_TYPES, mock_mgr.account_id)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run_scenario()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
    assert mock_mgr.scenario_data.scenario.filter_arn == mock_mgr.filter_arn
