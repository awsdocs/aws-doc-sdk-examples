# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for Amazon SES v2 operations demonstrating the new filter
support on list operations for configuration sets, email identities, and
tenants.
"""

import logging
from typing import Any, Dict, List, Optional, Tuple

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sesv2.SearchFilterWrapper.class]
# snippet-start:[python.example_code.sesv2.SearchFilterWrapper.decl]
class SearchFilterWrapper:
    """Encapsulates Amazon SES v2 search-and-filter operations."""

    def __init__(self, sesv2_client: boto3.client):
        """
        Initializes the SearchFilterWrapper with an Amazon SES v2 client.

        :param sesv2_client: A Boto3 Amazon SES v2 client.
        """
        self.sesv2_client = sesv2_client

    @classmethod
    def from_client(cls) -> "SearchFilterWrapper":
        """
        Creates a SearchFilterWrapper instance with a default Boto3 SES v2 client.

        :return: An initialized SearchFilterWrapper instance.
        """
        sesv2_client = boto3.client("sesv2")
        return cls(sesv2_client)

    # snippet-end:[python.example_code.sesv2.SearchFilterWrapper.decl]

    # snippet-start:[python.example_code.sesv2.ListConfigurationSets]
    def list_configuration_sets(
        self,
        filter_params: Optional[Dict[str, str]] = None,
        page_size: Optional[int] = None,
    ) -> List[str]:
        """
        Lists configuration sets associated with the account in the current
        region. Supports optional filter parameters and pagination.

        :param filter_params: Optional dictionary of filter key-value pairs
            (e.g., {"CONFIGURATION_SET_NAME_CONTAINS": "marketing"}).
        :param page_size: Optional number of results per page.
        :return: A list of configuration set name strings.
        """
        try:
            # ListConfigurationSets has no registered Boto3 paginator, so
            # pagination is handled manually using the NextToken field.
            configuration_sets = list()
            next_token = None
            while True:
                kwargs = dict()
                if filter_params is not None:
                    kwargs["Filter"] = filter_params
                if page_size is not None:
                    kwargs["PageSize"] = page_size
                if next_token is not None:
                    kwargs["NextToken"] = next_token

                response = self.sesv2_client.list_configuration_sets(**kwargs)
                configuration_sets.extend(response.get("ConfigurationSets", list()))
                next_token = response.get("NextToken")
                if not next_token:
                    break
            logger.info("Listed %d configuration sets.", len(configuration_sets))
            return configuration_sets
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request when listing configuration sets: %s",
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Error listing configuration sets: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sesv2.ListConfigurationSets]

    # snippet-start:[python.example_code.sesv2.ListEmailIdentities]
    def list_email_identities(
        self,
        filter_params: Optional[Dict[str, str]] = None,
        page_size: Optional[int] = None,
    ) -> List[Dict[str, Any]]:
        """
        Lists email identities associated with the account. Supports optional
        filter parameters and pagination.

        :param filter_params: Optional dictionary of filter key-value pairs
            (e.g., {"IDENTITY_NAME_CONTAINS": "example"}).
        :param page_size: Optional number of results per page.
        :return: A list of identity info dictionaries.
        """
        try:
            # ListEmailIdentities has no registered Boto3 paginator, so
            # pagination is handled manually using the NextToken field.
            identities = list()
            next_token = None
            while True:
                kwargs = dict()
                if filter_params is not None:
                    kwargs["Filter"] = filter_params
                if page_size is not None:
                    kwargs["PageSize"] = page_size
                if next_token is not None:
                    kwargs["NextToken"] = next_token

                response = self.sesv2_client.list_email_identities(**kwargs)
                identities.extend(response.get("EmailIdentities", list()))
                next_token = response.get("NextToken")
                if not next_token:
                    break
            logger.info("Listed %d email identities.", len(identities))
            return identities
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request when listing email identities: %s",
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Error listing email identities: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sesv2.ListEmailIdentities]

    # snippet-start:[python.example_code.sesv2.ListTenants]
    def list_tenants(
        self,
        filter_params: Optional[Dict[str, str]] = None,
        page_size: Optional[int] = None,
    ) -> List[Dict[str, Any]]:
        """
        Lists tenants associated with the account. Supports optional filter
        parameters and pagination.

        :param filter_params: Optional dictionary of filter key-value pairs
            (e.g., {"TENANT_NAME_CONTAINS": "customer"}).
        :param page_size: Optional number of results per page.
        :return: A list of tenant info dictionaries.
        """
        try:
            # Pagination is handled manually using the NextToken field to keep
            # the three list methods consistent. The sibling list operations
            # (configuration sets, email identities) have no registered Boto3
            # paginator, so the same manual approach is used here.
            tenants = list()
            next_token = None
            while True:
                kwargs = dict()
                if filter_params is not None:
                    kwargs["Filter"] = filter_params
                if page_size is not None:
                    kwargs["PageSize"] = page_size
                if next_token is not None:
                    kwargs["NextToken"] = next_token

                response = self.sesv2_client.list_tenants(**kwargs)
                tenants.extend(response.get("Tenants", list()))
                next_token = response.get("NextToken")
                if not next_token:
                    break
            logger.info("Listed %d tenants.", len(tenants))
            return tenants
        except ClientError as err:
            if err.response["Error"]["Code"] == "TooManyRequestsException":
                logger.error(
                    "Too many requests when listing tenants: %s",
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Error listing tenants: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sesv2.ListTenants]

    # snippet-start:[python.example_code.sesv2.CreateConfigurationSet]
    def create_configuration_set(self, configuration_set_name: str) -> bool:
        """
        Creates a configuration set with the specified name.

        :param configuration_set_name: The name for the new configuration set.
        :return: True if the configuration set was created, or False if a
            configuration set with that name already existed.
        """
        try:
            self.sesv2_client.create_configuration_set(
                ConfigurationSetName=configuration_set_name
            )
            logger.info("Created configuration set '%s'.", configuration_set_name)
            return True
        except ClientError as err:
            if err.response["Error"]["Code"] == "AlreadyExistsException":
                logger.warning(
                    "Configuration set '%s' already exists. Skipping creation.",
                    configuration_set_name,
                )
                return False
            else:
                logger.error(
                    "Error creating configuration set '%s': %s",
                    configuration_set_name,
                    err.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.sesv2.CreateConfigurationSet]

    def create_email_identity(self, email_identity: str) -> Dict[str, Any]:
        """
        Creates an email identity (email address or domain) for verification.

        :param email_identity: The email address or domain to verify.
        :return: A dictionary with identity type, verification status, and
            DKIM attributes.
        """
        try:
            response = self.sesv2_client.create_email_identity(
                EmailIdentity=email_identity
            )
            logger.info("Created email identity '%s'.", email_identity)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "TooManyRequestsException":
                logger.error(
                    "Too many requests when creating email identity '%s': %s",
                    email_identity,
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Error creating email identity '%s': %s",
                    email_identity,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-start:[python.example_code.sesv2.CreateTenant]
    def create_tenant(self, tenant_name: str) -> Dict[str, Any]:
        """
        Creates a tenant with the specified name.

        :param tenant_name: The name of the tenant to create.
        :return: A dictionary containing TenantName, TenantId, TenantArn, and
            CreatedTimestamp.
        """
        try:
            response = self.sesv2_client.create_tenant(TenantName=tenant_name)
            logger.info("Created tenant '%s'.", tenant_name)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request when creating tenant '%s': %s",
                    tenant_name,
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Error creating tenant '%s': %s",
                    tenant_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sesv2.CreateTenant]

    # snippet-start:[python.example_code.sesv2.DeleteConfigurationSet]
    def delete_configuration_set(self, configuration_set_name: str) -> None:
        """
        Deletes a configuration set.

        :param configuration_set_name: The name of the configuration set to
            delete.
        """
        try:
            self.sesv2_client.delete_configuration_set(
                ConfigurationSetName=configuration_set_name
            )
            logger.info("Deleted configuration set '%s'.", configuration_set_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.warning(
                    "Configuration set '%s' not found. Skipping deletion.",
                    configuration_set_name,
                )
            else:
                logger.error(
                    "Error deleting configuration set '%s': %s",
                    configuration_set_name,
                    err.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.sesv2.DeleteConfigurationSet]

    def delete_email_identity(self, email_identity: str) -> None:
        """
        Deletes an email identity.

        :param email_identity: The email address or domain to delete.
        """
        try:
            self.sesv2_client.delete_email_identity(EmailIdentity=email_identity)
            logger.info("Deleted email identity '%s'.", email_identity)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.warning(
                    "Email identity '%s' not found. Skipping deletion.",
                    email_identity,
                )
            else:
                logger.error(
                    "Error deleting email identity '%s': %s",
                    email_identity,
                    err.response["Error"]["Message"],
                )
                raise

    # snippet-start:[python.example_code.sesv2.DeleteTenant]
    def delete_tenant(self, tenant_name: str) -> None:
        """
        Deletes a tenant.

        :param tenant_name: The name of the tenant to delete.
        """
        try:
            self.sesv2_client.delete_tenant(TenantName=tenant_name)
            logger.info("Deleted tenant '%s'.", tenant_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.warning(
                    "Tenant '%s' not found. Skipping deletion.",
                    tenant_name,
                )
            else:
                logger.error(
                    "Error deleting tenant '%s': %s",
                    tenant_name,
                    err.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.sesv2.DeleteTenant]

    def list_configuration_sets_page(
        self,
        filter_params: Dict[str, str],
        page_size: int,
        next_token: Optional[str] = None,
    ) -> Tuple[List[str], Optional[str]]:
        """
        Gets one page of filtered configuration sets. Use this instead of
        list_configuration_sets to work with each page separately.

        :param filter_params: The filter to apply.
        :param page_size: The maximum number of results in the page.
        :param next_token: The NextToken from the previous page, or None for the
            first page.
        :return: The configuration set names in this page, and the NextToken for
            the next page, or None if this is the last page.
        """
        kwargs = {"Filter": filter_params, "PageSize": page_size}
        if next_token is not None:
            kwargs["NextToken"] = next_token
        try:
            response = self.sesv2_client.list_configuration_sets(**kwargs)
        except ClientError as err:
            logger.error(
                "Error listing configuration sets: %s",
                err.response["Error"]["Message"],
            )
            raise
        return response.get("ConfigurationSets", list()), response.get("NextToken")


# snippet-end:[python.example_code.sesv2.SearchFilterWrapper.class]
