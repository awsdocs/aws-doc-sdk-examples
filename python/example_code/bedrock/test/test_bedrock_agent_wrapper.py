# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_agent_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

import test_data as fake
from bedrock_agent_wrapper import BedrockAgentWrapper


@pytest.fixture(scope="module")
def client():
    return boto3.client(service_name="bedrock-agent", region_name="us-east-1")


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    role_arn = fake.ARN
    name = fake.AGENT_NAME
    foundation_model = fake.FOUNDATION_MODEL_ID
    instruction = fake.INSTRUCTION

    stubber.stub_create_agent(
        name, foundation_model, role_arn, instruction, error_code=error_code
    )

    if error_code is None:
        created_agent = wrapper.create_agent(
            name, foundation_model, role_arn, instruction
        )
        assert created_agent["agentName"] == name
        assert created_agent["foundationModel"] == foundation_model
        assert created_agent["instruction"] == instruction
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent(name, foundation_model, role_arn, instruction)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent_action_group(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    name = fake.ACTION_GROUP_NAME
    description = fake.DESCRIPTION
    agent_id = fake.AGENT_ID
    agent_version = fake.VERSION
    function_arn = fake.ARN
    api_schema = fake.API_SCHEMA

    stubber.stub_create_agent_action_group(
        name, description, agent_id, agent_version, function_arn, api_schema, error_code=error_code
    )

    if error_code is None:
        created_action_group = wrapper.create_agent_action_group(
            name, description, agent_id, agent_version, function_arn, api_schema
        )
        assert created_action_group["agentId"] == agent_id
        assert created_action_group["agentVersion"] == agent_version
        assert created_action_group["actionGroupName"] == name
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent_action_group(
                name, description, agent_id, agent_version, function_arn, api_schema
            )
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_delete_agent(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    agent_id = fake.AGENT_ID
    stubber.stub_delete_agent(agent_id, error_code=error_code)

    if error_code is None:
        response = wrapper.delete_agent(agent_id)
        assert response["agentId"] == agent_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_get_agent(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    agent_id = fake.AGENT_ID

    agent = {
        "agentStatus": "PREPARED",
        "idleSessionTTLInSeconds": 60,
        "agentId": agent_id,
        "agentName": fake.AGENT_ID,
        "agentArn": fake.ARN,
        "agentVersion": fake.VERSION,
        "agentResourceRoleArn": fake.ARN,
        "createdAt": fake.TIMESTAMP,
        "updatedAt": fake.TIMESTAMP,
    }

    stubber.stub_get_agent(agent_id, agent, error_code=error_code)

    if error_code is None:
        got_agent = wrapper.get_agent(agent_id)
        assert got_agent["agentId"] == agent_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agents(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)
    agents = [
        {
            "agentStatus": "PREPARED",
            "agentId": fake.AGENT_ID,
            "agentName": fake.AGENT_NAME,
            "updatedAt": fake.TIMESTAMP,
            "description": fake.DESCRIPTION,
            "latestAgentVersion": fake.VERSION,
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
def test_prepare_agent(client, make_stubber, error_code):
    stubber = make_stubber(client)
    wrapper = BedrockAgentWrapper(client)

    agent_id = fake.AGENT_ID
    stubber.stub_prepare_agent(agent_id, error_code=error_code)

    if error_code is None:
        response = wrapper.prepare_agent(agent_id)
        assert response["agentId"] == agent_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.prepare_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code
