# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for run_prompt.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest
import json
import io
from unittest import mock

# Import from the local conftest.py file
from conftest import FakePromptRunData

# Import the module to test
from prompts import run_prompt


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_invoke_prompt(make_stubber, error_code):
    bedrock_runtime_client = boto3.client("bedrock-runtime")
    bedrock_runtime_stubber = make_stubber(bedrock_runtime_client)

    # Create expected parameters for invoke_model
    expected_params = {
        "modelId": "anthropic.claude-v2",
        "contentType": "application/json",
        "accept": "application/json",
        "body": mock.ANY  # We'll use mock.ANY since the exact body is complex
    }

    # Create a response with a readable object for the body
    output_json = json.dumps({"completion": FakePromptRunData.OUTPUT_TEXT})
    output_stream = io.BytesIO(output_json.encode())
    
    response = {
        "body": output_stream,
        "contentType": "application/json"
    }

    if error_code is None:
        bedrock_runtime_stubber.stub_invoke_model(expected_params, response)
    else:
        bedrock_runtime_stubber.stub_invoke_model(expected_params, response, error_code=error_code)

    if error_code is None:
        result = run_prompt.invoke_prompt(
            bedrock_runtime_client,
            FakePromptRunData.PROMPT_ID,
            FakePromptRunData.VERSION_OR_ALIAS,
            FakePromptRunData.INPUT_VARIABLES
        )
        assert result == {"output": FakePromptRunData.OUTPUT_TEXT}
    else:
        with pytest.raises(ClientError) as exc:
            run_prompt.invoke_prompt(
                bedrock_runtime_client,
                FakePromptRunData.PROMPT_ID,
                FakePromptRunData.VERSION_OR_ALIAS,
                FakePromptRunData.INPUT_VARIABLES
            )
        assert exc.value.response["Error"]["Code"] == error_code
