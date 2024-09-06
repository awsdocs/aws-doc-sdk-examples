# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for models/anthropic/claude_3.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError
from models.anthropic.claude_3 import Claude3Wrapper


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_with_text(make_stubber, error_code):
    client = boto3.client(
        service_name="bedrock-runtime",
        region_name="us-east-1")
    wrapper = Claude3Wrapper(client)

    prompt = "Test prompt"

    make_stubber(client).stub_invoke_claude_3_with_text(prompt, error_code)

    if error_code is None:
        result = wrapper.invoke_claude_3_with_text(prompt)
        assert len(result["content"]) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_claude_3_with_text(prompt)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_multimodal(make_stubber, error_code):
    client = boto3.client(
        service_name="bedrock-runtime",
        region_name="us-east-1")
    wrapper = Claude3Wrapper(client)

    prompt = "Test prompt"
    base64_image_data = "FakeBase64Data=="

    make_stubber(client).stub_invoke_claude_3_multimodal(
        prompt, base64_image_data, error_code
    )

    if error_code is None:
        result = wrapper.invoke_claude_3_multimodal(prompt, base64_image_data)
        assert len(result["content"]) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_claude_3_multimodal(prompt, base64_image_data)
        assert exc_info.value.response["Error"]["Code"] == error_code
