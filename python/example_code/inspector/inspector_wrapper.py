# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Inspector to check
account status, enable and disable scanning, list findings and finding details,
review coverage and coverage statistics, manage findings filters, and track
usage totals.
"""

import logging
from typing import Any, Optional

import boto3
from botocore.client import BaseClient
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# The resource types that Amazon Inspector can scan.
RESOURCE_TYPES = ["EC2", "ECR", "LAMBDA", "LAMBDA_CODE"]


# snippet-start:[python.example_code.inspector2.Inspector2Wrapper.decl]
class Inspector2Wrapper:
    """Encapsulates Amazon Inspector (Inspector2) operations."""

    def __init__(self, inspector2_client: BaseClient) -> None:
        """
        :param inspector2_client: A Boto3 Amazon Inspector2 client.
        """
        self.inspector2_client = inspector2_client

    @classmethod
    def from_client(cls) -> "Inspector2Wrapper":
        """
        Creates an Inspector2Wrapper using a default Boto3 Inspector2 client.

        :return: An instance of Inspector2Wrapper.
        """
        inspector2_client = boto3.client("inspector2")
        return cls(inspector2_client)

    # snippet-end:[python.example_code.inspector2.Inspector2Wrapper.decl]

    # snippet-start:[python.example_code.inspector2.BatchGetAccountStatus]
    def get_account_status(self) -> list[dict[str, Any]]:
        """
        Gets the Amazon Inspector status of the current account.

        :return: A list of account states. Each includes the overall state and the
                 state of each resource type.
        """
        try:
            response = self.inspector2_client.batch_get_account_status()
            return response.get("accounts", list())
        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error(
                    "You do not have sufficient access to get the Inspector "
                    "account status. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Couldn't get the Inspector account status. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.BatchGetAccountStatus]

    # snippet-start:[python.example_code.inspector2.Enable]
    def enable(self, resource_types: list[str]) -> list[dict[str, Any]]:
        """
        Enables Amazon Inspector scans for the specified resource types.

        :param resource_types: The resource types to enable, such as EC2 or ECR.
        :return: The accounts that the request was applied to. If Inspector is
                 already enabled, a ValidationException is logged and an empty
                 list is returned.
        """
        try:
            response = self.inspector2_client.enable(resourceTypes=resource_types)
            return response.get("accounts", list())
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Inspector may already be enabled for this account. "
                    "Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return list()
            logger.error(
                "Couldn't enable Inspector for %s. Here's why: %s: %s",
                resource_types,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.Enable]

    # snippet-start:[python.example_code.inspector2.ListCoverage]
    def list_coverage(self, max_results: int = 5) -> list[dict[str, Any]]:
        """
        Lists coverage details for the resources that Amazon Inspector scans.

        :param max_results: The maximum number of items to return.
        :return: The covered resources. If a ValidationException occurs, it is
                 logged and an empty list is returned.
        """
        try:
            covered_resources = list()
            paginator = self.inspector2_client.get_paginator("list_coverage")
            for page in paginator.paginate(
                PaginationConfig={"MaxItems": max_results, "PageSize": max_results}
            ):
                covered_resources.extend(page.get("coveredResources", list()))
            return covered_resources
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Validation error listing coverage. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return list()
            logger.error(
                "Couldn't list coverage. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.ListCoverage]

    # snippet-start:[python.example_code.inspector2.ListCoverageStatistics]
    def list_coverage_statistics(
        self, group_by: Optional[str] = None
    ) -> dict[str, Any]:
        """
        Lists coverage statistics for the resources that Amazon Inspector scans.

        :param group_by: Optional value to group the counts by, such as RESOURCE_TYPE.
        :return: A dict with 'totalCounts' and 'countsByGroup'. If a
                 ValidationException occurs, it is logged and empty statistics
                 are returned.
        """
        try:
            stats = {"totalCounts": 0, "countsByGroup": list()}
            params = dict()
            if group_by is not None:
                params["groupBy"] = group_by
            paginator = self.inspector2_client.get_paginator("list_coverage_statistics")
            for page in paginator.paginate(**params):
                # totalCounts is the service-wide total, repeated on every page,
                # so it is assigned rather than summed across pages.
                stats["totalCounts"] = page.get("totalCounts", 0)
                stats["countsByGroup"].extend(page.get("countsByGroup", list()))
            return stats
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Validation error listing coverage statistics. "
                    "Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return {"totalCounts": 0, "countsByGroup": list()}
            logger.error(
                "Couldn't list coverage statistics. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.ListCoverageStatistics]

    # snippet-start:[python.example_code.inspector2.ListFindings]
    def list_findings(self, max_results: int = 10) -> list[dict[str, Any]]:
        """
        Lists Amazon Inspector findings across all resource types.

        :param max_results: The maximum number of findings to return.
        :return: The findings. If a ValidationException occurs, it is logged and
                 an empty list is returned.
        """
        try:
            findings = list()
            paginator = self.inspector2_client.get_paginator("list_findings")
            # This intentionally returns at most max_results findings. MaxItems
            # caps the total, and PageSize requests that many in a single call,
            # so only one request is made.
            for page in paginator.paginate(
                PaginationConfig={"MaxItems": max_results, "PageSize": max_results}
            ):
                findings.extend(page.get("findings", list()))
            return findings
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Validation error listing findings. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return list()
            logger.error(
                "Couldn't list findings. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.ListFindings]

    # snippet-start:[python.example_code.inspector2.BatchGetFindingDetails]
    def batch_get_finding_details(
        self, finding_arns: list[str]
    ) -> list[dict[str, Any]]:
        """
        Gets vulnerability intelligence details for the specified findings.

        :param finding_arns: The ARNs of the findings to look up.
        :return: The finding details.
        """
        try:
            response = self.inspector2_client.batch_get_finding_details(
                findingArns=finding_arns
            )
            return response.get("findingDetails", list())
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Couldn't find details for findings %s. Here's why: %s: %s",
                    finding_arns,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Couldn't get finding details. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.BatchGetFindingDetails]

    # snippet-start:[python.example_code.inspector2.CreateFilter]
    def create_low_severity_filter(
        self, filter_name: str, description: str
    ) -> Optional[str]:
        """
        Creates a filter that suppresses LOW severity findings.

        :param filter_name: The name of the filter.
        :param description: A description of the filter.
        :return: The ARN of the new filter. If a ValidationException occurs, it is
                 logged and None is returned.
        """
        try:
            response = self.inspector2_client.create_filter(
                name=filter_name,
                description=description,
                action="SUPPRESS",
                filterCriteria={"severity": [{"comparison": "EQUALS", "value": "LOW"}]},
            )
            return response["arn"]
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Validation error creating filter %s. Here's why: %s: %s",
                    filter_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return None
            logger.error(
                "Couldn't create filter %s. Here's why: %s: %s",
                filter_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.CreateFilter]

    # snippet-start:[python.example_code.inspector2.ListFilters]
    def list_filters(self, max_results: int = 10) -> list[dict[str, Any]]:
        """
        Lists the findings filters in the current account.

        :param max_results: The maximum number of items to return.
        :return: The filters.
        """
        try:
            filters = list()
            paginator = self.inspector2_client.get_paginator("list_filters")
            for page in paginator.paginate(
                PaginationConfig={"MaxItems": max_results, "PageSize": max_results}
            ):
                filters.extend(page.get("filters", list()))
            return filters
        except ClientError as err:
            logger.error(
                "Couldn't list filters. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.ListFilters]

    # snippet-start:[python.example_code.inspector2.ListUsageTotals]
    def list_usage_totals(self, max_results: int = 10) -> list[dict[str, Any]]:
        """
        Lists Amazon Inspector usage totals for the current account.

        :param max_results: The maximum number of items to return.
        :return: The usage totals. If a ValidationException occurs, it is logged
                 and an empty list is returned.
        """
        try:
            totals = list()
            paginator = self.inspector2_client.get_paginator("list_usage_totals")
            for page in paginator.paginate(
                PaginationConfig={"MaxItems": max_results, "PageSize": max_results}
            ):
                totals.extend(page.get("totals", list()))
            return totals
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Validation error listing usage totals. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return list()
            logger.error(
                "Couldn't list usage totals. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.ListUsageTotals]

    # snippet-start:[python.example_code.inspector2.DeleteFilter]
    def delete_filter(self, filter_arn: str) -> None:
        """
        Deletes a findings filter.

        :param filter_arn: The ARN of the filter to delete.
        """
        try:
            self.inspector2_client.delete_filter(arn=filter_arn)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Filter %s was not found. Here's why: %s: %s",
                    filter_arn,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Couldn't delete filter %s. Here's why: %s: %s",
                    filter_arn,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.DeleteFilter]

    # snippet-start:[python.example_code.inspector2.Disable]
    def disable(self, resource_types: list[str]) -> list[dict[str, Any]]:
        """
        Disables Amazon Inspector scans for the specified resource types.

        :param resource_types: The resource types to disable, such as EC2 or ECR.
        :return: The accounts that the request was applied to. If Inspector is
                 already disabled, a ValidationException is logged and an empty
                 list is returned.
        """
        try:
            response = self.inspector2_client.disable(resourceTypes=resource_types)
            return response.get("accounts", list())
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.warning(
                    "Inspector may already be disabled for this account. "
                    "Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
                return list()
            logger.error(
                "Couldn't disable Inspector for %s. Here's why: %s: %s",
                resource_types,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.inspector2.Disable]
