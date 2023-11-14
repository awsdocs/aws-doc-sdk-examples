# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_runtime_wrapper.py.
"""

import pytest
import random

import boto3
from botocore.exceptions import ClientError

from bedrock_runtime_wrapper import BedrockRuntimeWrapper


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_claude(make_stubber, error_code):
    bedrock_runtime = boto3.client(
        service_name="bedrock-runtime", region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime)

    prompt = "Hey, how are you?"

    bedrock_runtime_stubber.stub_invoke_claude(prompt, error_code=error_code)

    if error_code is None:
        got_completion = wrapper.invoke_claude(prompt)
        assert len(got_completion) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_claude(prompt)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_jurassic2(make_stubber, error_code):
    bedrock_runtime = boto3.client(
        service_name="bedrock-runtime", region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime)

    prompt = "Hey, how are you?"

    bedrock_runtime_stubber.stub_invoke_jurassic2(prompt, error_code=error_code)

    if error_code is None:
        got_completion = wrapper.invoke_jurassic2(prompt)
        assert len(got_completion) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_jurassic2(prompt)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_llama2(make_stubber, error_code):
    bedrock_runtime = boto3.client(
        service_name="bedrock-runtime", region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime)

    prompt = "Hey, how are you?"

    bedrock_runtime_stubber.stub_invoke_llama2(prompt, error_code=error_code)

    if error_code is None:
        got_completion = wrapper.invoke_llama2(prompt)
        assert len(got_completion) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_llama2(prompt)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.asyncio
@pytest.mark.parametrize("error_code", ["ClientError"])
async def test_invoke_model_with_response_stream(make_stubber, error_code):
    bedrock_runtime = boto3.client(
        service_name="bedrock-runtime", region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime)

    prompt = "Hey, how are you?"

    bedrock_runtime_stubber.stub_invoke_model_with_response_stream(
        prompt, error_code=error_code
    )

    if error_code:
        with pytest.raises(ClientError):
            async for _ in wrapper.invoke_model_with_response_stream(prompt):
                assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_invoke_stable_diffusion(make_stubber, error_code):
    bedrock_runtime = boto3.client(
        service_name="bedrock-runtime", region_name="us-east-1"
    )
    bedrock_runtime_stubber = make_stubber(bedrock_runtime)
    wrapper = BedrockRuntimeWrapper(bedrock_runtime)

    prompt = "A sunset over the ocean"
    style_preset = "cinematic"
    seed = random.randint(0, 4294967295)

    bedrock_runtime_stubber.stub_invoke_stable_diffusion(
        prompt, style_preset, seed, error_code=error_code
    )

    if error_code is None:
        got_completion = wrapper.invoke_stable_diffusion(prompt, seed, style_preset)
        assert len(got_completion) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_stable_diffusion(prompt, seed, style_preset)
        assert exc_info.value.response["Error"]["Code"] == error_code
