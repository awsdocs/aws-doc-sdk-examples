# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flow_alias.py.
"""


import boto3
from botocore.exceptions import ClientError
import pytest

from flows import flow_alias


ALIAS_NAME = "Fake_flow_alias"
ALIAS_DESCRIPTION = "Playlist creator flow alias"
FLOW_ID = "XXXXXXXXXX"
ALIAS_ID = "XXXXXXXXXX"
FLOW_VERSION = "1"
ALIAS_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}/alias/{ALIAS_ID}"
ROUTING_CONFIG = [
    {
        "flowVersion": FLOW_VERSION
    }
]
CREATED_AT = "2025-03-29T21:34:43.048609+00:00"
UPDATED_AT = "2025-03-30T21:34:43.048609+00:00"



@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "flowIdentifier" : FLOW_ID,
        "name" : ALIAS_NAME,
        "description" : ALIAS_DESCRIPTION,
        "routingConfiguration" : ROUTING_CONFIG
            }

    response = {
   "arn": ALIAS_ARN,
   "createdAt": CREATED_AT,
   "description": ALIAS_DESCRIPTION,
   "flowId": FLOW_ID,
   "id": ALIAS_ID,
   "name": ALIAS_DESCRIPTION,
   "routingConfiguration": [ 
      { 
         "flowVersion": FLOW_VERSION
      }
   ],
   "updatedAt": UPDATED_AT
}

    bedrock_agent_stubber.stub_create_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        alias_id = flow_alias.create_flow_alias(
            bedrock_agent_client, FLOW_ID, FLOW_VERSION, ALIAS_NAME, ALIAS_DESCRIPTION
        )
        assert alias_id == ALIAS_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.create_flow_alias(bedrock_agent_client, FLOW_ID, FLOW_VERSION, ALIAS_NAME, ALIAS_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code



@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "aliasIdentifier" : ALIAS_ID,
        "flowIdentifier" : FLOW_ID,
        "name" : ALIAS_NAME,
        "description" : ALIAS_DESCRIPTION,
        "routingConfiguration" : ROUTING_CONFIG
            }

    response = {
   "arn": ALIAS_ARN,
   "createdAt": CREATED_AT,
   "description": ALIAS_DESCRIPTION,
   "flowId": FLOW_ID,
   "id": ALIAS_ID,
   "name": ALIAS_DESCRIPTION,
   "routingConfiguration": [ 
      { 
         "flowVersion": FLOW_VERSION
      }
   ],
   "updatedAt": UPDATED_AT
}

    bedrock_agent_stubber.stub_update_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        response = flow_alias.update_flow_alias(
            bedrock_agent_client,ALIAS_ID, FLOW_ID, 
            FLOW_VERSION, ALIAS_NAME, ALIAS_DESCRIPTION
        )
        assert response["id"] == ALIAS_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.update_flow_alias(bedrock_agent_client,ALIAS_ID, FLOW_ID,
                    FLOW_VERSION, ALIAS_NAME, ALIAS_DESCRIPTION)
        assert exc_info.value.response["Error"]["Code"] == error_code




@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_flow_alias(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
        "aliasIdentifier" : ALIAS_ID,
        "flowIdentifier": FLOW_ID
    }

    response = {
        "flowId" : FLOW_ID,
        "id": ALIAS_ID
    }

    bedrock_agent_stubber.stub_delete_flow_alias(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_alias.delete_flow_alias(
            bedrock_agent_client, FLOW_ID, ALIAS_ID)

        assert call_response["id"] == FLOW_ID

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.delete_flow_alias(bedrock_agent_client, FLOW_ID, ALIAS_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code

    
@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_flow_aliases(make_stubber, error_code):
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)

    expected_params = {
   "flowIdentifier": FLOW_ID,
    "maxResults": 10
    }

    response = {
   "flowAliasSummaries": [ 
      { 
         "arn": ALIAS_ARN,
         "createdAt": CREATED_AT,
         "description": ALIAS_DESCRIPTION,
         "id": ALIAS_ID,
         "flowId" : FLOW_ID,
         "name": ALIAS_NAME,
         "routingConfiguration": ROUTING_CONFIG,
         "updatedAt": UPDATED_AT
      }
   ]
}

    bedrock_agent_stubber.stub_list_flow_aliases(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        call_response = flow_alias.list_flow_aliases(
            bedrock_agent_client, FLOW_ID)

        assert call_response is not None

    else:
        with pytest.raises(ClientError) as exc_info:
            flow_alias.list_flow_aliases(bedrock_agent_client, FLOW_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code