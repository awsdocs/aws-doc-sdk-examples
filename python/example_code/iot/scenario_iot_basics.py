# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS IoT to manage things, certificates, and topic rules.
This scenario demonstrates:
1. Creating an IoT thing
2. Generating and attaching a certificate
3. Updating the thing shadow
4. Getting the IoT endpoint
5. Creating a topic rule
6. Searching for things
7. Cleaning up resources
"""

import json
import logging
import sys
import time

import boto3
from botocore.exceptions import ClientError

from iot_wrapper import IoTWrapper

sys.path.append("../..")
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot.Scenario_GettingStarted]
class IoTScenario:
    """Runs an interactive scenario that shows how to use AWS IoT."""

    def __init__(self, iot_wrapper, iot_data_client, cfn_client):
        """
        :param iot_wrapper: An instance of the IoTWrapper class.
        :param iot_data_client: A Boto3 IoT Data Plane client.
        :param cfn_client: A Boto3 CloudFormation client.
        """
        self.iot_wrapper = iot_wrapper
        self.iot_data_client = iot_data_client
        self.cfn_client = cfn_client
        self.thing_name = None
        self.certificate_arn = None
        self.certificate_id = None
        self.rule_name = None
        self.stack_name = "IoTBasicsStack"

    def _deploy_stack(self):
        """Deploy CloudFormation stack and return outputs."""
        with open("../../../scenarios/basics/iot/iot_usecase/resources/cfn_template.yaml", "r") as f:
            template_body = f.read()
        
        try:
            self.cfn_client.create_stack(
                StackName=self.stack_name,
                TemplateBody=template_body,
                Capabilities=["CAPABILITY_NAMED_IAM"]
            )
            
            waiter = self.cfn_client.get_waiter("stack_create_complete")
            waiter.wait(StackName=self.stack_name)
            
            response = self.cfn_client.describe_stacks(StackName=self.stack_name)
            outputs = {output["OutputKey"]: output["OutputValue"] 
                      for output in response["Stacks"][0]["Outputs"]}
            return outputs["SNSTopicArn"], outputs["RoleArn"]
        except ClientError as err:
            if err.response["Error"]["Code"] == "AlreadyExistsException":
                response = self.cfn_client.describe_stacks(StackName=self.stack_name)
                outputs = {output["OutputKey"]: output["OutputValue"] 
                          for output in response["Stacks"][0]["Outputs"]}
                return outputs["SNSTopicArn"], outputs["RoleArn"]
            raise

    def _cleanup_stack(self):
        """Delete CloudFormation stack."""
        try:
            self.cfn_client.delete_stack(StackName=self.stack_name)
            waiter = self.cfn_client.get_waiter("stack_delete_complete")
            waiter.wait(StackName=self.stack_name)
            print("CloudFormation stack deleted successfully.")
        except ClientError as err:
            logger.error(f"Failed to delete stack: {err}")

    def _wait(self, msg):
        """Wait for user to press Enter."""
        input(f"\n{msg} Press Enter to continue...")

    def run_scenario(self, thing_name, rule_name):
        """
        Runs the IoT basics scenario.

        :param thing_name: The name of the thing to create.
        :param rule_name: The name of the topic rule to create.
        """
        print("-" * 88)
        print("Welcome to the AWS IoT basics scenario!")
        print("-" * 88)
        print(
            "This scenario demonstrates how to interact with AWS IoT using the AWS SDK for Python (Boto3).\n"
            "AWS IoT provides secure, bi-directional communication between Internet-connected devices\n"
            "and the AWS cloud. You can manage device connections, process device data, and build IoT applications.\n"
        )

        self.thing_name = thing_name
        self.rule_name = rule_name

        try:
            print("\nDeploying CloudFormation stack...")
            sns_topic_arn, role_arn = self._deploy_stack()
            print(f"Stack deployed. SNS Topic: {sns_topic_arn}")

            self._wait("Next, we'll create an AWS IoT thing.")
            print("\n" + "-" * 88)
            print("1. Create an AWS IoT thing")
            print("-" * 88)
            response = self.iot_wrapper.create_thing(thing_name)
            print(f"Created thing: {response['thingName']}")
            print(f"Thing ARN: {response['thingArn']}")

            self._wait("Next, we'll generate a device certificate.")
            print("\n" + "-" * 88)
            print("2. Generate a device certificate")
            print("-" * 88)
            cert_response = self.iot_wrapper.create_keys_and_certificate()
            self.certificate_arn = cert_response["certificateArn"]
            self.certificate_id = cert_response["certificateId"]
            print(f"Created certificate: {self.certificate_id}")

            self._wait("Next, we'll attach the certificate to the thing.")
            print("\n" + "-" * 88)
            print("3. Attach the certificate to the thing")
            print("-" * 88)
            self.iot_wrapper.attach_thing_principal(thing_name, self.certificate_arn)
            print(f"Attached certificate to thing: {thing_name}")

            self._wait("Next, we'll update the thing shadow.")
            print("\n" + "-" * 88)
            print("4. Update the thing shadow")
            print("-" * 88)
            shadow_state = {"state": {"reported": {"temperature": 25, "humidity": 50}}}
            self.iot_data_client.update_thing_shadow(
                thingName=thing_name, payload=json.dumps(shadow_state)
            )
            print(f"Updated shadow for thing: {thing_name}")

            self._wait("Next, we'll get the thing shadow.")
            print("\n" + "-" * 88)
            print("5. Get the thing shadow")
            print("-" * 88)
            shadow_response = self.iot_data_client.get_thing_shadow(thingName=thing_name)
            shadow = json.loads(shadow_response["payload"].read())
            print(f"Shadow state: {json.dumps(shadow['state'], indent=2)}")

            self._wait("Next, we'll get the AWS IoT endpoint.")
            print("\n" + "-" * 88)
            print("6. Get the AWS IoT endpoint")
            print("-" * 88)
            endpoint = self.iot_wrapper.describe_endpoint()
            print(f"IoT endpoint: {endpoint}")

            self._wait("Next, we'll list certificates.")
            print("\n" + "-" * 88)
            print("7. List certificates")
            print("-" * 88)
            certificates = self.iot_wrapper.list_certificates()
            print(f"Found {len(certificates)} certificate(s)")
            for cert in certificates:
                print(f"  Certificate ID: {cert['certificateId']}")
                print(f"  Certificate ARN: {cert['certificateArn']}")
                print(f"  Status: {cert['status']}")

            self._wait("Next, we'll create a topic rule.")
            print("\n" + "-" * 88)
            print("8. Create a topic rule")
            print("-" * 88)
            self.iot_wrapper.create_topic_rule(
                rule_name, f"device/{thing_name}/data", sns_topic_arn, role_arn
            )
            print(f"Created topic rule: {rule_name}")

            self._wait("Next, we'll list topic rules.")
            print("\n" + "-" * 88)
            print("9. List topic rules")
            print("-" * 88)
            rules = self.iot_wrapper.list_topic_rules()
            print(f"Found {len(rules)} topic rule(s)")

            self._wait("Next, we'll configure thing indexing.")
            print("\n" + "-" * 88)
            print("10. Configure thing indexing")
            print("-" * 88)
            self.iot_wrapper.update_indexing_configuration()
            print("Enabled thing indexing")
            print("Waiting for indexing to be ready...")
            time.sleep(10)

            self._wait("Next, we'll search for things.")
            print("\n" + "-" * 88)
            print("11. Search for things")
            print("-" * 88)
            try:
                things = self.iot_wrapper.search_index(f"thingName:{thing_name}")
                if things:
                    print(f"Found {len(things)} thing(s) matching the query")
                    for thing in things:
                        print(f"  Thing name: {thing.get('thingName', 'N/A')}")
                        print(f"  Thing type: {thing.get('thingTypeName', 'N/A')}")
                        print(f"  Thing ARN: {thing.get('thingArn', 'N/A')}")
                else:
                    print("No things found. Indexing may take a few minutes.")
            except ClientError as err:
                if err.response["Error"]["Code"] in [
                    "IndexNotReadyException",
                    "InvalidRequestException",
                ]:
                    print("Search index not ready yet. This is expected.")
                else:
                    raise

        except ClientError as err:
            logger.error(
                "Scenario failed: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        finally:
            self._cleanup()

    def _cleanup(self):
        """Cleans up resources created during the scenario."""
        if not self.thing_name:
            return

        print("\n" + "-" * 88)
        print("Cleanup")
        print("-" * 88)

        if q.ask("Do you want to delete the resources? (y/n) ", q.is_yesno):
            try:
                if self.certificate_arn:
                    print(f"Detaching certificate from thing: {self.thing_name}")
                    self.iot_wrapper.detach_thing_principal(
                        self.thing_name, self.certificate_arn
                    )

                if self.certificate_id:
                    print(f"Deleting certificate: {self.certificate_id}")
                    self.iot_wrapper.delete_certificate(self.certificate_id)

                if self.thing_name:
                    print(f"Deleting thing: {self.thing_name}")
                    self.iot_wrapper.delete_thing(self.thing_name)

                if self.rule_name:
                    print(f"Deleting topic rule: {self.rule_name}")
                    self.iot_wrapper.delete_topic_rule(self.rule_name)

                self._cleanup_stack()
                print("Resources deleted successfully.")
            except ClientError as err:
                logger.error(
                    "Cleanup failed: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
        else:
            print("Resources will remain in your account.")

        print("\n" + "-" * 88)
        print("Thanks for using AWS IoT!")
        print("-" * 88)


# snippet-end:[python.example_code.iot.Scenario_GettingStarted]


if __name__ == "__main__":
    try:
        iot_client = boto3.client("iot")
        iot_data_client = boto3.client("iot-data")
        cfn_client = boto3.client("cloudformation")
        wrapper = IoTWrapper(iot_client)
        scenario = IoTScenario(wrapper, iot_data_client, cfn_client)

        thing_name = q.ask("Enter a name for the IoT thing: ", q.non_empty)
        rule_name = q.ask("Enter a name for the topic rule: ", q.non_empty)

        scenario.run_scenario(thing_name, rule_name)
    except Exception as e:
        logger.exception("An error occurred during the scenario.")
        print(f"An error occurred: {e}")
