# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AWS IoT Data Plane wrapper class that encapsulates all IoT Data Plane operations.
"""

import json
import logging
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data.IoTDataPlaneWrapper]
# snippet-start:[python.example_code.iot-data.IoTDataPlaneWrapper.decl]
class IoTDataPlaneWrapper:
    """Encapsulates AWS IoT Data Plane operations."""

    def __init__(self, iot_data_client, iot_client=None):
        """
        :param iot_data_client: A Boto3 IoT Data Plane client.
        :param iot_client: A Boto3 IoT control plane client (for endpoint discovery).
        """
        self.iot_data_client = iot_data_client
        self.iot_client = iot_client

    @classmethod
    def from_client(cls, region_name: str = "us-west-2"):
        """
        Creates this wrapper from Boto3 clients, discovering the IoT Data ATS endpoint.

        :param region_name: AWS region.
        :return: An instance of IoTDataPlaneWrapper.
        """
        iot_client = boto3.client("iot", region_name=region_name)
        endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
        endpoint_url = f"https://{endpoint_response['endpointAddress']}"
        iot_data_client = boto3.client(
            "iot-data", region_name=region_name, endpoint_url=endpoint_url
        )
        return cls(iot_data_client, iot_client)

    # snippet-end:[python.example_code.iot-data.IoTDataPlaneWrapper.decl]

    # snippet-start:[python.example_code.iot-data.UpdateThingShadow]
    def update_thing_shadow(
        self,
        thing_name: str,
        shadow_state: Dict[str, Any],
        shadow_name: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Updates the shadow for a specified thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_state: The shadow state document as a dictionary.
        :param shadow_name: Optional named shadow. If None, updates the classic shadow.
        :return: The updated shadow document.
        """
        try:
            params = dict()
            params["thingName"] = thing_name
            params["payload"] = json.dumps(shadow_state)
            if shadow_name is not None:
                params["shadowName"] = shadow_name

            response = self.iot_data_client.update_thing_shadow(**params)
            shadow_doc = json.loads(response["payload"].read())
            shadow_label = shadow_name if shadow_name else "classic"
            logger.info(
                "Updated %s shadow for thing '%s'.", shadow_label, thing_name
            )
            return shadow_doc
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request when updating shadow: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data.UpdateThingShadow]

    # snippet-start:[python.example_code.iot-data.GetThingShadow]
    def get_thing_shadow(
        self, thing_name: str, shadow_name: Optional[str] = None
    ) -> Optional[Dict[str, Any]]:
        """
        Gets the shadow for a specified thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_name: Optional named shadow. If None, gets the classic shadow.
        :return: The shadow document as a dictionary, or None if not found.
        """
        try:
            params = dict()
            params["thingName"] = thing_name
            if shadow_name is not None:
                params["shadowName"] = shadow_name

            response = self.iot_data_client.get_thing_shadow(**params)
            shadow_doc = json.loads(response["payload"].read())
            shadow_label = shadow_name if shadow_name else "classic"
            logger.info(
                "Retrieved %s shadow for thing '%s'.", shadow_label, thing_name
            )
            return shadow_doc
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Shadow not found for thing '%s'.", thing_name
                )
                return None
            raise

    # snippet-end:[python.example_code.iot-data.GetThingShadow]

    # snippet-start:[python.example_code.iot-data.ListNamedShadowsForThing]
    def list_named_shadows_for_thing(self, thing_name: str) -> List[str]:
        """
        Lists the named shadows for a specified thing.

        :param thing_name: The name of the IoT thing.
        :return: A list of shadow names.
        """
        try:
            shadows = list()
            next_token = None
            while True:
                params = dict()
                params["thingName"] = thing_name
                if next_token is not None:
                    params["nextToken"] = next_token
                response = self.iot_data_client.list_named_shadows_for_thing(**params)
                results = response.get("results", list())
                shadows.extend(results)
                next_token = response.get("nextToken", None)
                if next_token is None:
                    break
            logger.info(
                "Found %d named shadows for thing '%s'.", len(shadows), thing_name
            )
            return shadows
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Thing '%s' not found in registry.", thing_name
                )
            raise

    # snippet-end:[python.example_code.iot-data.ListNamedShadowsForThing]

    # snippet-start:[python.example_code.iot-data.DeleteThingShadow]
    def delete_thing_shadow(
        self, thing_name: str, shadow_name: Optional[str] = None
    ) -> bool:
        """
        Deletes the shadow for a specified thing.

        :param thing_name: The name of the IoT thing.
        :param shadow_name: Optional named shadow. If None, deletes the classic shadow.
        :return: True if deleted successfully, False if not found.
        """
        try:
            params = dict()
            params["thingName"] = thing_name
            if shadow_name is not None:
                params["shadowName"] = shadow_name

            self.iot_data_client.delete_thing_shadow(**params)
            shadow_label = shadow_name if shadow_name else "classic"
            logger.info(
                "Deleted %s shadow for thing '%s'.", shadow_label, thing_name
            )
            return True
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Shadow not found for thing '%s' (already deleted).", thing_name
                )
                return False
            raise

    # snippet-end:[python.example_code.iot-data.DeleteThingShadow]

    # snippet-start:[python.example_code.iot-data.Publish]
    def publish(
        self,
        topic: str,
        payload: str,
        qos: int = 1,
        retain: bool = False,
    ) -> None:
        """
        Publishes an MQTT message to a topic.

        :param topic: The MQTT topic name.
        :param payload: The message payload as a string.
        :param qos: Quality of Service level (0 or 1).
        :param retain: Whether to retain the message.
        """
        try:
            self.iot_data_client.publish(
                topic=topic,
                qos=qos,
                retain=retain,
                payload=payload.encode("utf-8"),
            )
            logger.info("Published message to topic '%s' (QoS=%d, retain=%s).", topic, qos, retain)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request when publishing to topic '%s': %s",
                    topic,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.iot-data.Publish]

    # snippet-start:[python.example_code.iot-data.GetRetainedMessage]
    def get_retained_message(self, topic: str) -> Optional[Dict[str, Any]]:
        """
        Gets the details of a single retained message for the specified topic.

        :param topic: The topic name of the retained message.
        :return: A dictionary with retained message details, or None if not found.
        """
        try:
            response = self.iot_data_client.get_retained_message(topic=topic)
            result = dict()
            result["topic"] = response.get("topic", "")
            result["payload"] = response.get("payload", b"").decode("utf-8")
            result["qos"] = response.get("qos", 0)
            result["lastModifiedTime"] = response.get("lastModifiedTime", 0)
            logger.info("Retrieved retained message for topic '%s'.", topic)
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "No retained message found for topic '%s'.", topic
                )
                return None
            raise

    # snippet-end:[python.example_code.iot-data.GetRetainedMessage]

    # snippet-start:[python.example_code.iot-data.ListRetainedMessages]
    def list_retained_messages(self) -> List[Dict[str, Any]]:
        """
        Lists summary information about retained messages stored in the account.

        :return: A list of dictionaries with retained message summaries.
        """
        try:
            messages = list()
            next_token = None
            while True:
                params = dict()
                if next_token is not None:
                    params["nextToken"] = next_token
                response = self.iot_data_client.list_retained_messages(**params)
                summaries = response.get("retainedTopics", list())
                messages.extend(summaries)
                next_token = response.get("nextToken", None)
                if next_token is None:
                    break
            logger.info("Found %d retained messages.", len(messages))
            return messages
        except ClientError as err:
            if err.response["Error"]["Code"] == "ThrottlingException":
                logger.warning("Throttled when listing retained messages. Retry later.")
            raise

    # snippet-end:[python.example_code.iot-data.ListRetainedMessages]

    # snippet-start:[python.example_code.iot-data.GetConnection]
    def get_connection(self, client_id: str) -> Optional[Dict[str, Any]]:
        """
        Retrieves connection information for an MQTT client.

        :param client_id: The unique identifier of the MQTT client.
        :return: A dictionary with connection details, or None if not found.
        """
        try:
            response = self.iot_data_client.get_connection(
                clientId=client_id
            )
            result = dict()
            result["clientId"] = response.get("clientId", "")
            result["connected"] = response.get("connected", False)
            result["thingName"] = response.get("thingName", "")
            result["connectedSince"] = response.get("connectedSince", None)
            result["disconnectedSince"] = response.get("disconnectedSince", None)
            result["disconnectReason"] = response.get("disconnectReason", "")
            result["keepAliveDuration"] = response.get("keepAliveDuration", 0)
            logger.info(
                "Retrieved connection info for client '%s'. Connected: %s",
                client_id,
                result["connected"],
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "No connection info found for client '%s'.", client_id
                )
                return None
            raise

    # snippet-end:[python.example_code.iot-data.GetConnection]

    # snippet-start:[python.example_code.iot-data.ListSubscriptions]
    def list_subscriptions(self, client_id: str) -> List[Dict[str, Any]]:
        """
        Lists subscriptions for an MQTT client.

        :param client_id: The unique identifier of the MQTT client.
        :return: A list of subscription dictionaries with topicFilter and qos.
        """
        try:
            subscriptions = list()
            next_token = None
            while True:
                params = dict()
                params["clientId"] = client_id
                if next_token is not None:
                    params["nextToken"] = next_token
                response = self.iot_data_client.list_subscriptions(**params)
                subs = response.get("subscriptions", list())
                subscriptions.extend(subs)
                next_token = response.get("nextToken", None)
                if next_token is None:
                    break
            logger.info(
                "Found %d subscriptions for client '%s'.",
                len(subscriptions),
                client_id,
            )
            return subscriptions
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Client '%s' not connected or has no active session.", client_id
                )
                return list()
            raise

    # snippet-end:[python.example_code.iot-data.ListSubscriptions]

    # snippet-start:[python.example_code.iot-data.SendDirectMessage]
    def send_direct_message(
        self,
        client_id: str,
        topic: str,
        payload: str,
        content_type: str = "application/json",
        confirmation: bool = False,
    ) -> Optional[Dict[str, str]]:
        """
        Sends an MQTT message directly to a specific client.

        :param client_id: The target client ID.
        :param topic: The topic for the message.
        :param payload: The message payload as a string.
        :param content_type: The content type of the message.
        :param confirmation: Whether to wait for delivery confirmation.
        :return: A dictionary with message and traceId, or None if client not found.
        """
        try:
            response = self.iot_data_client.send_direct_message(
                clientId=client_id,
                topic=topic,
                payload=payload.encode("utf-8"),
                contentType=content_type,
                confirmation=confirmation,
            )
            result = dict()
            result["message"] = response.get("message", "")
            result["traceId"] = response.get("traceId", "")
            logger.info(
                "Sent direct message to client '%s' on topic '%s'.",
                client_id,
                topic,
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Client '%s' not currently connected.", client_id
                )
                return None
            raise

    # snippet-end:[python.example_code.iot-data.SendDirectMessage]


# snippet-end:[python.example_code.iot-data.IoTDataPlaneWrapper]
