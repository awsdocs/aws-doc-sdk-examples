# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Wrapper class for Amazon Inspector2 operations.

This module provides a wrapper around the Amazon Inspector2 (Inspector2) API
to simplify common operations such as enabling/disabling scanning, retrieving
account status, managing scan configuration, listing finding aggregations,
creating findings reports, searching vulnerabilities, and listing permissions.
"""

import logging
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.inspector2.Inspector2Wrapper.decl]
class Inspector2Wrapper:
    """Encapsulates Amazon Inspector2 operations."""

    def __init__(self, inspector2_client: boto3.client) -> None:
        """
        Initializes the Inspector2Wrapper with an Inspector2 client.

        :param inspector2_client: A Boto3 Inspector2 client.
        """
        self.inspector2_client = inspector2_client

    @classmethod
    def from_client(cls) -> "Inspector2Wrapper":
        """
        Creates an Inspector2Wrapper using the default Boto3 client.

        :return: An instance of Inspector2Wrapper.
        """
        inspector2_client = boto3.client("inspector2")
        return cls(inspector2_client)

    # snippet-end:[python.example_code.inspector2.Inspector2Wrapper.decl]

    # snippet-start:[python.example_code.inspector2.Enable]
    def enable(self, resource_types: list[str]) -> dict[str, Any]:
        """
        Enables Amazon Inspector scans for the specified resource types.

        :param resource_types: A list of resource types to enable scanning for
                               (e.g., ["EC2", "ECR"]).
        :return: A dict containing 'accounts' and 'failedAccounts' lists.
        :raises ClientError: If a ValidationException or other error occurs.
        """
        try:
            response = self.inspector2_client.enable(resourceTypes=resource_types)
            logger.info("Enabled Inspector for resource types: %s", resource_types)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Validation error enabling Inspector: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.Enable]

    # snippet-start:[python.example_code.inspector2.BatchGetAccountStatus]
    def batch_get_account_status(
        self, account_ids: Optional[list[str]] = None
    ) -> dict[str, Any]:
        """
        Retrieves the Amazon Inspector status for one or more AWS accounts.

        :param account_ids: Optional list of account IDs. If None, returns the
                            caller's account status.
        :return: A dict containing 'accounts' and 'failedAccounts' lists.
        :raises ClientError: If an AccessDeniedException or other error occurs.
        """
        try:
            params = dict()
            if account_ids is not None:
                params["accountIds"] = account_ids
            response = self.inspector2_client.batch_get_account_status(**params)
            logger.info("Retrieved account status successfully.")
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error(
                    "Access denied retrieving Inspector account status: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.BatchGetAccountStatus]

    # snippet-start:[python.example_code.inspector2.GetConfiguration]
    def get_configuration(self) -> dict[str, Any]:
        """
        Retrieves setting configurations for Inspector scans.

        :return: A dict containing 'ec2Configuration' and 'ecrConfiguration'.
        :raises ClientError: If a ResourceNotFoundException or other error occurs.
        """
        try:
            response = self.inspector2_client.get_configuration()
            logger.info("Retrieved Inspector scan configuration.")
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Resource not found getting configuration: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.GetConfiguration]

    # snippet-start:[python.example_code.inspector2.UpdateConfiguration]
    def update_configuration(self, ecr_rescan_duration: str) -> None:
        """
        Updates setting configurations for Inspector scans.

        :param ecr_rescan_duration: The ECR rescan duration to set
                                    (e.g., "DAYS_30", "DAYS_60", "DAYS_90",
                                     "DAYS_180", "LIFETIME").
        :raises ClientError: If a ValidationException or other error occurs.
        """
        try:
            self.inspector2_client.update_configuration(
                ecrConfiguration={"rescanDuration": ecr_rescan_duration}
            )
            logger.info(
                "Updated ECR rescan duration to: %s", ecr_rescan_duration
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Validation error updating configuration: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.UpdateConfiguration]

    # snippet-start:[python.example_code.inspector2.ListFindingAggregations]
    def list_finding_aggregations(
        self, aggregation_type: str
    ) -> list[dict[str, Any]]:
        """
        Lists aggregated finding data for the environment based on specific criteria.

        Uses pagination to retrieve all results.

        :param aggregation_type: The type of aggregation to perform
                                 (e.g., "FINDING_TYPE", "ACCOUNT").
        :return: A list of aggregation response dicts.
        :raises ClientError: If a ValidationException or other error occurs.
        """
        try:
            aggregations = list()
            paginator = self.inspector2_client.get_paginator(
                "list_finding_aggregations"
            )
            page_iterator = paginator.paginate(
                aggregationType=aggregation_type,
                PaginationConfig={"PageSize": 10},
            )
            for page in page_iterator:
                aggregations.extend(page.get("responses", list()))
            logger.info(
                "Retrieved %d finding aggregations for type %s.",
                len(aggregations),
                aggregation_type,
            )
            return aggregations
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Validation error listing finding aggregations: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.ListFindingAggregations]

    # snippet-start:[python.example_code.inspector2.CreateFindingsReport]
    def create_findings_report(
        self,
        report_format: str,
        bucket_name: str,
        key_prefix: str,
        kms_key_arn: str,
    ) -> str:
        """
        Creates a finding report and exports it to an S3 bucket.

        :param report_format: The report format ("CSV" or "JSON").
        :param bucket_name: The S3 bucket name for the report destination.
        :param key_prefix: The key prefix in the S3 bucket.
        :param kms_key_arn: The ARN of the KMS key for report encryption.
        :return: The report ID string.
        :raises ClientError: If a ResourceNotFoundException or other error occurs.
        """
        try:
            response = self.inspector2_client.create_findings_report(
                reportFormat=report_format,
                s3Destination={
                    "bucketName": bucket_name,
                    "keyPrefix": key_prefix,
                    "kmsKeyArn": kms_key_arn,
                },
            )
            report_id = response.get("reportId", "")
            logger.info("Created findings report with ID: %s", report_id)
            return report_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Resource not found creating findings report: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.CreateFindingsReport]

    # snippet-start:[python.example_code.inspector2.GetFindingsReportStatus]
    def get_findings_report_status(self, report_id: str) -> dict[str, Any]:
        """
        Gets the status of a findings report.

        :param report_id: The ID of the findings report.
        :return: A dict with 'status', 'destination', and 'errorCode' fields.
        :raises ClientError: If a ResourceNotFoundException or other error occurs.
        """
        try:
            response = self.inspector2_client.get_findings_report_status(
                reportId=report_id
            )
            logger.info(
                "Report %s status: %s",
                report_id,
                response.get("status", "UNKNOWN"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Report not found: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.GetFindingsReportStatus]

    # snippet-start:[python.example_code.inspector2.SearchVulnerabilities]
    def search_vulnerabilities(
        self, vulnerability_ids: list[str]
    ) -> list[dict[str, Any]]:
        """
        Searches for details about specific vulnerabilities by CVE ID.

        Uses pagination to retrieve all results.

        :param vulnerability_ids: A list of CVE IDs to search for
                                  (e.g., ["CVE-2021-44228"]).
        :return: A list of vulnerability detail dicts.
        :raises ClientError: If a ValidationException or other error occurs.
        """
        try:
            vulnerabilities = list()
            paginator = self.inspector2_client.get_paginator(
                "search_vulnerabilities"
            )
            page_iterator = paginator.paginate(
                filterCriteria={"vulnerabilityIds": vulnerability_ids}
            )
            for page in page_iterator:
                vulnerabilities.extend(page.get("vulnerabilities", list()))
            logger.info(
                "Found %d vulnerabilities for IDs: %s",
                len(vulnerabilities),
                vulnerability_ids,
            )
            return vulnerabilities
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Validation error searching vulnerabilities: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.SearchVulnerabilities]

    # snippet-start:[python.example_code.inspector2.ListAccountPermissions]
    def list_account_permissions(self) -> list[dict[str, str]]:
        """
        Lists the permissions an account has to configure Amazon Inspector.

        Uses pagination to retrieve all results.

        :return: A list of permission dicts with 'operation' and 'service' keys.
        :raises ClientError: If an AccessDeniedException or other error occurs.
        """
        try:
            permissions = list()
            paginator = self.inspector2_client.get_paginator(
                "list_account_permissions"
            )
            page_iterator = paginator.paginate()
            for page in page_iterator:
                permissions.extend(page.get("permissions", list()))
            logger.info("Retrieved %d account permissions.", len(permissions))
            return permissions
        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error(
                    "Access denied listing account permissions: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.ListAccountPermissions]

    # snippet-start:[python.example_code.inspector2.Disable]
    def disable(self, resource_types: list[str]) -> dict[str, Any]:
        """
        Disables Amazon Inspector scans for the specified resource types.

        :param resource_types: A list of resource types to disable scanning for
                               (e.g., ["EC2", "ECR"]).
        :return: A dict containing 'accounts' and 'failedAccounts' lists.
        :raises ClientError: If a ValidationException or other error occurs.
        """
        try:
            response = self.inspector2_client.disable(resourceTypes=resource_types)
            logger.info("Disabled Inspector for resource types: %s", resource_types)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Validation error disabling Inspector: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.inspector2.Disable]
