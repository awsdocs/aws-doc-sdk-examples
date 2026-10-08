# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the IoT Data Plane Basics scenario.

These tests verify all IoT Data Plane operations against the live AWS service.
No mocking is used — all calls hit real AWS endpoints.

Usage:
    pytest test_iot_data_plane_scenario.py -v
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import time

import boto3
import pytest
from botocore.exceptions import ClientError

from iot_data_plane_wrapper import IoTDataPlaneWrapper


@pytest.fixture(scope="module")
def iot_client():
    """Creates a Boto3 IoT Control Plane client."""
    return boto3.client("iot")


@pytest.fixture(scope="module")
def iot_data_wrapper(iot_client):
    """Creates an IoTDataPlaneWrapper with the account's Data-ATS endpoint."""
    endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
    endpoint_url = endpoint_response["endpointAddress"]
    return IoTDataPlaneWrapper.from_client(endpoint_url=endpoint_url)


@pytest.fixture(scope="module")
def thing_name(iot_client):
    """
    Creates a unique IoT thing for testing and cleans it up after all tests.
    """
    name = f"test-iot-data-plane-{int(time.time())}"
    iot_client.create_thing(thingName=name)
    yield name
    # Cleanup: delete all shadows and the thing.
    try:
        iot_data_client = boto3.client("iot-data")
        try:
            iot_data_client.delete_thing_shadow(thingName=name)
        except ClientError:
            pass
        try:
            iot_data_client.delete_thing_shadow(
                thingName=name, shadowName="test-config"
            )
        except ClientError:
            pass
    finally:
        try:
            iot_client.delete_thing(thingName=name)
        except ClientError:
            pass


@pytest.mark.integ
class TestIoTDataPlaneWrapper:
    """Integration tests for IoTDataPlaneWrapper methods."""

    def test_update_and_get_classic_shadow(self, iot_data_wrapper, thing_name):
        """Tests creating and retrieving the classic (unnamed) shadow."""
        shadow_state = {
            "state": {
                "reported": {
                    "temperature": 22.5,
                    "status": "online",
                }
            }
        }

        # Update the shadow.
        result = iot_data_wrapper.update_thing_shadow(
            thing_name=thing_name,
            shadow_state=shadow_state,
        )
        assert "state" in result
        assert "reported" in result["state"]
        assert result["state"]["reported"]["temperature"] == 22.5

        # Get the shadow.
        shadow = iot_data_wrapper.get_thing_shadow(thing_name=thing_name)
        assert "state" in shadow
        assert shadow["state"]["reported"]["status"] == "online"

    def test_update_and_get_named_shadow(self, iot_data_wrapper, thing_name):
        """Tests creating and retrieving a named shadow with delta."""
        shadow_state = {
            "state": {
                "desired": {"interval": 30},
                "reported": {"interval": 60},
            }
        }

        result = iot_data_wrapper.update_thing_shadow(
            thing_name=thing_name,
            shadow_state=shadow_state,
            shadow_name="test-config",
        )
        assert "state" in result

        # Retrieve the named shadow.
        shadow = iot_data_wrapper.get_thing_shadow(
            thing_name=thing_name,
            shadow_name="test-config",
        )
        assert "state" in shadow
        # Delta should exist because desired != reported.
        assert "delta" in shadow.get("state", dict())

    def test_list_named_shadows(self, iot_data_wrapper, thing_name):
        """Tests listing named shadows for a thing."""
        shadow_names = iot_data_wrapper.list_named_shadows_for_thing(
            thing_name=thing_name,
        )
        assert isinstance(shadow_names, list)
        # We created 'test-config' in the previous test.
        assert "test-config" in shadow_names

    def test_get_thing_shadow_not_found(self, iot_data_wrapper):
        """Tests that getting a shadow for a nonexistent thing raises an error."""
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_thing_shadow(
                thing_name="nonexistent-thing-xyz-123456"
            )
        assert exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"

    def test_publish_and_retained_message(self, iot_data_wrapper, thing_name):
        """Tests publishing a retained MQTT message and retrieving it."""
        topic = f"dt/test/{thing_name}/temperature"
        message = {
            "thing_name": thing_name,
            "temperature": 25.0,
            "unit": "celsius",
        }

        try:
            # Publish with retain flag.
            iot_data_wrapper.publish(
                topic=topic,
                payload=json.dumps(message).encode("utf-8"),
                qos=1,
                retain=True,
            )

            # Allow time for the retained message to be stored.
            time.sleep(3)

            # List retained messages and check for our topic.
            retained = iot_data_wrapper.list_retained_messages(max_results=25)
            assert isinstance(retained, list)

            # Get the specific retained message.
            result = iot_data_wrapper.get_retained_message(topic=topic)
            assert result["topic"] == topic
            payload = json.loads(result["payload"])
            assert payload["temperature"] == 25.0
        finally:
            # Clean up: delete the retained message.
            try:
                iot_data_wrapper.publish(
                    topic=topic,
                    payload=b"",
                    qos=1,
                    retain=True,
                )
                # Allow time for deletion to propagate.
                time.sleep(2)
            except ClientError:
                pass

    def test_delete_named_shadow(self, iot_data_wrapper, thing_name):
        """Tests deleting a named shadow."""
        # Ensure the named shadow exists.
        try:
            iot_data_wrapper.update_thing_shadow(
                thing_name=thing_name,
                shadow_state={"state": {"reported": {"key": "value"}}},
                shadow_name="test-config",
            )
        except ClientError:
            pass

        payload = iot_data_wrapper.delete_thing_shadow(
            thing_name=thing_name,
            shadow_name="test-config",
        )
        assert isinstance(payload, dict)

        # Verify the shadow is deleted.
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_thing_shadow(
                thing_name=thing_name,
                shadow_name="test-config",
            )
        assert exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"

    def test_delete_classic_shadow(self, iot_data_wrapper, thing_name):
        """Tests deleting the classic shadow."""
        # Ensure the classic shadow exists.
        try:
            iot_data_wrapper.update_thing_shadow(
                thing_name=thing_name,
                shadow_state={"state": {"reported": {"status": "offline"}}},
            )
        except ClientError:
            pass

        payload = iot_data_wrapper.delete_thing_shadow(
            thing_name=thing_name,
        )
        assert isinstance(payload, dict)

    def test_update_thing_shadow_invalid_request(self, iot_data_wrapper, thing_name):
        """Tests that an invalid shadow document raises InvalidRequestException."""
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.update_thing_shadow(
                thing_name=thing_name,
                shadow_state={"invalid": "not a valid shadow document"},
            )
        assert exc_info.value.response["Error"]["Code"] == "InvalidRequestException"

    def test_get_retained_message_not_found(self, iot_data_wrapper):
        """Tests that getting a nonexistent retained message raises ResourceNotFoundException."""
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_retained_message(
                topic="nonexistent/topic/xyz/123456"
            )
        assert exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"


@pytest.mark.integ
class TestIoTDataPlaneHello:
    """Integration test for the Hello IoT Data Plane example."""

    def test_hello_runs_successfully(self, iot_client):
        """Tests the Hello example flow end to end."""
        thing_name = f"hello-test-{int(time.time())}"
        endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
        endpoint_url = endpoint_response["endpointAddress"]
        iot_data_client = boto3.client(
            "iot-data", endpoint_url=f"https://{endpoint_url}"
        )

        try:
            # Create thing.
            iot_client.create_thing(thingName=thing_name)

            # Get shadow — should fail with ResourceNotFoundException.
            with pytest.raises(
                iot_data_client.exceptions.ResourceNotFoundException
            ):
                iot_data_client.get_thing_shadow(thingName=thing_name)

            # Update shadow.
            shadow_doc = {"state": {"reported": {"status": "online"}}}
            iot_data_client.update_thing_shadow(
                thingName=thing_name, payload=json.dumps(shadow_doc)
            )

            # Get shadow — should succeed now.
            response = iot_data_client.get_thing_shadow(thingName=thing_name)
            shadow = json.loads(response["payload"].read())
            assert shadow["state"]["reported"]["status"] == "online"
        finally:
            # Clean up.
            try:
                iot_data_client.delete_thing_shadow(thingName=thing_name)
            except ClientError:
                pass
            try:
                iot_client.delete_thing(thingName=thing_name)
            except ClientError:
                pass
