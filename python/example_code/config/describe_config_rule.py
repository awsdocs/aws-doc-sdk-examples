# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-start:[config.python.describe_config_rule.complete]


import boto3
from botocore.exceptions import ClientError

config = boto3.client('config')

rule_to_describe = 'S3BucketRule'

try:
    response = config.describe_config_rules(
        ConfigRuleNames=[
            rule_to_describe,
        ]
    )
    print('\n\rResponse: ' + str(response))
except ClientError as e:
    print(e)
 
 
#snippet-end:[config.python.describe_config_rule.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[describe_config_rule.py demonstrates how to retrieve information about an existing AWS Config rule.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Config]
#snippet-service:[config]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-12-26]
#snippet-sourceauthor:[walkerk1980]

