# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import boto3

# Create IAM client
sts_default_provider_chain = boto3.client('sts')

print('Default Provider Identity: : ' + sts_default_provider_chain.get_caller_identity()['Arn'])

role1_to_assume_arn='arn:aws:iam::123456789012:role/roleName'
role1_session_name='test_session'

role2_to_assume_arn='arn:aws:iam::210987654321:role/roleName'
role2_session_name='test_session'

response1=sts_default_provider_chain.assume_role(
    RoleArn=role1_to_assume_arn,
    RoleSessionName=role1_session_name
)

response2=sts_default_provider_chain.assume_role(
    RoleArn=role2_to_assume_arn,
    RoleSessionName=role2_session_name
)

creds1=response1['Credentials']

creds2=response2['Credentials']

sts_account1 = boto3.client('sts',
    aws_access_key_id=creds1['AccessKeyId'],
    aws_secret_access_key=creds1['SecretAccessKey'],
    aws_session_token=creds1['SessionToken'],
)

sts_account2 = boto3.client('sts',
    aws_access_key_id=creds2['AccessKeyId'],
    aws_secret_access_key=creds2['SecretAccessKey'],
    aws_session_token=creds2['SessionToken'],
)

print('AssumedRole1: ' + sts_account1.get_caller_identity()['Arn'])

print('AssumedRole2: ' + sts_account2.get_caller_identity()['Arn'])
