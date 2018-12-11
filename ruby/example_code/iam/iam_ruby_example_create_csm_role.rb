#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates an IAM role for client-side access to .]
#snippet-keyword:[AWS Identity and Access Management]
#snippet-keyword:[attach_role_policy method]
#snippet-keyword:[create_policy method]
#snippet-keyword:[create_role method]
#snippet-keyword:[Ruby]
#snippet-service:[iam]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-11]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
//snippet-start:[iam.ruby.create_csm_role]
require 'aws-sdk-iam' # v2: require 'aws-sdk'

role_name = 'AmazonCSM'

client = Aws::IAM::Client.new(region: 'us-west-2')

csm_policy = {
    'Version': '2012-10-17',
    'Statement': [
        {
            'Effect': 'Allow',
            'Action': [
                'sdkmetrics:*'
            ],
            'Resource': '*'
        },
        {
            'Effect': 'Allow',
            'Action': [
                'ssm:GetParameter'
            ],
            'Resource': 'arn:aws:ssm:*:*:parameter/AmazonCSM*'
        }
    ]
}

# Create policy
resp = client.create_policy({
                                policy_name: role_name,
                                policy_document: csm_policy.to_json,
                            })

policy_arn = resp.policy.arn

puts 'Created policy with ARN: ' + policy_arn

policy_doc = {
    Version: '2012-10-17',
    Statement: [
        {
            Effect: 'Allow',
            Principal: {
                Service: 'ec2.amazonaws.com'
            },
            Action: 'sts:AssumeRole'
        },]
}

# Create role
client.create_role(
    {
        role_name: role_name,
        description: 'An instance role that has permission for AWS Systems Manager and SDK Metric Monitoring.',
        assume_role_policy_document: policy_doc.to_json,
    })

puts 'Created role ' + role_name

# Attach policy to role
client.attach_role_policy(
    {
        policy_arn: policy_arn,
        role_name: role_name,
    })

puts 'Attached policy ' + role_name + 'policy to role: ' + role_name
//snippet-end:[iam.ruby.create_csm_role]
