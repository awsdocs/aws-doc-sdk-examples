# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_runtime_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from bedrock_runtime_wrapper import BedrockRuntimeWrapper


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke(make_stubber, error_code):
    bedrock_runtime_client = boto3.client(
        service_name="bedrock-runtime",
        region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime_client)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime_client)

    model_id = "anthropic.claude-v2"
    prompt = "Hey, how are you?"

    bedrock_runtime_stubber.stub_invoke_model(model_id, prompt, error_code=error_code)

    if error_code is None:
        got_completion = wrapper.invoke_model(model_id, prompt)
        assert len(got_completion) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_model(model_id, prompt)
        assert exc_info.value.response["Error"]["Code"] == error_code
