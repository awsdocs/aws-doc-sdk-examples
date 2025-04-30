# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for prompt.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from conftest import FakePromptData
from prompts import prompt


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_prompt(make_stubber, error_code):
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_stubber = make_stubber(bedrock_client)

    # The expected parameters for the API call
    expected_params = {
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "variants": [{
            "name": "default",
            "templateType": "TEXT",
            "templateConfiguration": {
                "text": {
                    "text": FakePromptData.PROMPT_TEMPLATE,
                    "inputVariables": [{"name": "variable"}]
                }
            },
            "modelId": FakePromptData.MODEL_ID
        }]
    }

    # The response from the API
    response = {
        "id": FakePromptData.PROMPT_ID,
        "arn": FakePromptData.PROMPT_ARN,
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "createdAt": FakePromptData.CREATED_AT,
        "updatedAt": FakePromptData.UPDATED_AT,
        "version": "1"
    }

    if error_code is None:
        bedrock_stubber.stub_create_prompt(expected_params, response)
    else:
        bedrock_stubber.stub_create_prompt(
            expected_params, response, error_code=error_code)

    if error_code is None:
        result = prompt.create_prompt(
            bedrock_client,
            FakePromptData.PROMPT_NAME,
            FakePromptData.PROMPT_DESCRIPTION,
            FakePromptData.PROMPT_TEMPLATE,
            FakePromptData.MODEL_ID
        )
        assert result == response
    else:
        with pytest.raises(ClientError) as exc:
            prompt.create_prompt(
                bedrock_client,
                FakePromptData.PROMPT_NAME,
                FakePromptData.PROMPT_DESCRIPTION,
                FakePromptData.PROMPT_TEMPLATE,
                FakePromptData.MODEL_ID
            )
        assert exc.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_prompt(make_stubber, error_code):
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_stubber = make_stubber(bedrock_client)

    # The expected parameters for the API call
    expected_params = {
        "promptIdentifier": FakePromptData.PROMPT_ID
    }

    # The response from the API
    response = {
        "id": FakePromptData.PROMPT_ID,
        "arn": FakePromptData.PROMPT_ARN,
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "createdAt": FakePromptData.CREATED_AT,
        "updatedAt": FakePromptData.UPDATED_AT,
        "version": "1",
        "variants": [{
            "name": "default",
            "templateType": "TEXT",
            "templateConfiguration": {
                "text": {
                    "text": FakePromptData.PROMPT_TEMPLATE,
                    "inputVariables": [{"name": "variable"}]
                }
            },
            "modelId": FakePromptData.MODEL_ID
        }]
    }

    if error_code is None:
        bedrock_stubber.stub_get_prompt(expected_params, response)
    else:
        bedrock_stubber.stub_get_prompt(
            expected_params, response, error_code=error_code)

    if error_code is None:
        result = prompt.get_prompt(
            bedrock_client,
            FakePromptData.PROMPT_ID
        )
        assert result == response
    else:
        with pytest.raises(ClientError) as exc:
            prompt.get_prompt(
                bedrock_client,
                FakePromptData.PROMPT_ID
            )
        assert exc.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_prompt(make_stubber, error_code):
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_stubber = make_stubber(bedrock_client)

    # The expected parameters for the API call
    expected_params = {
        "promptIdentifier": FakePromptData.PROMPT_ID
    }

    # The response from the API
    response = {
        "id": FakePromptData.PROMPT_ID
    }

    if error_code is None:
        bedrock_stubber.stub_delete_prompt(expected_params, response)
    else:
        bedrock_stubber.stub_delete_prompt(
            expected_params, response, error_code=error_code)

    if error_code is None:
        result = prompt.delete_prompt(
            bedrock_client,
            FakePromptData.PROMPT_ID
        )
        assert result == response
    else:
        with pytest.raises(ClientError) as exc:
            prompt.delete_prompt(
                bedrock_client,
                FakePromptData.PROMPT_ID
            )
        assert exc.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_prompt_version(make_stubber, error_code):
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_stubber = make_stubber(bedrock_client)

    # The expected parameters for the API call
    expected_params = {
        "promptIdentifier": FakePromptData.PROMPT_ID
    }

    # The response from the API
    response = {
        "id": FakePromptData.PROMPT_ID,
        "arn": FakePromptData.PROMPT_ARN,
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "createdAt": FakePromptData.CREATED_AT,
        "updatedAt": FakePromptData.UPDATED_AT,
        "version": "2"  # Incremented version number
    }

    if error_code is None:
        bedrock_stubber.stub_create_prompt_version(expected_params, response)
    else:
        bedrock_stubber.stub_create_prompt_version(
            expected_params, response, error_code=error_code)

    if error_code is None:
        result = prompt.create_prompt_version(
            bedrock_client,
            FakePromptData.PROMPT_ID
        )
        assert result == response
    else:
        with pytest.raises(ClientError) as exc:
            prompt.create_prompt_version(
                bedrock_client,
                FakePromptData.PROMPT_ID
            )
        assert exc.value.response["Error"]["Code"] == error_code
