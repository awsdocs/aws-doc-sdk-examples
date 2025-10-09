# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Inspector
to manage vulnerability assessments and findings.
"""

import logging
import boto3
from botocore.exceptions import ClientError
from typing import Dict, List, Optional, Any

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.inspector.InspectorWrapper.class]
# snippet-start:[python.example_code.inspector.InspectorWrapper.decl]
class InspectorWrapper:
    """Encapsulates Amazon Inspector functionality."""

    def __init__(self, inspector_client: boto3.client):
        """
        :param inspector_client: A Boto3 Amazon Inspector client.
        """
        self.inspector_client = inspector_client

    @classmethod
    def from_client(cls):
        inspector_client = boto3.client("inspector2")
        return cls(inspector_client)

    # snippet-end:[python.example_code.inspector.InspectorWrapper.decl]

    # snippet-start:[python.example_code.inspector.Enable]
    def enable_inspector(
        self,
        account_ids: Optional[List[str]] = None,
        resource_types: Optional[List[str]] = None,
    ) -> Dict[str, Any]:
        """
        Enable Amazon Inspector for the specified accounts and resource types.

        :param account_ids: List of account IDs to enable Inspector for. If None, enables for current account.
        :param resource_types: List of resource types to enable scanning for (EC2, ECR, LAMBDA).
        :return: Response from the Enable operation.
        """
        try:
            params = {}
            if account_ids is not None:
                params["accountIds"] = account_ids
            if resource_types is not None:
                params["resourceTypes"] = resource_types

            response = self.inspector_client.enable(**params)
            logger.info("Successfully enabled Inspector")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when enabling Inspector: %s", e)
            elif error_code == "AccessDeniedException":
                logger.error("Access denied when enabling Inspector: %s", e)
            else:
                logger.error("Error enabling Inspector: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.Enable]

    # snippet-start:[python.example_code.inspector.BatchGetAccountStatus]
    def get_account_status(
        self, account_ids: Optional[List[str]] = None
    ) -> Dict[str, Any]:
        """
        Get the status of Amazon Inspector for the specified accounts.

        :param account_ids: List of account IDs to get status for. If None, gets status for current account.
        :return: Response containing account status information.
        """
        try:
            params = {}
            if account_ids is not None:
                params["accountIds"] = account_ids

            response = self.inspector_client.batch_get_account_status(**params)
            logger.info("Successfully retrieved account status")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when getting account status: %s", e)
            elif error_code == "AccessDeniedException":
                logger.error("Access denied when getting account status: %s", e)
            else:
                logger.error("Error getting account status: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.BatchGetAccountStatus]

    # snippet-start:[python.example_code.inspector.ListFindings]
    def list_findings(
        self,
        filter_criteria: Optional[Dict[str, Any]] = None,
        max_results: Optional[int] = None,
        next_token: Optional[str] = None,
        sort_criteria: Optional[Dict[str, Any]] = None,
    ) -> Dict[str, Any]:
        """
        List security findings from Amazon Inspector.

        :param filter_criteria: Criteria to filter findings.
        :param max_results: Maximum number of results to return.
        :param next_token: Token for pagination.
        :param sort_criteria: Criteria to sort findings.
        :return: Response containing findings.
        """
        try:
            params = {}
            if filter_criteria is not None:
                params["filterCriteria"] = filter_criteria
            if max_results is not None:
                params["maxResults"] = max_results
            if next_token is not None:
                params["nextToken"] = next_token
            if sort_criteria is not None:
                params["sortCriteria"] = sort_criteria

            response = self.inspector_client.list_findings(**params)
            logger.info(
                "Successfully listed %d findings", len(response.get("findings", []))
            )
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when listing findings: %s", e)
            elif error_code == "InternalServerException":
                logger.error("Internal server error when listing findings: %s", e)
            else:
                logger.error("Error listing findings: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.ListFindings]

    # snippet-start:[python.example_code.inspector.BatchGetFindingDetails]
    def get_finding_details(self, finding_arns: List[str]) -> Dict[str, Any]:
        """
        Get detailed information for specific findings.

        :param finding_arns: List of finding ARNs to get details for.
        :return: Response containing finding details.
        """
        try:
            response = self.inspector_client.batch_get_finding_details(
                findingArns=finding_arns
            )
            logger.info(
                "Successfully retrieved details for %d findings", len(finding_arns)
            )
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when getting finding details: %s", e)
            elif error_code == "AccessDeniedException":
                logger.error("Access denied when getting finding details: %s", e)
            else:
                logger.error("Error getting finding details: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.BatchGetFindingDetails]

    # snippet-start:[python.example_code.inspector.ListCoverage]
    def list_coverage(
        self,
        filter_criteria: Optional[Dict[str, Any]] = None,
        max_results: Optional[int] = None,
        next_token: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        List coverage statistics for resources scanned by Amazon Inspector.

        :param filter_criteria: Criteria to filter coverage results.
        :param max_results: Maximum number of results to return.
        :param next_token: Token for pagination.
        :return: Response containing coverage information.
        """
        try:
            params = {}
            if filter_criteria is not None:
                params["filterCriteria"] = filter_criteria
            if max_results is not None:
                params["maxResults"] = max_results
            if next_token is not None:
                params["nextToken"] = next_token

            response = self.inspector_client.list_coverage(**params)
            logger.info(
                "Successfully listed coverage for %d resources",
                len(response.get("coveredResources", [])),
            )
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when listing coverage: %s", e)
            else:
                logger.error("Error listing coverage: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.ListCoverage]

    # snippet-start:[python.example_code.inspector.Disable]
    def disable_inspector(
        self,
        account_ids: Optional[List[str]] = None,
        resource_types: Optional[List[str]] = None,
    ) -> Dict[str, Any]:
        """
        Disable Amazon Inspector for the specified accounts and resource types.

        :param account_ids: List of account IDs to disable Inspector for. If None, disables for current account.
        :param resource_types: List of resource types to disable scanning for (EC2, ECR, LAMBDA).
        :return: Response from the Disable operation.
        """
        try:
            params = {}
            if account_ids is not None:
                params["accountIds"] = account_ids
            if resource_types is not None:
                params["resourceTypes"] = resource_types

            response = self.inspector_client.disable(**params)
            logger.info("Successfully disabled Inspector")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "ValidationException":
                logger.error("Validation error when disabling Inspector: %s", e)
            elif error_code == "ConflictException":
                logger.error("Conflict error when disabling Inspector: %s", e)
            else:
                logger.error("Error disabling Inspector: %s", e)
            raise

    # snippet-end:[python.example_code.inspector.Disable]


# snippet-end:[python.example_code.inspector.InspectorWrapper.class]
