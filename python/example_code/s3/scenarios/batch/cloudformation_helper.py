# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Helper class for managing CloudFormation stack operations for S3 Batch Operations.
"""

import json
from typing import Dict, Any

import boto3
from botocore.exceptions import ClientError, WaiterError

# snippet-start:[python.example_code.s3control.CloudFormationHelper]
class CloudFormationHelper:
    """Helper class for managing CloudFormation stack operations."""
    
    def __init__(self, cfn_client: Any) -> None:
        """
        Initializes the CloudFormationHelper with a CloudFormation client.
        
        :param cfn_client: A Boto3 Amazon CloudFormation client. This client provides
                          low-level access to AWS CloudFormation services.
        """
        self.cfn_client = cfn_client

    def deploy_cloudformation_stack(self, stack_name: str) -> None:
        """
        Deploy a CloudFormation stack with S3 batch operation permissions.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Raises:
            ClientError: If stack creation fails
        """
        try:
            template = {
                "AWSTemplateFormatVersion": "2010-09-09",
                "Resources": {
                    "S3BatchRole": {
                        "Type": "AWS::IAM::Role",
                        "Properties": {
                            "AssumeRolePolicyDocument": {
                                "Version": "2012-10-17",
                                "Statement": [
                                    {
                                        "Effect": "Allow",
                                        "Principal": {
                                            "Service": "batchoperations.s3.amazonaws.com"
                                        },
                                        "Action": "sts:AssumeRole"
                                    }
                                ]
                            },
                            "ManagedPolicyArns": [
                                "arn:aws:iam::aws:policy/AmazonS3FullAccess"
                            ],
                            "Policies": [
                                {
                                    "PolicyName": "S3BatchOperationsPolicy",
                                    "PolicyDocument": {
                                        "Version": "2012-10-17",
                                        "Statement": [
                                            {
                                                "Effect": "Allow",
                                                "Action": [
                                                    "s3:*",
                                                    "s3-object-lambda:*"
                                                ],
                                                "Resource": "*"
                                            }
                                        ]
                                    }
                                }
                            ]
                        }
                    }
                },
                "Outputs": {
                    "S3BatchRoleArn": {
                        "Description": "ARN of IAM Role for S3 Batch Operations",
                        "Value": {"Fn::GetAtt": ["S3BatchRole", "Arn"]}
                    }
                }
            }

            self.cfn_client.create_stack(
                StackName=stack_name,
                TemplateBody=json.dumps(template),
                Capabilities=['CAPABILITY_IAM']
            )

            print(f"Creating stack {stack_name}...")
            self._wait_for_stack_completion(stack_name, 'CREATE')
            print(f"Stack {stack_name} created successfully")

        except ClientError as e:
            print(f"Error creating CloudFormation stack: {e}")
            raise

    def get_stack_outputs(self, stack_name: str) -> Dict[str, str]:
        """
        Get CloudFormation stack outputs.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Returns:
            dict: Stack outputs

        Raises:
            ClientError: If getting stack outputs fails
        """
        try:
            response = self.cfn_client.describe_stacks(StackName=stack_name)
            outputs = {}
            if 'Stacks' in response and response['Stacks']:
                for output in response['Stacks'][0].get('Outputs', []):
                    outputs[output['OutputKey']] = output['OutputValue']
            return outputs

        except ClientError as e:
            print(f"Error getting stack outputs: {e}")
            raise

    def destroy_cloudformation_stack(self, stack_name: str) -> None:
        """
        Delete a CloudFormation stack.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Raises:
            ClientError: If stack deletion fails
        """
        try:
            self.cfn_client.delete_stack(StackName=stack_name)
            print(f"Deleting stack {stack_name}...")
            self._wait_for_stack_completion(stack_name, 'DELETE')
            print(f"Stack {stack_name} deleted successfully")

        except ClientError as e:
            print(f"Error deleting CloudFormation stack: {e}")
            raise

    def _wait_for_stack_completion(self, stack_name: str, operation: str) -> None:
        """
        Wait for CloudFormation stack operation to complete.

        Args:
            stack_name (str): Name of the CloudFormation stack
            operation (str): Stack operation (CREATE or DELETE)

        Raises:
            WaiterError: If waiting for stack completion fails
        """
        try:
            waiter = self.cfn_client.get_waiter(
                'stack_create_complete' if operation == 'CREATE'
                else 'stack_delete_complete'
            )
            waiter.wait(
                StackName=stack_name,
                WaiterConfig={'Delay': 5, 'MaxAttempts': 60}
            )
        except WaiterError as e:
            print(f"Error waiting for stack {operation}: {e}")
            raise
# snippet-end:[python.example_code.s3control.CloudFormationHelper]