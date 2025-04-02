# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
IAM Role Management for Amazon Bedrock Flows

This module provides functionality to create, update, and delete IAM roles specifically
configured for Amazon Bedrock flows. It handles the complete lifecycle of IAM roles
including trust relationships, inline policies, and permissions management.
"""

import json
from botocore.exceptions import ClientError


def create_flow_role(client, role_name):
    """
    Creates an IAM role for Amazon Bedrock with permissions to run a flow.
    
    Args:
        role_name (str): Name for the new IAM role
    Returns:
        dict: Created role information if successful, None if failed
    """

    
    # Trust relationship policy - allows Bedrock service to assume this role.
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
    
    # Basic inline policy for for running a flow.

    resources = "*"

    bedrock_policy = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "bedrock:InvokeModel",
                    "bedrock:Retrieve",
                    "bedrock:RetrieveAndGenerate"
                ],
                # Using * as placeholder - Later you update with specific ARNs.
                "Resource": resources
            }
        ]
    }


    
    try:
        # Create the IAM role with trust policy
        print(f"Creating role: {role_name}")
        role = client.create_role(
            RoleName=role_name,
            AssumeRolePolicyDocument=json.dumps(trust_policy),
            Description="Role for Amazon Bedrock operations"
        )
        
        # Attach inline policy to the role
        print("Attaching inline policy")
        client.put_role_policy(
            RoleName=role_name,
            PolicyName=f"{role_name}-policy",
            PolicyDocument=json.dumps(bedrock_policy)
        )
        
        print(f"Role ARN: {role['Role']['Arn']}")
        return role['Role']
        
    except ClientError as e:
        print(f"Error creating role: {str(e)}")
        raise
    except Exception as e:
        print(f"Unexpected error: {str(e)}")
        raise

def update_role_policy(client, role_name, resource_arns):
    """
    Updates an IAM role's inline policy with specific resource ARNs.
    
    Args:
        role_name (str): Name of the existing role
        resource_arns (list): List of resource ARNs to allow access to
    """

    
    updated_policy = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "bedrock:GetFlow",
                    "bedrock:InvokeModel",
                    "bedrock:Retrieve",
                    "bedrock:RetrieveAndGenerate"
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
        print(f"Updated policy for role: {role_name}")
        
    except ClientError as e:
        print(f"Error updating role policy: {str(e)}")
        raise 


def delete_flow_role(client, role_name):
    """
    Deletes an IAM role.

    Args:
        role_name (str): Name of the role to delete
    """



    try:
        # Detach and delete inline policies
        policies = client.list_role_policies(RoleName=role_name)['PolicyNames']
        for policy_name in policies:
            client.delete_role_policy(RoleName=role_name, PolicyName=policy_name)

        # Delete the role
        client.delete_role(RoleName=role_name)
        print(f"Deleted role: {role_name}")


    except ClientError as e:
        print(f"Error deleting role: {str(e)}")
        raise

