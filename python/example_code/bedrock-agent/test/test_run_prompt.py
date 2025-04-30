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
from conftest import FakePromptData

# Import the module to test
from prompts import run_prompt


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_invoke_prompt(make_stubber, error_code):
    bedrock_runtime_client = boto3.client("bedrock-runtime")
    bedrock_runtime_stubber = make_stubber(bedrock_runtime_client)



    # Create expected parameters for converse API
    expected_params = {
        "modelId": FakePromptData.PROMPT_ARN,  # Now using the version as ARN
        "promptVariables":{
                "genre": {
                    "text": "pop"
                },
                "number": {
                    "text": "1"
                }
            }
    }


    response = {    
    "output": {
        "message": {
            "role": "assistant",
            "content": [
                {
                    "text": "Here is a 5 song pop playlist I made for you:\n\n1. Blinding Lights - The Weeknd\n2. Watermelon Sugar - Harry Styles \n3. Don't Start Now - Dua Lipa\n4. Levitating - Dua Lipa\n5. Positions - Ariana Grande"
                }
            ]
        }
    },
    "stopReason": "end_turn",
    "usage": {
        "inputTokens": 25,
        "outputTokens": 72,
        "totalTokens": 97
    },
    "metrics": {
        "latencyMs": 1896
    }
}

    if error_code is None:
        bedrock_runtime_stubber.stub_converse(expected_params, response)
    else:
        bedrock_runtime_stubber.stub_converse(expected_params, response, error_code=error_code)

    if error_code is None:
        result = run_prompt.invoke_prompt(
            bedrock_runtime_client,
            FakePromptData.PROMPT_ARN,
            FakePromptData.INPUT_VARIABLES
        )
        assert result is not None
    else:
        with pytest.raises(ClientError) as exc:
            run_prompt.invoke_prompt(
                bedrock_runtime_client,
                FakePromptData.PROMPT_ARN,
                FakePromptData.INPUT_VARIABLES
            )
        assert exc.value.response["Error"]["Code"] == error_code
