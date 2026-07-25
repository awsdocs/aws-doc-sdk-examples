# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AWS IoT Data Plane Basics Scenario

This scenario demonstrates how to use the AWS IoT Data Plane to:
1. Create and update the classic device shadow
2. Retrieve the classic device shadow
3. Create named shadows for different aspects
4. List named shadows for the thing
5. Publish MQTT messages to a topic
6. Work with retained messages
7. Check client connection status
8. List client subscriptions
9. Send a direct message to a client
10. Update shadow to reflect configuration change
"""

import json
import logging
import time
import uuid
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError, WaiterError

from iot_data_wrapper import IoTDataPlaneWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data.IoTDataPlaneScenario]
class IoTDataPlaneScenario:
    """Demonstrates AWS IoT Data Plane operations in a smart home IoT scenario."""

    def __init__(self, iot_data_wrapper: IoTDataPlaneWrapper, cf_client=None):
        """
        :param iot_data_wrapper: An instance of IoTDataPlaneWrapper.
        :param cf_client: A Boto3 CloudFormation client.
        """
        self.iot_data_wrapper = iot_data_wrapper
        self.cf_client = cf_client if cf_client else boto3.client("cloudformation")
        self.unique_id = uuid.uuid4().hex[:8]
        self.thing_name = f"temp-sensor-{self.unique_id}"
        self.stack_name = f"iot-data-basics-{self.unique_id}"

    def _get_cfn_template(self) -> str:
        """Returns the CloudFormation template to create the IoT Thing and Policy."""
        template = {
            "AWSTemplateFormatVersion": "2010-09-09",
            "Description": "IoT Data Plane Basics Scenario - Creates IoT Thing and Policy",
            "Resources": {
                "IoTThing": {
                    "Type": "AWS::IoT::Thing",
                    "Properties": {"ThingName": self.thing_name},
                },
                "IoTPolicy": {
                    "Type": "AWS::IoT::Policy",
                    "Properties": {
                        "PolicyName": f"temp-sensor-policy-{self.unique_id}",
                        "PolicyDocument": {
                            "Version": "2012-10-17",
                            "Statement": [
                                {
                                    "Effect": "Allow",
                                    "Action": "iot:*",
                                    "Resource": "*",
                                }
                            ],
                        },
                    },
                },
            },
            "Outputs": {
                "ThingName": {
                    "Value": {"Ref": "IoTThing"},
                    "Description": "The name of the IoT Thing created",
                }
            },
        }
        return json.dumps(template)

    def setup(self):
        """Deploy CloudFormation stack to create IoT resources."""
        print("\n--- Setup: Deploying CloudFormation Stack ---")
        print(f"  Stack Name: {self.stack_name}")
        print(f"  Thing Name: {self.thing_name}")

        self.cf_client.create_stack(
            StackName=self.stack_name,
            TemplateBody=self._get_cfn_template(),
        )

        print("  Waiting for stack creation to complete...")
        waiter = self.cf_client.get_waiter("stack_create_complete")
        try:
            waiter.wait(
                StackName=self.stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
        except WaiterError as err:
            logger.error("Stack creation failed: %s", err)
            raise

        print("  ✓ Stack created successfully.")

    def step_1_update_classic_shadow(self):
        """Create and update the classic device shadow."""
        print("\n--- Step 1: Create and Update the Classic Device Shadow ---")

        shadow_state = {
            "state": {
                "reported": {
                    "temperature": 22.5,
                    "humidity": 45,
                    "status": "online",
                    "firmware_version": "1.0.0",
                },
                "desired": {
                    "temperature_unit": "celsius",
                    "reporting_interval": 60,
                },
            }
        }

        result = self.iot_data_wrapper.update_thing_shadow(
            self.thing_name, shadow_state
        )
        print(f"  ✓ Classic shadow created/updated for '{self.thing_name}'.")
        print(f"  Shadow version: {result.get('version', 'N/A')}")
        print(
            "  The classic (unnamed) shadow stores the primary device state."
        )

    def step_2_get_classic_shadow(self):
        """Retrieve the classic device shadow."""
        print("\n--- Step 2: Retrieve the Classic Device Shadow ---")

        shadow = self.iot_data_wrapper.get_thing_shadow(self.thing_name)
        if shadow is not None:
            state = shadow.get("state", dict())
            reported = state.get("reported", dict())
            desired = state.get("desired", dict())
            delta = state.get("delta", dict())
            metadata = shadow.get("metadata", dict())

            print("  ✓ Shadow retrieved successfully.")
            print(f"  Reported state: {json.dumps(reported, indent=4)}")
            print(f"  Desired state: {json.dumps(desired, indent=4)}")
            if delta:
                print(f"  Delta (desired vs reported): {json.dumps(delta, indent=4)}")
            print(f"  Shadow version: {shadow.get('version', 'N/A')}")
            print(
                "  Applications use GetThingShadow to read the latest known "
                "device state even when the device is offline."
            )
        else:
            print("  Shadow not found.")

    def step_3_create_named_shadows(self):
        """Create named shadows for different aspects."""
        print("\n--- Step 3: Create Named Shadows for Different Aspects ---")

        # Connectivity shadow
        connectivity_state = {
            "state": {
                "reported": {
                    "wifi_signal_strength": -45,
                    "connection_type": "wifi",
                    "ip_address": "192.168.1.100",
                    "last_heartbeat": datetime.now(timezone.utc).isoformat(),
                }
            }
        }
        self.iot_data_wrapper.update_thing_shadow(
            self.thing_name, connectivity_state, shadow_name="connectivity"
        )
        print("  ✓ Named shadow 'connectivity' created.")

        # Configuration shadow
        configuration_state = {
            "state": {
                "reported": {
                    "sampling_rate": 5,
                    "alert_threshold_high": 35.0,
                    "alert_threshold_low": 10.0,
                },
                "desired": {
                    "sampling_rate": 10,
                    "alert_threshold_high": 30.0,
                },
            }
        }
        self.iot_data_wrapper.update_thing_shadow(
            self.thing_name, configuration_state, shadow_name="configuration"
        )
        print("  ✓ Named shadow 'configuration' created.")
        print(
            "  Named shadows allow organizing device state into logical groupings."
        )

    def step_4_list_named_shadows(self):
        """List named shadows for the thing."""
        print("\n--- Step 4: List Named Shadows for the Thing ---")

        shadows = self.iot_data_wrapper.list_named_shadows_for_thing(self.thing_name)
        print(f"  ✓ Found {len(shadows)} named shadow(s):")
        for name in shadows:
            print(f"    - {name}")
        print(
            "  ListNamedShadowsForThing helps discover all shadow aspects of a device."
        )

    def step_5_publish_messages(self):
        """Publish MQTT messages to topics."""
        print("\n--- Step 5: Publish MQTT Messages to a Topic ---")

        # Non-retained telemetry message
        telemetry_topic = f"sensors/{self.thing_name}/telemetry"
        telemetry_payload = json.dumps(
            {
                "temperature": 23.1,
                "humidity": 44,
                "timestamp": datetime.now(timezone.utc).isoformat(),
            }
        )
        self.iot_data_wrapper.publish(
            topic=telemetry_topic,
            payload=telemetry_payload,
            qos=1,
            retain=False,
        )
        print(f"  ✓ Published telemetry message to '{telemetry_topic}' (QoS=1, retain=False).")

        # Retained status message
        status_topic = f"sensors/{self.thing_name}/status"
        status_payload = json.dumps(
            {
                "status": "online",
                "last_seen": datetime.now(timezone.utc).isoformat(),
            }
        )
        self.iot_data_wrapper.publish(
            topic=status_topic,
            payload=status_payload,
            qos=1,
            retain=True,
        )
        print(f"  ✓ Published retained status message to '{status_topic}' (QoS=1, retain=True).")
        print(
            "  Retained messages persist on the broker and are sent to new subscribers."
        )

    def step_6_work_with_retained_messages(self):
        """Work with retained messages."""
        print("\n--- Step 6: Work with Retained Messages ---")

        # Allow time for retained message to be stored
        time.sleep(2)

        # List retained messages
        try:
            messages = self.iot_data_wrapper.list_retained_messages()
            print(f"  ✓ Found {len(messages)} retained message(s) in account.")
            for msg in messages:
                topic = msg.get("topic", "unknown")
                payload_size = msg.get("payloadSize", 0)
                print(f"    Topic: {topic}, Size: {payload_size} bytes")
        except ClientError:
            print("  Could not list retained messages (may be throttled).")

        # Get specific retained message
        status_topic = f"sensors/{self.thing_name}/status"
        retained = self.iot_data_wrapper.get_retained_message(status_topic)
        if retained is not None:
            print(f"  ✓ Retrieved retained message for topic '{status_topic}':")
            print(f"    Payload: {retained['payload']}")
            print(f"    QoS: {retained['qos']}")
        else:
            print(f"  No retained message found for topic '{status_topic}'.")

        print(
            "  Retained messages provide the last known value on a topic to new subscribers."
        )

    def step_7_check_connection(self):
        """Check client connection status."""
        print("\n--- Step 7: Check Client Connection Status ---")

        connection = self.iot_data_wrapper.get_connection(self.thing_name)
        if connection is not None:
            print(f"  ✓ Connection info for client '{self.thing_name}':")
            print(f"    Connected: {connection['connected']}")
            if connection["thingName"]:
                print(f"    Thing Name: {connection['thingName']}")
            if connection["connected"] and connection["connectedSince"]:
                print(f"    Connected Since: {connection['connectedSince']}")
            if not connection["connected"] and connection["disconnectedSince"]:
                print(f"    Disconnected Since: {connection['disconnectedSince']}")
                print(f"    Disconnect Reason: {connection['disconnectReason']}")
        else:
            print(
                f"  Client '{self.thing_name}' has no connection history "
                "(device has never connected via MQTT)."
            )
        print(
            "  GetConnection helps monitor device connectivity in real-time."
        )

    def step_8_list_subscriptions(self):
        """List client subscriptions."""
        print("\n--- Step 8: List Client Subscriptions ---")

        subscriptions = self.iot_data_wrapper.list_subscriptions(self.thing_name)
        if subscriptions:
            print(f"  ✓ Found {len(subscriptions)} subscription(s):")
            for sub in subscriptions:
                print(
                    f"    Topic Filter: {sub.get('topicFilter', 'N/A')}, "
                    f"QoS: {sub.get('qos', 'N/A')}"
                )
        else:
            print(
                f"  No active subscriptions for client '{self.thing_name}' "
                "(device is not connected)."
            )
        print(
            "  ListSubscriptions helps administrators understand which topics "
            "a device is listening on."
        )

    def step_9_send_direct_message(self):
        """Send a direct message to a client."""
        print("\n--- Step 9: Send a Direct Message to a Client ---")

        command_topic = f"commands/{self.thing_name}/config-update"
        command_payload = json.dumps(
            {"action": "update_interval", "value": 30}
        )

        result = self.iot_data_wrapper.send_direct_message(
            client_id=self.thing_name,
            topic=command_topic,
            payload=command_payload,
            content_type="application/json",
            confirmation=False,
        )
        if result is not None:
            print(f"  ✓ Direct message sent to client '{self.thing_name}'.")
            print(f"    Message: {result.get('message', '')}")
            print(f"    Trace ID: {result.get('traceId', '')}")
        else:
            print(
                f"  Client '{self.thing_name}' is not currently connected. "
                "Message could not be delivered directly."
            )
        print(
            "  SendDirectMessage delivers messages directly to a specific client "
            "without requiring a subscription."
        )

    def step_10_update_shadow_for_config(self):
        """Update shadow to reflect configuration change."""
        print("\n--- Step 10: Update Shadow to Reflect Configuration Change ---")

        # Update desired state
        desired_update = {
            "state": {
                "desired": {
                    "reporting_interval": 30,
                }
            }
        }
        self.iot_data_wrapper.update_thing_shadow(self.thing_name, desired_update)
        print("  ✓ Updated classic shadow with new desired reporting_interval=30.")

        # Retrieve updated shadow
        shadow = self.iot_data_wrapper.get_thing_shadow(self.thing_name)
        if shadow is not None:
            state = shadow.get("state", dict())
            delta = state.get("delta", dict())
            if delta:
                print(f"  Delta (desired vs reported): {json.dumps(delta, indent=4)}")
            print(f"  Shadow version: {shadow.get('version', 'N/A')}")
        print(
            "  The shadow delta mechanism drives device configuration synchronization."
        )

    def cleanup(self):
        """Clean up all resources created during the scenario."""
        print("\n--- Cleanup ---")

        # Delete named shadows
        for shadow_name in ["connectivity", "configuration"]:
            deleted = self.iot_data_wrapper.delete_thing_shadow(
                self.thing_name, shadow_name=shadow_name
            )
            status = "deleted" if deleted else "not found"
            print(f"  Named shadow '{shadow_name}': {status}")

        # Delete classic shadow
        deleted = self.iot_data_wrapper.delete_thing_shadow(self.thing_name)
        status = "deleted" if deleted else "not found"
        print(f"  Classic shadow: {status}")

        # Clear retained message by publishing empty payload with retain=True
        status_topic = f"sensors/{self.thing_name}/status"
        try:
            self.iot_data_wrapper.publish(
                topic=status_topic, payload="", qos=1, retain=True
            )
            print(f"  Retained message on '{status_topic}': cleared")
        except ClientError:
            print(f"  Retained message on '{status_topic}': could not clear")

        # Delete CloudFormation stack
        try:
            self.cf_client.delete_stack(StackName=self.stack_name)
            print(f"  CloudFormation stack '{self.stack_name}': deletion initiated")
            waiter = self.cf_client.get_waiter("stack_delete_complete")
            waiter.wait(
                StackName=self.stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
            print(f"  CloudFormation stack '{self.stack_name}': deleted")
        except (ClientError, WaiterError) as err:
            logger.warning("Stack cleanup issue: %s", err)
            print(f"  CloudFormation stack cleanup warning: {err}")

        print("  ✓ Cleanup complete.")

    def run_scenario(self):
        """Runs the full IoT Data Plane basics scenario."""
        print("=" * 60)
        print("  AWS IoT Data Plane Basics Scenario")
        print("=" * 60)
        print(
            "  This scenario demonstrates managing device shadows, publishing"
            " MQTT messages, working with retained messages, checking"
            " connections, and sending direct messages."
        )

        try:
            self.setup()
            self.step_1_update_classic_shadow()
            self.step_2_get_classic_shadow()
            self.step_3_create_named_shadows()
            self.step_4_list_named_shadows()
            self.step_5_publish_messages()
            self.step_6_work_with_retained_messages()
            self.step_7_check_connection()
            self.step_8_list_subscriptions()
            self.step_9_send_direct_message()
            self.step_10_update_shadow_for_config()
        finally:
            self.cleanup()

        print("\n" + "=" * 60)
        print("  Thanks for watching!")
        print("=" * 60)


# snippet-end:[python.example_code.iot-data.IoTDataPlaneScenario]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    wrapper = IoTDataPlaneWrapper.from_client()
    scenario = IoTDataPlaneScenario(wrapper)
    scenario.run_scenario()
