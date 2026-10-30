# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime

from botocore.exceptions import ClientError
import pytest

from conftest import TEST_FILTER_NAME


def make_finding(account_id, index):
    return {
        "findingArn": f"arn:aws:inspector2:us-east-1:{account_id}:finding/f{index}",
        "awsAccountId": account_id,
        "type": "PACKAGE_VULNERABILITY",
        "title": f"CVE-2024-000{index} - test-package",
        "description": "Test finding description.",
        "remediation": {"recommendation": {"text": "Upgrade the package."}},
        "severity": "HIGH",
        "firstObservedAt": datetime(2026, 1, 1),
        "lastObservedAt": datetime(2026, 1, 2),
        "status": "ACTIVE",
        "resources": [{"type": "AWS_EC2_INSTANCE", "id": "i-0123456789abcdef0"}],
    }


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.findings = [make_finding(self.account_id, i) for i in range(1, 3)]
        self.finding_details = [
            {
                "findingArn": self.findings[0]["findingArn"],
                "riskScore": 42,
                "cwes": ["CWE-79"],
                "referenceUrls": ["https://example.com/advisory"],
            }
        ]
        self.filter_name = TEST_FILTER_NAME
        self.filter_description = "Suppress low severity findings"
        self.filter_arn = (
            f"arn:aws:inspector2:us-east-1:{self.account_id}:owner/"
            f"{self.account_id}/filter/test-filter"
        )
        input_mocker.mock_answers([])
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_list_findings, self.findings)
            if self.findings:
                runner.add(
                    stubber.stub_batch_get_finding_details,
                    [self.findings[0]["findingArn"]],
                    self.finding_details,
                )
            runner.add(
                stubber.stub_create_filter,
                self.filter_name,
                self.filter_description,
                self.filter_arn,
            )
            runner.add(
                stubber.stub_list_filters,
                [{"arn": self.filter_arn, "name": self.filter_name}],
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize("has_findings", [True, False])
def test_manage_findings(mock_mgr, capsys, has_findings):
    if not has_findings:
        mock_mgr.findings = []
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.manage_findings()

    assert mock_mgr.scenario_data.scenario.filter_arn == mock_mgr.filter_arn
    capt = capsys.readouterr()
    if has_findings:
        assert mock_mgr.findings[0]["title"] in capt.out
        assert "Risk Score: 42" in capt.out
        assert "CWE-79" in capt.out
    else:
        assert "No findings found." in capt.out
    assert f"Created filter: {mock_mgr.filter_arn}" in capt.out
    assert f"{mock_mgr.filter_name}: {mock_mgr.filter_arn} (SUPPRESS)" in capt.out


def test_manage_findings_create_filter_validation_error(mock_mgr, capsys, caplog):
    """Spec: CreateFilter ValidationException prints validation error details."""
    mock_mgr.setup_stubs(
        "ValidationException", "stub_create_filter", mock_mgr.scenario_data.stubber
    )
    # The scenario keeps going after a ValidationException, so stub the rest.
    mock_mgr.scenario_data.stubber.stub_list_filters([])

    mock_mgr.scenario_data.scenario.manage_findings()

    assert mock_mgr.scenario_data.scenario.filter_arn is None
    assert "Validation error creating filter" in caplog.text
    assert "The filter was not created." in capsys.readouterr().out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_findings", 0),
        ("TESTERROR-stub_batch_get_finding_details", 1),
        ("ResourceNotFoundException", 1),
        ("TESTERROR-stub_create_filter", 2),
        ("TESTERROR-stub_list_filters", 3),
        ("ValidationException", 3),
    ],
)
def test_manage_findings_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.manage_findings()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
