

import boto3
import json

# Create IAM Client
iam = boto3.client('iam')

# Create an In-line policy

# This is a role that currently exists in IAM
role_name = 'TestRole' 

# This is the in-line policy that is being added to the role
policy_name = 'TestPolicy' 

inline_policy={
    "Version": "2012-10-17",
    "Statement": [
      {
        "Action": "lambda:InvokeFunction",
        "Effect": "Allow",
        "Resource": "RESOURCE_ARN"
      }
    ]
}

iam.put_role_policy(
  PolicyDocument=json.dumps(policy_json), 
  PolicyName=policy_name, 
  RoleName=role_name
)

