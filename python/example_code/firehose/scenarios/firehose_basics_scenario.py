# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Scenario demonstrating the basics of Amazon Data Firehose.

This scenario walks through the complete lifecycle of a Firehose delivery stream:
1. Create a delivery stream with an S3 destination.
2. Wait for the stream to become active.
3. Add tags and verify them.
4. Enable server-side encryption.
5. Put a single record and a batch of records.
6. Update the destination buffering configuration.
7. List delivery streams.
8. Delete the delivery stream.
"""

import json
import logging
import sys
import time
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError

import os

_scenario_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.join(_scenario_dir, ".."))
from firehose_wrapper import FirehoseWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.FirehoseScenario]
class FirehoseScenario:
    """Runs an interactive scenario demonstrating Amazon Data Firehose basics."""

    def __init__(self, firehose_wrapper: FirehoseWrapper):
        """
        :param firehose_wrapper: An instance of FirehoseWrapper for Firehose operations.
        """
        self.firehose_wrapper = firehose_wrapper
        self.stream_name = None

    def run_scenario(
        self,
        stream_name: str,
        bucket_arn: str,
        role_arn: str,
    ) -> None:
        """
        Runs the full Firehose basics scenario.

        :param stream_name: Name for the delivery stream.
        :param bucket_arn: ARN of the S3 bucket destination.
        :param role_arn: ARN of the IAM role for Firehose.
        """
        self.stream_name = stream_name

        print("-" * 88)
        print("Welcome to the Amazon Data Firehose Basics Scenario!")
        print("-" * 88)

        try:
            # Step 1: Create a delivery stream
            print(f"\n1. Creating delivery stream: {stream_name}")
            stream_arn = self.firehose_wrapper.create_delivery_stream(
                stream_name=stream_name,
                bucket_arn=bucket_arn,
                role_arn=role_arn,
                interval_in_seconds=60,
                size_in_mbs=1,
            )
            print(f"   Delivery stream ARN: {stream_arn}")

            # Step 2: Wait for stream to become active
            print("\n2. Waiting for the stream to become ACTIVE...")
            description = self.firehose_wrapper.wait_for_stream_active(
                stream_name, poll_interval=5, max_wait=300
            )
            print(f"   Stream status: {description.get('DeliveryStreamStatus')}")
            print(f"   Stream ARN: {description.get('DeliveryStreamARN')}")
            print(f"   Stream type: {description.get('DeliveryStreamType')}")
            create_time = description.get("CreateTimestamp")
            if create_time:
                print(f"   Created at: {create_time}")

            # Step 3: Tag the delivery stream
            print("\n3. Adding tags to the delivery stream...")
            tags = [
                {"Key": "Environment", "Value": "Development"},
                {"Key": "Project", "Value": "FirehoseBasics"},
            ]
            self.firehose_wrapper.tag_delivery_stream(stream_name, tags)
            print("   Tags added successfully.")

            # Step 4: Verify tags
            print("\n4. Verifying tags on the delivery stream...")
            retrieved_tags = self.firehose_wrapper.list_tags_for_delivery_stream(
                stream_name
            )
            for tag in retrieved_tags:
                print(f"   Tag: {tag['Key']} = {tag['Value']}")

            # Step 5: Enable server-side encryption
            print("\n5. Enabling server-side encryption (AWS_OWNED_CMK)...")
            self.firehose_wrapper.start_delivery_stream_encryption(stream_name)
            encryption_status = self.firehose_wrapper.wait_for_encryption_enabled(
                stream_name, poll_interval=5, max_wait=120
            )
            print(f"   Encryption status: {encryption_status}")

            # Step 6: Put a single record
            print("\n6. Putting a single record...")
            now = datetime.now(timezone.utc).isoformat()
            single_record = (
                json.dumps(
                    {
                        "sensorId": "sensor-001",
                        "temperature": 72.5,
                        "timestamp": now,
                    }
                )
                + "\n"
            )
            put_result = self.firehose_wrapper.put_record(stream_name, single_record)
            print(f"   RecordId: {put_result['RecordId']}")
            print(f"   Encrypted: {put_result['Encrypted']}")

            # Step 7: Put multiple records in a batch
            print("\n7. Putting a batch of 5 records...")
            batch_records = list()
            sensor_data = [
                ("sensor-002", 68.3),
                ("sensor-003", 75.1),
                ("sensor-004", 70.8),
                ("sensor-005", 66.2),
                ("sensor-006", 73.9),
            ]
            for sensor_id, temp in sensor_data:
                record = (
                    json.dumps(
                        {
                            "sensorId": sensor_id,
                            "temperature": temp,
                            "timestamp": datetime.now(timezone.utc).isoformat(),
                        }
                    )
                    + "\n"
                )
                batch_records.append(record)

            batch_result = self.firehose_wrapper.put_record_batch(
                stream_name, batch_records
            )
            print(f"   Successfully delivered: {batch_result['SuccessCount']} records")
            if batch_result["FailedPutCount"] > 0:
                print(f"   Failed: {batch_result['FailedPutCount']} records")
                for failed in batch_result["FailedRecords"]:
                    print(
                        f"     Record {failed['index']}: "
                        f"{failed['ErrorCode']} - {failed['ErrorMessage']}"
                    )

            # Step 8: Update the destination configuration
            print("\n8. Updating destination buffering hints (120s / 5 MB)...")
            self.firehose_wrapper.update_destination(
                stream_name, interval_in_seconds=120, size_in_mbs=5
            )
            print("   Destination buffering hints updated successfully.")

            # Step 9: List delivery streams
            print("\n9. Listing delivery streams (DirectPut type)...")
            stream_names = self.firehose_wrapper.list_delivery_streams(
                stream_type="DirectPut", limit=20
            )
            print(f"   Found {len(stream_names)} DirectPut stream(s):")
            for name in stream_names:
                marker = " <-- (this stream)" if name == stream_name else ""
                print(f"     - {name}{marker}")

        finally:
            # Step 10: Delete the delivery stream
            print(f"\n10. Deleting delivery stream: {stream_name}...")
            max_retries = 5
            for attempt in range(max_retries):
                try:
                    self.firehose_wrapper.delete_delivery_stream(stream_name)
                    print("   Deletion initiated. Stream will be removed shortly.")
                    break
                except ClientError as err:
                    if (
                        err.response["Error"]["Code"] == "ResourceInUseException"
                        and attempt < max_retries - 1
                    ):
                        print(
                            "   Stream not yet deletable. "
                            "Waiting 15 seconds before retry..."
                        )
                        time.sleep(15)
                    else:
                        logger.error("Failed to delete stream: %s", err)
                        print(f"   Warning: Could not delete stream: {err}")
                        break

        print("\n" + "-" * 88)
        print("Firehose Basics Scenario complete. Thanks for watching!")
        print("-" * 88)


# snippet-end:[python.example_code.firehose.FirehoseScenario]


def setup_resources(region: str, suffix: str):
    """
    Creates an S3 bucket and an IAM role for the Firehose scenario.

    :param region: AWS region for the bucket.
    :param suffix: Unique suffix for resource names.
    :return: Tuple of (bucket_arn, role_arn, bucket_name, role_name).
    """
    bucket_name = f"firehose-basics-bucket-{suffix}"
    role_name = f"firehose-basics-role-{suffix}"

    # Create S3 bucket
    s3_client = boto3.client("s3", region_name=region)
    print(f"   Creating S3 bucket: {bucket_name}...")
    if region == "us-east-1":
        s3_client.create_bucket(Bucket=bucket_name)
    else:
        s3_client.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={"LocationConstraint": region},
        )
    bucket_arn = f"arn:aws:s3:::{bucket_name}"
    print(f"   Bucket created: {bucket_arn}")

    # Create IAM role with Firehose trust policy
    iam_client = boto3.client("iam")
    trust_policy = json.dumps(
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {"Service": "firehose.amazonaws.com"},
                    "Action": "sts:AssumeRole",
                }
            ],
        }
    )
    print(f"   Creating IAM role: {role_name}...")
    role_response = iam_client.create_role(
        RoleName=role_name,
        AssumeRolePolicyDocument=trust_policy,
        Description="Role for Firehose basics scenario to write to S3.",
    )
    role_arn = role_response["Role"]["Arn"]

    # Attach inline policy granting S3 write access
    s3_policy = json.dumps(
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:PutObject",
                        "s3:GetBucketLocation",
                        "s3:ListBucket",
                        "s3:AbortMultipartUpload",
                        "s3:GetObject",
                        "s3:ListBucketMultipartUploads",
                    ],
                    "Resource": [
                        bucket_arn,
                        f"{bucket_arn}/*",
                    ],
                }
            ],
        }
    )
    iam_client.put_role_policy(
        RoleName=role_name,
        PolicyName="FirehoseS3Access",
        PolicyDocument=s3_policy,
    )
    print(f"   Role created: {role_arn}")

    # Wait for the role to propagate
    print("   Waiting 10 seconds for IAM role propagation...")
    time.sleep(10)

    return bucket_arn, role_arn, bucket_name, role_name


def cleanup_resources(region: str, bucket_name: str, role_name: str):
    """
    Deletes the S3 bucket and IAM role created for the scenario.

    :param region: AWS region for the bucket.
    :param bucket_name: Name of the S3 bucket to delete.
    :param role_name: Name of the IAM role to delete.
    """
    print("\nCleaning up resources...")

    # Delete all objects in the bucket, then delete the bucket
    try:
        s3 = boto3.resource("s3", region_name=region)
        bucket = s3.Bucket(bucket_name)
        print(f"   Emptying and deleting S3 bucket: {bucket_name}...")
        bucket.objects.all().delete()
        bucket.delete()
        print("   Bucket deleted.")
    except ClientError as err:
        print(f"   Warning: Could not delete bucket: {err}")

    # Delete the inline policy, then the role
    try:
        iam_client = boto3.client("iam")
        print(f"   Deleting IAM role: {role_name}...")
        iam_client.delete_role_policy(RoleName=role_name, PolicyName="FirehoseS3Access")
        iam_client.delete_role(RoleName=role_name)
        print("   Role deleted.")
    except ClientError as err:
        print(f"   Warning: Could not delete role: {err}")


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    region = "us-east-1"
    timestamp = int(time.time())
    suffix = str(timestamp)
    stream_name = f"firehose-basics-stream-{suffix}"

    print("Amazon Data Firehose Basics Scenario")
    print("=" * 50)
    print("\nSetting up required resources (S3 bucket + IAM role)...")

    bucket_name = None
    role_name = None
    try:
        bucket_arn, role_arn, bucket_name, role_name = setup_resources(region, suffix)
        wrapper = FirehoseWrapper.from_client()
        scenario = FirehoseScenario(wrapper)
        scenario.run_scenario(
            stream_name=stream_name,
            bucket_arn=bucket_arn,
            role_arn=role_arn,
        )
    except Exception:
        logging.exception("Something went wrong with the scenario.")
    finally:
        if bucket_name and role_name:
            cleanup_resources(region, bucket_name, role_name)
