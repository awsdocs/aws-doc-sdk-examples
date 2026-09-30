# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Cognito Identity
Provider to manage app clients, obtain M2M access tokens, and discover Terms
documents.
"""

import logging
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cognito-idp.CognitoIdentityProviderWrapper.decl]
class CognitoIdentityProviderWrapper:
    """Encapsulates Amazon Cognito Identity Provider actions."""

    def __init__(self, cognito_idp_client: boto3.client):
        """
        Initializes the CognitoIdentityProviderWrapper with an Amazon Cognito
        Identity Provider client.

        :param cognito_idp_client: A Boto3 Amazon Cognito Identity Provider client.
        """
        self.cognito_idp_client = cognito_idp_client

    # snippet-end:[python.example_code.cognito-idp.CognitoIdentityProviderWrapper.decl]

    # snippet-start:[python.example_code.cognito-idp.ListUserPoolClients]
    def list_user_pool_clients(
        self, user_pool_id: str, max_results: int = 5
    ) -> list[dict[str, Any]]:
        """
        Lists app clients in a user pool using pagination.

        :param user_pool_id: The ID of the user pool to list clients for.
        :param max_results: The maximum number of results per page.
        :return: A list of app client descriptions.
        """
        try:
            clients = list()
            paginator = self.cognito_idp_client.get_paginator("list_user_pool_clients")
            page_iterator = paginator.paginate(
                UserPoolId=user_pool_id,
                PaginationConfig={"PageSize": max_results},
            )
            for page in page_iterator:
                clients.extend(page.get("UserPoolClients", list()))
            logger.info(
                "Listed %d app clients for user pool %s.",
                len(clients),
                user_pool_id,
            )
            return clients
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "The user pool %s does not exist. Verify the user pool ID.",
                    user_pool_id,
                )
            raise

    # snippet-end:[python.example_code.cognito-idp.ListUserPoolClients]

    # snippet-start:[python.example_code.cognito-idp.DescribeUserPoolClient]
    def describe_user_pool_client(
        self, user_pool_id: str, client_id: str
    ) -> dict[str, Any]:
        """
        Describes an app client in a user pool, including the client secret.

        :param user_pool_id: The ID of the user pool that contains the app client.
        :param client_id: The ID of the app client to describe.
        :return: A dictionary with the app client configuration.
        """
        try:
            response = self.cognito_idp_client.describe_user_pool_client(
                UserPoolId=user_pool_id, ClientId=client_id
            )
            client_info = response.get("UserPoolClient", dict())
            logger.info(
                "Described app client %s in user pool %s.",
                client_id,
                user_pool_id,
            )
            return client_info
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "The user pool %s or client %s does not exist. "
                    "Verify the stack deployed successfully and the outputs are correct.",
                    user_pool_id,
                    client_id,
                )
            raise

    # snippet-end:[python.example_code.cognito-idp.DescribeUserPoolClient]

    # snippet-start:[python.example_code.cognito-idp.GetClientToken]
    def get_client_token(
        self,
        client_id: str,
        client_secret: str,
        scopes: Optional[list[str]] = None,
    ) -> dict[str, Any]:
        """
        Obtains an M2M access token for an app client using its client ID
        and secret, without end-user sign-in.

        :param client_id: The ID of the app client.
        :param client_secret: The client secret for the app client.
        :param scopes: Optional list of scopes in the format
                       'resource-server-identifier/scope-name'. If omitted,
                       all scopes configured for the app client are authorized.
        :return: A dictionary containing the ClientAuthenticationResult with
                 AccessToken, ExpiresIn, and TokenType.
        """
        try:
            params = dict()
            params["ClientId"] = client_id
            params["Secret"] = client_secret
            if scopes is not None:
                params["Scopes"] = scopes

            response = self.cognito_idp_client.get_client_token(**params)
            auth_result = response.get("ClientAuthenticationResult", dict())
            logger.info(
                "Obtained M2M access token for client %s. Token type: %s, expires in: %d seconds.",
                client_id,
                auth_result.get("TokenType", "Unknown"),
                auth_result.get("ExpiresIn", 0),
            )
            return auth_result
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotAuthorizedException":
                logger.error(
                    "Not authorized for client %s. Verify that the client ID and "
                    "secret are correct, and that the ALLOW_CLIENT_TOKEN_AUTH flow "
                    "is enabled for the app client.",
                    client_id,
                )
            raise

    # snippet-end:[python.example_code.cognito-idp.GetClientToken]

    # snippet-start:[python.example_code.cognito-idp.DescribeTermsByClient]
    def describe_terms_by_client(
        self, user_pool_id: str, client_id: str, terms_name: str
    ) -> dict[str, Any]:
        """
        Looks up a Terms document associated with an app client by client ID,
        user pool ID, and terms name.

        :param user_pool_id: The ID of the user pool that contains the Terms documents.
        :param client_id: The ID of the app client associated with the Terms.
        :param terms_name: The name of the terms document ('terms-of-use' or
                           'privacy-policy').
        :return: A dictionary with the Terms document details.
        """
        try:
            response = self.cognito_idp_client.describe_terms_by_client(
                UserPoolId=user_pool_id,
                ClientId=client_id,
                TermsName=terms_name,
            )
            terms = response.get("Terms", dict())
            logger.info(
                "Described terms '%s' for client %s in user pool %s.",
                terms_name,
                client_id,
                user_pool_id,
            )
            return terms
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "No terms documents found for client %s, user pool %s, "
                    "and terms name '%s'.",
                    client_id,
                    user_pool_id,
                    terms_name,
                )
            raise

    # snippet-end:[python.example_code.cognito-idp.DescribeTermsByClient]
