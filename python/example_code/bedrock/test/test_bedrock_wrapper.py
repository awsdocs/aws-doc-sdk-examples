# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from bedrock_wrapper import BedrockWrapper


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_foundation_models(make_stubber, error_code):
    bedrock_client = boto3.client(service_name="bedrock", region_name="us-east-1")
    bedrock_stubber = make_stubber(bedrock_client)
    wrapper = BedrockWrapper(bedrock_client)
    models = [
        {
            "modelArn": "arn:aws:test:::test-resource",
            "modelId": "testId",
            "modelName": "testModelName",
            "providerName": "testProviderName",
            "inputModalities": ["TEXT"],
            "outputModalities": ["TEXT"],
            "responseStreamingSupported": False,
            "customizationsSupported": ["FINE_TUNING"],
            "inferenceTypesSupported": ["ON_DEMAND"],
        }
    ]

    bedrock_stubber.stub_list_foundation_models(models, error_code=error_code)

    if error_code is None:
        got_models = wrapper.list_foundation_models()
        assert len(got_models) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_foundation_models()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_get_foundation_model(make_stubber, error_code):
    bedrock_client = boto3.client(service_name="bedrock", region_name="us-east-1")
    bedrock_stubber = make_stubber(bedrock_client)
    wrapper = BedrockWrapper(bedrock_client)

    model_identifier = "fake.model.id"

    bedrock_stubber.stub_get_foundation_model(model_identifier, error_code=error_code)

    if error_code is None:
        got_model = wrapper.get_foundation_model(model_identifier)
        assert got_model is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_foundation_model(model_identifier)
        assert exc_info.value.response["Error"]["Code"] == error_code
