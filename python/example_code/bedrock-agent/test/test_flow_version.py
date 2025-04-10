# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flow_version.py.
"""

from conftest import FakeFlowData as Fake

import boto3
from botocore.exceptions import ClientError
import pytest

from flows import flow_version


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_flow_version(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    create_flow_version_expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "description": Fake.VERSION_DESCRIPTION
    }

    get_flow_version_expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "version": Fake.FLOW_VERSION
    }

    response = {
        "arn": Fake.VERSION_ARN,
        "createdAt":  Fake.CREATED_AT,
        "definition": {},
        "description": Fake.VERSION_DESCRIPTION,
        "executionRoleArn": Fake.ROLE_ARN,
        "id": Fake.VERSION_ID,
        "name": Fake.VERSION_NAME,
        "status": "NotPrepared",
        "version": Fake.FLOW_VERSION
    }

            # First stub - Start creating the flow version.
    bedrock_agent_stubber.stub_create_flow_version(
            create_flow_version_expected_params,
            response,            
            error_code=error_code)

    if error_code is None:
        version = flow_version.create_flow_version(
            bedrock_agent_client, Fake.FLOW_ID, Fake.VERSION_DESCRIPTION)
        assert version == Fake.FLOW_VERSION
    else:
        with pytest.raises(ClientError) as exc_info:
            flow_version.create_flow_version(
                bedrock_agent_client, Fake.FLOW_ID, Fake.VERSION_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_flow_version(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "flowVersion": Fake.FLOW_VERSION
    }

    response = {
        "id": Fake.FLOW_ID,
        "version": Fake.FLOW_VERSION
    }

    bedrock_agent_stubber.stub_delete_flow_version(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_version.delete_flow_version(
            bedrock_agent_client, Fake.FLOW_ID, Fake.FLOW_VERSION)

        assert call_response["id"] == Fake.FLOW_ID
        assert call_response["version"] == Fake.FLOW_VERSION

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_version.delete_flow_version(
                bedrock_agent_client, Fake.FLOW_ID, Fake.FLOW_VERSION)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_flow_versions(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier": Fake.FLOW_ID,
        "maxResults": 10
    }

    response = {
        "flowVersionSummaries": [
            {
                "arn": Fake.VERSION_ARN,
                "createdAt": Fake.CREATED_AT,
                "id": Fake.VERSION_ID,
                "status": "Prepared",
                "version": Fake.FLOW_VERSION
            }
        ]
    }

    bedrock_agent_stubber.stub_list_flow_versions(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_version.list_flow_versions(
            bedrock_agent_client, Fake.FLOW_ID)

        assert call_response is not None

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_version.list_flow_versions(bedrock_agent_client, Fake.FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code
