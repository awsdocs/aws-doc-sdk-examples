# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello IoT Data Plane — A standalone example that verifies connectivity to
the AWS IoT Data Plane service by creating a thing, managing its shadow,
and cleaning up.

Purpose: Demonstrates basic GetThingShadow and UpdateThingShadow usage.
"""

import json
import logging
import time

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data-plane.Hello]
def hello_iot_data_plane() -> None:
    """
    Verifies connectivity to the AWS IoT Data Plane service.

    Creates a temporary IoT thing, demonstrates shadow creation and retrieval,
    and then cleans up all resources.
    """
    # Use a timestamp suffix so repeat runs (or a prior run that failed before
    # cleanup) don't collide with an existing thing and raise
    # ResourceAlreadyExistsException from create_thing.
    thing_name = f"hello-iot-data-plane-thing-{int(time.time())}"

    # Create IoT Control Plane client to manage things and get endpoint.
    iot_client = boto3.client("iot")

    # Obtain the IoT Data-ATS endpoint.
    endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
    endpoint_url = endpoint_response["endpointAddress"]
    print(f"IoT Data-ATS endpoint: {endpoint_url}")

    # Create IoT Data Plane client configured with the custom endpoint.
    iot_data_client = boto3.client("iot-data", endpoint_url=f"https://{endpoint_url}")

    try:
        # Create a temporary thing.
        iot_client.create_thing(thingName=thing_name)
        print(f"Created temporary IoT thing: '{thing_name}'")

        # Attempt to get the shadow — this will fail because no shadow exists yet.
        try:
            iot_data_client.get_thing_shadow(thingName=thing_name)
        except iot_data_client.exceptions.ResourceNotFoundException:
            print(
                "No shadow exists yet for this thing — "
                "this is expected for a newly created device."
            )

        # Create a shadow with a minimal reported state.
        shadow_doc = {"state": {"reported": {"status": "online"}}}
        iot_data_client.update_thing_shadow(
            thingName=thing_name, payload=json.dumps(shadow_doc)
        )
        print("Created shadow with reported state: {'status': 'online'}")

        # Retrieve the shadow to confirm it was created.
        response = iot_data_client.get_thing_shadow(thingName=thing_name)
        shadow = json.loads(response["payload"].read())
        print(f"Shadow document retrieved:\n{json.dumps(shadow, indent=2)}")

    finally:
        # Clean up: delete the shadow and the thing.
        try:
            iot_data_client.delete_thing_shadow(thingName=thing_name)
            print(f"Deleted shadow for thing '{thing_name}'.")
        except ClientError:
            pass  # Shadow may not exist.

        try:
            iot_client.delete_thing(thingName=thing_name)
            print(f"Deleted IoT thing: '{thing_name}'")
        except ClientError:
            pass  # Thing may not exist.

    print("Hello IoT Data Plane completed successfully!")


# snippet-end:[python.example_code.iot-data-plane.Hello]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_iot_data_plane()
