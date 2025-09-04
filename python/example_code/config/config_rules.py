# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Config to create and
manage configuration rules.
"""

import logging
from pprint import pprint
import time

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.config.ConfigWrapper]
class ConfigWrapper:
    """
    Encapsulates AWS Config functions.
    """

    def __init__(self, config_client):
        """
        :param config_client: A Boto3 AWS Config client.
        """
        self.config_client = config_client

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
                    "ConfigRuleName": rule_name,
                    "Description": "S3 Public Read Prohibited Bucket Rule",
                    "Scope": {
                        "ComplianceResourceTypes": [
                            "AWS::S3::Bucket",
                        ],
                    },
                    "Source": {
                        "Owner": "AWS",
                        "SourceIdentifier": "S3_BUCKET_PUBLIC_READ_PROHIBITED",
                    },
                    "InputParameters": "{}",
                    "ConfigRuleState": "ACTIVE",
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
                ConfigRuleNames=[rule_name]
            )
            rule = response["ConfigRules"]
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

    # snippet-start:[python.example_code.config.PutConfigurationRecorder]
    def put_configuration_recorder(self, recorder_name, role_arn, resource_types=None):
        """
        Creates a configuration recorder to track AWS resource configurations.

        :param recorder_name: The name of the configuration recorder.
        :param role_arn: The ARN of the IAM role that grants AWS Config permissions.
        :param resource_types: List of resource types to record. If None, records all supported types.
        """
        try:
            recording_group = {
                'allSupported': resource_types is None,
                'includeGlobalResourceTypes': True
            }
            
            if resource_types:
                recording_group['allSupported'] = False
                recording_group['resourceTypes'] = resource_types

            self.config_client.put_configuration_recorder(
                ConfigurationRecorder={
                    'name': recorder_name,
                    'roleARN': role_arn,
                    'recordingGroup': recording_group
                }
            )
            logger.info("Created configuration recorder %s.", recorder_name)
        except ClientError:
            logger.exception("Couldn't create configuration recorder %s.", recorder_name)
            raise

    # snippet-end:[python.example_code.config.PutConfigurationRecorder]

    # snippet-start:[python.example_code.config.PutDeliveryChannel]
    def put_delivery_channel(self, channel_name, bucket_name, bucket_prefix=None):
        """
        Creates a delivery channel to specify where AWS Config sends configuration snapshots.

        :param channel_name: The name of the delivery channel.
        :param bucket_name: The name of the S3 bucket where Config delivers configuration snapshots.
        :param bucket_prefix: The prefix for the S3 bucket (optional).
        """
        try:
            delivery_channel = {
                'name': channel_name,
                's3BucketName': bucket_name
            }
            
            if bucket_prefix:
                delivery_channel['s3KeyPrefix'] = bucket_prefix

            self.config_client.put_delivery_channel(DeliveryChannel=delivery_channel)
            logger.info("Created delivery channel %s.", channel_name)
        except ClientError:
            logger.exception("Couldn't create delivery channel %s.", channel_name)
            raise

    # snippet-end:[python.example_code.config.PutDeliveryChannel]

    # snippet-start:[python.example_code.config.StartConfigurationRecorder]
    def start_configuration_recorder(self, recorder_name):
        """
        Starts the configuration recorder to begin monitoring resources.

        :param recorder_name: The name of the configuration recorder to start.
        """
        try:
            self.config_client.start_configuration_recorder(
                ConfigurationRecorderName=recorder_name
            )
            logger.info("Started configuration recorder %s.", recorder_name)
        except ClientError:
            logger.exception("Couldn't start configuration recorder %s.", recorder_name)
            raise

    # snippet-end:[python.example_code.config.StartConfigurationRecorder]

    # snippet-start:[python.example_code.config.DescribeConfigurationRecorders]
    def describe_configuration_recorders(self, recorder_names=None):
        """
        Gets data for configuration recorders.

        :param recorder_names: List of recorder names to describe. If None, describes all recorders.
        :return: List of configuration recorder data.
        """
        try:
            if recorder_names:
                response = self.config_client.describe_configuration_recorders(
                    ConfigurationRecorderNames=recorder_names
                )
            else:
                response = self.config_client.describe_configuration_recorders()
            
            recorders = response.get('ConfigurationRecorders', [])
            logger.info("Got data for %d configuration recorder(s).", len(recorders))
            return recorders
        except ClientError:
            logger.exception("Couldn't get configuration recorder data.")
            raise

    # snippet-end:[python.example_code.config.DescribeConfigurationRecorders]

    # snippet-start:[python.example_code.config.DescribeConfigurationRecorderStatus]
    def describe_configuration_recorder_status(self, recorder_names=None):
        """
        Gets the status of configuration recorders.

        :param recorder_names: List of recorder names to check. If None, checks all recorders.
        :return: List of configuration recorder status data.
        """
        try:
            if recorder_names:
                response = self.config_client.describe_configuration_recorder_status(
                    ConfigurationRecorderNames=recorder_names
                )
            else:
                response = self.config_client.describe_configuration_recorder_status()
            
            statuses = response.get('ConfigurationRecordersStatus', [])
            logger.info("Got status for %d configuration recorder(s).", len(statuses))
            return statuses
        except ClientError:
            logger.exception("Couldn't get configuration recorder status.")
            raise

    # snippet-end:[python.example_code.config.DescribeConfigurationRecorderStatus]

    # snippet-start:[python.example_code.config.ListDiscoveredResources]
    def list_discovered_resources(self, resource_type, limit=20):
        """
        Lists discovered AWS resources of a specific type.

        :param resource_type: The type of resources to list (e.g., 'AWS::S3::Bucket').
        :param limit: Maximum number of resources to return.
        :return: List of discovered resources.
        """
        try:
            response = self.config_client.list_discovered_resources(
                resourceType=resource_type,
                limit=limit
            )
            resources = response.get('resourceIdentifiers', [])
            logger.info("Found %d resources of type %s.", len(resources), resource_type)
            return resources
        except ClientError:
            logger.exception("Couldn't list discovered resources of type %s.", resource_type)
            raise

    # snippet-end:[python.example_code.config.ListDiscoveredResources]

    # snippet-start:[python.example_code.config.GetResourceConfigHistory]
    def get_resource_config_history(self, resource_type, resource_id, limit=10):
        """
        Gets the configuration history for a specific resource.

        :param resource_type: The type of the resource (e.g., 'AWS::S3::Bucket').
        :param resource_id: The ID of the resource.
        :param limit: Maximum number of configuration items to return.
        :return: List of configuration items showing the resource's history.
        """
        try:
            response = self.config_client.get_resource_config_history(
                resourceType=resource_type,
                resourceId=resource_id,
                limit=limit
            )
            config_items = response.get('configurationItems', [])
            logger.info("Got %d configuration items for resource %s.", len(config_items), resource_id)
            return config_items
        except ClientError:
            logger.exception("Couldn't get configuration history for resource %s.", resource_id)
            raise

    # snippet-end:[python.example_code.config.GetResourceConfigHistory]

    # snippet-start:[python.example_code.config.StopConfigurationRecorder]
    def stop_configuration_recorder(self, recorder_name):
        """
        Stops the configuration recorder.

        :param recorder_name: The name of the configuration recorder to stop.
        """
        try:
            self.config_client.stop_configuration_recorder(
                ConfigurationRecorderName=recorder_name
            )
            logger.info("Stopped configuration recorder %s.", recorder_name)
        except ClientError:
            logger.exception("Couldn't stop configuration recorder %s.", recorder_name)
            raise

    # snippet-end:[python.example_code.config.StopConfigurationRecorder]

    # snippet-start:[python.example_code.config.DeleteConfigurationRecorder]
    def delete_configuration_recorder(self, recorder_name):
        """
        Deletes the configuration recorder.

        :param recorder_name: The name of the configuration recorder to delete.
        """
        try:
            self.config_client.delete_configuration_recorder(
                ConfigurationRecorderName=recorder_name
            )
            logger.info("Deleted configuration recorder %s.", recorder_name)
        except ClientError:
            logger.exception("Couldn't delete configuration recorder %s.", recorder_name)
            raise

    # snippet-end:[python.example_code.config.DeleteConfigurationRecorder]

    # snippet-start:[python.example_code.config.DeleteDeliveryChannel]
    def delete_delivery_channel(self, channel_name):
        """
        Deletes the delivery channel.

        :param channel_name: The name of the delivery channel to delete.
        """
        try:
            self.config_client.delete_delivery_channel(
                DeliveryChannelName=channel_name
            )
            logger.info("Deleted delivery channel %s.", channel_name)
        except ClientError:
            logger.exception("Couldn't delete delivery channel %s.", channel_name)
            raise

    # snippet-end:[python.example_code.config.DeleteDeliveryChannel]
# snippet-end:[python.example_code.config.ConfigWrapper]


def usage_demo():
    print("-" * 88)
    print("Welcome to the AWS Config demo!")
    print("-" * 88)

    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    config = ConfigWrapper(boto3.client("config"))
    rule_name = "DemoS3BucketRule"
    print(f"Creating AWS Config rule '{rule_name}'...")
    config.put_config_rule(rule_name)
    print(f"Describing AWS Config rule '{rule_name}'...")
    rule = config.describe_config_rule(rule_name)
    pprint(rule)
    print(f"Deleting AWS Config rule '{rule_name}'...")
    config.delete_config_rule(rule_name)

    print("Thanks for watching!")
    print("-" * 88)


if __name__ == "__main__":
    usage_demo()
