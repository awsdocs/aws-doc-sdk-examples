# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_get_started_with_prompts.py.
"""

import boto3

from conftest import FakePromptData
from prompts import scenario_get_started_with_prompts as scenario



def test_run_scenario(monkeypatch):
    """Test the run_scenario function."""
    bedrock_client = boto3.client("bedrock-agent")
    bedrock_runtime_client = boto3.client("bedrock-runtime")

    
    # Mock create_prompt
    create_response = {
        "id": FakePromptData.PROMPT_ID,
        "arn": FakePromptData.PROMPT_ARN,
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "createdAt": FakePromptData.CREATED_AT,
        "updatedAt": FakePromptData.UPDATED_AT,
        "version": "1"
    }

    create_prompt_version_response = {
        "id": FakePromptData.PROMPT_ID,
        "arn": FakePromptData.PROMPT_ARN,
        "name": FakePromptData.PROMPT_NAME,
        "description": FakePromptData.PROMPT_DESCRIPTION,
        "createdAt": FakePromptData.CREATED_AT,
        "updatedAt": FakePromptData.UPDATED_AT,
        "version": "1"
    }
    
    def mock_create_prompt(*args, **kwargs):
        return create_response
    def mock_create_prompt_version(*args, **kwargs):
        return create_prompt_version_response
    
    monkeypatch.setattr(scenario, "create_prompt", mock_create_prompt)

    monkeypatch.setattr(scenario, "create_prompt_version", mock_create_prompt_version)
    
    # Mock invoke_prompt
    invoke_response = {
        "output": FakePromptData.OUTPUT_TEXT
    }
    
    def mock_invoke_prompt(*args, **kwargs):
        return invoke_response
    
    monkeypatch.setattr(scenario, "invoke_prompt", mock_invoke_prompt)
    
    

    # Mock delete_prompt
    delete_response = {
        "id": FakePromptData.PROMPT_ID
    }
    
    def mock_delete_prompt(*args, **kwargs):
        return delete_response
    
    monkeypatch.setattr(scenario, "delete_prompt", mock_delete_prompt)
    
    # Run the scenario
    scenario.run_scenario(
        bedrock_client,
        bedrock_runtime_client,
        FakePromptData.MODEL_ID,
        cleanup=True
    )
    
