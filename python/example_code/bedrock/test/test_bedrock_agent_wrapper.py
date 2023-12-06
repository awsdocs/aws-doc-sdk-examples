# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_agent_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agents(make_stubber, error_code):
    client = boto3.client(
        service_name="bedrock-agent",
        region_name="us-east-1"
    )

    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)
    agents = [
        {
            "agentId": "ABCDEFGHIJ",
            "agentName": "Test_Agent_Name",
            "agentStatus": "PREPARED",
            "updatedAt": "1970-01-01 00:00:00.000000+00:00",
            "description": "A test description",
            "latestAgentVersion": "0"
        }
    ]

    stubber.stub_list_agents(agents, error_code=error_code)

    if error_code is None:
        got_agents = wrapper.list_agents()
        assert len(got_agents) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_agents()
        assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_get_agent(make_stubber, error_code):
    client = boto3.client(
        service_name="bedrock-agent",
        region_name="us-east-1"
    )

    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    agent_id = "ABCDEFGHIJ"

    agent = {
        "agentId": agent_id,
        "agentName": "fakeName",
        "agentArn": "fakeArn",
        "agentVersion": "1.234.5",
        "agentStatus": "PREPARED",
        "idleSessionTTLInSeconds": 60,
        "agentResourceRoleArn": "FakeResourceRoleArn",
        "createdAt": "1970-01-01 00:00:00.000000+00:00",
        "updatedAt": "1970-01-01 00:00:00.000000+00:00"
    }

    stubber.stub_get_agent(agent_id, agent, error_code=error_code)

    if error_code is None:
        response = wrapper.get_agent(agent_id)
        got_agent = response["agent"]
        assert got_agent["agentId"] == agent_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code
