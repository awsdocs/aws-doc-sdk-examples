# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Support unit tests.
"""

from datetime import datetime, timedelta
import json
from test_tools.example_stubber import ExampleStubber


class SupportStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Support unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Support client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_describe_services(self, language, services, error_code=None):
        expected_params = {"language": language}
        response = {"services": services}
        self._stub_bifurcator(
            "describe_services", expected_params, response, error_code=error_code
        )

    def stub_describe_severity_levels(self, language, severity_levels, error_code=None):
        expected_params = {"language": language}
        response = {"severityLevels": severity_levels}
        self._stub_bifurcator(
            "describe_severity_levels", expected_params, response, error_code=error_code
        )

    def stub_create_case(self, service, category, severity, case_id, error_code=None):
        expected_params = {
            "subject": "Example case for testing, ignore.",
            "serviceCode": service["code"],
            "severityCode": severity["code"],
            "categoryCode": category["code"],
            "communicationBody": "Example support case body.",
            "language": "en",
            "issueType": "customer-service",
        }
        response = {"caseId": case_id}
        self._stub_bifurcator(
            "create_case", expected_params, response, error_code=error_code
        )

    def stub_add_attachments_to_set(self, set_id, error_code=None):
        expected_params = {
            "attachments": [
                {
                    "data": b"This is a sample file for attachment to a support case.",
                    "fileName": "attachment_file.txt",
                }
            ]
        }
        response = {"attachmentSetId": set_id}
        self._stub_bifurcator(
            "add_attachments_to_set", expected_params, response, error_code=error_code
        )

    def stub_add_communication_to_case(
        self, attachment_set_id, case_id, error_code=None
    ):
        expected_params = {
            "caseId": case_id,
            "communicationBody": "This is an example communication added to a support case.",
            "attachmentSetId": attachment_set_id,
        }
        response = {}
        self._stub_bifurcator(
            "add_communication_to_case",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_describe_communications(self, case_id, communications, error_code=None):
        expected_params = {"caseId": case_id}
        response = {"communications": communications}
        self._stub_bifurcator(
            "describe_communications", expected_params, response, error_code=error_code
        )

    def stub_describe_attachment(self, attachment_id, file_name, error_code=None):
        expected_params = {"attachmentId": attachment_id}
        response = {"attachment": {"fileName": file_name}}
        self._stub_bifurcator(
            "describe_attachment", expected_params, response, error_code=error_code
        )

    def stub_resolve_case(self, case_id, error_code=None):
        expected_params = {"caseId": case_id}
        response = {"finalCaseStatus": "resolved"}
        self._stub_bifurcator(
            "resolve_case", expected_params, response, error_code=error_code
        )

    def stub_describe_cases(self, cases, resolved, error_code=None):
        start_time = str(datetime.utcnow().date())
        end_time = str(datetime.utcnow().date() + timedelta(days=1))
        expected_params = {
            "afterTime": start_time,
            "beforeTime": end_time,
            "includeResolvedCases": resolved,
            "language": "en",
        }
        response = {"cases": cases}
        self._stub_bifurcator(
            "describe_cases", expected_params, response, error_code=error_code
        )
