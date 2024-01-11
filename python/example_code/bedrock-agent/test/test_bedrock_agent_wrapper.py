# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bedrock_agent_wrapper.py.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from conftest import FakeData as Fake
from bedrock_agent_wrapper import BedrockAgentWrapper


@pytest.fixture(scope="module")
def client():
    return boto3.client(service_name="bedrock-agent", region_name="us-east-1")


@pytest.fixture(scope="function")
def stubber(client, make_stubber):
    return make_stubber(client)


@pytest.fixture(scope="function")
def wrapper(client):
    return BedrockAgentWrapper(client)


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent(stubber, wrapper, error_code):
    name = Fake.AGENT_NAME
    foundation_model = Fake.FOUNDATION_MODEL_ID
    role_arn = Fake.ARN
    instruction = Fake.INSTRUCTION

    expected_params = {
        "agentName": name,
        "foundationModel": foundation_model,
        "agentResourceRoleArn": role_arn,
        "instruction": instruction,
    }

    response = {
        "agent": {
            "agentStatus": "NOT_PREPARED",
            "idleSessionTTLInSeconds": 60,
            "agentId": Fake.AGENT_ID,
            "agentName": name,
            "agentArn": Fake.ARN,
            "foundationModel": foundation_model,
            "instruction": Fake.INSTRUCTION,
            "agentVersion": Fake.VERSION,
            "agentResourceRoleArn": role_arn,
            "createdAt": Fake.TIMESTAMP,
            "updatedAt": Fake.TIMESTAMP,
        }
    }

    stubber.stub_create_agent(expected_params, response, error_code=error_code)

    if error_code is None:
        created_agent = wrapper.create_agent(
            name, foundation_model, role_arn, instruction
        )
        assert created_agent is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent(name, foundation_model, role_arn, instruction)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent_action_group(stubber, wrapper, error_code):
    name = Fake.ACTION_GROUP_NAME
    description = Fake.DESCRIPTION
    agent_id = Fake.AGENT_ID
    agent_version = Fake.VERSION
    function_arn = Fake.ARN
    api_schema = Fake.API_SCHEMA

    expected_params = {
        "actionGroupName": name,
        "description": description,
        "agentId": agent_id,
        "agentVersion": agent_version,
        "actionGroupExecutor": {"lambda": function_arn},
        "apiSchema": {"payload": api_schema},
    }
    response = {
        "agentActionGroup": {
            "agentId": agent_id,
            "agentVersion": agent_version,
            "actionGroupState": "ENABLED",
            "actionGroupName": name,
            "actionGroupId": Fake.ACTION_GROUP_ID,
            "createdAt": Fake.TIMESTAMP,
            "updatedAt": Fake.TIMESTAMP,
        }
    }

    stubber.stub_create_agent_action_group(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        created_action_group = wrapper.create_agent_action_group(
            name, description, agent_id, agent_version, function_arn, api_schema
        )
        assert created_action_group is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent_action_group(
                name, description, agent_id, agent_version, function_arn, api_schema
            )
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_create_agent_alias(stubber, wrapper, error_code):
    name = Fake.ALIAS_NAME
    agent_id = Fake.AGENT_ID

    expected_params = {"agentId": agent_id, "agentAliasName": name}
    created_alias = {
        "agentAlias": {
            "agentId": "",
            "agentAliasId": Fake.ALIAS_ID,
            "agentAliasName": Fake.ALIAS_NAME,
            "agentAliasArn": Fake.ARN,
            "routingConfiguration": [],
            "createdAt": Fake.TIMESTAMP,
            "updatedAt": Fake.TIMESTAMP,
            "agentAliasStatus": "",
        }
    }

    stubber.stub_create_agent_alias(
        expected_params, created_alias, error_code=error_code
    )

    if error_code is None:
        created_alias = wrapper.create_agent_alias(name, agent_id)
        assert created_alias is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_agent_alias(name, agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_delete_agent(stubber, wrapper, error_code):
    agent_id = Fake.AGENT_ID

    expected_params = {"agentId": agent_id, "skipResourceInUseCheck": False}
    response = {"agentStatus": "DELETING", "agentId": agent_id}

    stubber.stub_delete_agent(expected_params, response, error_code=error_code)

    if error_code is None:
        response = wrapper.delete_agent(agent_id)
        assert response is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_delete_agent_alias(stubber, wrapper, error_code):
    agent_id = Fake.AGENT_ID
    agent_alias_id = Fake.ALIAS_ID

    expected_params = {"agentId": agent_id, "agentAliasId": agent_alias_id}
    response = {
        "agentId": agent_id,
        "agentAliasId": agent_alias_id,
        "agentAliasStatus": "DELETING",
    }

    stubber.stub_delete_agent_alias(expected_params, response, error_code=error_code)

    if error_code is None:
        response = wrapper.delete_agent_alias(agent_id, agent_alias_id)
        assert response is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_agent_alias(agent_id, agent_alias_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_get_agent(stubber, wrapper, error_code):
    agent_id = Fake.AGENT_ID

    expected_params = {"agentId": agent_id}
    response = {
        "agent": {
            "agentStatus": "PREPARED",
            "idleSessionTTLInSeconds": 60,
            "agentId": agent_id,
            "agentName": Fake.AGENT_ID,
            "agentArn": Fake.ARN,
            "agentVersion": Fake.VERSION,
            "agentResourceRoleArn": Fake.ARN,
            "createdAt": Fake.TIMESTAMP,
            "updatedAt": Fake.TIMESTAMP,
        }
    }

    stubber.stub_get_agent(expected_params, response, error_code=error_code)

    if error_code is None:
        agent = wrapper.get_agent(agent_id)
        assert agent is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agents(stubber, wrapper, error_code):
    expected_params = {"maxResults": 10}
    response = {
        "agentSummaries": [
            {
                "agentStatus": "PREPARED",
                "agentId": Fake.AGENT_ID,
                "agentName": Fake.AGENT_NAME,
                "updatedAt": Fake.TIMESTAMP,
                "description": Fake.DESCRIPTION,
                "latestAgentVersion": Fake.VERSION,
            }
        ]
    }

    stubber.stub_list_agents(expected_params, response, error_code=error_code)

    if error_code is None:
        got_agents = wrapper.list_agents()
        assert len(got_agents) > 0
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_agents()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agent_action_groups(stubber, wrapper, error_code):
    expected_params = {
        "agentId": Fake.AGENT_ID,
        "agentVersion": Fake.VERSION,
        "maxResults": 10,
    }
    response = {"actionGroupSummaries": []}

    stubber.stub_list_agent_action_groups(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        got_action_groups = wrapper.list_agent_action_groups(
            Fake.AGENT_ID, Fake.VERSION
        )
        assert got_action_groups is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_agent_action_groups(Fake.AGENT_ID, Fake.VERSION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_list_agent_knowledge_bases(stubber, wrapper, error_code):
    expected_params = {
        "agentId": Fake.AGENT_ID,
        "agentVersion": Fake.VERSION,
        "maxResults": 10,
    }
    response = {"agentKnowledgeBaseSummaries": []}

    stubber.stub_list_agent_knowledge_bases(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        got_action_groups = wrapper.list_agent_knowledge_bases(
            Fake.AGENT_ID, Fake.VERSION
        )
        assert got_action_groups is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_agent_knowledge_bases(Fake.AGENT_ID, Fake.VERSION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ClientError"])
def test_prepare_agent(stubber, wrapper, error_code):
    agent_id = Fake.AGENT_ID

    expected_params = {"agentId": agent_id}

    response = {
        "agentStatus": "PREPARED",
        "agentId": agent_id,
        "agentVersion": Fake.VERSION,
        "preparedAt": Fake.TIMESTAMP,
    }

    stubber.stub_prepare_agent(expected_params, response, error_code=error_code)

    if error_code is None:
        agent = wrapper.prepare_agent(agent_id)
        assert agent is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.prepare_agent(agent_id)
            assert exc_info.value.response["Error"]["Code"] == error_code
