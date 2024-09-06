# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_agent_runtime_wrapper.py.
"""

import boto3
import pytest
from bedrock_agent_runtime_wrapper import BedrockAgentRuntimeWrapper
from botocore.exceptions import ClientError


@pytest.mark.asyncio
@pytest.mark.parametrize("error_code", [None, "ClientError"])
async def test_invoke_agent(make_stubber, error_code):
    runtime_client = boto3.client(
        service_name="bedrock-agent-runtime", region_name="us-east-1"
    )
    stubber = make_stubber(runtime_client)
    wrapper = BedrockAgentRuntimeWrapper(runtime_client)

    agent_id = "FAKE_AGENT_ID"
    agent_alias_id = "FAKE_AGENT_ALIAS_ID"
    session_id = "FAKE_SESSION_ID"
    prompt = "Hey, how are you?"

    expected_params = {
        "agentId": agent_id,
        "agentAliasId": agent_alias_id,
        "sessionId": session_id,
        "inputText": prompt,
    }
    response = {"completion": {}, "contentType": "", "sessionId": session_id}

    stubber.stub_invoke_agent(expected_params, response, error_code=error_code)

    if error_code is None:
        wrapper.invoke_agent(agent_id, agent_alias_id, session_id, prompt)
    else:
        with pytest.raises(ClientError):
            async for _ in wrapper.invoke_agent(
                agent_id, agent_alias_id, session_id, prompt
            ):
                assert exc_info.value.response["Error"]["Code"] == error_code
