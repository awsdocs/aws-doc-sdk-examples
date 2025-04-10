# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for flows.py.
"""


import boto3
from botocore.exceptions import ClientError
import pytest

from conftest import FakeFlowData as Fake

from flows import run_flow



@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_invoke_flow(make_stubber, error_code):
    bedrock_agent_runtime_client = boto3.client(
        service_name="bedrock-agent-runtime")
    bedrock_agent_runtime_stubber = make_stubber(bedrock_agent_runtime_client)
    
    flow_input_data = {
        "content": {
            "document": {
                "genre" : "pop",
                "number" : "3"
            }
        },
        "nodeName": "FlowInput",
        "nodeOutputName": "document"
    }

    expected_params =  {
            "flowIdentifier": Fake.FLOW_ID,
            "flowAliasIdentifier": Fake.ALIAS_ID,
            "inputs": [ flow_input_data],
            "enableTrace": True
        }

    response = {"responseStream": {}}
    
    bedrock_agent_runtime_stubber.stub_invoke_flow(
        expected_params, response, error_code=error_code
    )

    if error_code is None:
        response = run_flow.invoke_flow(
            bedrock_agent_runtime_client, Fake.FLOW_ID, Fake.ALIAS_ID, flow_input_data
        )
        assert response is not None

    else:
        with pytest.raises(ClientError) as exc_info:
            run_flow.invoke_flow(bedrock_agent_runtime_client, Fake.FLOW_ID, Fake.ALIAS_ID, flow_input_data)
        assert exc_info.value.response["Error"]["Code"] == error_code


