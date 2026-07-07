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

from firehose_wrapper import FirehoseWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../../..")
import demo_tools.question as q  # noqa

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
                stream_name, poll_interval=5, max_wait=60
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
                stream_name, poll_interval=5, max_wait=60
            )
            print(f"   Encryption status: {encryption_status}")

            # Step 6: Put a single record
            print("\n6. Putting a single record...")
            now = datetime.now(timezone.utc).isoformat()
            single_record = json.dumps(
                {
                    "sensorId": "sensor-001",
                    "temperature": 72.5,
                    "timestamp": now,
                }
            ) + "\n"
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
                record = json.dumps(
                    {
                        "sensorId": sensor_id,
                        "temperature": temp,
                        "timestamp": datetime.now(timezone.utc).isoformat(),
                    }
                ) + "\n"
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
            try:
                self.firehose_wrapper.delete_delivery_stream(stream_name)
                print("   Deletion initiated. Stream will be removed shortly.")
            except ClientError as err:
                logger.error("Failed to delete stream: %s", err)
                print(f"   Warning: Could not delete stream: {err}")

        print("\n" + "-" * 88)
        print("Firehose Basics Scenario complete. Thanks for watching!")
        print("-" * 88)


# snippet-end:[python.example_code.firehose.FirehoseScenario]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("Amazon Data Firehose Basics Scenario")
    print("=" * 50)
    print(
        "This scenario requires:\n"
        "  - An S3 bucket ARN for the stream destination\n"
        "  - An IAM role ARN that grants Firehose permission to write to S3\n"
    )

    bucket_arn = q.ask("Enter the S3 bucket ARN: ", q.non_empty)
    role_arn = q.ask("Enter the IAM role ARN: ", q.non_empty)
    timestamp = int(time.time())
    stream_name = f"firehose-basics-stream-{timestamp}"

    try:
        wrapper = FirehoseWrapper.from_client()
        scenario = FirehoseScenario(wrapper)
        scenario.run_scenario(
            stream_name=stream_name,
            bucket_arn=bucket_arn,
            role_arn=role_arn,
        )
    except Exception:
        logging.exception("Something went wrong with the scenario.")
