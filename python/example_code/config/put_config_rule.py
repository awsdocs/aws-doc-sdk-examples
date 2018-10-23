# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the 'License').
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import boto3
from botocore.exceptions import ClientError

config = boto3.client('config')

try:
    response = config.put_config_rule(
        ConfigRule={
            'ConfigRuleName': 'S3BucketRule',
            'Description': 'S3 Public Read Prohibited Bucket Rule',
            'Scope': {
                'ComplianceResourceTypes': [
                    'AWS::S3::Bucket',
                ],
            },
            'Source': {
                'Owner': 'AWS',
                'SourceIdentifier': 'S3_BUCKET_PUBLIC_READ_PROHIBITED',
            },
            'InputParameters': '{}',
            'ConfigRuleState': 'ACTIVE'
        }
    )
    print('\n\rResponse: ' + str(response) + '\n\r')
except ClientError as e:
    print(e)

 

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[put_config_rule.py demonstrates how to add a AWS Config rule.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Config]
#snippet-service:[config]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-06-08]
#snippet-sourceauthor:[walkerk1980]

