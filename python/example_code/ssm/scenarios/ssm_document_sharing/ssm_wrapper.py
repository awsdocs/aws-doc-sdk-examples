# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Systems Manager (SSM)
to manage SSM documents, including creating, describing, listing, sharing,
and deleting documents.
"""

import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ssm.SsmDocumentWrapper.class]
# snippet-start:[python.example_code.ssm.SsmDocumentWrapper.decl]
class SsmDocumentWrapper:
    """Encapsulates AWS Systems Manager document operations."""

    def __init__(self, ssm_client: boto3.client):
        """
        :param ssm_client: A Boto3 SSM client.
        """
        self.ssm_client = ssm_client

    @classmethod
    def from_client(cls) -> "SsmDocumentWrapper":
        """
        Instantiates this class from a default Boto3 SSM client.

        :return: An instance of SsmDocumentWrapper.
        """
        ssm_client = boto3.client("ssm")
        return cls(ssm_client)

    # snippet-end:[python.example_code.ssm.SsmDocumentWrapper.decl]

    def create_document(
        self,
        name: str,
        content: str,
        document_type: str = "Automation",
        document_format: str = "YAML",
        tags: Optional[List[Dict[str, str]]] = None,
    ) -> Dict[str, Any]:
        """
        Creates an SSM document that defines the actions Systems Manager
        performs on managed nodes.

        :param name: The name for the document. Must be unique within the account.
        :param content: The document content in YAML or JSON format.
        :param document_type: The type of document (e.g., Automation, Command).
        :param document_format: The format of the content (YAML or JSON).
        :param tags: Optional list of tags to attach to the document.
        :return: The document description returned by the API.
        """
        if tags is None:
            tags = list()
        try:
            params = dict()
            params["Name"] = name
            params["Content"] = content
            params["DocumentType"] = document_type
            params["DocumentFormat"] = document_format
            if tags:
                params["Tags"] = tags
            response = self.ssm_client.create_document(**params)
            document_description = response.get("DocumentDescription", dict())
            logger.info(
                "Created SSM document '%s' with status '%s'.",
                name,
                document_description.get("Status"),
            )
            return document_description
        except ClientError as err:
            if err.response["Error"]["Code"] == "DocumentAlreadyExists":
                logger.error(
                    "Document '%s' already exists. Use a unique name or delete "
                    "the existing document first.",
                    name,
                )
            raise

    def describe_document(self, name: str) -> Dict[str, Any]:
        """
        Describes an SSM document, returning metadata such as status, owner,
        and document version.

        :param name: The name of the document to describe.
        :return: The document description.
        """
        try:
            response = self.ssm_client.describe_document(Name=name)
            document = response.get("Document", dict())
            logger.info(
                "Described document '%s': Status=%s, Owner=%s, Type=%s.",
                name,
                document.get("Status"),
                document.get("Owner"),
                document.get("DocumentType"),
            )
            return document
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidDocument":
                logger.error(
                    "Document '%s' not found. Verify the name is correct and "
                    "the document exists in the current Region.",
                    name,
                )
            raise

    # snippet-start:[python.example_code.ssm.ModifyDocumentPermission]
    def modify_document_permission(
        self,
        name: str,
        permission_type: str = "Share",
        account_ids_to_add: Optional[List[str]] = None,
        account_ids_to_remove: Optional[List[str]] = None,
    ) -> None:
        """
        Shares or un-shares an SSM document with specified AWS account IDs.

        :param name: The name of the document to modify permissions for.
        :param permission_type: The permission type (only 'Share' is valid).
        :param account_ids_to_add: AWS account IDs to grant access to.
        :param account_ids_to_remove: AWS account IDs to revoke access from.
        """
        if account_ids_to_add is None:
            account_ids_to_add = list()
        if account_ids_to_remove is None:
            account_ids_to_remove = list()
        try:
            params = dict()
            params["Name"] = name
            params["PermissionType"] = permission_type
            if account_ids_to_add:
                params["AccountIdsToAdd"] = account_ids_to_add
            if account_ids_to_remove:
                params["AccountIdsToRemove"] = account_ids_to_remove
            self.ssm_client.modify_document_permission(**params)
            logger.info(
                "Modified document permissions for '%s'. Added: %s, Removed: %s.",
                name,
                account_ids_to_add,
                account_ids_to_remove,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "DocumentPermissionLimit":
                logger.error(
                    "Document '%s' cannot be shared with more accounts. "
                    "Max 1,000 accounts total, 20 per call. Use RAM for "
                    "organization/OU-level sharing instead.",
                    name,
                )
            raise

    # snippet-end:[python.example_code.ssm.ModifyDocumentPermission]

    # snippet-start:[python.example_code.ssm.DescribeDocumentPermission]
    def describe_document_permission(
        self, name: str, permission_type: str = "Share"
    ) -> Dict[str, Any]:
        """
        Describes the sharing configuration for an SSM document.

        :param name: The name of the document.
        :param permission_type: The permission type (only 'Share' is valid).
        :return: A dict with AccountIds and AccountSharingInfoList.
        """
        try:
            response = self.ssm_client.describe_document_permission(
                Name=name, PermissionType=permission_type
            )
            account_ids = response.get("AccountIds", list())
            sharing_info = response.get("AccountSharingInfoList", list())
            logger.info(
                "Document '%s' is shared with %d account(s).",
                name,
                len(account_ids),
            )
            return {
                "AccountIds": account_ids,
                "AccountSharingInfoList": sharing_info,
            }
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidPermissionType":
                logger.error(
                    "Invalid permission type. The only valid value is 'Share'."
                )
            raise

    # snippet-end:[python.example_code.ssm.DescribeDocumentPermission]

    # snippet-start:[python.example_code.ssm.ListDocuments]
    def list_documents(
        self,
        filters: Optional[List[Dict[str, Any]]] = None,
    ) -> List[Dict[str, Any]]:
        """
        Lists SSM documents in the current account and Region using pagination.

        :param filters: Optional list of DocumentKeyValuesFilter objects.
        :return: A list of document identifiers.
        """
        if filters is None:
            filters = list()
        try:
            documents = list()
            params = dict()
            if filters:
                params["Filters"] = filters
            paginator = self.ssm_client.get_paginator("list_documents")
            for page in paginator.paginate(**params):
                documents.extend(page.get("DocumentIdentifiers", list()))
            logger.info("Listed %d SSM document(s).", len(documents))
            return documents
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidFilterKey":
                logger.error(
                    "Invalid filter key. Supported keys: Owner, Name, "
                    "PlatformTypes, DocumentType, TargetType."
                )
            raise

    # snippet-end:[python.example_code.ssm.ListDocuments]

    def delete_document(self, name: str) -> None:
        """
        Deletes an SSM document and all managed node associations.

        :param name: The name of the document to delete.
        """
        try:
            self.ssm_client.delete_document(Name=name)
            logger.info("Deleted SSM document '%s'.", name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidDocumentOperation":
                logger.error(
                    "Cannot delete document '%s'. It may still be shared. "
                    "Remove all sharing permissions and delete RAM resource "
                    "shares before deleting the document.",
                    name,
                )
            raise


# snippet-end:[python.example_code.ssm.SsmDocumentWrapper.class]
