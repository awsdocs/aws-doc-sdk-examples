# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[create_policy_version_from_file.py demonstrates how to create a new version of an IAM policy by reading the policy from a file.]
# snippet-service:[iam]
# snippet-keyword:[AWS Identity and Access Management (IAM)]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-04-10]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
from botocore.exceptions import ClientError

# Assign these values before running the program
policy_arn = 'arn:aws:iam::123456789012:policy/POLICY_NAME'
policy_file_name = 'POLICY_FILENAME.JSON'

try:
    # Read the policy definition from a file
    with open(policy_file_name, 'r') as f:
        policy_version = f.read()

    # Show the loaded policy definition
    print(f'Creating new version of IAM policy {policy_arn}')
    print(policy_version)

    # Create a new version of the policy and set it as the default version
    iam = boto3.client('iam')
    response = iam.create_policy_version(PolicyArn=policy_arn,
                                         PolicyDocument=policy_version,
                                         SetAsDefault=True)

    print(f'Policy Version Created: {response["PolicyVersion"]["VersionId"]}')
except ClientError as e:
    print(e)
