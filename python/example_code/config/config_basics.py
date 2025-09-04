# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Config.
This scenario demonstrates how to:
- Set up a configuration recorder to track AWS resource configurations
- Create a delivery channel to specify where Config sends configuration snapshots
- Start the configuration recorder to begin monitoring resources
- Monitor configuration recorder status and settings
- Discover AWS resources in the account
- Retrieve configuration history for specific resources
- Clean up resources when done

This example requires an S3 bucket and IAM role with appropriate permissions.
"""

import logging
import time
import boto3
from botocore.exceptions import ClientError

from config_rules import ConfigWrapper
import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..'))
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.config.Scenario_ConfigBasics]
class ConfigBasicsScenario:
    """
    Runs an interactive scenario that shows how to get started with AWS Config.
    """

    def __init__(self, config_wrapper, s3_resource, iam_resource):
        """
        :param config_wrapper: An object that wraps AWS Config operations.
        :param s3_resource: A Boto3 S3 resource.
        :param iam_resource: A Boto3 IAM resource.
        """
        self.config_wrapper = config_wrapper
        self.s3_resource = s3_resource
        self.iam_resource = iam_resource
        self.recorder_name = None
        self.channel_name = None
        self.bucket_name = None
        self.role_arn = None

    def run_scenario(self):
        """
        Runs the scenario.
        """
        print("-" * 88)
        print("Welcome to the AWS Config basics scenario!")
        print("-" * 88)

        print(
            "AWS Config provides a detailed view of the resources associated with your AWS account, "
            "including how they are configured, how they are related to one another, and how the "
            "configurations and their relationships have changed over time."
        )
        print()

        # Setup phase
        if not self._setup_resources():
            return

        try:
            # Configuration monitoring phase
            self._demonstrate_configuration_monitoring()

            # Resource discovery phase
            self._demonstrate_resource_discovery()

            # Configuration history phase
            self._demonstrate_configuration_history()

        finally:
            # Cleanup phase
            self._cleanup_resources()

        print("Thanks for watching!")
        print("-" * 88)

    def _setup_resources(self):
        """
        Sets up the necessary resources for the scenario.
        """
        print("\n" + "-" * 60)
        print("Setup")
        print("-" * 60)

        # Get S3 bucket for delivery channel
        self.bucket_name = q.ask(
            "Enter the name of an S3 bucket for Config to deliver configuration snapshots "
            "(the bucket must exist and have appropriate permissions): ",
            q.non_empty
        )

        # Verify bucket exists
        try:
            self.s3_resource.meta.client.head_bucket(Bucket=self.bucket_name)
            print(f"✓ S3 bucket '{self.bucket_name}' found.")
        except ClientError as err:
            if err.response['Error']['Code'] == '404':
                print(f"✗ S3 bucket '{self.bucket_name}' not found.")
                return False
            else:
                print(f"✗ Error accessing S3 bucket: {err}")
                return False

        # Get IAM role ARN
        self.role_arn = q.ask(
            "Enter the ARN of an IAM role that grants AWS Config permissions to access your resources "
            "(e.g., arn:aws:iam::123456789012:role/config-role): ",
            q.non_empty
        )

        # Verify role exists
        try:
            role_name = self.role_arn.split('/')[-1]
            self.iam_resource.Role(role_name).load()
            print(f"✓ IAM role found.")
        except ClientError as err:
            print(f"✗ Error accessing IAM role: {err}")
            return False

        # Create configuration recorder
        self.recorder_name = "demo-config-recorder"
        print(f"\nCreating configuration recorder '{self.recorder_name}'...")
        try:
            self.config_wrapper.put_configuration_recorder(
                self.recorder_name, 
                self.role_arn
            )
            print("✓ Configuration recorder created successfully.")
        except ClientError as err:
            if 'MaxNumberOfConfigurationRecordersExceededException' in str(err):
                print("✗ Maximum number of configuration recorders exceeded.")
                print("You can have only one configuration recorder per region.")
                return False
            else:
                print(f"✗ Error creating configuration recorder: {err}")
                return False

        # Create delivery channel
        self.channel_name = "demo-delivery-channel"
        print(f"\nCreating delivery channel '{self.channel_name}'...")
        try:
            self.config_wrapper.put_delivery_channel(
                self.channel_name,
                self.bucket_name,
                "config-snapshots/"
            )
            print("✓ Delivery channel created successfully.")
        except ClientError as err:
            print(f"✗ Error creating delivery channel: {err}")
            return False

        # Start configuration recorder
        print(f"\nStarting configuration recorder '{self.recorder_name}'...")
        try:
            self.config_wrapper.start_configuration_recorder(self.recorder_name)
            print("✓ Configuration recorder started successfully.")
            print("AWS Config is now monitoring your resources!")
        except ClientError as err:
            print(f"✗ Error starting configuration recorder: {err}")
            return False

        return True

    def _demonstrate_configuration_monitoring(self):
        """
        Demonstrates configuration monitoring capabilities.
        """
        print("\n" + "-" * 60)
        print("Configuration Monitoring")
        print("-" * 60)

        # Show recorder status
        print("Checking configuration recorder status...")
        try:
            statuses = self.config_wrapper.describe_configuration_recorder_status([self.recorder_name])
            if statuses:
                status = statuses[0]
                print(f"Recorder: {status['name']}")
                print(f"Recording: {status['recording']}")
                print(f"Last Status: {status.get('lastStatus', 'N/A')}")
                if 'lastStartTime' in status:
                    print(f"Last Started: {status['lastStartTime']}")
        except ClientError as err:
            print(f"Error getting recorder status: {err}")

        # Show recorder configuration
        print("\nConfiguration recorder settings:")
        try:
            recorders = self.config_wrapper.describe_configuration_recorders([self.recorder_name])
            if recorders:
                recorder = recorders[0]
                recording_group = recorder.get('recordingGroup', {})
                print(f"Recording all supported resources: {recording_group.get('allSupported', False)}")
                print(f"Including global resources: {recording_group.get('includeGlobalResourceTypes', False)}")
                
                if not recording_group.get('allSupported', True):
                    resource_types = recording_group.get('resourceTypes', [])
                    print(f"Specific resource types: {', '.join(resource_types)}")
        except ClientError as err:
            print(f"Error getting recorder configuration: {err}")

        # Wait a moment for resources to be discovered
        print("\nWaiting for AWS Config to discover resources...")
        time.sleep(10)

    def _demonstrate_resource_discovery(self):
        """
        Demonstrates resource discovery capabilities.
        """
        print("\n" + "-" * 60)
        print("Resource Discovery")
        print("-" * 60)

        # Common resource types to check
        resource_types = [
            'AWS::S3::Bucket',
            'AWS::EC2::Instance',
            'AWS::IAM::Role',
            'AWS::Lambda::Function'
        ]

        print("Discovering AWS resources in your account...")
        total_resources = 0

        for resource_type in resource_types:
            try:
                resources = self.config_wrapper.list_discovered_resources(resource_type, limit=10)
                count = len(resources)
                total_resources += count
                print(f"{resource_type}: {count} resources")
                
                # Show details for first few resources
                if resources and count > 0:
                    print(f"  Sample resources:")
                    for i, resource in enumerate(resources[:3]):
                        print(f"    {i+1}. {resource.get('resourceId', 'N/A')} ({resource.get('resourceName', 'Unnamed')})")
                    if count > 3:
                        print(f"    ... and {count - 3} more")
                print()
                
            except ClientError as err:
                print(f"Error listing {resource_type}: {err}")

        print(f"Total resources discovered: {total_resources}")

    def _demonstrate_configuration_history(self):
        """
        Demonstrates configuration history capabilities.
        """
        print("\n" + "-" * 60)
        print("Configuration History")
        print("-" * 60)

        # Try to get configuration history for the S3 bucket we're using
        print(f"Getting configuration history for S3 bucket '{self.bucket_name}'...")
        try:
            config_items = self.config_wrapper.get_resource_config_history(
                'AWS::S3::Bucket',
                self.bucket_name,
                limit=5
            )
            
            if config_items:
                print(f"Found {len(config_items)} configuration item(s):")
                for i, item in enumerate(config_items):
                    print(f"\n  Configuration {i+1}:")
                    print(f"    Configuration Item Capture Time: {item.get('configurationItemCaptureTime', 'N/A')}")
                    print(f"    Configuration State Id: {item.get('configurationStateId', 'N/A')}")
                    print(f"    Configuration Item Status: {item.get('configurationItemStatus', 'N/A')}")
                    print(f"    Resource Type: {item.get('resourceType', 'N/A')}")
                    print(f"    Resource Id: {item.get('resourceId', 'N/A')}")
                    
                    # Show some configuration details
                    config_data = item.get('configuration')
                    if config_data and isinstance(config_data, dict):
                        print(f"    Sample configuration keys: {list(config_data.keys())[:5]}")
            else:
                print("No configuration history found yet. This is normal for newly monitored resources.")
                print("Configuration history will be available after resources are modified.")
                
        except ClientError as err:
            if 'ResourceNotDiscoveredException' in str(err):
                print("Resource not yet discovered by AWS Config. This is normal for new setups.")
            else:
                print(f"Error getting configuration history: {err}")

    def _cleanup_resources(self):
        """
        Cleans up resources created during the scenario.
        """
        print("\n" + "-" * 60)
        print("Cleanup")
        print("-" * 60)

        if self.recorder_name:
            cleanup = q.ask(
                f"Do you want to stop and delete the configuration recorder '{self.recorder_name}'? "
                "This will stop monitoring your resources. (y/n): ",
                q.is_yesno
            )
            
            if cleanup:
                # Stop the configuration recorder
                print(f"Stopping configuration recorder '{self.recorder_name}'...")
                try:
                    self.config_wrapper.stop_configuration_recorder(self.recorder_name)
                    print("✓ Configuration recorder stopped.")
                except ClientError as err:
                    print(f"Error stopping configuration recorder: {err}")

                # Note: In a real scenario, you might also want to delete the recorder and delivery channel
                # However, this example leaves them for the user to manage manually
                print("\nNote: The configuration recorder and delivery channel have been left in your account.")
                print("You can manage them through the AWS Console or delete them manually if needed.")
            else:
                print("Configuration recorder left running. You can manage it through the AWS Console.")

        print("\nScenario completed!")


# snippet-end:[python.example_code.config.Scenario_ConfigBasics]


def main():
    """
    Runs the Config basics scenario.
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("-" * 88)
    print("Welcome to the AWS Config basics scenario!")
    print("-" * 88)

    config_wrapper = ConfigWrapper(boto3.client('config'))
    s3_resource = boto3.resource('s3')
    iam_resource = boto3.resource('iam')

    scenario = ConfigBasicsScenario(config_wrapper, s3_resource, iam_resource)
    scenario.run_scenario()


if __name__ == '__main__':
    main()