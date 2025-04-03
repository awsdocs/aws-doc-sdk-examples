# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flow_alias.py.
"""

from conftest import FakeFlowData as Fake

import boto3
from botocore.exceptions import ClientError
import pytest

from flows import flow_alias


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier" : Fake.FLOW_ID,
        "name" : Fake.ALIAS_NAME,
        "description" : Fake.ALIAS_DESCRIPTION,
        "routingConfiguration" : Fake.ROUTING_CONFIG
            }

    response = {
   "arn": Fake.ALIAS_ARN,
   "createdAt": Fake.CREATED_AT,
   "description": Fake.ALIAS_DESCRIPTION,
   "flowId": Fake.FLOW_ID,
   "id": Fake.ALIAS_ID,
   "name": Fake.ALIAS_DESCRIPTION,
   "routingConfiguration": [ 
      { 
         "flowVersion": Fake.FLOW_VERSION
      }
   ],
   "updatedAt": Fake.UPDATED_AT
}

    bedrock_agent_stubber.stub_create_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        alias_id = flow_alias.create_flow_alias(
            bedrock_agent_client, Fake.FLOW_ID, Fake.FLOW_VERSION, Fake.ALIAS_NAME, Fake.ALIAS_DESCRIPTION
        )
        assert alias_id == Fake.ALIAS_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.create_flow_alias(bedrock_agent_client, Fake.FLOW_ID, Fake.FLOW_VERSION, Fake.ALIAS_NAME, Fake.ALIAS_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code



@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "aliasIdentifier" : Fake.ALIAS_ID,
        "flowIdentifier" : Fake.FLOW_ID,
        "name" : Fake.ALIAS_NAME,
        "description" : Fake.ALIAS_DESCRIPTION,
        "routingConfiguration" : Fake.ROUTING_CONFIG
            }

    response = {
   "arn": Fake.ALIAS_ARN,
   "createdAt": Fake.CREATED_AT,
   "description": Fake.ALIAS_DESCRIPTION,
   "flowId": Fake.FLOW_ID,
   "id": Fake.ALIAS_ID,
   "name": Fake.ALIAS_DESCRIPTION,
   "routingConfiguration": [ 
      { 
         "flowVersion": Fake.FLOW_VERSION
      }
   ],
   "updatedAt": Fake.UPDATED_AT
}

    bedrock_agent_stubber.stub_update_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        response = flow_alias.update_flow_alias(
            bedrock_agent_client,Fake.ALIAS_ID, Fake.FLOW_ID, 
            Fake.FLOW_VERSION, Fake.ALIAS_NAME, Fake.ALIAS_DESCRIPTION
        )
        assert response["id"] == Fake.ALIAS_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.update_flow_alias(bedrock_agent_client,Fake.ALIAS_ID, Fake.FLOW_ID,
                    Fake.FLOW_VERSION, Fake.ALIAS_NAME, Fake.ALIAS_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code




@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "aliasIdentifier" : Fake.ALIAS_ID,
        "flowIdentifier": Fake.FLOW_ID
    }

    response = {
        "flowId" : Fake.FLOW_ID,
        "id": Fake.ALIAS_ID
    }

    bedrock_agent_stubber.stub_delete_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_alias.delete_flow_alias(
            bedrock_agent_client, Fake.FLOW_ID, Fake.ALIAS_ID)

        assert call_response["id"] == Fake.FLOW_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.delete_flow_alias(bedrock_agent_client, Fake.FLOW_ID, Fake.ALIAS_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code

    
@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_flow_aliases(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
   "flowIdentifier": Fake.FLOW_ID,
    "maxResults": 10
    }

    response = {
   "flowAliasSummaries": [ 
      { 
         "arn": Fake.ALIAS_ARN,
         "createdAt": Fake.CREATED_AT,
         "description": Fake.ALIAS_DESCRIPTION,
         "id": Fake.ALIAS_ID,
         "flowId" : Fake.FLOW_ID,
         "name": Fake.ALIAS_NAME,
         "routingConfiguration": Fake.ROUTING_CONFIG,
         "updatedAt": Fake.UPDATED_AT
      }
   ]
}

    bedrock_agent_stubber.stub_list_flow_aliases(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_alias.list_flow_aliases(
            bedrock_agent_client, Fake.FLOW_ID)

        assert call_response is not None

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.list_flow_aliases(bedrock_agent_client, Fake.FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code