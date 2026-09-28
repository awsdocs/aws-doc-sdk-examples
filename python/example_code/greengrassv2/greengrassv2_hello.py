# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello AWS IoT Greengrass V2 — A standalone program that verifies connectivity
to the AWS IoT Greengrass V2 service by listing core devices.

Purpose: Demonstrates using the AWS SDK for Python (Boto3) with AWS IoT
Greengrass V2 to list registered Greengrass core devices.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.greengrassv2.Hello]
def hello_greengrassv2() -> None:
    """
    Lists Greengrass core devices to verify connectivity to the service.
    Uses a paginator to handle pagination of results.
    """
    greengrassv2_client = boto3.client("greengrassv2")

    print("\n----------- Welcome to AWS IoT Greengrass V2 ----------\n")
    print("Listing Greengrass core devices in your account...\n")

    try:
        devices = list()
        paginator = greengrassv2_client.get_paginator("list_core_devices")
        for page in paginator.paginate():
            devices.extend(page.get("coreDevices", list()))

        if devices:
            print(f"Found {len(devices)} core device(s):")
            for device in devices:
                thing_name = device.get("coreDeviceThingName", "Unknown")
                status = device.get("status", "Unknown")
                last_updated = device.get("lastStatusUpdateTimestamp", "N/A")
                print(
                    f"  - {thing_name} (Status: {status}, Last updated: {last_updated})"
                )
        else:
            print("No Greengrass core devices found in your account.")
            print(
                "To get started, install the Greengrass Core software on an IoT device."
            )

        print("\nHello from AWS IoT Greengrass V2!")

    except ClientError as err:
        logger.error(
            "Error listing core devices: %s",
            err.response["Error"]["Message"],
        )
        print(f"Service error: {err.response['Error']['Message']}")
        raise


if __name__ == "__main__":
    hello_greengrassv2()
# snippet-end:[python.example_code.greengrassv2.Hello]
