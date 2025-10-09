# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for inspector_wrapper.py functions.
"""

import pytest
import sys
from botocore.exceptions import ClientError

sys.path.append("..")
from inspector_wrapper import InspectorWrapper


@pytest.mark.parametrize(
    "error_code", [None, "ValidationException", "AccessDeniedException"]
)
def test_enable_inspector(scenario_data, error_code):
    """Test enabling Inspector with various parameters and error conditions."""
    account_ids = ["123456789012"] if error_code != "ValidationException" else None
    resource_types = ["EC2", "ECR", "LAMBDA"]

    scenario_data.inspector_stubber.stub_enable(
        account_ids=account_ids, resource_types=resource_types, error_code=error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.enable_inspector(
            account_ids=account_ids, resource_types=resource_types
        )
        assert "accounts" in response
        assert len(response["accounts"]) > 0
        assert response["accounts"][0]["status"] == "ENABLED"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.enable_inspector(
                account_ids=account_ids, resource_types=resource_types
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "ValidationException", "AccessDeniedException"]
)
def test_get_account_status(scenario_data, error_code):
    """Test getting account status with various parameters and error conditions."""
    account_ids = ["123456789012"] if error_code != "ValidationException" else None

    scenario_data.inspector_stubber.stub_batch_get_account_status(
        account_ids=account_ids, error_code=error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.get_account_status(account_ids=account_ids)
        assert "accounts" in response
        assert len(response["accounts"]) > 0
        assert "state" in response["accounts"][0]
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.get_account_status(account_ids=account_ids)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "ValidationException", "InternalServerException"]
)
def test_list_findings(scenario_data, error_code):
    """Test listing findings with various parameters and error conditions."""
    filter_criteria = (
        {"severity": [{"comparison": "EQUALS", "value": "CRITICAL"}]}
        if error_code != "ValidationException"
        else None
    )
    max_results = 10
    sort_criteria = {"field": "SEVERITY", "sortOrder": "DESC"}

    scenario_data.inspector_stubber.stub_list_findings(
        filter_criteria=filter_criteria,
        max_results=max_results,
        sort_criteria=sort_criteria,
        error_code=error_code,
    )

    if error_code is None:
        response = scenario_data.wrapper.list_findings(
            filter_criteria=filter_criteria,
            max_results=max_results,
            sort_criteria=sort_criteria,
        )
        assert "findings" in response
        if response["findings"]:
            finding = response["findings"][0]
            assert "findingArn" in finding
            assert "severity" in finding
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_findings(
                filter_criteria=filter_criteria,
                max_results=max_results,
                sort_criteria=sort_criteria,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "ValidationException", "AccessDeniedException"]
)
def test_get_finding_details(scenario_data, error_code):
    """Test getting finding details with various parameters and error conditions."""
    finding_arns = ["arn:aws:inspector2:us-east-1:123456789012:finding/finding-1"]

    scenario_data.inspector_stubber.stub_batch_get_finding_details(
        finding_arns=finding_arns, error_code=error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.get_finding_details(finding_arns=finding_arns)
        assert "findingDetails" in response
        if response["findingDetails"]:
            details = response["findingDetails"][0]
            assert "findingArn" in details
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.get_finding_details(finding_arns=finding_arns)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ValidationException"])
def test_list_coverage(scenario_data, error_code):
    """Test listing coverage with various parameters and error conditions."""
    filter_criteria = (
        {"resourceType": [{"comparison": "EQUALS", "value": "AWS_EC2_INSTANCE"}]}
        if error_code != "ValidationException"
        else None
    )
    max_results = 10

    scenario_data.inspector_stubber.stub_list_coverage(
        filter_criteria=filter_criteria, max_results=max_results, error_code=error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.list_coverage(
            filter_criteria=filter_criteria, max_results=max_results
        )
        assert "coveredResources" in response
        if response["coveredResources"]:
            resource = response["coveredResources"][0]
            assert "resourceId" in resource
            assert "resourceType" in resource
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_coverage(
                filter_criteria=filter_criteria, max_results=max_results
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "ValidationException", "ConflictException"]
)
def test_disable_inspector(scenario_data, error_code):
    """Test disabling Inspector with various parameters and error conditions."""
    account_ids = ["123456789012"] if error_code != "ValidationException" else None
    resource_types = ["EC2", "ECR", "LAMBDA"]

    scenario_data.inspector_stubber.stub_disable(
        account_ids=account_ids, resource_types=resource_types, error_code=error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.disable_inspector(
            account_ids=account_ids, resource_types=resource_types
        )
        assert "accounts" in response
        assert len(response["accounts"]) > 0
        assert response["accounts"][0]["status"] == "DISABLED"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.disable_inspector(
                account_ids=account_ids, resource_types=resource_types
            )
        assert exc_info.value.response["Error"]["Code"] == error_code
