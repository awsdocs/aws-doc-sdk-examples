# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flow_version.py.
"""


import boto3
from botocore.exceptions import ClientError
import pytest

from flows import flow_version


VERSION_NAME = "Fake_flow_alias"
VERSION_DESCRIPTION = "Playlist creator flow version"
FLOW_NAME = "Fake_flow"
FLOW_ID = "XXXXXXXXXX"
VERSION_ID = "XXXXXXXXXX"
FLOW_VERSION = "1"
VERSION_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}/version/{VERSION_ID}"
ROLE_ARN = f"arn:aws:iam::123456789012:role/BedrockFlowRole-{FLOW_NAME}"
CREATED_AT = "2025-03-29T21:34:43.048609+00:00"


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_flow_version(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    create_flow_version_expected_params = {
        "flowIdentifier": FLOW_ID,
        "description": VERSION_DESCRIPTION
    }

    get_flow_version_expected_params = {
        "flowIdentifier": FLOW_ID,
        "version": FLOW_VERSION
    }

    response = {
        "arn": VERSION_ARN,
        "createdAt":  CREATED_AT,
        "definition": {},
        "description": VERSION_DESCRIPTION,
        "executionRoleArn": ROLE_ARN,
        "id": VERSION_ID,
        "name": VERSION_NAME,
        "status": "preparing",
        "version": FLOW_VERSION
    }

    if error_code is None:

        # First stub - Start creating the flow version.
        bedrock_agent_stubber.stub_create_flow_version(
            create_flow_version_expected_params,
            response)

        # Second stub - Get flow status for created flow version.
        response['status'] = "Prepared"
        bedrock_agent_stubber.stub_get_flow_version(
            get_flow_version_expected_params,
            response
        )

        version = flow_version.create_flow_version(
            bedrock_agent_client, FLOW_ID, VERSION_DESCRIPTION)
        assert version == FLOW_VERSION
    else:
        bedrock_agent_stubber.stub_create_flow_version(
            create_flow_version_expected_params,
            response,
            error_code=error_code
        )
        with pytest.raises(ClientError) as exc_info:
            flow_version.create_flow_version(
                bedrock_agent_client, FLOW_ID, VERSION_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_flow_version(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": FLOW_ID,
        "flowVersion": FLOW_VERSION
    }

    response = {
        "id": FLOW_ID,
        "version": FLOW_VERSION
    }

    bedrock_agent_stubber.stub_delete_flow_version(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_version.delete_flow_version(
            bedrock_agent_client, FLOW_ID, FLOW_VERSION)

        assert call_response["id"] == FLOW_ID
        assert call_response["version"] == FLOW_VERSION

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_version.delete_flow_version(
                bedrock_agent_client, FLOW_ID, FLOW_VERSION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_flow_versions(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": FLOW_ID,
        "maxResults": 10
    }

    response = {
        "flowVersionSummaries": [
            {
                "arn": VERSION_ARN,
                "createdAt": CREATED_AT,
                "id": VERSION_ID,
                "status": "Prepared",
                "version": FLOW_VERSION
            }
        ]
    }

    bedrock_agent_stubber.stub_list_flow_versions(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_version.list_flow_versions(
            bedrock_agent_client, FLOW_ID)

        assert call_response is not None

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_version.list_flow_versions(bedrock_agent_client, FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code
