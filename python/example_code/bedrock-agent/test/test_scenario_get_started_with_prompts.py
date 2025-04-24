# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_get_started_with_prompts.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest
import io
import json

from conftest import FakeData

import sys
sys.path.append("..")
from prompts import scenario_get_started_with_prompts as scenario


class FakeScenarioData:
    PROMPT_ID = "FAKE_PROMPT_ID"
    PROMPT_NAME = "Product-Description-Generator-12345"
    PROMPT_DESCRIPTION = "Generates product descriptions based on product details"
    MODEL_ID = "anthropic.claude-v2"
    CREATED_AT = "2025-03-29T21:34:43.048609+00:00"
    UPDATED_AT = "2025-03-30T21:34:43.048609+00:00"
    OUTPUT_TEXT = "This is the generated product description."

@pytest.fixture
def mock_wait_for_prompt_status(monkeypatch):
    """Mock the wait_for_prompt_status function."""
    def mock_wait(*args, **kwargs):
        return True
    
    monkeypatch.setattr(scenario, "wait_for_prompt_status", mock_wait)
    return mock_wait

def test_run_scenario(make_stubber, mock_wait_for_prompt_status, monkeypatch):
    """Test the run_scenario function."""
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_runtime_client = boto3.client("bedrock-runtime")
    bedrock_stubber = make_stubber(bedrock_client)
    bedrock_runtime_stubber = make_stubber(bedrock_runtime_client)
    
    # Mock create_prompt
    create_response = {
        "id": FakeScenarioData.PROMPT_ID,
        "arn": f"arn:aws:bedrock:us-east-1:123456789012:prompt/{FakeScenarioData.PROMPT_ID}",
        "name": FakeScenarioData.PROMPT_NAME,
        "description": FakeScenarioData.PROMPT_DESCRIPTION,
        "createdAt": FakeScenarioData.CREATED_AT,
        "updatedAt": FakeScenarioData.UPDATED_AT,
        "version": "1"
    }
    
    def mock_create_prompt(*args, **kwargs):
        return create_response
    
    monkeypatch.setattr(scenario, "create_prompt", mock_create_prompt)
    
    # Mock invoke_prompt
    invoke_response = {
        "output": FakeScenarioData.OUTPUT_TEXT
    }
    
    def mock_invoke_prompt(*args, **kwargs):
        return invoke_response
    
    monkeypatch.setattr(scenario, "invoke_prompt", mock_invoke_prompt)
    
    # Mock update_prompt
    update_response = {
        "id": FakeScenarioData.PROMPT_ID,
        "arn": f"arn:aws:bedrock:us-east-1:123456789012:prompt/{FakeScenarioData.PROMPT_ID}",
        "name": FakeScenarioData.PROMPT_NAME,
        "description": FakeScenarioData.PROMPT_DESCRIPTION,
        "createdAt": FakeScenarioData.CREATED_AT,
        "updatedAt": FakeScenarioData.UPDATED_AT,
        "version": "1"
    }
    
    def mock_update_prompt(*args, **kwargs):
        return update_response
    
    monkeypatch.setattr(scenario, "update_prompt", mock_update_prompt)
    
    # Mock delete_prompt
    delete_response = {
        "id": FakeScenarioData.PROMPT_ID
    }
    
    def mock_delete_prompt(*args, **kwargs):
        return delete_response
    
    monkeypatch.setattr(scenario, "delete_prompt", mock_delete_prompt)
    
    # Run the scenario
    resources = scenario.run_scenario(
        bedrock_client,
        bedrock_runtime_client,
        FakeScenarioData.MODEL_ID,
        cleanup=True
    )
    
    # Verify the resources
    assert resources["prompt_id"] == FakeScenarioData.PROMPT_ID
