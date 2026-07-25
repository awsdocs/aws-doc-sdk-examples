# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello AWS IoT Data Plane - Lists retained messages as a connectivity check.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.iot-data.Hello]
def hello_iot_data_plane():
    """
    Demonstrates the simplest interaction with the AWS IoT Data Plane service.
    Lists retained messages as a connectivity check.
    """
    # Create IoT control plane client to discover the data endpoint.
    iot_client = boto3.client("iot")
    endpoint_response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
    endpoint_url = f"https://{endpoint_response['endpointAddress']}"

    # Create IoT Data Plane client with the discovered endpoint.
    iot_data_client = boto3.client("iot-data", endpoint_url=endpoint_url)

    print("Hello, AWS IoT Data Plane!")
    print("-" * 40)

    try:
        response = iot_data_client.list_retained_messages()
        retained_topics = response.get("retainedTopics", list())

        if retained_topics:
            print(f"Found {len(retained_topics)} retained message(s):")
            for msg in retained_topics:
                topic = msg.get("topic", "unknown")
                payload_size = msg.get("payloadSize", 0)
                print(f"  Topic: {topic}, Payload Size: {payload_size} bytes")
        else:
            print("No retained messages found in this account.")

        print("-" * 40)
        print("Successfully connected to AWS IoT Data Plane!")

    except ClientError as err:
        logger.error(
            "Error listing retained messages: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.iot-data.Hello]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_iot_data_plane()
