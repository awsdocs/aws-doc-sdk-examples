# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# 
# http://aws.amazon.com/apache2.0/
# 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[assume_role.py demonstrates how to use Amazon STS AssumeRole to access resources on an external account.]
# snippet-service:[iam]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS Identity and Access Management (IAM)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AssumeRole]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-22]
# snippet-sourceauthor:[stephswo (AWS)]
# snippet-start:[iam.python.assume_role.complete]

import boto3

# Create an STS client
sts_client = boto3.client('sts')

# Assume a role defined on an external account. The role specifies the
# permissions that are allowed on the account.
# Replace EXTERNAL_ACCOUNT_NUMBER with the account number of the external
# account.
# Replace ROLE_NAME with the name of the role defined on the external account.
# Optional, but recommended: Specify a unique ExternalId= string assigned by
# the external account.
response = sts_client.assume_role(RoleArn='arn:aws:iam::EXTERNAL_ACCOUNT_NUMBER:role/ROLE_NAME',
                                  RoleSessionName='AssumeRoleSession1')

# Reference the temporary credentials section of the response
tempCredentials = response['Credentials']

# Use the temporary credentials to create an S3 resource that can access the
# external account. The assumed role's permissions must allow the desired S3
# access.
s3_resource = boto3.resource('s3',
                             aws_access_key_id=tempCredentials['AccessKeyId'],
                             aws_secret_access_key=tempCredentials['SecretAccessKey'],
                             aws_session_token=tempCredentials['SessionToken'])

# Use the S3 resource to list the external account's buckets.
for bucket in s3_resource.buckets.all():
    print(bucket.name)
# snippet-end:[iam.python.assume_role.complete]
