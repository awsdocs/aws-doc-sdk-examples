# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_guardduty_basics.py functions.
"""

import pytest
from unittest.mock import patch
import boto3
from botocore.exceptions import ClientError


def test_scenario_create_detector_success(scenario_data, monkeypatch):
    """Test successful detector creation in scenario."""
    detector_id = "test-detector-123"

    scenario_data.guardduty_stubber.stub_create_detector(detector_id)

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.return_value = 1  # Choose frequency 1 (15 minutes)

        result = scenario_data.scenario._create_new_detector()
        assert result == detector_id


def test_scenario_setup_phase_with_existing_detector(scenario_data, monkeypatch):
    """Test setup phase when existing detectors are found."""
    existing_detector_id = "existing-detector-123"

    scenario_data.guardduty_stubber.stub_list_detectors([existing_detector_id])
    scenario_data.guardduty_stubber.stub_get_detector(existing_detector_id)
    scenario_data.guardduty_stubber.stub_get_detector(
        existing_detector_id
    )  # Called twice

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.return_value = True  # Use existing detector

        scenario_data.scenario._setup_phase()
        assert scenario_data.scenario.detector_id == existing_detector_id


def test_scenario_create_sample_findings(scenario_data):
    """Test creating sample findings."""
    detector_id = "test-detector-123"
    scenario_data.scenario.detector_id = detector_id

    scenario_data.guardduty_stubber.stub_create_sample_findings(detector_id)
    scenario_data.guardduty_stubber.stub_list_findings(
        detector_id, ["finding-1", "finding-2"]
    )

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.side_effect = [True, False]  # Create findings, don't examine details

        with patch("time.sleep"):  # Mock sleep to speed up test
            scenario_data.scenario._demonstration_phase()


def test_scenario_examine_findings(scenario_data):
    """Test examining findings in detail."""
    detector_id = "test-detector-123"
    scenario_data.scenario.detector_id = detector_id  # Set the detector_id
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
            "Service": {"ServiceName": "GuardDuty", "DetectorId": detector_id},
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
            "Service": {"ServiceName": "GuardDuty", "DetectorId": detector_id},
        },
    ]

    scenario_data.guardduty_stubber.stub_get_findings(
        detector_id, finding_ids, findings
    )

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.return_value = False  # Don't continue to next finding

        scenario_data.scenario._examine_findings(finding_ids)


def test_scenario_cleanup_keep_detector(scenario_data):
    """Test cleanup phase when keeping the detector."""
    detector_id = "test-detector-123"
    scenario_data.scenario.detector_id = detector_id

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.return_value = 1  # Keep detector running

        scenario_data.scenario._cleanup_phase()


def test_scenario_cleanup_delete_detector(scenario_data):
    """Test cleanup phase when deleting the detector."""
    detector_id = "test-detector-123"
    scenario_data.scenario.detector_id = detector_id

    scenario_data.guardduty_stubber.stub_delete_detector(detector_id)

    with patch("scenario_guardduty_basics.q.ask") as mock_ask:
        mock_ask.side_effect = [2, True]  # Delete detector, confirm deletion

        scenario_data.scenario._cleanup_phase()


def test_scenario_examine_findings_by_severity(scenario_data):
    """Test examining findings grouped by severity."""
    detector_id = "test-detector-123"
    scenario_data.scenario.detector_id = detector_id  # Set the detector_id
    finding_ids = ["finding-1", "finding-2", "finding-3"]
    findings = [
        {
            "Id": "finding-1",
            "AccountId": "123456789012",
            "Arn": "arn:aws:guardduty:us-east-1:123456789012:detector/test-detector-id/finding/finding-1",
            "Type": "High severity",
            "Severity": 8.0,
            "Title": "High finding",
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
            "Type": "Medium severity",
            "Severity": 5.0,
            "Title": "Medium finding",
            "CreatedAt": "2023-01-01T00:00:00.000Z",
            "UpdatedAt": "2023-01-01T00:00:00.000Z",
            "Region": "us-east-1",
            "SchemaVersion": "2.0",
            "Resource": {
                "ResourceType": "Instance",
                "InstanceDetails": {"InstanceId": "i-0987654321fedcba0"},
            },
        },
        {
            "Id": "finding-3",
            "AccountId": "123456789012",
            "Arn": "arn:aws:guardduty:us-east-1:123456789012:detector/test-detector-id/finding/finding-3",
            "Type": "Low severity",
            "Severity": 2.0,
            "Title": "Low finding",
            "CreatedAt": "2023-01-01T00:00:00.000Z",
            "UpdatedAt": "2023-01-01T00:00:00.000Z",
            "Region": "us-east-1",
            "SchemaVersion": "2.0",
            "Resource": {
                "ResourceType": "Instance",
                "InstanceDetails": {"InstanceId": "i-1111222233334444"},
            },
        },
    ]

    scenario_data.guardduty_stubber.stub_get_findings(
        detector_id, finding_ids, findings
    )

    scenario_data.scenario._examine_findings_by_severity(finding_ids)
