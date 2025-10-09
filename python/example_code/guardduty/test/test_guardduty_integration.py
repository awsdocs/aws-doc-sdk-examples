# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for guardduty_wrapper.py functions.
"""

import boto3
import pytest
import time

from guardduty_wrapper import GuardDutyWrapper


@pytest.mark.integ
def test_guardduty_integration():
    """
    Integration test that works with existing detectors or creates a new one,
    generates sample findings, examines them, and cleans up appropriately.
    """
    wrapper = GuardDutyWrapper.from_client()
    detector_id = None
    created_detector = False

    try:
        # Check for existing detectors first
        existing_detectors = wrapper.list_detectors()

        if existing_detectors:
            # Use existing detector
            detector_id = existing_detectors[0]
            print(f"Using existing detector: {detector_id}")
        else:
            # Create a new detector
            detector_id = wrapper.create_detector(
                enable=True, finding_publishing_frequency="FIFTEEN_MINUTES"
            )
            created_detector = True
            print(f"Created new detector: {detector_id}")

        assert detector_id is not None

        # Verify detector exists and get its info
        detector_info = wrapper.get_detector(detector_id)
        assert detector_info["Status"] == "ENABLED"

        # List detectors should include our detector
        detector_ids = wrapper.list_detectors()
        assert detector_id in detector_ids

        # Create sample findings
        wrapper.create_sample_findings(detector_id)

        # Wait a moment for findings to be processed
        time.sleep(10)

        # List findings
        finding_ids = wrapper.list_findings(detector_id)

        # If we have findings, examine them
        if finding_ids:
            findings = wrapper.get_findings(detector_id, finding_ids[:5])
            assert len(findings) > 0

            # Verify finding structure
            for finding in findings:
                assert "Id" in finding
                assert "Type" in finding
                assert "Severity" in finding

    finally:
        # Only delete the detector if we created it
        if detector_id and created_detector:
            wrapper.delete_detector(detector_id)

            # Verify deletion
            detector_ids = wrapper.list_detectors()
            assert detector_id not in detector_ids


@pytest.mark.integ
def test_list_existing_detectors():
    """
    Simple integration test to list existing detectors.
    """
    wrapper = GuardDutyWrapper.from_client()

    # This should not raise an exception
    detector_ids = wrapper.list_detectors()
    assert isinstance(detector_ids, list)
