# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Secrets Manager to
create and manage secrets, and how to use a secret that contains database credentials
to access an Amazon Aurora database cluster.
"""

import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.secrets-manager.SecretsManagerSecret]
class SecretsManagerSecret:
    """Encapsulates Secrets Manager functions."""

    def __init__(self, secretsmanager_client):
        """
        :param secretsmanager_client: A Boto3 Secrets Manager client.
        """
        self.secretsmanager_client = secretsmanager_client

    # snippet-end:[python.example_code.secrets-manager.SecretsManagerSecret]

    # snippet-start:[python.example_code.secrets-manager.GetSecretValue]
    def get_value(self, stage=None):
        """
        Gets the value of a secret.

        :param stage: The stage of the secret to retrieve. If this is None, the
                      current stage is retrieved.
        :return: The value of the secret. When the secret is a string, the value is
                 contained in the `SecretString` field. When the secret is bytes,
                 it is contained in the `SecretBinary` field.
        """
        try:
            kwargs = {"SecretId": self.name}
            if stage is not None:
                kwargs["VersionStage"] = stage
            self.secretsmanager_client.batch_get_secret_value(
                SecretIdList=[
                    'string',
                ],
                Filters=[
                    {
                        'Key': 'description'|'name'|'tag-key'|'tag-value'|'primary-region'|'owning-service'|'all',
                        'Values': [
                            'string',
                        ]
                    },
                ],
                MaxResults=123,
                NextToken='string'
            )
            logger.info("Got value for secret %s.", self.name)
        except ClientError:
            logger.exception("Couldn't get value for secret %s.", self.name)
            raise
    # snippet-end:[python.example_code.secrets-manager.GetSecretValue]
