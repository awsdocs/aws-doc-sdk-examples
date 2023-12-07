# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_agent_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper


@pytest.fixture(scope="module")
def client():
    return boto3.client(service_name="bedrock-agent", region_name="us-east-1")

@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agents(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)
    agents = [
        {
            "agentId": "ABCDEFGHIJ",
            "agentName": "Test_Agent_Name",
            "agentStatus": "PREPARED",
            "updatedAt": "1970-01-01 00:00:00.000000+00:00",
            "description": "A test description",
            "latestAgentVersion": "0",
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
def test_get_agent(client, make_stubber, error_code):
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
        "updatedAt": "1970-01-01 00:00:00.000000+00:00",
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

@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    name = "fake_agent_name"
    foundation_model = "fake.model-id"
    role_arn = "fake:arn"
    instruction = "fake instruction with a minimum of 40 characters"

    stubber.stub_create_agent(name, foundation_model, role_arn, instruction, error_code=error_code)

    if error_code is None:
        response = wrapper.create_agent(name, foundation_model, role_arn, instruction)
        created_agent = response["agent"]
        assert created_agent["agentName"] == name
        assert created_agent["foundationModel"] == foundation_model
        assert created_agent["instruction"] == instruction
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent(name, foundation_model, role_arn, instruction)
            assert exc_info.value.response["Error"]["Code"] == error_code
