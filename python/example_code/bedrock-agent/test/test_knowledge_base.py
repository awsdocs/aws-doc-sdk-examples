# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for knowledge_base.py.

This file contains comprehensive tests for the Amazon Bedrock Knowledge Base API operations:
1. CreateKnowledgeBase - Tests creating a knowledge base with valid parameters and error handling
2. GetKnowledgeBase - Tests retrieving details of a specific knowledge base and error handling
3. UpdateKnowledgeBase - Tests updating a knowledge base with new name, description, and role ARN
4. DeleteKnowledgeBase - Tests deleting a knowledge base and error handling
5. ListKnowledgeBases - Tests listing knowledge bases in the account and error handling

Each test function:
- Creates a Bedrock Agent client and stubber
- Sets up test data using the FakeKnowledgeBaseData class from conftest.py
- Defines expected parameters and mock responses
- Configures the stubber with these parameters and responses
- Tests both success and error cases using pytest's parametrize feature
- Verifies the results match the expected values

These tests follow the same pattern as the existing tests in the codebase,
ensuring consistency with the project's testing approach.
"""

import boto3
from botocore.exceptions import ClientError
import pytest
import uuid
from unittest.mock import patch, MagicMock

from knowledge_bases.knowledge_base import (
    create_knowledge_base,
    get_knowledge_base,
    update_knowledge_base,
    delete_knowledge_base,
    list_knowledge_bases
)

from conftest import FakeKnowledgeBaseData
    
@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_knowledge_base(make_stubber, error_code):
    """
    Test the create_knowledge_base function.
    
    This test verifies that:
    1. The function correctly calls the CreateKnowledgeBase API
    2. The function properly processes and returns the knowledge base details
    3. Error handling works as expected when the API call fails
    
    Parameters:
        make_stubber: Fixture that creates a stubber for the Bedrock Agent client
        error_code: Simulated error code (None for success case, string for error case)
    """
    # Create a Bedrock Agent client and stubber
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
    # Create test data
    kb_data = FakeKnowledgeBaseData()
    name = kb_data.KB_NAME_1
    role_arn = "arn:aws:iam::123456789012:role/KnowledgeBaseRole"
    description = kb_data.DESCRIPTION

    # Mock uuid.uuid4 to return a consistent value for testing
    with patch('uuid.uuid4', return_value=MagicMock(return_value="test-uuid")):
        
        # Define the expected parameters for the API call
        expected_params = {
            "name": name,
            "roleArn": role_arn,
            "knowledgeBaseConfiguration": {
                "type": "VECTOR",
                "vectorKnowledgeBaseConfiguration": {
                    "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                },
            },
            "storageConfiguration": {
                "type": "OPENSEARCH_SERVERLESS",
                "opensearchServerlessConfiguration": {
                    "collectionArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1",
                    "fieldMapping": {
                        "metadataField": "metadata",
                        "textField": "text",
                        "vectorField": "vector"
                    },
                    "vectorIndexName": "test-uuid"
                }
            },            
            # Add a longer client token that meets minimum length requirement
            "clientToken": "test-client-token-" + str(uuid.uuid4())
        }

        if description:
            expected_params["description"] = description
        
        # Define the mock response that the API would return
        response = {
            "knowledgeBase": {
                "knowledgeBaseId": kb_data.KB_ID_1,
                "name": name,
                "description": description,
                "roleArn": role_arn,
                "knowledgeBaseArn": kb_data.KB_ARN_1,
                "status": kb_data.STATUS,
                "createdAt": kb_data.CREATED_AT,
                "updatedAt": kb_data.UPDATED_AT,
                "knowledgeBaseConfiguration": {
                    "type": "VECTOR",
                    "vectorKnowledgeBaseConfiguration": {
                        "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                    }
                }
            }
        }
        
        # Configure the stubber with the expected parameters and response
        bedrock_agent_stubber.stub_create_knowledge_base(
            expected_params, response, error_code=error_code
        )
        
        if error_code is None:
            # Test the success case
            result = create_knowledge_base(bedrock_agent_client, name, role_arn, description)
            
            # Verify the results contain the expected knowledge base details
            assert result["knowledgeBaseId"] == kb_data.KB_ID_1
            assert result["name"] == name
            assert result["description"] == description
            assert result["roleArn"] == role_arn
            assert result["knowledgeBaseArn"] == kb_data.KB_ARN_1
        else:
            # Test the error case
            with pytest.raises(ClientError) as exc_info:
                create_knowledge_base(bedrock_agent_client, name, role_arn, description)
            
            # Verify the error code matches what we expected
            assert exc_info.value.response["Error"]["Code"] == error_code

@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_knowledge_base(make_stubber, error_code):
    """
    Test the get_knowledge_base function.
    
    This test verifies that:
    1. The function correctly calls the GetKnowledgeBase API
    2. The function properly processes and returns the knowledge base details
    3. Error handling works as expected when the API call fails
    
    Parameters:
        make_stubber: Fixture that creates a stubber for the Bedrock Agent client
        error_code: Simulated error code (None for success case, string for error case)
    """
    # Create a Bedrock Agent client and stubber
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
    # Create test data
    kb_data = FakeKnowledgeBaseData()
    knowledge_base_id = kb_data.KB_ID_1
    
    # Define the expected parameters for the API call
    expected_params = {
        "knowledgeBaseId": knowledge_base_id
    }
    
    # Define the mock response that the API would return
    response = {
        "knowledgeBase": {
            "knowledgeBaseId": knowledge_base_id,
            "name": kb_data.KB_NAME_1,
            "description": kb_data.DESCRIPTION,
            "roleArn": "arn:aws:iam::123456789012:role/KnowledgeBaseRole",
            "knowledgeBaseArn": kb_data.KB_ARN_1,
            "status": kb_data.STATUS,
            "createdAt": kb_data.CREATED_AT,
            "updatedAt": kb_data.UPDATED_AT,
            "knowledgeBaseConfiguration": {
                "type": "VECTOR",
                "vectorKnowledgeBaseConfiguration": {
                    "embeddingModelArn": kb_data.EMBEDDING_MODEL_ARN
                }
            }
        }
    }
    
    # Configure the stubber with the expected parameters and response
    bedrock_agent_stubber.stub_get_knowledge_base(
        expected_params, response, error_code=error_code
    )
    
    if error_code is None:
        # Test the success case
        result = get_knowledge_base(bedrock_agent_client, knowledge_base_id)
        
        # Verify the results contain the expected knowledge base details
        assert result["knowledgeBaseId"] == knowledge_base_id
        assert result["name"] == kb_data.KB_NAME_1
        assert result["description"] == kb_data.DESCRIPTION
        assert result["knowledgeBaseArn"] == kb_data.KB_ARN_1
        assert result["status"] == kb_data.STATUS
    else:
        # Test the error case
        with pytest.raises(ClientError) as exc_info:
            get_knowledge_base(bedrock_agent_client, knowledge_base_id)
        
        # Verify the error code matches what we expected
        assert exc_info.value.response["Error"]["Code"] == error_code

    
@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_knowledge_base(make_stubber, error_code):
    """
    Test the update_knowledge_base function.
    
    This test verifies that:
    1. The function correctly calls the UpdateKnowledgeBase API
    2. The function properly processes and returns the updated knowledge base details
    3. Error handling works as expected when the API call fails
    
    Parameters:
        make_stubber: Fixture that creates a stubber for the Bedrock Agent client
        error_code: Simulated error code (None for success case, string for error case)
    """
    # Create a Bedrock Agent client and stubber
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
    # Create test data
    kb_data = FakeKnowledgeBaseData()
    knowledge_base_id = kb_data.KB_ID_1
    name = "Updated KB Name"
    description = "Updated description"
    role_arn = "arn:aws:iam::123456789012:role/UpdatedKnowledgeBaseRole"
    
    # Mock uuid.uuid4 to return a consistent value for testing
    with patch('uuid.uuid4', return_value=MagicMock(return_value="test-uuid")):
        # Define the expected parameters for the API call
        expected_params = {
            "knowledgeBaseId": knowledge_base_id,
            "name": name,
            "description": description,
            "roleArn": role_arn,
            "knowledgeBaseConfiguration": {
                "type": "VECTOR",
                "vectorKnowledgeBaseConfiguration": {
                    "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                }
            }
        }
        
        # Define the mock response that the API would return
        response = {
            "knowledgeBase": {
                "knowledgeBaseId": knowledge_base_id,
                "name": name,
                "description": description,
                "roleArn": role_arn,
                "knowledgeBaseArn": kb_data.KB_ARN_1,
                "knowledgeBaseConfiguration": {
                    "type": "VECTOR",
                    "vectorKnowledgeBaseConfiguration": {
                        "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                    }
                },
                "status": kb_data.STATUS,
                "createdAt": kb_data.CREATED_AT,
                "updatedAt": kb_data.UPDATED_AT,
                
            }
        }
        
        # Configure the stubber with the expected parameters and response
        bedrock_agent_stubber.stub_update_knowledge_base(
            expected_params, response, error_code=error_code
        )
        
        if error_code is None:
            # Test the success case
            result = update_knowledge_base(
                bedrock_agent_client, 
                knowledge_base_id, 
                name=name, 
                description=description, 
                role_arn=role_arn
            )
            
            # Verify the results contain the expected knowledge base details
            assert result["knowledgeBaseId"] == knowledge_base_id
            assert result["name"] == name
            assert result["description"] == description
            assert result["roleArn"] == role_arn
            assert result["knowledgeBaseArn"] == kb_data.KB_ARN_1
        else:
            # Test the error case
            with pytest.raises(ClientError) as exc_info:
                update_knowledge_base(
                    bedrock_agent_client, 
                    knowledge_base_id, 
                    name=name, 
                    description=description, 
                    role_arn=role_arn
                )
            
            # Verify the error code matches what we expected
            assert exc_info.value.response["Error"]["Code"] == error_code#         make_stubber: Fixture that creates a stubber for the Bedrock Agent client
#         error_code: Simulated error code (None for success case, string for error case)
#     """
#     # Create a Bedrock Agent client and stubber
#     bedrock_agent_client = boto3.client("bedrock-agent")
#     bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
#     # Create test data
#     kb_data = FakeKnowledgeBaseData()
#     knowledge_base_id = kb_data.KB_ID_1
#     name = "Updated KB Name"
#     description = "Updated description"
#     role_arn = "arn:aws:iam::123456789012:role/UpdatedKnowledgeBaseRole"
    
#     # Mock uuid.uuid4 to return a consistent value for testing
#     with patch('uuid.uuid4', return_value=MagicMock(return_value="test-uuid")):
#         # Define the expected parameters for the API call
#         expected_params = {
#             "knowledgeBaseId": knowledge_base_id,
#             "name": name,
#             "description": description,
#             "roleArn": role_arn,
#             "clientToken": str(uuid.uuid4())
#         }
        
#         # Define the mock response that the API would return
#         response = {
#             "knowledgeBase": {
#                 "knowledgeBaseId": knowledge_base_id,
#                 "name": name,
#                 "description": description,
#                 "roleArn": role_arn,
#                 "knowledgeBaseArn": kb_data.KB_ARN_1,
#                 "status": kb_data.STATUS,
#                 "createdAt": kb_data.CREATED_AT,
#                 "updatedAt": kb_data.UPDATED_AT
#             }
#         }
        
#         # Configure the stubber with the expected parameters and response
#         bedrock_agent_stubber.stub_update_knowledge_base(
#             expected_params, response, error_code=error_code
#         )
        
#         if error_code is None:
#             # Test the success case
#             result = update_knowledge_base(
#                 bedrock_agent_client, 
#                 knowledge_base_id, 
#                 name=name, 
#                 description=description, 
#                 role_arn=role_arn
#             )
            
#             # Verify the results contain the expected knowledge base details
#             assert result["knowledgeBaseId"] == knowledge_base_id
#             assert result["name"] == name
#             assert result["description"] == description
#             assert result["roleArn"] == role_arn
#             assert result["knowledgeBaseArn"] == kb_data.KB_ARN_1
#         else:
#             # Test the error case
#             with pytest.raises(ClientError) as exc_info:
#                 update_knowledge_base(
#                     bedrock_agent_client, 
#                     knowledge_base_id, 
#                     name=name, 
#                     description=description, 
#                     role_arn=role_arn
#                 )
            
#             # Verify the error code matches what we expected
#             assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_knowledge_base(make_stubber, error_code):
    """
    Test the delete_knowledge_base function.
    
    This test verifies that:
    1. The function correctly calls the DeleteKnowledgeBase API
    2. The function properly processes the response
    3. Error handling works as expected when the API call fails
    
    Parameters:
        make_stubber: Fixture that creates a stubber for the Bedrock Agent client
        error_code: Simulated error code (None for success case, string for error case)
    """
    # Create a Bedrock Agent client and stubber
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
    # Create test data
    kb_data = FakeKnowledgeBaseData()
    knowledge_base_id = kb_data.KB_ID_1
    
    # Define the expected parameters for the API call
    expected_params = {
        "knowledgeBaseId": knowledge_base_id
    }
    
    # Define the mock response that the API would return (empty for delete operation)
    response = {
        "knowledgeBaseId": knowledge_base_id,
        "status": "Deleting"
    }
    
    # Configure the stubber with the expected parameters and response
    bedrock_agent_stubber.stub_delete_knowledge_base(
        expected_params, response, error_code=error_code
    )
    
    if error_code is None:
        # Test the success case
        result = delete_knowledge_base(bedrock_agent_client, knowledge_base_id)
        
        # Verify the function returns True on success
        assert result is True
    else:
        # Test the error case
        with pytest.raises(ClientError) as exc_info:
            delete_knowledge_base(bedrock_agent_client, knowledge_base_id)
        
        # Verify the error code matches what we expected
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_knowledge_bases(make_stubber, error_code):
    """
    Test the list_knowledge_bases function.
    
    This test verifies that:
    1. The function correctly calls the ListKnowledgeBases API
    2. The function properly processes and returns the knowledge base summaries
    3. Error handling works as expected when the API call fails
    
    Parameters:
        make_stubber: Fixture that creates a stubber for the Bedrock Agent client
        error_code: Simulated error code (None for success case, string for error case)
    """
    # Create a Bedrock Agent client and stubber
    bedrock_agent_client = boto3.client("bedrock-agent")
    bedrock_agent_stubber = make_stubber(bedrock_agent_client)
    
    # Create test data for knowledge bases
    kb_data = FakeKnowledgeBaseData()
    
    # Define the expected parameters for the API call (empty for basic list)
    expected_params = {}
    
    # Define the mock response that the API would return
    response = {
        "knowledgeBaseSummaries": [
            {
                "knowledgeBaseId": kb_data.KB_ID_1,
                "name": kb_data.KB_NAME_1,
                "description": kb_data.DESCRIPTION,
                "status": kb_data.STATUS,
                "updatedAt": kb_data.UPDATED_AT,
            },
            {
                "knowledgeBaseId": kb_data.KB_ID_2,
                "name": kb_data.KB_NAME_2,
                "description": kb_data.DESCRIPTION,
                "status": kb_data.STATUS,
                "updatedAt": kb_data.UPDATED_AT,
            }
        ]
    }
    
    # Configure the stubber with the expected parameters and response
    bedrock_agent_stubber.stub_list_knowledge_bases(
        expected_params, response, error_code=error_code
    )
    
    if error_code is None:
        # Test the success case
        result = list_knowledge_bases(bedrock_agent_client)
        
        # Verify the results contain the expected knowledge bases
        assert len(result) == 2
        assert result[0]["knowledgeBaseId"] == kb_data.KB_ID_1
        assert result[1]["knowledgeBaseId"] == kb_data.KB_ID_2
    else:
        # Test the error case
        with pytest.raises(ClientError) as exc_info:
            list_knowledge_bases(bedrock_agent_client)
        
        # Verify the error code matches what we expected
        assert exc_info.value.response["Error"]["Code"] == error_code
