# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[create_policy_version.py demonstrates how to create a new version of an IAM policy.]
# snippet-service:[iam]
# snippet-keyword:[AWS Identity and Access Management (IAM)]
# snippet-keyword:[Python]
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

# snippet-start:[iam.python.create_policy_version.complete]

import json
import boto3
from botocore.exceptions import ClientError

# Set this value before running the program
# Policy to create a new version of
policy_arn = 'arn:aws:iam::123456789012:policy/POLICY_NAME'

# Define the new version of the policy
new_policy = {
    'Version': '2012-10-17',
    'Statement': [
        {
            'Sid': 'Statement1',
            'Effect': 'Allow',
            'Action': 'EC2:*',
            'Resource': '*'
        },
        {
            'Sid': 'Statement2',
            'Effect': 'Allow',
            'Action': 'S3:*',
            'Resource': '*'
        }
    ]
}
print(f'Creating new version of IAM policy {policy_arn}')
print(json.dumps(new_policy))

# Create the new version of the policy and set it as the default version
try:
    iam = boto3.client('iam')
    response = iam.create_policy_version(PolicyArn=policy_arn,
                                         PolicyDocument=json.dumps(new_policy),
                                         SetAsDefault=True)

    print(f'Policy Version Created: {response["PolicyVersion"]["VersionId"]}')
except ClientError as e:
    print(e)

# snippet-end:[iam.python.create_policy_version.complete]
