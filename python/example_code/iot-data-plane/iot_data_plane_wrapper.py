# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for AWS IoT Data Plane operations.

This module encapsulates AWS IoT Data Plane actions for managing device shadows,
publishing MQTT messages, and working with retained messages.
"""

import json
import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.client import BaseClient
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data-plane.IoTDataPlaneWrapper.decl]
class IoTDataPlaneWrapper:
    """Encapsulates AWS IoT Data Plane actions."""

    def __init__(
        self,
        iot_data_client: BaseClient,
    ):
        """
        Initializes the IoTDataPlaneWrapper with an IoT Data Plane client.

        :param iot_data_client: A Boto3 IoT Data Plane client.
        """
        self.iot_data_client = iot_data_client

    @classmethod
    def from_client(cls, endpoint_url: Optional[str] = None) -> "IoTDataPlaneWrapper":
        """
        Creates an IoTDataPlaneWrapper using a new Boto3 IoT Data Plane client.

        :param endpoint_url: Optional custom endpoint URL for the IoT Data Plane.
        :return: A new IoTDataPlaneWrapper instance.
        """
        kwargs = dict()
        if endpoint_url is not None:
            kwargs["endpoint_url"] = f"https://{endpoint_url}"
        iot_data_client = boto3.client("iot-data", **kwargs)
        return cls(iot_data_client)

    # snippet-start:[python.example_code.iot-data-plane.UpdateThingShadow]
    def update_thing_shadow(
        self,
        thing_name: str,
        shadow_state: Dict[str, Any],
        shadow_name: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Updates the shadow for an IoT thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_state: The shadow state document as a dictionary.
        :param shadow_name: Optional name for a named shadow. Omit for the classic shadow.
        :return: The updated shadow document as a dictionary.
        :raises ClientError: If the request is invalid (InvalidRequestException).
        """
        try:
            params = {"thingName": thing_name, "payload": json.dumps(shadow_state)}
            if shadow_name is not None:
                params["shadowName"] = shadow_name
            response = self.iot_data_client.update_thing_shadow(**params)
            payload = json.loads(response["payload"].read())
            logger.info(
                "Updated shadow for thing '%s'%s.",
                thing_name,
                f" (shadow: '{shadow_name}')" if shadow_name else "",
            )
            return payload
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid shadow document for thing '%s': %s",
                    thing_name,
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Couldn't update shadow for thing '%s'. Here's why: %s: %s",
                    thing_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.UpdateThingShadow]

    # snippet-start:[python.example_code.iot-data-plane.GetThingShadow]
    def get_thing_shadow(
        self,
        thing_name: str,
        shadow_name: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Gets the shadow for an IoT thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_name: Optional name for a named shadow. Omit for the classic shadow.
        :return: The shadow document as a dictionary.
        :raises ClientError: If the shadow does not exist (ResourceNotFoundException).
        """
        try:
            params = {"thingName": thing_name}
            if shadow_name is not None:
                params["shadowName"] = shadow_name
            response = self.iot_data_client.get_thing_shadow(**params)
            payload = json.loads(response["payload"].read())
            logger.info(
                "Retrieved shadow for thing '%s'%s.",
                thing_name,
                f" (shadow: '{shadow_name}')" if shadow_name else "",
            )
            return payload
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "No shadow exists for thing '%s'%s.",
                    thing_name,
                    f" (shadow: '{shadow_name}')" if shadow_name else "",
                )
            else:
                logger.error(
                    "Couldn't get shadow for thing '%s'. Here's why: %s: %s",
                    thing_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.GetThingShadow]

    # snippet-start:[python.example_code.iot-data-plane.ListNamedShadowsForThing]
    def list_named_shadows_for_thing(
        self,
        thing_name: str,
        page_size: int = 25,
    ) -> List[str]:
        """
        Lists the named shadows for an IoT thing.

        Note: The unnamed (classic) shadow is NOT included in this list.

        :param thing_name: The name of the IoT thing.
        :param page_size: The number of shadow names to return per page.
        :return: A list of shadow names.
        :raises ClientError: If the thing does not exist (ResourceNotFoundException).
        """
        try:
            shadow_names = list()
            next_token = None
            while True:
                params = {"thingName": thing_name, "pageSize": page_size}
                if next_token is not None:
                    params["nextToken"] = next_token
                response = self.iot_data_client.list_named_shadows_for_thing(**params)
                results = response.get("results", list())
                shadow_names.extend(results)
                next_token = response.get("nextToken", None)
                if next_token is None:
                    break
            logger.info(
                "Listed %d named shadow(s) for thing '%s'.",
                len(shadow_names),
                thing_name,
            )
            return shadow_names
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Thing '%s' does not exist in the IoT registry.", thing_name
                )
            else:
                logger.error(
                    "Couldn't list named shadows for thing '%s'. Here's why: %s: %s",
                    thing_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.ListNamedShadowsForThing]

    # snippet-start:[python.example_code.iot-data-plane.DeleteThingShadow]
    def delete_thing_shadow(
        self,
        thing_name: str,
        shadow_name: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Deletes the shadow for an IoT thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_name: Optional name for a named shadow. Omit for the classic shadow.
        :return: The deleted shadow document as a dictionary.
        :raises ClientError: If the shadow does not exist (ResourceNotFoundException).
        """
        try:
            params = {"thingName": thing_name}
            if shadow_name is not None:
                params["shadowName"] = shadow_name
            response = self.iot_data_client.delete_thing_shadow(**params)
            payload = json.loads(response["payload"].read())
            logger.info(
                "Deleted shadow for thing '%s'%s.",
                thing_name,
                f" (shadow: '{shadow_name}')" if shadow_name else "",
            )
            return payload
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Shadow not found for thing '%s'%s. It may already be deleted.",
                    thing_name,
                    f" (shadow: '{shadow_name}')" if shadow_name else "",
                )
            else:
                logger.error(
                    "Couldn't delete shadow for thing '%s'. Here's why: %s: %s",
                    thing_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.DeleteThingShadow]

    # snippet-start:[python.example_code.iot-data-plane.Publish]
    def publish(
        self,
        topic: str,
        payload: Optional[bytes] = None,
        qos: int = 0,
        retain: bool = False,
    ) -> None:
        """
        Publishes an MQTT message to a topic.

        :param topic: The MQTT topic name.
        :param payload: The message payload as bytes. Use empty payload with retain=True
                        to delete a retained message.
        :param qos: The Quality of Service level (0 or 1). Default is 0.
        :param retain: Whether to set the RETAIN flag. Default is False.
        :raises ClientError: If the request is invalid (InvalidRequestException).
        """
        try:
            params = {"topic": topic, "qos": qos, "retain": retain}
            if payload is not None:
                params["payload"] = payload
            self.iot_data_client.publish(**params)
            logger.info(
                "Published message to topic '%s' (qos=%d, retain=%s).",
                topic,
                qos,
                retain,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid publish request for topic '%s': %s",
                    topic,
                    err.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Couldn't publish to topic '%s'. Here's why: %s: %s",
                    topic,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.Publish]

    # snippet-start:[python.example_code.iot-data-plane.ListRetainedMessages]
    def list_retained_messages(
        self,
        max_results: int = 25,
    ) -> List[Dict[str, Any]]:
        """
        Lists summary information about the retained messages stored for the account.

        Uses pagination to retrieve all retained messages.

        :param max_results: The maximum number of results to return per page.
        :return: A list of retained topic summary dictionaries.
        :raises ClientError: If the request is throttled (ThrottlingException).
        """
        try:
            retained_topics = list()
            paginator = self.iot_data_client.get_paginator("list_retained_messages")
            page_iterator = paginator.paginate(
                PaginationConfig={"MaxItems": 500, "PageSize": max_results}
            )
            for page in page_iterator:
                topics = page.get("retainedTopics", list())
                retained_topics.extend(topics)
            logger.info("Listed %d retained message(s).", len(retained_topics))
            return retained_topics
        except ClientError as err:
            if err.response["Error"]["Code"] == "ThrottlingException":
                logger.error(
                    "Request rate exceeded for ListRetainedMessages. "
                    "Please retry with exponential backoff."
                )
            else:
                logger.error(
                    "Couldn't list retained messages. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.ListRetainedMessages]

    # snippet-start:[python.example_code.iot-data-plane.GetRetainedMessage]
    def get_retained_message(
        self,
        topic: str,
    ) -> Dict[str, Any]:
        """
        Gets the details of a single retained message for the specified topic.

        :param topic: The topic name of the retained message to retrieve.
        :return: A dictionary containing the retained message details
                 (topic, payload, qos, lastModifiedTime).
        :raises ClientError: If no retained message exists (ResourceNotFoundException).
        """
        try:
            response = self.iot_data_client.get_retained_message(topic=topic)
            # Decode the payload bytes to a string.
            raw_payload = response.get("payload", b"")
            if isinstance(raw_payload, bytes):
                decoded_payload = raw_payload.decode("utf-8")
            else:
                decoded_payload = str(raw_payload)
            result = dict()
            result["topic"] = response.get("topic", "")
            result["payload"] = decoded_payload
            result["qos"] = response.get("qos", 0)
            result["lastModifiedTime"] = response.get("lastModifiedTime", 0)
            logger.info("Retrieved retained message for topic '%s'.", topic)
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("No retained message exists for topic '%s'.", topic)
            else:
                logger.error(
                    "Couldn't get retained message for topic '%s'. Here's why: %s: %s",
                    topic,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data-plane.GetRetainedMessage]


# snippet-end:[python.example_code.iot-data-plane.IoTDataPlaneWrapper.decl]
