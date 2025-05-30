# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to work with
knowledge bases in your AWS account.

This example demonstrates how to:
- Create a Bedrock Agent client
- Get details of a knowledge base
- Update a knowledge base
- List knowledge bases in your account
- Delete a knowledge base
"""

import argparse
import logging
from pprint import pprint
import boto3
import uuid
import time
from botocore.exceptions import ClientError
from roles import create_knowledge_base_role, delete_knowledge_base_role

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock-agent.create_knowledge_base]
def create_knowledge_base(bedrock_agent_client, name, role_arn, description=None):
    """
    Creates a new knowledge base.

    Args:
        bedrock_agent_client: The Boto3 Bedrock Agent client.
        name (str): The name of the knowledge base.
        role_arn (str): The ARN of the IAM role that the knowledge base assumes to access resources.
        description (str, optional): A description of the knowledge base.

    Returns:
        dict: The details of the created knowledge base.
    """
    try:
        kwargs = {
            "name": name,
            "roleArn": role_arn,
            "knowledgeBaseConfiguration": {
                "type": "VECTOR",
                "vectorKnowledgeBaseConfiguration": {
                    "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                }
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
            "clientToken": "test-client-token-" + str(uuid.uuid4())
        }
        
        if description:
            kwargs["description"] = description
            
        response = bedrock_agent_client.create_knowledge_base(**kwargs)
        
        logger.info("Created knowledge base with ID: %s", response["knowledgeBase"]["knowledgeBaseId"])
        return response["knowledgeBase"]
    
    except ClientError as err:
        logger.error(
            "Couldn't create knowledge base. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise
# snippet-end:[python.example_code.bedrock-agent.create_knowledge_base]


# snippet-start:[python.example_code.bedrock-agent.get_knowledge_base]
def get_knowledge_base(bedrock_agent_client, knowledge_base_id):
    """
    Gets details about a specific knowledge base.

    Args:
        bedrock_agent_client: The Boto3 Bedrock Agent client.
        knowledge_base_id (str): The ID of the knowledge base.

    Returns:
        dict: The details of the knowledge base.
    """
    try:
        response = bedrock_agent_client.get_knowledge_base(
            knowledgeBaseId=knowledge_base_id
        )
        
        logger.info("Retrieved knowledge base: %s", knowledge_base_id)
        return response["knowledgeBase"]
    except ClientError as err:
        logger.error(
            "Couldn't get knowledge base %s. Here's why: %s: %s",
            knowledge_base_id,
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise
# snippet-end:[python.example_code.bedrock-agent.get_knowledge_base]


# snippet-start:[python.example_code.bedrock-agent.update_knowledge_base]
def update_knowledge_base(bedrock_agent_client, knowledge_base_id, name=None, description=None, role_arn=None):
    """
    Updates an existing knowledge base.

    Args:
        bedrock_agent_client: The Boto3 Bedrock Agent client.
        knowledge_base_id (str): The ID of the knowledge base to update.
        name (str, optional): The new name for the knowledge base.
        description (str, optional): The new description for the knowledge base.
        role_arn (str, optional): The new IAM role ARN for the knowledge base.

    Returns:
        dict: The details of the updated knowledge base.
    """
    try:
        kwargs = {
            "knowledgeBaseId": knowledge_base_id,
            "knowledgeBaseConfiguration": {
                "type": "VECTOR",
                "vectorKnowledgeBaseConfiguration": {
                    "embeddingModelArn": "arn:aws:bedrock:us-east-1::foundation-model/amazon.titan-embed-text-v1"
                }
            }
        }
        
        if name:
            kwargs["name"] = name
        if description:
            kwargs["description"] = description
        if role_arn:
            kwargs["roleArn"] = role_arn
            
        response = bedrock_agent_client.update_knowledge_base(**kwargs)
        
        logger.info("Updated knowledge base: %s", knowledge_base_id)
        return response["knowledgeBase"]
    
    except ClientError as err:
        logger.error(
            "Couldn't update knowledge base %s. Here's why: %s: %s",
            knowledge_base_id,
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise
# snippet-end:[python.example_code.bedrock-agent.update_knowledge_base]


# snippet-start:[python.example_code.bedrock-agent.delete_knowledge_base]
def delete_knowledge_base(bedrock_agent_client, knowledge_base_id):
    """
    Deletes a knowledge base.

    Args:
        bedrock_agent_client: The Boto3 Bedrock Agent client.
        knowledge_base_id (str): The ID of the knowledge base to delete.

    Returns:
        bool: True if the deletion was successful.
    """
    try:
        bedrock_agent_client.delete_knowledge_base(
            knowledgeBaseId=knowledge_base_id
        )
        
        logger.info("Deleted knowledge base: %s", knowledge_base_id)
        return True
    except ClientError as err:
        logger.error(
            "Couldn't delete knowledge base %s. Here's why: %s: %s",
            knowledge_base_id,
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise
# snippet-end:[python.example_code.bedrock-agent.delete_knowledge_base]


# snippet-start:[python.example_code.bedrock-agent.list_knowledge_bases]
def list_knowledge_bases(bedrock_agent_client, max_results=None):
    """
    Lists the knowledge bases in your AWS account.

    Args:
        bedrock_agent_client: The Boto3 Bedrock Agent client.
        max_results (int, optional): The maximum number of knowledge bases to return.

    Returns:
        list: A list of knowledge base details.
    """
    try:
        kwargs = {}
        if max_results is not None:
            kwargs["maxResults"] = max_results

        # Initialize an empty list to store all knowledge bases
        all_knowledge_bases = []
        
        # Use paginator to handle pagination automatically
        paginator = bedrock_agent_client.get_paginator('list_knowledge_bases')
        page_iterator = paginator.paginate(**kwargs)
        
        # Iterate through each page of results
        for page in page_iterator:
            all_knowledge_bases.extend(page.get('knowledgeBaseSummaries', []))
            
        logger.info("Found %s knowledge bases.", len(all_knowledge_bases))
        return all_knowledge_bases
    except ClientError as err:
        logger.error(
            "Couldn't list knowledge bases. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise
# snippet-end:[python.example_code.bedrock-agent.list_knowledge_bases]


def run_knowledge_base_scenario():
    """
    1. Create an IAM role for the knowledge base
    2. Create a knowledge base
    3. Get details of the knowledge base
    4. Update the knowledge base
    5. Delete the knowledge base and IAM role
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Knowledge Bases scenario.")
    print("-" * 88)
    
    # Create clients
    bedrock_agent_client = boto3.client(service_name="bedrock-agent")
    iam_client = boto3.client(service_name="iam")
    
    # Generate unique names for resources
    kb_name = "example-knowledge-base-" + str(uuid.uuid4().hex[:8])    
    role_name = "example-kb-role" + str(uuid.uuid4().hex[:8])
    
    knowledge_base_id = None
    
    try:
        # Step 1: Create IAM role
        print("\nCreating IAM role: " + role_name + " ...")
        role = create_knowledge_base_role(iam_client, role_name)
        role_arn = role['Arn']
        print("Created role with ARN: " + role_arn)
        
        # Wait for role to propagate
        print("Waiting for role to propagate...")
        time.sleep(10)
        
        # Step 2: Create knowledge base
        print("Creating knowledge base: " + kb_name + " ...")
        kb = create_knowledge_base(
            bedrock_agent_client,
            kb_name,
            role_arn,
            "Example knowledge base for demonstration"
        )
        knowledge_base_id = kb["knowledgeBaseId"]
        print("Created knowledge base with ID: " + knowledge_base_id)
        
        # Step 3: Get knowledge base details
        print("\nGetting details for knowledge base: " + knowledge_base_id + " ...")
        kb_details = get_knowledge_base(bedrock_agent_client, knowledge_base_id)
        print("Knowledge base details:")
        pprint(kb_details)
        
        # Step 4: Update knowledge base
        new_name = kb_name + "-updated"
        print("\nUpdating knowledge base name to: " + new_name + " ...")
        updated_kb = update_knowledge_base(
            bedrock_agent_client,
            knowledge_base_id,
            new_name,
            "Updated description for the knowledge base",
            role_arn
        )
        print("Updated knowledge base details:")
        pprint(updated_kb)
        
        # Step 5: List knowledge bases
        print("\nListing all knowledge bases:")
        all_kbs = list_knowledge_bases(bedrock_agent_client)
        print("Found " + len(all_kbs) + " knowledge bases.")
        
        print("\nCleaning up resources...")
        if knowledge_base_id:
            print("Deleting knowledge base " + knowledge_base_id + " ...")
            delete_knowledge_base(bedrock_agent_client, knowledge_base_id)
            print("Knowledge base " + knowledge_base_id + " deleted successfully.")
        
        print("Deleting IAM role " + role_name + " ...")
        delete_knowledge_base_role(iam_client, role_name)
        print("Role " + role_name + " deleted successfully.")
        
        print("\nScenario completed successfully!")
        
    except ClientError as error:
        print("Operation failed: " + error)
        # Clean up resources on error
        if knowledge_base_id:
            try:
                print("Attempting to delete knowledge base " + knowledge_base_id + " ...")
                delete_knowledge_base(bedrock_agent_client, knowledge_base_id)
            except Exception as e:
                print("Failed to delete knowledge base: " + e)
        
        try:
            print("Attempting to delete IAM role: " + role_name + " ...")
            delete_knowledge_base_role(iam_client, role_name)
        except Exception as e:
            print("Failed to delete IAM role: " + e)
    
    print("-" * 88)


def main():
    """
    Shows how to use the Bedrock Agent API to work with knowledge bases.
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    parser = argparse.ArgumentParser(
        description="Work with knowledge bases in your AWS account."
    )
    parser.add_argument(
        "--action",
        choices=["list", "create", "get", "update", "delete", "scenario"],
        default="list",
        help="The action to perform on knowledge bases.",
    )
    parser.add_argument(
        "--knowledge-base-id",
        help="The ID of the knowledge base (required for get, update, and delete actions).",
    )
    parser.add_argument(
        "--name",
        help="The name of the knowledge base (required for create, optional for update).",
    )
    parser.add_argument(
        "--role-arn",
        help="The ARN of the IAM role for the knowledge base (required for create, optional for update).",
    )
    parser.add_argument(
        "--description",
        help="A description of the knowledge base (optional for create and update).",
    )
    parser.add_argument(
        "--max-results",
        type=int,
        help="The maximum number of knowledge bases to return when listing.",
    )
    args = parser.parse_args()

    print("-" * 88)
    print("Welcome to the Amazon Bedrock Knowledge Bases example.")
    print("-" * 88)

    bedrock_agent_client = boto3.client(service_name="bedrock-agent")

    try:
        if args.action == "scenario":
            run_knowledge_base_scenario()
        elif args.action == "list":
            print("Listing knowledge bases in your AWS account...")
            knowledge_bases = list_knowledge_bases(bedrock_agent_client, args.max_results)
            
            if knowledge_bases:
                print("Found " + len(knowledge_bases) + " knowledge bases")
                for kb in knowledge_bases:
                    print("\n" + "-" * 40)
                    pprint(kb)
            else:
                print("No knowledge bases found in your account.")
                
        elif args.action == "create":
            if not args.name or not args.role_arn:
                print("Error: --name and --role-arn are required for create action.")
                return
                
            print("Creating knowledge base " + args.name + " ...")
            kb = create_knowledge_base(
                bedrock_agent_client, 
                args.name, 
                args.role_arn, 
                args.description
            )
            print("Knowledge base created successfully:")
            pprint(kb)
            
        elif args.action == "get":
            if not args.knowledge_base_id:
                print("Error: --knowledge-base-id is required for get action.")
                return
                
            print("Getting details for knowledge base " + args.knowledge_base_id + " ...")
            kb = get_knowledge_base(bedrock_agent_client, args.knowledge_base_id)
            print("Knowledge base details:")
            pprint(kb)
            
        elif args.action == "update":
            if not args.knowledge_base_id:
                print("Error: --knowledge-base-id is required for update action.")
                return
                
            print("Updating knowledge base " + args.knowledge_base_id + " ...")
            kb = update_knowledge_base(
                bedrock_agent_client,
                args.knowledge_base_id,
                args.name,
                args.description,
                args.role_arn
            )
            print("Knowledge base updated successfully:")
            pprint(kb)
            
        elif args.action == "delete":
            if not args.knowledge_base_id:
                print("Error: --knowledge-base-id is required for delete action.")
                return
                
            print("Deleting knowledge base " + args.knowledge_base_id + " ...")
            if delete_knowledge_base(bedrock_agent_client, args.knowledge_base_id):
                print("Knowledge base " + args.knowledge_base_id + " deleted successfully.")
    
    except ClientError as error:
        print("Operation failed: " + error)

    print("-" * 88)


if __name__ == "__main__":
    main()
