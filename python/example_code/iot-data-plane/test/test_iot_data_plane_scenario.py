# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the IoT Data Plane wrapper (iot_data_plane_wrapper.py).

These tests use the botocore Stubber (via the shared ``make_stubber`` fixture
from test_tools) to intercept AWS calls, so no live AWS resources are used.
A separate set of integration tests, marked with ``@pytest.mark.integ``, runs
the same operations against real AWS endpoints.
"""

import os
import sys

# Make the parent directory importable so the wrapper module resolves.
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import time

import boto3
import pytest
from botocore.exceptions import ClientError

from iot_data_plane_wrapper import IoTDataPlaneWrapper

# -----------------------------------------------------------------------------
# Unit tests — use the botocore Stubber. No live AWS resources are used.
# -----------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "InvalidRequestException"])
def test_update_thing_shadow(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    thing_name = "test-thing"
    shadow_state = {"state": {"reported": {"temperature": 22.5}}}
    returned_doc = {"state": {"reported": {"temperature": 22.5}}, "version": 1}

    stubber.stub_update_thing_shadow(thing_name, returned_doc, error_code=error_code)

    if error_code is None:
        result = wrapper.update_thing_shadow(thing_name, shadow_state)
        assert result["state"]["reported"]["temperature"] == 22.5
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_thing_shadow(thing_name, shadow_state)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_update_named_thing_shadow(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    thing_name = "test-thing"
    shadow_name = "sensor-config"
    shadow_state = {"state": {"desired": {"interval": 30}}}
    returned_doc = {"state": {"desired": {"interval": 30}}, "version": 1}

    stubber.stub_update_thing_shadow(
        thing_name, returned_doc, shadow_name=shadow_name, error_code=error_code
    )

    if error_code is None:
        result = wrapper.update_thing_shadow(
            thing_name, shadow_state, shadow_name=shadow_name
        )
        assert result["version"] == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_thing_shadow(
                thing_name, shadow_state, shadow_name=shadow_name
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_get_thing_shadow(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    thing_name = "test-thing"
    returned_doc = {"state": {"reported": {"status": "online"}}, "version": 2}

    stubber.stub_get_thing_shadow(thing_name, returned_doc, error_code=error_code)

    if error_code is None:
        result = wrapper.get_thing_shadow(thing_name)
        assert result["state"]["reported"]["status"] == "online"
        assert result["version"] == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_thing_shadow(thing_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_delete_thing_shadow(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    thing_name = "test-thing"
    shadow_name = "sensor-config"
    returned_doc = {"version": 3, "timestamp": 1234567890}

    stubber.stub_delete_thing_shadow(
        thing_name, returned_doc, shadow_name=shadow_name, error_code=error_code
    )

    if error_code is None:
        result = wrapper.delete_thing_shadow(thing_name, shadow_name=shadow_name)
        assert result["version"] == 3
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_thing_shadow(thing_name, shadow_name=shadow_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_list_named_shadows_for_thing(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    thing_name = "test-thing"
    shadow_names = ["sensor-config", "firmware-config"]

    stubber.stub_list_named_shadows_for_thing(
        thing_name, shadow_names, error_code=error_code
    )

    if error_code is None:
        result = wrapper.list_named_shadows_for_thing(thing_name)
        assert result == shadow_names
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_named_shadows_for_thing(thing_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidRequestException"])
def test_publish(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    topic = "dt/sensors/test/temperature"
    payload = json.dumps({"temperature": 23.1}).encode("utf-8")

    stubber.stub_publish(topic, qos=1, retain=True, error_code=error_code)

    if error_code is None:
        # publish returns None on success.
        assert wrapper.publish(topic, payload=payload, qos=1, retain=True) is None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.publish(topic, payload=payload, qos=1, retain=True)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ThrottlingException"])
def test_list_retained_messages(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    retained_topics = [
        {"topic": "dt/sensors/test/temperature", "payloadSize": 42, "qos": 1}
    ]

    stubber.stub_list_retained_messages(retained_topics, error_code=error_code)

    if error_code is None:
        result = wrapper.list_retained_messages()
        assert result == retained_topics
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_retained_messages()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_get_retained_message(make_stubber, error_code):
    iot_data_client = boto3.client("iot-data")
    stubber = make_stubber(iot_data_client)
    wrapper = IoTDataPlaneWrapper(iot_data_client)
    topic = "dt/sensors/test/temperature"
    payload = json.dumps({"temperature": 23.1})

    stubber.stub_get_retained_message(
        topic, payload, qos=1, last_modified_time=1234567890, error_code=error_code
    )

    if error_code is None:
        result = wrapper.get_retained_message(topic)
        assert result["topic"] == topic
        assert result["qos"] == 1
        assert json.loads(result["payload"])["temperature"] == 23.1
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_retained_message(topic)
        assert exc_info.value.response["Error"]["Code"] == error_code


# -----------------------------------------------------------------------------
# Integration tests — hit live AWS endpoints. Marked with @pytest.mark.integ so
# they are only run on demand (they may incur charges).
# -----------------------------------------------------------------------------


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
def thing_name(iot_client, iot_data_wrapper):
    """
    Creates a unique IoT thing for testing and cleans it up after all tests.
    """
    name = f"test-iot-data-plane-{int(time.time())}"
    iot_client.create_thing(thingName=name)
    yield name
    # Cleanup: delete all shadows and the thing. Reuse the wrapper's client,
    # which is configured with the account-specific Data-ATS endpoint.
    iot_data_client = iot_data_wrapper.iot_data_client
    try:
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
class TestIoTDataPlaneWrapperIntegration:
    """Integration tests for IoTDataPlaneWrapper methods against live AWS."""

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

        result = iot_data_wrapper.update_thing_shadow(
            thing_name=thing_name,
            shadow_state=shadow_state,
        )
        assert "state" in result
        assert "reported" in result["state"]
        assert result["state"]["reported"]["temperature"] == 22.5

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

        shadow = iot_data_wrapper.get_thing_shadow(
            thing_name=thing_name,
            shadow_name="test-config",
        )
        assert "state" in shadow
        assert "delta" in shadow.get("state", dict())

    def test_list_named_shadows(self, iot_data_wrapper, thing_name):
        """Tests listing named shadows for a thing."""
        # Ensure the named shadow exists so this test does not depend on the
        # ordering of other tests in the class.
        iot_data_wrapper.update_thing_shadow(
            thing_name=thing_name,
            shadow_state={"state": {"reported": {"key": "value"}}},
            shadow_name="test-config",
        )
        shadow_names = iot_data_wrapper.list_named_shadows_for_thing(
            thing_name=thing_name,
        )
        assert isinstance(shadow_names, list)
        assert "test-config" in shadow_names

    def test_get_thing_shadow_not_found(self, iot_data_wrapper):
        """Tests that getting a shadow for a nonexistent thing raises an error."""
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_thing_shadow(thing_name="nonexistent-thing-xyz-123456")
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
            iot_data_wrapper.publish(
                topic=topic,
                payload=json.dumps(message).encode("utf-8"),
                qos=1,
                retain=True,
            )

            time.sleep(3)

            retained = iot_data_wrapper.list_retained_messages(max_results=25)
            assert isinstance(retained, list)

            result = iot_data_wrapper.get_retained_message(topic=topic)
            assert result["topic"] == topic
            payload = json.loads(result["payload"])
            assert payload["temperature"] == 25.0
        finally:
            try:
                iot_data_wrapper.publish(
                    topic=topic,
                    payload=b"",
                    qos=1,
                    retain=True,
                )
                time.sleep(2)
            except ClientError:
                pass

    def test_delete_named_shadow(self, iot_data_wrapper, thing_name):
        """Tests deleting a named shadow."""
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

        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_thing_shadow(
                thing_name=thing_name,
                shadow_name="test-config",
            )
        assert exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"

    def test_delete_classic_shadow(self, iot_data_wrapper, thing_name):
        """Tests deleting the classic shadow."""
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
        """Tests that getting a nonexistent retained message raises an error."""
        with pytest.raises(ClientError) as exc_info:
            iot_data_wrapper.get_retained_message(topic="nonexistent/topic/xyz/123456")
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
            iot_client.create_thing(thingName=thing_name)

            with pytest.raises(iot_data_client.exceptions.ResourceNotFoundException):
                iot_data_client.get_thing_shadow(thingName=thing_name)

            shadow_doc = {"state": {"reported": {"status": "online"}}}
            iot_data_client.update_thing_shadow(
                thingName=thing_name, payload=json.dumps(shadow_doc)
            )

            response = iot_data_client.get_thing_shadow(thingName=thing_name)
            shadow = json.loads(response["payload"].read())
            assert shadow["state"]["reported"]["status"] == "online"
        finally:
            try:
                iot_data_client.delete_thing_shadow(thingName=thing_name)
            except ClientError:
                pass
            try:
                iot_client.delete_thing(thingName=thing_name)
            except ClientError:
                pass
