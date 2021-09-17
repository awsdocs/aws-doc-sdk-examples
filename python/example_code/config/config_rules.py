# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Config to create and
manage configuration rules.
"""

import logging
from pprint import pprint

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.config-service.ConfigWrapper]
class ConfigWrapper:
    """
    Encapsulates AWS Config functions.
    """
    def __init__(self, config_client):
        """
        :param config_client: A Boto3 AWS Config client.
        """
        self.config_client = config_client
# snippet-end:[python.example_code.config-service.ConfigWrapper]

# snippet-start:[python.example_code.config-service.PutConfigRule]
    def put_config_rule(self, rule_name):
        """
        Sets a configuration rule that prohibits making Amazon S3 buckets publicly
        readable.

        :param rule_name: The name to give the rule.
        """
        try:
            self.config_client.put_config_rule(
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
            logger.info("Created configuration rule %s.", rule_name)
        except ClientError:
            logger.exception("Couldn't create configuration rule %s.", rule_name)
            raise
# snippet-end:[python.example_code.config-service.PutConfigRule]

# snippet-start:[python.example_code.config-service.DescribeConfigRules]
    def describe_config_rule(self, rule_name):
        """
        Gets data for the specified rule.

        :param rule_name: The name of the rule to retrieve.
        :return: The rule data.
        """
        try:
            response = self.config_client.describe_config_rules(
                ConfigRuleNames=[rule_name])
            rule = response['ConfigRules']
            logger.info("Got data for rule %s.", rule_name)
        except ClientError:
            logger.exception("Couldn't get data for rule %s.", rule_name)
            raise
        else:
            return rule
# snippet-end:[python.example_code.config-service.DescribeConfigRules]

# snippet-start:[python.example_code.config-service.DeleteConfigRule]
    def delete_config_rule(self, rule_name):
        """
        Delete the specified rule.

        :param rule_name: The name of the rule to delete.
        """
        try:
            self.config_client.delete_config_rule(ConfigRuleName=rule_name)
            logger.info("Deleted rule %s.", rule_name)
        except ClientError:
            logger.exception("Couldn't delete rule %s.", rule_name)
            raise
# snippet-end:[python.example_code.config-service.DeleteConfigRule]


def usage_demo():
    print('-'*88)
    print("Welcome to the AWS Config demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    config = ConfigWrapper(boto3.client('config'))
    rule_name = 'DemoS3BucketRule'
    print(f"Creating AWS Config rule '{rule_name}'...")
    config.put_config_rule(rule_name)
    print(f"Describing AWS Config rule '{rule_name}'...")
    rule = config.describe_config_rule(rule_name)
    pprint(rule)
    print(f"Deleting AWS Config rule '{rule_name}'...")
    config.delete_config_rule(rule_name)

    print("Thanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    usage_demo()
