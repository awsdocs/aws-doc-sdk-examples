# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to manage
Bedrock models.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock.BedrockWrapper.class]
# snippet-start:[python.example_code.bedrock.BedrockWrapper.decl]
class BedrockWrapper:
    """Encapsulates Amazon Bedrock foundation model actions."""

    def __init__(self, bedrock_client):
        """
        :param bedrock_client: A Boto3 Amazon Bedrock client, which is a low-level client that
                               represents Amazon Bedrock and describes the API operations for
                               creating and managing Bedrock models.
        """
        self.bedrock_client = bedrock_client

    # snippet-end:[python.example_code.bedrock.BedrockWrapper.decl]

    # snippet-start:[python.example_code.bedrock.ListFoundationModels]
    def list_foundation_models(self):
        """
        List the available Amazon Bedrock foundation models.

        :return: The list of available bedrock foundation models.
        """

        try:
            response = self.bedrock_client.list_foundation_models()
            models = response["modelSummaries"]
            logger.info("Got %s foundation models.", len(models))
        except ClientError:
            logger.error("Couldn't list foundation models.")
            raise
        else:
            return models

    # snippet-end:[python.example_code.bedrock.ListFoundationModels]


# snippet-end:[python.example_code.bedrock.BedrockWrapper.class]


def usage_demo():
    """
    Shows how to list the available foundation models.
    This demonstration gets the list of available foundation models and
    prints their respective summaries.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock demo.")
    print("-" * 88)

    bedrock_client = boto3.client(service_name="bedrock", region_name="us-east-1")

    wrapper = BedrockWrapper(bedrock_client)

    print("Listing the available Bedrock foundation models.")

    try:
        for model in wrapper.list_foundation_models():
            print("\n" + "=" * 42)
            print(f' Model: {model["modelId"]}')
            print("-" * 42)
            print(f' Name: {model["modelName"]}')
            print(f' Provider: {model["providerName"]}')
            print(f' Input modalities: {model["inputModalities"]}')
            print(f' Output modalities: {model["outputModalities"]}')
            print(f' Supported customizations: {model["customizationsSupported"]}')
            print(f' Supported inference types: {model["inferenceTypesSupported"]}')
            print("=" * 42)
    except ClientError:
        logger.exception("Couldn't list Bedrock foundation models.")
        raise


if __name__ == "__main__":
    usage_demo()
