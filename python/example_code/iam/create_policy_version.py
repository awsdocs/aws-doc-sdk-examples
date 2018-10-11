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

import json
import boto3

# Create IAM client
iam = boto3.client('iam')

policy_arn='arn:aws:iam::123456789012:policy/PolicyName'

#create Policy Skeleton
new_policy = {}
new_policy['Version'] = '2012-10-17'
new_policy['Statement'] = []

new_policy['Statement'].append({})
new_policy['Statement'][0]['Sid'] = 'Statement1'
new_policy['Statement'][0]['Effect'] = 'Allow'
new_policy['Statement'][0]['Action'] = ['EC2:*']
new_policy['Statement'][0]['Resource'] = ['*']

new_policy['Statement'].append({})
new_policy['Statement'][1]['Sid'] = 'Statement2'
new_policy['Statement'][1]['Effect'] = 'Allow'
new_policy['Statement'][1]['Action'] = ['S3:*']
new_policy['Statement'][1]['Resource'] = ['*']

print('Creating New IAM Policy Version of Policy ' + policy_arn + ' with the follwing statements: \n\r')
print(json.dumps(new_policy) + '\n\r')

try:
    # Get default version of policy
    create_policy_version_response = iam.create_policy_version(
        PolicyArn=policy_arn,
        PolicyDocument=json.dumps(new_policy),
        SetAsDefault=True
    )

    print('Policy Version Created: ' + create_policy_version_response['PolicyVersion']['VersionId'])

except Exception as e:
    print(e)
 

#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-service:[<<ADD SERVICE>>]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]

