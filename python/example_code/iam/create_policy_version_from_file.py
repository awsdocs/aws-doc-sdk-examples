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
import json

# Create IAM client
iam = boto3.client('iam')

policy_arn='arn:aws:iam::123456789012:policy/PolicyName'
policy_file_name='/users/ec2-user/policy.json'

try:
    policy_file = open(policy_file_name, 'r')
    new_policy = policy_file.read()
    policy_file.close()

    print('Creating New IAM Policy Version of Policy ' + policy_arn + ' with the follwing statements: \n\r')
    print(str(new_policy) + '\n\r')

    create_policy_version_response = iam.create_policy_version(
        PolicyArn=policy_arn,
        PolicyDocument=new_policy,
        SetAsDefault=True
    )

    print('Policy Version Created: ' + create_policy_version_response['PolicyVersion']['VersionId'])

except Exception as e:
    print(e)
 

#snippet-sourceauthor: [jschwarzwalder]

#snippet-sourcedescription:[Description]

#snippet-service:[AWSService]

#snippet-sourcetype:[full example]

#snippet-sourcedate:[N/A]

