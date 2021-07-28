# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.config.config_rule_imports]
from pprint import pprint
import boto3
from botocore.exceptions import ClientError
# snippet-end:[python.example_code.config.config_rule_imports]


# snippet-start:[python.example_code.config.put_config_rule]
def put_config_rule(config_client, rule_name):
    try:
        response = config_client.put_config_rule(
            ConfigRule={
                'ConfigRuleName': rule_name,
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
        pprint(response)
    except ClientError as e:
        print(e)
# snippet-end:[python.example_code.config.put_config_rule]


# snippet-start:[python.example_code.config.describe_config_rules]
def describe_config_rule(config_client, rule_name):
    try:
        response = config_client.describe_config_rules(ConfigRuleNames=[rule_name])
        pprint(response)
    except ClientError as e:
        print(e)
# snippet-end:[python.example_code.config.describe_config_rules]


# snippet-start:[python.example_code.config.delete_config_rule]
def delete_config_rule(config_client, rule_name):
    try:
        response = config_client.delete_config_rule(ConfigRuleName=rule_name)
        pprint(response)
    except ClientError as e:
        print(e)
# snippet-end:[python.example_code.config.delete_config_rule]


def usage_demo():
    config_client = boto3.client('config')
    rule_name = 'S3BucketRule'
    print(f"Putting AWS Config rule '{rule_name}'...")
    put_config_rule(config_client, rule_name)
    print(f"Describing AWS Config rule '{rule_name}'...")
    describe_config_rule(config_client, rule_name)
    print(f"Deleting AWS Config rule '{rule_name}'...")
    delete_config_rule(config_client, rule_name)
    print("Thanks for watching!")


if __name__ == '__main__':
    usage_demo()
