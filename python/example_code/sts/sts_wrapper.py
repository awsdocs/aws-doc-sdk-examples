# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Wrapper class for AWS Security Token Service (STS) operations.

This module provides a wrapper around the Boto3 STS client that demonstrates
how to use STS operations including GetCallerIdentity, AssumeRole,
GetSessionToken, GetFederationToken, GetAccessKeyInfo, and
DecodeAuthorizationMessage.
"""

import json
import logging
from typing import Any, Dict, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sts.STSWrapper.decl]
class STSWrapper:
    """Encapsulates AWS STS operations."""

    def __init__(self, sts_client: boto3.client) -> None:
        """
        Initializes the STSWrapper with a Boto3 STS client.

        :param sts_client: A Boto3 STS client.
        """
        self.sts_client = sts_client

    @classmethod
    def from_client(cls) -> "STSWrapper":
        """
        Creates an STSWrapper instance using the default Boto3 STS client.

        :return: An initialized STSWrapper instance.
        """
        sts_client = boto3.client("sts")
        return cls(sts_client)

    # snippet-end:[python.example_code.sts.STSWrapper.decl]

    # snippet-start:[python.example_code.sts.GetCallerIdentity]
    def get_caller_identity(self) -> Dict[str, str]:
        """
        Returns details about the IAM user or role whose credentials are used
        to call this operation. No parameters are required.

        :return: A dictionary containing Account, Arn, and UserId.
        :raises ClientError: If the call fails (e.g., ExpiredTokenException).
        """
        try:
            response = self.sts_client.get_caller_identity()
            logger.info("Got caller identity: ARN=%s", response.get("Arn", ""))
            return {
                "Account": response["Account"],
                "Arn": response["Arn"],
                "UserId": response["UserId"],
            }
        except ClientError as error:
            if error.response["Error"]["Code"] == "ExpiredTokenException":
                logger.error(
                    "Temporary credentials have expired. Obtain fresh credentials."
                )
            else:
                logger.error(
                    "Couldn't get caller identity. Error: %s: %s",
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.GetCallerIdentity]

    # snippet-start:[python.example_code.sts.AssumeRole]
    def assume_role(
        self,
        role_arn: str,
        role_session_name: str,
        duration_seconds: int = 900,
    ) -> Dict[str, Any]:
        """
        Assumes an IAM role and returns temporary security credentials.

        :param role_arn: The ARN of the role to assume.
        :param role_session_name: An identifier for the assumed role session.
        :param duration_seconds: The duration, in seconds, of the role session
                                 (default 900, minimum 900, maximum 43200).
        :return: A dictionary containing Credentials and AssumedRoleUser.
        :raises ClientError: If the call fails (e.g., MalformedPolicyDocumentException).
        """
        try:
            response = self.sts_client.assume_role(
                RoleArn=role_arn,
                RoleSessionName=role_session_name,
                DurationSeconds=duration_seconds,
            )
            logger.info(
                "Assumed role %s with session name %s.",
                role_arn,
                role_session_name,
            )
            return {
                "Credentials": response["Credentials"],
                "AssumedRoleUser": response["AssumedRoleUser"],
            }
        except ClientError as error:
            if error.response["Error"]["Code"] == "MalformedPolicyDocumentException":
                logger.error(
                    "The session policy document is invalid. "
                    "Check the policy syntax for role: %s",
                    role_arn,
                )
            else:
                logger.error(
                    "Couldn't assume role %s. Error: %s: %s",
                    role_arn,
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.AssumeRole]

    # snippet-start:[python.example_code.sts.GetSessionToken]
    def get_session_token(self, duration_seconds: int = 900) -> Dict[str, Any]:
        """
        Returns temporary credentials for an IAM user session.

        :param duration_seconds: The duration, in seconds, for the session
                                 (default 900, minimum 900, maximum 129600).
        :return: A dictionary containing the Credentials object.
        :raises ClientError: If the call fails (e.g., RegionDisabledException).
        """
        try:
            response = self.sts_client.get_session_token(
                DurationSeconds=duration_seconds,
            )
            credentials = response["Credentials"]
            logger.info(
                "Got session token. Access Key ID: %s",
                credentials["AccessKeyId"],
            )
            return {"Credentials": credentials}
        except ClientError as error:
            if error.response["Error"]["Code"] == "RegionDisabledException":
                logger.error(
                    "STS is not activated in the requested region. "
                    "Go to the IAM console to activate it."
                )
            else:
                logger.error(
                    "Couldn't get session token. Error: %s: %s",
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.GetSessionToken]

    # snippet-start:[python.example_code.sts.GetFederationToken]
    def get_federation_token(
        self,
        name: str,
        duration_seconds: int = 900,
        policy: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Returns temporary credentials for a federated user.

        :param name: The name of the federated user (2-32 characters).
        :param duration_seconds: The duration, in seconds, for the session
                                 (default 900, minimum 900, maximum 129600).
        :param policy: An optional JSON policy string to scope down permissions.
        :return: A dictionary containing Credentials and FederatedUser.
        :raises ClientError: If the call fails (e.g., PackedPolicyTooLargeException).
        """
        try:
            params = dict()
            params["Name"] = name
            params["DurationSeconds"] = duration_seconds
            if policy is not None:
                params["Policy"] = policy

            response = self.sts_client.get_federation_token(**params)
            logger.info("Got federation token for user: %s", name)
            return {
                "Credentials": response["Credentials"],
                "FederatedUser": response["FederatedUser"],
            }
        except ClientError as error:
            if error.response["Error"]["Code"] == "PackedPolicyTooLargeException":
                logger.error(
                    "The combined session policies exceed the allowed size. "
                    "Reduce the policy size and try again."
                )
            else:
                logger.error(
                    "Couldn't get federation token for %s. Error: %s: %s",
                    name,
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.GetFederationToken]

    # snippet-start:[python.example_code.sts.GetAccessKeyInfo]
    def get_access_key_info(self, access_key_id: str) -> str:
        """
        Returns the account identifier for the specified access key ID.

        :param access_key_id: The access key ID to look up (16-128 characters).
        :return: The account ID that owns the access key.
        :raises ClientError: If the call fails (e.g., ExpiredTokenException).
        """
        try:
            response = self.sts_client.get_access_key_info(
                AccessKeyId=access_key_id,
            )
            account = response["Account"]
            logger.info(
                "Access key %s belongs to account %s.", access_key_id, account
            )
            return account
        except ClientError as error:
            if error.response["Error"]["Code"] == "ExpiredTokenException":
                logger.error(
                    "Credentials used to call GetAccessKeyInfo have expired. "
                    "Refresh your credentials."
                )
            else:
                logger.error(
                    "Couldn't get access key info for %s. Error: %s: %s",
                    access_key_id,
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.GetAccessKeyInfo]

    # snippet-start:[python.example_code.sts.DecodeAuthorizationMessage]
    def decode_authorization_message(self, encoded_message: str) -> str:
        """
        Decodes an encoded authorization failure message.

        :param encoded_message: The encoded authorization message (1-10240 characters).
        :return: The decoded message as a JSON string.
        :raises ClientError: If the call fails (e.g., InvalidAuthorizationMessageException).
        """
        try:
            response = self.sts_client.decode_authorization_message(
                EncodedMessage=encoded_message,
            )
            decoded = response["DecodedMessage"]
            logger.info("Successfully decoded authorization message.")
            return decoded
        except ClientError as error:
            if (
                error.response["Error"]["Code"]
                == "InvalidAuthorizationMessageException"
            ):
                logger.error(
                    "The encoded message is invalid (malformed or expired). "
                    "Raw message: %s...",
                    encoded_message[:100],
                )
            else:
                logger.error(
                    "Couldn't decode authorization message. Error: %s: %s",
                    error.response["Error"]["Code"],
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.sts.DecodeAuthorizationMessage]
