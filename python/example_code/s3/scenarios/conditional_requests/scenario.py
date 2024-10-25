# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use AWS SDK for Python (Boto3) to get started using conditional requests for
Amazon Simple Storage Service (Amazon S3).

"""

import logging
import random
import sys

import boto3
from botocore.exceptions import ClientError

from s3_conditional_requests import S3ConditionalRequests

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../../..")
import demo_tools.question as q # noqa

# Constants
FILE_CONTENT = "This is a test file for S3 conditional requests."
RANDOM_SUFFIX = str(random.randint(100, 999))
LOG_FORMAT = "%(asctime)s [%(levelname)s] %(message)s"

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.s3.SetupScenario]
def setup_scenario(s3_client, source_bucket: str, dest_bucket: str):
    """
    Sets up the scenario by creating a source and destination bucket.
    Prompts the user to provide a bucket name prefix.

    :param s3_client: The Boto3 S3 client.
    :param source_bucket: The name of the source bucket.
    :param dest_bucket: The name of the destination bucket.
    """
    print("This scenario will create a source and destination bucket with sample files.")

    # Create the buckets.
    try:
        s3_client.create_bucket(Bucket=source_bucket)
        s3_client.create_bucket(Bucket=dest_bucket)
        print(f"Created source bucket: {source_bucket} and destination bucket: {dest_bucket}")
    except ClientError as e:
        error_code = e.response['Error']['Code']
        logger.error(f"Error creating buckets: {error_code}")
        raise

    # Upload test files into the source bucket.
    for i in range(2):
        key = f"file{i}.txt"
        try:
            print(f"Uploading file {key} to bucket {source_bucket}")
            s3_client.put_object(Bucket=source_bucket, Key=key, Body=FILE_CONTENT)

        except Exception as e:
            logger.error(
                f"Failed to upload file {key} to bucket {source_bucket}: {e}")

# snippet-end:[python.example_code.s3.SetupScenario]


# snippet-start:[python.example_code.s3.CleanupScenario]
def cleanup_scenario(s3_client, source_bucket: str, dest_bucket: str):
    """
    Cleans up the scenario by deleting the source and destination buckets.

    :param s3_client: The Boto3 S3 client.
    :param source_bucket: The name of the source bucket.
    :param dest_bucket: The name of the destination bucket.
    """
    cleanup_bucket(s3_client, source_bucket)
    cleanup_bucket(s3_client, dest_bucket)


def cleanup_bucket(s3_client, bucket_name: str):
    """
        Cleans up the bucket by deleting all objects and then the bucket itself.

        :param s3_client: The Boto3 S3 client.
        :param bucket_name: The name of the bucket.
    """
    try:
        # Get list of all objects in the bucket.
        list_response = s3_client.list_objects_v2(
            Bucket=bucket_name
        )
        objs = list_response.get("Contents", [])
        for obj in objs:
            key = obj["Key"]
            s3_client.delete_object(Bucket=bucket_name, Key=key)
        s3_client.delete_bucket(Bucket=bucket_name)
        print(f"Cleaned up bucket: {bucket_name}.")
    except ClientError as e:
        error_code = e.response['Error']['Code']
        if error_code == 'NoSuchBucket':
            logger.info(f"Bucket {bucket_name} does not exist, skipping cleanup.")
        else:
            logger.error(f"Error deleting bucket: {error_code}")
            raise

# snippet-end:[python.example_code.s3.CleanupScenario]


def display_buckets(s3_client, source_bucket: str, dest_bucket: str):
    """
    Display a list of the objects in the test buckets.

    :param s3_client: The Boto3 S3 client.
    :param source_bucket: The name of the source bucket.
    :param dest_bucket: The name of the destination bucket.
    """
    list_bucket_contents(s3_client, source_bucket)
    list_bucket_contents(s3_client, dest_bucket)


def list_bucket_contents(s3_client, bucket_name):
    """
        Display a list of the objects in the bucket.

        :param s3_client: The Boto3 S3 client.
        :param bucket_name: The name of the bucket.
        """
    try:
        # Get list of all objects in the bucket.
        print(f"\t Items in bucket {bucket_name}")
        list_response = s3_client.list_objects_v2(
            Bucket=bucket_name
        )
        objs = list_response.get("Contents", [])
        if not objs:
            print("\t\tNo objects found.")
        for obj in objs:
            key = obj["Key"]
            print(f"\t\t object: {key}")
    except ClientError as e:
        error_code = e.response['Error']['Code']
        if error_code == 'NoSuchBucket':
            logger.info(f"Bucket {bucket_name} does not exist.")
        else:
            logger.error(f"Error listing bucket and objects: {error_code}")
            raise

# snippet-start:[python.example_code.s3.DisplayMenu]


def display_menu(s3_client, s3_conditional_requests, source_bucket: str, dest_bucket: str):
    """
    Displays the menu of conditional request options for the user.

    :param s3_client: The Boto3 S3 client.
    :param s3_conditional_requests: The wrapper for S3 conditional requests.
    :param source_bucket: The name of the source bucket.
    :param dest_bucket: The name of the destination bucket.
    """

    actions = [
        "Print list of bucket items.",
        "Perform a conditional read.",
        "Perform a conditional copy.",
        "Perform a conditional write.",
        "Clean up and exit.",
    ]

    conditions = [
        "If-Match",
        "If-None-Match",
        "If-Modified-Since",
        "If-Unmodified-Since",
        "No Condition",
    ]

    choice = 0
    while choice != 4:
        print("-" * 88)
        print("Choose an action to explore conditional request features.")
        choice = q.choose("Which action would you like to take? ", actions)
        if choice == 0:
            logging.info("Listing the objects and buckets.")
            display_buckets(s3_client, source_bucket, dest_bucket)
        elif choice == 1:
            logging.info("Perform a conditional read.")
            object_name = q.ask("Enter the object name: ", lambda x: (x, f"'{x}' is not a valid object name."))
            condition_type = q.choose("Enter the condition type : ", conditions)
            condition_types = ["IfMatch", "IfNoneMatch", "IfModifiedSince", "IfUnmodifiedSince"]
            s3_conditional_requests.get_object_conditional(object_name, condition_types[condition_type])
        elif choice == 2:
            logging.info("Perform a conditional copy.")
            source_key = q.ask("Enter the source object key: ", lambda x: (x, f"'{x}' is not a valid object key."))
            dest_key = q.ask("Enter the destination object key: ", lambda x: (x, f"'{x}' is not a valid object key."))
            condition_type = q.choose("Enter the condition type (copy-source-if-match, copy-source-if-none-match, "
                                      "copy-source-if-modified-since, copy-source-if-unmodified-since): ",
                                      ["copy-source-if-match", "copy-source-if-none-match",
                                       "copy-source-if-modified-since", "copy-source-if-unmodified-since"])
            condition_types = ["CopySourceIfMatch", "CopySourceIfNoneMatch", "CopySourceIfModifiedSince",
                               "CopySourceIfUnmodifiedSince"]
            s3_conditional_requests.copy_object_conditional(source_key, dest_key, condition_types[condition_type])
        elif choice == 3:
            logging.info("Perform a conditional write.")
            object_name = q.ask("Enter the object name: ", lambda x: (x, f"'{x}' is not a valid object name."))
            condition_type = q.choose("Enter the condition type (If-None-Match): ", ["If-None-Match"])
            condition_types = ["IfNoneMatch"]
            s3_conditional_requests.put_object_conditional(object_name, b"Overwrite example data.", condition_types[condition_type])
        elif choice == 4:
            logging.info("Proceeding to cleanup.")

# snippet-end:[python.example_code.s3.DisplayMenu]


def do_scenario(s3_client):
    print("-" * 88)
    print("Welcome to the Amazon S3 conditional requests example.")
    print("-" * 88)

    bucket_prefix = q.ask("Enter a bucket name prefix: ", q.non_empty,
                          lambda x: (x, f"'{x}' is not a valid bucket name prefix."))

    source_bucket_name = f"{bucket_prefix}-source-{RANDOM_SUFFIX}"
    dest_bucket_name = f"{bucket_prefix}-dest-{RANDOM_SUFFIX}"

    try:
        conditional_requests = S3ConditionalRequests(s3_client, source_bucket_name, dest_bucket_name)

        setup_scenario(s3_client, source_bucket_name, dest_bucket_name)

        display_menu(s3_client, conditional_requests, source_bucket_name, dest_bucket_name)
    finally:
        cleanup_scenario(s3_client, source_bucket_name, dest_bucket_name)

    print("-" * 88)
    print("Thanks for watching.")
    print("-" * 88)


if __name__ == "__main__":
    do_scenario(boto3.client("s3"))


