# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
IAM Role Management for Amazon Bedrock Knowledge Bases

This module provides functionality to create, update, and delete IAM roles specifically
configured for Amazon Bedrock knowledge bases. It handles the complete lifecycle of IAM roles
including trust relationships, inline policies, and permissions management.
"""
import logging
import json
from botocore.exceptions import ClientError

logging.basicConfig(
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock-agent.knowledge_base_iam_role]

def create_knowledge_base_role(client, role_name):
    """
    Creates an IAM role for Amazon Bedrock with permissions to manage knowledge bases.
    
    Args:
        client: The Boto3 IAM client.
        role_name (str): Name for the new IAM role.
    Returns:
        dict: The created role information including ARN.
    """
    
    # Trust relationship policy - allows Amazon Bedrock service to assume this role.
    trust_policy = {
        "Version": "2012-10-17",
        "Statement": [{
            "Effect": "Allow",
            "Principal": {
                "Service": "bedrock.amazonaws.com"
            },
            "Action": "sts:AssumeRole"
        }]
    }
    
    # Basic inline policy for knowledge base operations
    knowledge_base_policy = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "bedrock:InvokeModel",
                    "bedrock:Retrieve",
                    "bedrock:RetrieveAndGenerate",
                    "aoss:APIAccessAll",
                    "aoss:CreateIndex",
                    "aoss:DeleteIndex",
                    "aoss:UpdateIndex",
                    "aoss:BatchGetCollection",
                    "aoss:CreateCollection",
                    "aoss:DeleteCollection"
                ],
                # Using * as placeholder - Later you can update with specific ARNs.
                "Resource": "*"
            }
        ]
    }

    try:
        # Create the IAM role with trust policy
        logging.info("Creating role: %s", role_name)
        role = client.create_role(
            RoleName=role_name,
            AssumeRolePolicyDocument=json.dumps(trust_policy),
            Description="Role for Amazon Bedrock knowledge base operations"
        )
        
        # Attach inline policy to the role
        print("Attaching inline policy")
        client.put_role_policy(
            RoleName=role_name,
            PolicyName=f"{role_name}-policy",
            PolicyDocument=json.dumps(knowledge_base_policy)
        )
        
        logging.info("Created Role ARN: %s", role['Role']['Arn'])
        return role['Role']
        
    except ClientError as e:
        logging.warning("Error creating role: %s", str(e))
        raise
    except Exception as e:
        logging.warning("Unexpected error: %s", str(e))
        raise


def update_role_policy(client, role_name, resource_arns):
    """
    Updates an IAM role's inline policy with specific resource ARNs.
    
    Args:
        client: The Boto3 IAM client.
        role_name (str): Name of the existing role.
        resource_arns (list): List of resource ARNs to allow access to.
    """
    
    updated_policy = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "bedrock:InvokeModel",
                    "bedrock:Retrieve",
                    "bedrock:RetrieveAndGenerate",
                    "aoss:APIAccessAll",
                    "aoss:CreateIndex",
                    "aoss:DeleteIndex",
                    "aoss:UpdateIndex",
                    "aoss:BatchGetCollection",
                    "aoss:CreateCollection",
                    "aoss:DeleteCollection"
                ],
                "Resource": resource_arns
            }
        ]
    }
    
    try:
        client.put_role_policy(
            RoleName=role_name,
            PolicyName=f"{role_name}-policy",
            PolicyDocument=json.dumps(updated_policy)
        )
        logging.info("Updated policy for role: %s", role_name)
        
    except ClientError as e:
        logging.warning("Error updating role policy: %s", str(e))
        raise


def delete_knowledge_base_role(client, role_name):
    """
    Deletes an IAM role.

    Args:
        client: The Boto3 IAM client.
        role_name (str): Name of the role to delete.
    """
    try:
        # Detach and delete inline policies
        policies = client.list_role_policies(RoleName=role_name)['PolicyNames']
        for policy_name in policies:
            client.delete_role_policy(RoleName=role_name, PolicyName=policy_name)

        # Delete the role
        client.delete_role(RoleName=role_name)
        logging.info("Deleted role: %s", role_name)

    except ClientError as e:
        logging.info("Error Deleting role: %s", str(e))
        raise

# snippet-end:[python.example_code.bedrock-agent.knowledge_base_iam_role]
