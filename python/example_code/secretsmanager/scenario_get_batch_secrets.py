# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS
Secrets Manager to get a batch of secrets that match a
specified filter
"""

import boto3
import logging

from batch_get_secret_value import BatchGetSecretsWrapper

# Configure logging
logging.basicConfig(level=logging.INFO)


def run_scenario(secret_filter):
    """
    Retrieve secrets from AWS Secrets Manager using a filter.

    :param secret_filter: Filter criteria for selecting secrets.
    :type secret_filter: str
    """
    try:
        # Validate secret_filter
        if not secret_filter:
            raise ValueError("Secret filter must be provided.")
        # Retrieve the secrets using the filter
        client = boto3.client("secretsmanager")
        wrapper = BatchGetSecretsWrapper(client)
        secrets = wrapper.batch_get_secrets(secret_filter)
        if isinstance(secrets, list):
            logging.info("Secrets retrieved successfully.")
        # Note: Secrets should not be logged.
        return secrets
    except Exception as e:
        logging.error(f"Error retrieving secrets: {e}")
        raise


if __name__ == "__main__":
    run_scenario(secret_filter="mySecret")
