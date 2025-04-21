# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flows.py.
"""


import boto3
from botocore.exceptions import ClientError
import pytest

from conftest import FakeFlowData as Fake

from flows import flow


FLOW_NAME = "Fake_flow"
FLOW_DESCRIPTION = "Playlist creator flow"
FLOW_ID = "XXXXXXXXXX"
FLOW_VERSION = "DRAFT"
ROLE_ARN = f"arn:aws:iam::123456789012:role/BedrockFlowRole-{FLOW_NAME}"
FLOW_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}"
FLOW_DEFINITION = {}
CREATED_AT = "2025-03-29T21:34:43.048609+00:00"
UPDATED_AT = "2025-03-30T21:34:43.048609+00:00"


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_flow(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "name": Fake.FLOW_NAME,
        "description": Fake.FLOW_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "definition": Fake.FLOW_DEFINITION
    }

    response = {
        "arn": Fake.FLOW_ARN,
        "createdAt": Fake.CREATED_AT,
        "definition": Fake.FLOW_DEFINITION,
        "description": Fake.FLOW_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "id": Fake.FLOW_ID,
        "name": Fake.FLOW_NAME,
        "status": "NotPrepared",
        "updatedAt": Fake.UPDATED_AT,
        "version": "DRAFT"
    }

    bedrock_agent_stubber.stub_create_flow(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow.create_flow(
            bedrock_agent_client, Fake.FLOW_NAME, Fake.FLOW_DESCRIPTION, Fake.ROLE_ARN, Fake.FLOW_DEFINITION
        )
        assert call_response["status"] == "NotPrepared"

    else:
        with pytest.raises(ClientError) as exc_info:
            flow.create_flow(bedrock_agent_client, Fake.FLOW_NAME,
                             Fake.FLOW_DESCRIPTION, Fake.ROLE_ARN, Fake.FLOW_DEFINITION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_flow(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "name": Fake.FLOW_NAME,
        "description": Fake.FLOW_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "definition": Fake.FLOW_DEFINITION
    }

    response = {
        "arn": Fake.FLOW_ARN,
        "createdAt": Fake.CREATED_AT,
        "definition": Fake.FLOW_DEFINITION,
        "description": Fake.FLOW_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "id": Fake.FLOW_ID,
        "name": Fake.FLOW_NAME,
        "status": "NotPrepared",
        "updatedAt": Fake.UPDATED_AT,
        "version": "DRAFT"
    }

    bedrock_agent_stubber.stub_update_flow(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow.update_flow(
            bedrock_agent_client, Fake.FLOW_ID, Fake.FLOW_NAME, Fake.FLOW_DESCRIPTION, Fake.ROLE_ARN, Fake.FLOW_DEFINITION
        )
        assert call_response["id"] == Fake.FLOW_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow.update_flow(bedrock_agent_client, Fake.FLOW_ID,  Fake.FLOW_NAME,
                             Fake.FLOW_DESCRIPTION, Fake.ROLE_ARN, Fake.FLOW_DEFINITION)
        assert exc_info.value.response["Error"]["Code"] == error_code







@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_prepare_flow(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
    }

    if error_code is None:

        # First stub - Flow starts preparing
        bedrock_agent_stubber.stub_prepare_flow(
            expected_params,
            {
                "id": Fake.FLOW_ID,
                "status": "Preparing"
            }
        )

        # Second stub - Get flow status for prepared flow.
        bedrock_agent_stubber.stub_get_flow(
            expected_params,
            {
                "arn": Fake.FLOW_ARN,
                "createdAt": Fake.CREATED_AT,
                "definition": Fake.FLOW_DEFINITION,
                "description": Fake.FLOW_DESCRIPTION,
                "executionRoleArn": Fake.ROLE_ARN,
                "id": Fake.FLOW_ID,
                "name": Fake.FLOW_NAME,
                "status": "Prepared",
                "updatedAt": Fake.UPDATED_AT,
                "version": "DRAFT"
            })

        call_response = flow.prepare_flow(bedrock_agent_client, FLOW_ID)
        assert call_response == "Prepared"

    else:
        bedrock_agent_stubber.stub_prepare_flow(
            expected_params,
            {"id": Fake.FLOW_ID},
            error_code=error_code
        )
        with pytest.raises(ClientError) as exc_info:
            flow.prepare_flow(bedrock_agent_client, FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_flow(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID
    }

    response = {
        "arn": Fake.FLOW_ARN,
        "createdAt": Fake.CREATED_AT,
        "definition": Fake.FLOW_DEFINITION,
        "description": Fake.FLOW_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "id": Fake.FLOW_ID,
        "name": Fake.FLOW_NAME,
        "status": "NotPrepared",
        "updatedAt": Fake.UPDATED_AT,
        "version": "DRAFT"
    }

    bedrock_agent_stubber.stub_get_flow(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow.get_flow(
            bedrock_agent_client, Fake.FLOW_ID)

        assert call_response["status"] == "NotPrepared"

    else:
        with pytest.raises(ClientError) as exc_info:
            flow.get_flow(bedrock_agent_client, Fake.FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_flow(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "skipResourceInUseCheck": True
    }

    response = {
        "id": Fake.FLOW_ID
    }

    bedrock_agent_stubber.stub_delete_flow(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow.delete_flow(
            bedrock_agent_client, Fake.FLOW_ID)

        assert call_response["id"] == Fake.FLOW_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow.delete_flow(bedrock_agent_client, Fake.FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_flows(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {"maxResults":10}

    response = {
        "flowSummaries": [
            {
                "arn": Fake.FLOW_ARN,
                "createdAt": Fake.CREATED_AT,
                "description": Fake.FLOW_DESCRIPTION,
                "name": Fake.FLOW_NAME,
                "id": Fake.FLOW_ID,
                "status": "Prepared",
                "updatedAt": Fake.UPDATED_AT,
                "version": "DRAFT"
            }
        ]
    }

    bedrock_agent_stubber.stub_list_flows(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow.list_flows(
            bedrock_agent_client)

        assert call_response is None

    else:
        with pytest.raises(ClientError) as exc_info:
            flow.list_flows(bedrock_agent_client)
        assert exc_info.value.response["Error"]["Code"] == error_code
