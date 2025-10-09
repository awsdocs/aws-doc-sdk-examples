# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon GuardDuty to
manage threat detection and security findings.
"""

import logging
from typing import Dict, List, Optional, Any
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.guardduty.GuardDutyWrapper.class]
# snippet-start:[python.example_code.guardduty.GuardDutyWrapper.decl]
class GuardDutyWrapper:
    """Encapsulates Amazon GuardDuty functionality."""

    def __init__(self, guardduty_client: boto3.client):
        """
        :param guardduty_client: A Boto3 GuardDuty client.
        """
        self.guardduty_client = guardduty_client

    @classmethod
    def from_client(cls):
        """
        Creates a GuardDutyWrapper instance with a default GuardDuty client.

        :return: An instance of GuardDutyWrapper.
        """
        guardduty_client = boto3.client("guardduty")
        return cls(guardduty_client)

    # snippet-end:[python.example_code.guardduty.GuardDutyWrapper.decl]

    # snippet-start:[python.example_code.guardduty.CreateDetector]
    def create_detector(
        self, enable: bool = True, finding_publishing_frequency: str = "FIFTEEN_MINUTES"
    ) -> str:
        """
        Creates a GuardDuty detector to enable threat detection.

        :param enable: Whether to enable the detector immediately.
        :param finding_publishing_frequency: How often to publish findings.
        :return: The detector ID.
        """
        try:
            response = self.guardduty_client.create_detector(
                Enable=enable, FindingPublishingFrequency=finding_publishing_frequency
            )
            detector_id = response["DetectorId"]
            logger.info(f"Created GuardDuty detector with ID: {detector_id}")
            return detector_id
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error("Invalid parameters provided for detector creation")
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred during detector creation")
            else:
                logger.error(f"Error creating detector: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.CreateDetector]

    # snippet-start:[python.example_code.guardduty.ListDetectors]
    def list_detectors(self) -> List[str]:
        """
        Lists all GuardDuty detectors in the current region.

        :return: A list of detector IDs.
        """
        try:
            response = self.guardduty_client.list_detectors()
            detector_ids = response.get("DetectorIds", [])
            logger.info(f"Found {len(detector_ids)} GuardDuty detectors")
            return detector_ids
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error("Invalid request parameters for listing detectors")
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred while listing detectors")
            else:
                logger.error(f"Error listing detectors: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.ListDetectors]

    # snippet-start:[python.example_code.guardduty.GetDetector]
    def get_detector(self, detector_id: str) -> Dict[str, Any]:
        """
        Gets detailed information about a GuardDuty detector.

        :param detector_id: The ID of the detector to retrieve.
        :return: Detector details.
        """
        try:
            response = self.guardduty_client.get_detector(DetectorId=detector_id)
            logger.info(f"Retrieved detector details for ID: {detector_id}")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error(
                    f"Invalid detector ID format or detector not found: {detector_id}"
                )
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred while retrieving detector")
            else:
                logger.error(f"Error getting detector {detector_id}: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.GetDetector]

    # snippet-start:[python.example_code.guardduty.CreateSampleFindings]
    def create_sample_findings(
        self, detector_id: str, finding_types: Optional[List[str]] = None
    ) -> None:
        """
        Creates sample findings for testing and demonstration purposes.

        :param detector_id: The ID of the detector to create sample findings for.
        :param finding_types: Optional list of specific finding types to generate.
        """
        try:
            params = {"DetectorId": detector_id}
            if finding_types:
                params["FindingTypes"] = finding_types

            self.guardduty_client.create_sample_findings(**params)
            logger.info(f"Created sample findings for detector: {detector_id}")
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error(f"Invalid detector ID or finding types: {detector_id}")
            elif error_code == "InternalServerErrorException":
                logger.error(
                    "Internal server error occurred while creating sample findings"
                )
            else:
                logger.error(
                    f"Error creating sample findings for detector {detector_id}: {e}"
                )
            raise

    # snippet-end:[python.example_code.guardduty.CreateSampleFindings]

    # snippet-start:[python.example_code.guardduty.ListFindings]
    def list_findings(self, detector_id: str, max_results: int = 50) -> List[str]:
        """
        Lists finding IDs for a GuardDuty detector.

        :param detector_id: The ID of the detector to list findings for.
        :param max_results: Maximum number of findings to return.
        :return: A list of finding IDs.
        """
        try:
            response = self.guardduty_client.list_findings(
                DetectorId=detector_id, MaxResults=max_results
            )
            finding_ids = response.get("FindingIds", [])
            logger.info(f"Found {len(finding_ids)} findings for detector {detector_id}")
            return finding_ids
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error(f"Invalid parameters for listing findings: {detector_id}")
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred while listing findings")
            else:
                logger.error(f"Error listing findings for detector {detector_id}: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.ListFindings]

    # snippet-start:[python.example_code.guardduty.GetFindings]
    def get_findings(
        self, detector_id: str, finding_ids: List[str]
    ) -> List[Dict[str, Any]]:
        """
        Gets detailed information about specific findings.

        :param detector_id: The ID of the detector.
        :param finding_ids: List of finding IDs to retrieve.
        :return: List of finding details.
        """
        try:
            response = self.guardduty_client.get_findings(
                DetectorId=detector_id, FindingIds=finding_ids
            )
            findings = response.get("Findings", [])
            logger.info(f"Retrieved {len(findings)} finding details")
            return findings
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error(f"Invalid finding IDs format: {finding_ids}")
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred while retrieving findings")
            else:
                logger.error(f"Error getting findings: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.GetFindings]

    # snippet-start:[python.example_code.guardduty.DeleteDetector]
    def delete_detector(self, detector_id: str) -> None:
        """
        Deletes a GuardDuty detector.

        :param detector_id: The ID of the detector to delete.
        """
        try:
            self.guardduty_client.delete_detector(DetectorId=detector_id)
            logger.info(f"Deleted GuardDuty detector: {detector_id}")
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error(f"Detector not found or invalid ID: {detector_id}")
            elif error_code == "InternalServerErrorException":
                logger.error("Internal server error occurred while deleting detector")
            else:
                logger.error(f"Error deleting detector {detector_id}: {e}")
            raise

    # snippet-end:[python.example_code.guardduty.DeleteDetector]


# snippet-end:[python.example_code.guardduty.GuardDutyWrapper.class]
