# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Resource Access Manager
(RAM) to manage resource shares, including creating, associating, disassociating,
getting, and deleting resource shares.
"""

import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ram.RamWrapper.class]
# snippet-start:[python.example_code.ram.RamWrapper.decl]
class RamWrapper:
    """Encapsulates AWS Resource Access Manager (RAM) operations."""

    def __init__(self, ram_client: boto3.client):
        """
        :param ram_client: A Boto3 RAM client.
        """
        self.ram_client = ram_client

    @classmethod
    def from_client(cls) -> "RamWrapper":
        """
        Instantiates this class from a default Boto3 RAM client.

        :return: An instance of RamWrapper.
        """
        ram_client = boto3.client("ram")
        return cls(ram_client)

    # snippet-end:[python.example_code.ram.RamWrapper.decl]

    # snippet-start:[python.example_code.ram.CreateResourceShare]
    def create_resource_share(
        self,
        name: str,
        resource_arns: Optional[List[str]] = None,
        principals: Optional[List[str]] = None,
        allow_external_principals: bool = False,
        tags: Optional[List[Dict[str, str]]] = None,
    ) -> Dict[str, Any]:
        """
        Creates a resource share with specified resources and principals.

        :param name: A descriptive name for the resource share.
        :param resource_arns: List of ARNs of resources to include in the share.
        :param principals: List of principal ARNs (orgs, OUs, or accounts) to share with.
        :param allow_external_principals: Whether to allow sharing outside the org.
        :param tags: Optional list of tags to attach to the resource share. RAM tags
                     use lowercase keys, such as {"key": "Name", "value": "Demo"}.
                     SSM tags use capitalized keys, such as
                     {"Key": "Name", "Value": "Demo"}.
        :return: The resource share object from the response.
        """
        if resource_arns is None:
            resource_arns = list()
        if principals is None:
            principals = list()
        if tags is None:
            tags = list()
        try:
            params = dict()
            params["name"] = name
            # Always send this value. The RAM default is true, so leaving it out
            # would allow sharing with accounts outside your organization.
            params["allowExternalPrincipals"] = allow_external_principals
            if resource_arns:
                params["resourceArns"] = resource_arns
            if principals:
                params["principals"] = principals
            if tags:
                params["tags"] = tags
            response = self.ram_client.create_resource_share(**params)
            resource_share = response.get("resourceShare", dict())
            logger.info(
                "Created RAM resource share '%s' with status '%s'.",
                name,
                resource_share.get("status"),
            )
            return resource_share
        except ClientError as err:
            if err.response["Error"]["Code"] == "MalformedArnException":
                logger.error(
                    "Malformed ARN in request. Verify resource ARN format "
                    "(arn:aws:ssm:region:account-id:document/name) and "
                    "principal ARN format "
                    "(arn:aws:organizations::account-id:organization/o-id)."
                )
            raise

    # snippet-end:[python.example_code.ram.CreateResourceShare]

    # snippet-start:[python.example_code.ram.AssociateResourceShare]
    def associate_resource_share(
        self,
        resource_share_arn: str,
        resource_arns: Optional[List[str]] = None,
        principals: Optional[List[str]] = None,
    ) -> List[Dict[str, Any]]:
        """
        Adds principals or resources to an existing resource share.

        :param resource_share_arn: The ARN of the resource share to update.
        :param resource_arns: Optional list of resource ARNs to add.
        :param principals: Optional list of principal ARNs to add.
        :return: List of resource share association objects.
        """
        if resource_arns is None:
            resource_arns = list()
        if principals is None:
            principals = list()
        try:
            params = dict()
            params["resourceShareArn"] = resource_share_arn
            if resource_arns:
                params["resourceArns"] = resource_arns
            if principals:
                params["principals"] = principals
            response = self.ram_client.associate_resource_share(**params)
            associations = response.get("resourceShareAssociations", list())
            logger.info(
                "Associated %d item(s) with resource share '%s'.",
                len(associations),
                resource_share_arn,
            )
            return associations
        except ClientError as err:
            if err.response["Error"]["Code"] == "OperationNotPermittedException":
                logger.error(
                    "Not permitted to associate with resource share '%s'. "
                    "Verify IAM permissions include ram:AssociateResourceShare "
                    "and that AWS Organizations integration is enabled in RAM.",
                    resource_share_arn,
                )
            raise

    # snippet-end:[python.example_code.ram.AssociateResourceShare]

    # snippet-start:[python.example_code.ram.GetResourceShares]
    def get_resource_shares(
        self,
        resource_owner: str = "SELF",
        resource_share_arns: Optional[List[str]] = None,
    ) -> List[Dict[str, Any]]:
        """
        Retrieves details about resource shares you own or that are shared
        with you. Uses a paginator, so the results include every page.

        :param resource_owner: 'SELF' for owned shares, 'OTHER-ACCOUNTS' for shares
                               received from others.
        :param resource_share_arns: Optional list of resource share ARNs to filter by.
        :return: List of resource share objects.
        """
        if resource_share_arns is None:
            resource_share_arns = list()
        try:
            params = dict()
            params["resourceOwner"] = resource_owner
            if resource_share_arns:
                params["resourceShareArns"] = resource_share_arns
            shares = list()
            paginator = self.ram_client.get_paginator("get_resource_shares")
            for page in paginator.paginate(**params):
                shares.extend(page.get("resourceShares", list()))
            logger.info("Retrieved %d resource share(s).", len(shares))
            return shares
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter. Verify resourceOwner is 'SELF' or "
                    "'OTHER-ACCOUNTS' and ARNs are properly formatted."
                )
            raise

    # snippet-end:[python.example_code.ram.GetResourceShares]

    # snippet-start:[python.example_code.ram.DisassociateResourceShare]
    def disassociate_resource_share(
        self,
        resource_share_arn: str,
        resource_arns: Optional[List[str]] = None,
        principals: Optional[List[str]] = None,
    ) -> List[Dict[str, Any]]:
        """
        Removes principals or resources from a resource share.

        :param resource_share_arn: The ARN of the resource share to update.
        :param resource_arns: Optional list of resource ARNs to remove.
        :param principals: Optional list of principal ARNs to remove.
        :return: List of resource share association objects.
        """
        if resource_arns is None:
            resource_arns = list()
        if principals is None:
            principals = list()
        try:
            params = dict()
            params["resourceShareArn"] = resource_share_arn
            if resource_arns:
                params["resourceArns"] = resource_arns
            if principals:
                params["principals"] = principals
            response = self.ram_client.disassociate_resource_share(**params)
            associations = response.get("resourceShareAssociations", list())
            logger.info(
                "Disassociated %d item(s) from resource share '%s'.",
                len(associations),
                resource_share_arn,
            )
            return associations
        except ClientError as err:
            if err.response["Error"]["Code"] == "UnknownResourceException":
                logger.error(
                    "Resource share or principal not found. Verify the resource "
                    "share ARN '%s' exists and the principal is currently "
                    "associated.",
                    resource_share_arn,
                )
            raise

    # snippet-end:[python.example_code.ram.DisassociateResourceShare]

    # snippet-start:[python.example_code.ram.DeleteResourceShare]
    def delete_resource_share(self, resource_share_arn: str) -> bool:
        """
        Deletes the specified resource share.

        :param resource_share_arn: The ARN of the resource share to delete.
        :return: True if deletion was successful.
        """
        try:
            response = self.ram_client.delete_resource_share(
                resourceShareArn=resource_share_arn
            )
            success = response.get("returnValue", False)
            logger.info("Deleted resource share '%s': %s.", resource_share_arn, success)
            return success
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidStateTransitionException":
                logger.error(
                    "Resource share '%s' is not in a valid state for deletion. "
                    "Ensure all associations are removed and the share is ACTIVE.",
                    resource_share_arn,
                )
            raise

    # snippet-end:[python.example_code.ram.DeleteResourceShare]


# snippet-end:[python.example_code.ram.RamWrapper.class]
