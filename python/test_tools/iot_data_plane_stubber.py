# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS IoT Data Plane unit tests.
"""

import json
import io

from botocore.response import StreamingBody
from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


def _streaming_payload(doc):
    """
    Wraps a dictionary as a JSON-encoded ``StreamingBody``, matching the
    ``payload`` blob that the real IoT Data Plane client returns for shadow
    operations (the wrapper calls ``response["payload"].read()``).
    """
    data = json.dumps(doc).encode("utf-8")
    return StreamingBody(io.BytesIO(data), len(data))


class IoTDataPlaneStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS IoT Data Plane unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 IoT Data Plane client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_update_thing_shadow(
        self, thing_name, payload, shadow_name=None, error_code=None
    ):
        expected_params = {"thingName": thing_name, "payload": ANY}
        if shadow_name is not None:
            expected_params["shadowName"] = shadow_name
        response = {"payload": _streaming_payload(payload)}
        self._stub_bifurcator(
            "update_thing_shadow", expected_params, response, error_code
        )

    def stub_get_thing_shadow(
        self, thing_name, payload, shadow_name=None, error_code=None
    ):
        expected_params = {"thingName": thing_name}
        if shadow_name is not None:
            expected_params["shadowName"] = shadow_name
        response = {"payload": _streaming_payload(payload)}
        self._stub_bifurcator("get_thing_shadow", expected_params, response, error_code)

    def stub_delete_thing_shadow(
        self, thing_name, payload, shadow_name=None, error_code=None
    ):
        expected_params = {"thingName": thing_name}
        if shadow_name is not None:
            expected_params["shadowName"] = shadow_name
        response = {"payload": _streaming_payload(payload)}
        self._stub_bifurcator(
            "delete_thing_shadow", expected_params, response, error_code
        )

    def stub_list_named_shadows_for_thing(
        self, thing_name, results, page_size=25, next_token=None, error_code=None
    ):
        expected_params = {"thingName": thing_name, "pageSize": page_size}
        if next_token is not None:
            expected_params["nextToken"] = next_token
        response = {"results": results}
        self._stub_bifurcator(
            "list_named_shadows_for_thing", expected_params, response, error_code
        )

    def stub_publish(self, topic, qos=0, retain=False, error_code=None):
        expected_params = {"topic": topic, "qos": qos, "retain": retain}
        # The wrapper only sets payload when it is not None; tests that publish a
        # payload pass ANY-matching bytes via the expected_params below.
        expected_params["payload"] = ANY
        response = {}
        self._stub_bifurcator("publish", expected_params, response, error_code)

    def stub_list_retained_messages(
        self, retained_topics, max_results=25, error_code=None
    ):
        expected_params = {"maxResults": max_results}
        response = {"retainedTopics": retained_topics}
        self._stub_bifurcator(
            "list_retained_messages", expected_params, response, error_code
        )

    def stub_get_retained_message(
        self, topic, payload, qos=0, last_modified_time=0, error_code=None
    ):
        expected_params = {"topic": topic}
        response = {
            "topic": topic,
            "payload": payload.encode("utf-8") if isinstance(payload, str) else payload,
            "qos": qos,
            "lastModifiedTime": last_modified_time,
        }
        self._stub_bifurcator(
            "get_retained_message", expected_params, response, error_code
        )
