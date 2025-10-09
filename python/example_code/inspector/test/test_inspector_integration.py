# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for inspector_wrapper.py functions.
"""

import pytest
import sys
import boto3
from botocore.exceptions import ClientError

sys.path.append("..")
from inspector_wrapper import InspectorWrapper


@pytest.mark.integ
def test_get_account_status_integration():
    """Test getting account status against real AWS service."""
    inspector_wrapper = InspectorWrapper.from_client()

    try:
        response = inspector_wrapper.get_account_status()
        assert "accounts" in response
        # The response should contain at least one account (the current account)
        assert len(response["accounts"]) >= 1

        account = response["accounts"][0]
        assert "accountId" in account
        assert "state" in account or "resourceState" in account

    except ClientError as e:
        # Access denied is acceptable for integration tests
        if e.response["Error"]["Code"] != "AccessDeniedException":
            raise


@pytest.mark.integ
def test_list_findings_integration():
    """Test listing findings against real AWS service."""
    inspector_wrapper = InspectorWrapper.from_client()

    try:
        response = inspector_wrapper.list_findings(max_results=5)
        assert "findings" in response

        # If there are findings, validate their structure
        for finding in response.get("findings", []):
            assert "findingArn" in finding
            assert "awsAccountId" in finding
            assert "type" in finding

    except ClientError as e:
        # Access denied or validation errors are acceptable for integration tests
        if e.response["Error"]["Code"] not in [
            "AccessDeniedException",
            "ValidationException",
        ]:
            raise


@pytest.mark.integ
def test_list_coverage_integration():
    """Test listing coverage against real AWS service."""
    inspector_wrapper = InspectorWrapper.from_client()

    try:
        response = inspector_wrapper.list_coverage(max_results=5)
        assert "coveredResources" in response

        # If there are covered resources, validate their structure
        for resource in response.get("coveredResources", []):
            assert "accountId" in resource
            assert "resourceId" in resource
            assert "resourceType" in resource

    except ClientError as e:
        # Access denied or validation errors are acceptable for integration tests
        if e.response["Error"]["Code"] not in [
            "AccessDeniedException",
            "ValidationException",
        ]:
            raise


@pytest.mark.integ
def test_hello_inspector_integration():
    """Test the hello Inspector function against real AWS service."""
    from inspector_hello import hello_inspector

    inspector_wrapper = InspectorWrapper.from_client()

    # This should not raise an exception, even if Inspector is not enabled
    # The function should handle errors gracefully
    try:
        hello_inspector(inspector_wrapper)
    except Exception as e:
        # Only fail if it's an unexpected error
        if "AccessDeniedException" not in str(e):
            raise
