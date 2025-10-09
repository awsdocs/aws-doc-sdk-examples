# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to get started with Amazon GuardDuty.
This example demonstrates the most basic GuardDuty operation: listing existing detectors
in the current region.
"""

import logging
import boto3
from botocore.exceptions import ClientError

from guardduty_wrapper import GuardDutyWrapper


# snippet-start:[python.example_code.guardduty.Hello]
def hello_guardduty():
    """
    Use the AWS SDK for Python (Boto3) to check if GuardDuty is available
    in the current region and list any existing detectors.
    This function is typically used to verify GuardDuty service connectivity.
    """
    print("Hello, Amazon GuardDuty!")

    try:
        # Create GuardDuty wrapper
        guardduty_wrapper = GuardDutyWrapper.from_client()

        # List existing detectors
        detector_ids = guardduty_wrapper.list_detectors()

        if detector_ids:
            print(f"Found {len(detector_ids)} GuardDuty detector(s) in this region:")
            for detector_id in detector_ids:
                print(f"  - {detector_id}")
        else:
            print("No GuardDuty detectors found in this region.")
            print(
                "You can create a detector to start using GuardDuty threat detection."
            )

    except ClientError as e:
        error_code = e.response["Error"]["Code"]
        if error_code == "AccessDeniedException":
            print("Access denied. Please check your AWS credentials and permissions.")
        elif error_code == "UnauthorizedOperation":
            print(
                "Unauthorized operation. Please ensure you have GuardDuty permissions."
            )
        else:
            print(f"Error accessing GuardDuty: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")


# snippet-end:[python.example_code.guardduty.Hello]

if __name__ == "__main__":
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")
    hello_guardduty()
