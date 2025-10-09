# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for guardduty_wrapper.py functions.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from guardduty_wrapper import GuardDutyWrapper


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_create_detector(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"
    enable = True
    frequency = "FIFTEEN_MINUTES"

    guardduty_stubber.stub_create_detector(
        detector_id, enable, frequency, error_code=error_code
    )

    if error_code is None:
        result = wrapper.create_detector(enable, frequency)
        assert result == detector_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_detector(enable, frequency)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_list_detectors(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_ids = ["detector-1", "detector-2"]

    guardduty_stubber.stub_list_detectors(detector_ids, error_code=error_code)

    if error_code is None:
        result = wrapper.list_detectors()
        assert result == detector_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_detectors()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_get_detector(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"
    status = "ENABLED"
    service_role = "arn:aws:iam::123456789012:role/aws-guardduty-service-role"
    frequency = "FIFTEEN_MINUTES"

    guardduty_stubber.stub_get_detector(
        detector_id, status, service_role, frequency, error_code=error_code
    )

    if error_code is None:
        result = wrapper.get_detector(detector_id)
        assert result["Status"] == status
        assert result["ServiceRole"] == service_role
        assert result["FindingPublishingFrequency"] == frequency
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_detector(detector_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_create_sample_findings(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"
    finding_types = ["Backdoor:EC2/C&CActivity.B!DNS"]

    guardduty_stubber.stub_create_sample_findings(
        detector_id, finding_types, error_code=error_code
    )

    if error_code is None:
        wrapper.create_sample_findings(detector_id, finding_types)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_sample_findings(detector_id, finding_types)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_list_findings(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"
    finding_ids = ["finding-1", "finding-2"]
    max_results = 50

    guardduty_stubber.stub_list_findings(
        detector_id, finding_ids, max_results, error_code=error_code
    )

    if error_code is None:
        result = wrapper.list_findings(detector_id, max_results)
        assert result == finding_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_findings(detector_id, max_results)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_get_findings(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"
    finding_ids = ["finding-1", "finding-2"]
    findings = [
        {
            "Id": "finding-1",
            "AccountId": "123456789012",
            "Arn": "arn:aws:guardduty:us-east-1:123456789012:detector/test-detector-id/finding/finding-1",
            "Type": "Backdoor:EC2/C&CActivity.B!DNS",
            "Severity": 8.0,
            "Title": "Sample finding 1",
            "Description": "Test finding description",
            "CreatedAt": "2023-01-01T00:00:00.000Z",
            "UpdatedAt": "2023-01-01T00:00:00.000Z",
            "Region": "us-east-1",
            "SchemaVersion": "2.0",
            "Resource": {
                "ResourceType": "Instance",
                "InstanceDetails": {"InstanceId": "i-1234567890abcdef0"},
            },
        },
        {
            "Id": "finding-2",
            "AccountId": "123456789012",
            "Arn": "arn:aws:guardduty:us-east-1:123456789012:detector/test-detector-id/finding/finding-2",
            "Type": "Trojan:EC2/BlackholeTraffic",
            "Severity": 5.0,
            "Title": "Sample finding 2",
            "Description": "Another test finding",
            "CreatedAt": "2023-01-01T00:00:00.000Z",
            "UpdatedAt": "2023-01-01T00:00:00.000Z",
            "Region": "us-east-1",
            "SchemaVersion": "2.0",
            "Resource": {
                "ResourceType": "Instance",
                "InstanceDetails": {"InstanceId": "i-0987654321fedcba0"},
            },
        },
    ]

    guardduty_stubber.stub_get_findings(
        detector_id, finding_ids, findings, error_code=error_code
    )

    if error_code is None:
        result = wrapper.get_findings(detector_id, finding_ids)
        assert result == findings
        assert len(result) == 2
        assert result[0]["Id"] == "finding-1"
        assert result[1]["Id"] == "finding-2"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_findings(detector_id, finding_ids)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "BadRequestException", "InternalServerErrorException"]
)
def test_delete_detector(make_stubber, error_code):
    guardduty_client = boto3.client("guardduty", region_name="us-east-1")
    guardduty_stubber = make_stubber(guardduty_client)
    wrapper = GuardDutyWrapper(guardduty_client)

    detector_id = "test-detector-id"

    guardduty_stubber.stub_delete_detector(detector_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_detector(detector_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_detector(detector_id)
        assert exc_info.value.response["Error"]["Code"] == error_code
