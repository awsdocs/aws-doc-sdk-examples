# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon GuardDuty unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber


class GuardDutyStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon GuardDuty unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, guardduty_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param guardduty_client: A Boto 3 Amazon GuardDuty client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(guardduty_client, use_stubs)

    def stub_create_detector(self, detector_id: str, enable: bool = True, 
                           finding_publishing_frequency: str = "FIFTEEN_MINUTES", 
                           error_code: str = None) -> None:
        """
        Stub the create_detector function.

        :param detector_id: The detector ID to return.
        :param enable: Whether the detector is enabled.
        :param finding_publishing_frequency: How often to publish findings.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "Enable": enable,
            "FindingPublishingFrequency": finding_publishing_frequency
        }
        response = {
            "DetectorId": detector_id
        }
        self._stub_bifurcator(
            "create_detector", expected_params, response, error_code=error_code
        )

    def stub_list_detectors(self, detector_ids: list, error_code: str = None) -> None:
        """
        Stub the list_detectors function.

        :param detector_ids: List of detector IDs to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {}
        response = {
            "DetectorIds": detector_ids
        }
        self._stub_bifurcator(
            "list_detectors", expected_params, response, error_code=error_code
        )

    def stub_get_detector(self, detector_id: str, status: str = "ENABLED", 
                         service_role: str = "arn:aws:iam::123456789012:role/aws-guardduty-service-role",
                         finding_publishing_frequency: str = "FIFTEEN_MINUTES",
                         error_code: str = None) -> None:
        """
        Stub the get_detector function.

        :param detector_id: The detector ID.
        :param status: The detector status.
        :param service_role: The service role ARN.
        :param finding_publishing_frequency: How often to publish findings.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "DetectorId": detector_id
        }
        response = {
            "Status": status,
            "ServiceRole": service_role,
            "FindingPublishingFrequency": finding_publishing_frequency,
            "CreatedAt": "2023-01-01T00:00:00.000Z",
            "UpdatedAt": "2023-01-01T00:00:00.000Z"
        }
        self._stub_bifurcator(
            "get_detector", expected_params, response, error_code=error_code
        )

    def stub_create_sample_findings(self, detector_id: str, finding_types: list = None, 
                                  error_code: str = None) -> None:
        """
        Stub the create_sample_findings function.

        :param detector_id: The detector ID.
        :param finding_types: Optional list of finding types.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "DetectorId": detector_id
        }
        if finding_types:
            expected_params["FindingTypes"] = finding_types
            
        response = {}
        self._stub_bifurcator(
            "create_sample_findings", expected_params, response, error_code=error_code
        )

    def stub_list_findings(self, detector_id: str, finding_ids: list, 
                          max_results: int = 50, error_code: str = None) -> None:
        """
        Stub the list_findings function.

        :param detector_id: The detector ID.
        :param finding_ids: List of finding IDs to return.
        :param max_results: Maximum number of results.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "DetectorId": detector_id,
            "MaxResults": max_results
        }
        response = {
            "FindingIds": finding_ids
        }
        self._stub_bifurcator(
            "list_findings", expected_params, response, error_code=error_code
        )

    def stub_get_findings(self, detector_id: str, finding_ids: list, 
                         findings: list, error_code: str = None) -> None:
        """
        Stub the get_findings function.

        :param detector_id: The detector ID.
        :param finding_ids: List of finding IDs to retrieve.
        :param findings: List of finding details to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "DetectorId": detector_id,
            "FindingIds": finding_ids
        }
        response = {
            "Findings": findings
        }
        self._stub_bifurcator(
            "get_findings", expected_params, response, error_code=error_code
        )

    def stub_delete_detector(self, detector_id: str, error_code: str = None) -> None:
        """
        Stub the delete_detector function.

        :param detector_id: The detector ID to delete.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "DetectorId": detector_id
        }
        response = {}
        self._stub_bifurcator(
            "delete_detector", expected_params, response, error_code=error_code
        )