# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
IoT Data Plane Basics Scenario — Smart Home Temperature Monitoring System.

This scenario demonstrates a realistic IoT device shadow management and MQTT
messaging workflow for a smart home temperature sensor:

1. Create the classic (unnamed) device shadow with initial state.
2. Retrieve and display the classic shadow.
3. Create a named shadow for sensor configuration.
4. List all named shadows for the thing.
5. Retrieve and inspect the named shadow with delta.
6. Publish an MQTT message with the retain flag.
7. List retained messages.
8. Get the retained message details.
9. Update the named shadow to simulate device applying configuration.
10. Delete the named shadow.

Setup creates an IoT Thing, and cleanup removes all resources.
"""

import json
import logging
import os
import sys
import time
from datetime import datetime, timezone

import boto3
from botocore.client import BaseClient
from botocore.exceptions import BotoCoreError, ClientError

# Make the parent directory importable so the wrapper module resolves even when
# this script is run from outside the iot-data-plane/ directory.
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from iot_data_plane_wrapper import IoTDataPlaneWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data-plane.IoTDataPlaneScenario]
class IoTDataPlaneScenario:
    """Runs the IoT Data Plane Basics scenario."""

    def __init__(
        self,
        iot_data_wrapper: IoTDataPlaneWrapper,
        iot_client: BaseClient,
    ):
        """
        Initializes the scenario.

        :param iot_data_wrapper: An IoTDataPlaneWrapper instance for data plane operations.
        :param iot_client: A Boto3 IoT Control Plane client for thing management.
        """
        self.iot_data_wrapper = iot_data_wrapper
        self.iot_client = iot_client
        self.thing_name = ""
        self.thing_arn = ""
        self.topic = ""
        self.thing_created = False

    def setup(self) -> None:
        """
        Sets up the scenario by creating an IoT thing.

        Marks ``thing_created`` so cleanup only attempts to delete a thing that
        was actually created (or confirmed to exist).
        """
        timestamp = int(time.time())
        self.thing_name = f"temp-sensor-{timestamp}"
        self.topic = f"dt/sensors/{self.thing_name}/temperature"

        try:
            response = self.iot_client.create_thing(thingName=self.thing_name)
            self.thing_arn = response.get("thingArn", "")
            self.thing_created = True
            print(f"Created IoT thing: {self.thing_name} (ARN: {self.thing_arn})")
        except self.iot_client.exceptions.ResourceAlreadyExistsException:
            print(f"Thing '{self.thing_name}' already exists. Using existing thing.")
            # Describe the thing to get the ARN.
            desc = self.iot_client.describe_thing(thingName=self.thing_name)
            self.thing_arn = desc.get("thingArn", "")
            self.thing_created = True
        except (ClientError, BotoCoreError):
            # Creation failed outright — there is nothing to clean up.
            logger.exception("Failed to create IoT thing '%s'", self.thing_name)
            raise

    def step_1_create_classic_shadow(self) -> None:
        """Step 1: Create the classic (unnamed) device shadow with initial state."""
        print("\n" + "=" * 70)
        print("Step 1: Create the classic (unnamed) device shadow with initial state")
        print("=" * 70)

        shadow_state = {
            "state": {
                "reported": {
                    "temperature": 22.5,
                    "humidity": 45,
                    "battery_pct": 100,
                    "firmware_version": "1.0.0",
                    "status": "online",
                }
            }
        }

        payload = self.iot_data_wrapper.update_thing_shadow(
            thing_name=self.thing_name,
            shadow_state=shadow_state,
        )
        print("Created classic device shadow with initial sensor readings.")
        print(f"Response payload:\n{json.dumps(payload, indent=2)}")

    def step_2_get_classic_shadow(self) -> None:
        """Step 2: Retrieve and display the classic shadow."""
        print("\n" + "=" * 70)
        print("Step 2: Retrieve and display the classic shadow")
        print("=" * 70)

        shadow = self.iot_data_wrapper.get_thing_shadow(
            thing_name=self.thing_name,
        )
        print(f"Full shadow document:\n{json.dumps(shadow, indent=2)}")

        reported = shadow.get("state", dict()).get("reported", dict())
        temperature = reported.get("temperature", "N/A")
        humidity = reported.get("humidity", "N/A")
        version = shadow.get("version", "N/A")
        print(
            f"\nClassic shadow retrieved. Current temperature: {temperature}C, "
            f"humidity: {humidity}%"
        )
        print(f"Shadow version: {version}")

    def step_3_create_named_shadow(self) -> None:
        """Step 3: Create a named shadow for sensor configuration."""
        print("\n" + "=" * 70)
        print("Step 3: Create a named shadow for sensor configuration")
        print("=" * 70)

        shadow_state = {
            "state": {
                "desired": {
                    "reporting_interval_sec": 30,
                    "temperature_unit": "celsius",
                    "alert_threshold_high": 35.0,
                    "alert_threshold_low": 5.0,
                },
                "reported": {
                    "reporting_interval_sec": 60,
                    "temperature_unit": "celsius",
                    "alert_threshold_high": 40.0,
                    "alert_threshold_low": 0.0,
                },
            }
        }

        payload = self.iot_data_wrapper.update_thing_shadow(
            thing_name=self.thing_name,
            shadow_state=shadow_state,
            shadow_name="sensor-config",
        )
        print(
            "Created named shadow 'sensor-config' with desired and reported configuration."
        )
        print(f"Response payload:\n{json.dumps(payload, indent=2)}")
        print(
            "\nNote: Because 'desired' and 'reported' differ, the shadow response "
            "includes a 'delta' section showing the differences."
        )

    def step_4_list_named_shadows(self) -> None:
        """Step 4: List all named shadows for the thing."""
        print("\n" + "=" * 70)
        print("Step 4: List all named shadows for the thing")
        print("=" * 70)

        shadow_names = self.iot_data_wrapper.list_named_shadows_for_thing(
            thing_name=self.thing_name,
            page_size=25,
        )
        count = len(shadow_names)
        print(
            f"Found {count} named shadow(s) for thing '{self.thing_name}': "
            f"{shadow_names}"
        )

        # Per the specification, also surface the response `timestamp` (the date
        # and time the response was generated, in epoch seconds).
        response = self.iot_data_wrapper.iot_data_client.list_named_shadows_for_thing(
            thingName=self.thing_name,
            pageSize=25,
        )
        print(f"Response timestamp (epoch seconds): {response.get('timestamp')}")

    def step_5_get_named_shadow_with_delta(self) -> None:
        """Step 5: Retrieve and inspect the named shadow with delta."""
        print("\n" + "=" * 70)
        print("Step 5: Retrieve and inspect the named shadow with delta")
        print("=" * 70)

        shadow = self.iot_data_wrapper.get_thing_shadow(
            thing_name=self.thing_name,
            shadow_name="sensor-config",
        )
        print(f"Full shadow document:\n{json.dumps(shadow, indent=2)}")

        delta = shadow.get("state", dict()).get("delta", dict())
        delta_count = len(delta)
        print(
            f"\nThe delta shows {delta_count} configuration changes pending "
            "for the device."
        )

    def step_6_publish_mqtt_message(self) -> None:
        """Step 6: Publish an MQTT message with retain flag."""
        print("\n" + "=" * 70)
        print("Step 6: Publish an MQTT message with retain flag")
        print("=" * 70)

        message = {
            "thing_name": self.thing_name,
            "temperature": 23.1,
            "humidity": 44,
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "unit": "celsius",
        }

        self.iot_data_wrapper.publish(
            topic=self.topic,
            payload=json.dumps(message).encode("utf-8"),
            qos=1,
            retain=True,
        )
        print(f"Published retained MQTT message to topic '{self.topic}'")
        print(f"Message payload:\n{json.dumps(message, indent=2)}")

    def step_7_list_retained_messages(self) -> None:
        """Step 7: List retained messages."""
        print("\n" + "=" * 70)
        print("Step 7: List retained messages")
        print("=" * 70)

        # Allow a brief delay for the retained message to propagate.
        print("Waiting a few seconds for the retained message to be stored...")
        time.sleep(3)

        retained_topics = self.iot_data_wrapper.list_retained_messages(
            max_results=25,
        )

        count = len(retained_topics)
        print(f"Found {count} retained message(s). Looking for our sensor topic...")

        found = False
        for topic_info in retained_topics:
            topic_name = topic_info.get("topic", "")
            payload_size = topic_info.get("payloadSize", 0)
            qos = topic_info.get("qos", 0)
            last_modified = topic_info.get("lastModifiedTime", 0)
            print(
                f"  Topic: {topic_name}, PayloadSize: {payload_size} bytes, "
                f"QoS: {qos}, LastModified: {last_modified}"
            )
            if topic_name == self.topic:
                found = True
                print("  ^ This is our sensor topic!")

        if not found:
            print(
                "  Our sensor topic was not found in the retained messages list. "
                "It may take a moment to appear."
            )

    def step_8_get_retained_message(self) -> None:
        """Step 8: Get the retained message details."""
        print("\n" + "=" * 70)
        print("Step 8: Get the retained message details")
        print("=" * 70)

        result = self.iot_data_wrapper.get_retained_message(topic=self.topic)
        print(f"Retrieved retained message payload: {result['payload']}")
        print(f"  Topic: {result['topic']}")
        print(f"  QoS: {result['qos']}")
        print(f"  LastModifiedTime: {result['lastModifiedTime']}")

        # Parse and display the JSON content.
        try:
            parsed = json.loads(result["payload"])
            print(f"  Decoded JSON content:\n{json.dumps(parsed, indent=2)}")
        except json.JSONDecodeError:
            print(f"  Raw payload: {result['payload']}")

    def step_9_update_named_shadow_apply_config(self) -> None:
        """Step 9: Update the named shadow to simulate device applying config."""
        print("\n" + "=" * 70)
        print("Step 9: Update named shadow — simulate device applying configuration")
        print("=" * 70)

        shadow_state = {
            "state": {
                "reported": {
                    "reporting_interval_sec": 30,
                    "temperature_unit": "celsius",
                    "alert_threshold_high": 35.0,
                    "alert_threshold_low": 5.0,
                },
                "desired": None,
            }
        }

        payload = self.iot_data_wrapper.update_thing_shadow(
            thing_name=self.thing_name,
            shadow_state=shadow_state,
            shadow_name="sensor-config",
        )
        print("Device has applied configuration changes. Delta cleared.")
        print(f"Response payload:\n{json.dumps(payload, indent=2)}")

        # Verify the delta is empty.
        shadow = self.iot_data_wrapper.get_thing_shadow(
            thing_name=self.thing_name,
            shadow_name="sensor-config",
        )
        delta = shadow.get("state", dict()).get("delta", None)
        if delta is None:
            print("\nVerified: delta is now empty — all configuration changes applied.")
        else:
            print(f"\nDelta still present: {json.dumps(delta, indent=2)}")

    def step_10_delete_named_shadow(self) -> None:
        """Step 10: Delete the named shadow."""
        print("\n" + "=" * 70)
        print("Step 10: Delete the named shadow")
        print("=" * 70)

        payload = self.iot_data_wrapper.delete_thing_shadow(
            thing_name=self.thing_name,
            shadow_name="sensor-config",
        )
        print(
            f"Deleted named shadow 'sensor-config'. "
            f"Returned state: {json.dumps(payload, indent=2)}"
        )

    def cleanup(self) -> None:
        """
        Cleans up all resources created during the scenario.

        Each deletion is guarded independently so that a failure in one step
        never prevents the remaining resources from being cleaned up. Both
        ``ClientError`` and connection-level ``BotoCoreError`` exceptions are
        handled, and the method reports whether every resource was removed.
        """
        print("\n" + "=" * 70)
        print("Cleanup")
        print("=" * 70)

        if not self.thing_name:
            print("Nothing to clean up — setup did not run.")
            return

        all_clean = True

        # 1. Delete the retained message by publishing an empty payload with retain=True.
        try:
            self.iot_data_wrapper.publish(
                topic=self.topic,
                payload=b"",
                qos=1,
                retain=True,
            )
            print(f"Deleted retained message for topic '{self.topic}'")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                print("No retained message to delete — continuing.")
            else:
                all_clean = False
                logger.error("Failed to delete retained message: %s", err)
        except BotoCoreError as err:
            all_clean = False
            logger.error("Failed to delete retained message: %s", err)

        # 2. Delete the classic (unnamed) shadow.
        try:
            self.iot_data_wrapper.delete_thing_shadow(
                thing_name=self.thing_name,
            )
            print("Deleted classic device shadow.")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                print("Classic shadow already deleted — continuing.")
            else:
                all_clean = False
                logger.error("Failed to delete classic shadow: %s", err)
        except BotoCoreError as err:
            all_clean = False
            logger.error("Failed to delete classic shadow: %s", err)

        # 3. Delete the named shadow (if not already deleted in Step 10).
        try:
            self.iot_data_wrapper.delete_thing_shadow(
                thing_name=self.thing_name,
                shadow_name="sensor-config",
            )
            print("Deleted named shadow 'sensor-config'.")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                print(
                    "Named shadow 'sensor-config' already deleted in Step 10 — continuing."
                )
            else:
                all_clean = False
                logger.error("Failed to delete named shadow: %s", err)
        except BotoCoreError as err:
            all_clean = False
            logger.error("Failed to delete named shadow: %s", err)

        # 4. Delete the IoT thing (only if one was created during setup).
        if self.thing_created:
            try:
                self.iot_client.delete_thing(thingName=self.thing_name)
                print(f"Deleted IoT thing: {self.thing_name}")
            except ClientError as err:
                if err.response["Error"]["Code"] == "ResourceNotFoundException":
                    print(f"Thing '{self.thing_name}' already deleted — continuing.")
                else:
                    all_clean = False
                    logger.error("Failed to delete thing: %s", err)
            except BotoCoreError as err:
                all_clean = False
                logger.error("Failed to delete thing: %s", err)

        if all_clean:
            print("All resources cleaned up successfully.")
        else:
            print(
                "Cleanup finished with errors — some resources may remain. "
                "Review the logs above and remove any leftover resources manually."
            )

    def run(self) -> None:
        """
        Runs the full IoT Data Plane Basics scenario.
        """
        print("=" * 70)
        print("Welcome to the IoT Data Plane Basics Scenario")
        print("Smart Home Temperature Monitoring System")
        print("=" * 70)

        self.setup()
        try:
            self.step_1_create_classic_shadow()
            self.step_2_get_classic_shadow()
            self.step_3_create_named_shadow()
            self.step_4_list_named_shadows()
            self.step_5_get_named_shadow_with_delta()
            self.step_6_publish_mqtt_message()
            self.step_7_list_retained_messages()
            self.step_8_get_retained_message()
            self.step_9_update_named_shadow_apply_config()
            self.step_10_delete_named_shadow()
        finally:
            self.cleanup()

        print("\n" + "=" * 70)
        print("IoT Data Plane Basics Scenario completed successfully!")
        print("=" * 70)


# snippet-end:[python.example_code.iot-data-plane.IoTDataPlaneScenario]


def main() -> None:
    """Entry point for running the scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    # Obtain the IoT Data-ATS endpoint.
    iot_client = boto3.client("iot")
    endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
    endpoint_url = endpoint_response["endpointAddress"]
    print(f"IoT Data-ATS endpoint: {endpoint_url}")

    # Create the wrapper with the custom endpoint.
    wrapper = IoTDataPlaneWrapper.from_client(endpoint_url=endpoint_url)

    scenario = IoTDataPlaneScenario(
        iot_data_wrapper=wrapper,
        iot_client=iot_client,
    )
    scenario.run()


if __name__ == "__main__":
    main()
