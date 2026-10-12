# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Kinesis Data Streams Basics Scenario

This scenario demonstrates advanced Kinesis Data Streams management:
1. Create a stream and wait for it to become ACTIVE.
2. List streams and verify creation.
3. Describe stream summary.
4. List shards.
5. Batch put records with PutRecords.
6. Get a shard iterator and read records.
7. Add tags to the stream.
8. List tags.
9. Remove tags.
10. Register an enhanced fan-out consumer.
11. Deregister the consumer.
12. Clean up resources.
"""

import json
import logging
import time
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError

from kinesis_wrapper import KinesisWrapper

logger = logging.getLogger(__name__)

DASHES = "-" * 80


def run_scenario(kinesis_wrapper):
    """
    Runs the Kinesis Data Streams basics scenario.

    :param kinesis_wrapper: A KinesisWrapper instance.
    """
    stream_name = f"sdk-example-stream-{int(time.time())}"
    consumer_name = "sdk-example-consumer"
    consumer_arn = None
    stream_arn = None

    print(DASHES)
    print("Welcome to the Amazon Kinesis Data Streams Scenario.")
    print(
        "This scenario demonstrates advanced stream management, batch data\n"
        "ingestion, shard inspection, tagging, and enhanced fan-out consumers."
    )
    print(DASHES)

    try:
        # --- Setup: Create stream ---
        print("\nSetting up resources...")
        print(f"Creating Kinesis stream: {stream_name}")
        kinesis_wrapper.kinesis_client.create_stream(
            StreamName=stream_name, ShardCount=1
        )
        print("Waiting for stream to become ACTIVE...")
        summary = kinesis_wrapper.wait_for_stream_active(stream_name)
        stream_arn = summary.get("StreamARN", "")
        print("Stream is now ACTIVE.")
        print(DASHES)

        # --- Step 1: List Streams ---
        print("\nStep 1: Listing streams in your account...")
        streams = kinesis_wrapper.list_streams()
        for s in streams:
            mode = s.get("StreamModeDetails", dict()).get("StreamMode", "N/A")
            print(
                f"  Stream: {s.get('StreamName')} "
                f"(Status: {s.get('StreamStatus')}, Mode: {mode})"
            )
        found = any(s.get("StreamName") == stream_name for s in streams)
        print(
            f"Found {len(streams)} stream(s). "
            f"Our scenario stream is {'present' if found else 'NOT found'}."
        )
        print(DASHES)

        # --- Step 2: Describe Stream Summary ---
        print("\nStep 2: Describing stream summary...")
        summary = kinesis_wrapper.describe_stream_summary(stream_name)
        print(f"  Stream Name: {summary.get('StreamName')}")
        print(f"  Stream ARN: {summary.get('StreamARN')}")
        print(f"  Status: {summary.get('StreamStatus')}")
        print(f"  Retention Period: {summary.get('RetentionPeriodHours')} hours")
        print(f"  Open Shard Count: {summary.get('OpenShardCount')}")
        print(f"  Encryption: {summary.get('EncryptionType', 'NONE')}")
        mode = summary.get("StreamModeDetails", dict()).get("StreamMode", "N/A")
        print(f"  Stream Mode: {mode}")
        stream_arn = summary.get("StreamARN", stream_arn)
        print(DASHES)

        # --- Step 3: List Shards ---
        print("\nStep 3: Listing shards in the stream...")
        shards = kinesis_wrapper.list_shards(stream_name)
        first_shard_id = None
        for shard in shards:
            shard_id = shard.get("ShardId", "")
            if first_shard_id is None:
                first_shard_id = shard_id
            hash_range = shard.get("HashKeyRange", dict())
            seq_range = shard.get("SequenceNumberRange", dict())
            print(f"  Shard ID: {shard_id}")
            print(
                f"    Hash Key Range: {hash_range.get('StartingHashKey')} - "
                f"{hash_range.get('EndingHashKey')}"
            )
            print(
                f"    Starting Sequence Number: "
                f"{seq_range.get('StartingSequenceNumber')}"
            )
        print(f"Found {len(shards)} shard(s).")
        print(DASHES)

        # --- Step 4: PutRecords ---
        print(f"\nStep 4: Putting 5 records into the stream using PutRecords...")
        sensor_data = [
            {"sensor_id": "sensor-001", "temperature": 72.5, "timestamp": "2024-01-15T10:00:00Z"},
            {"sensor_id": "sensor-002", "temperature": 68.3, "timestamp": "2024-01-15T10:00:01Z"},
            {"sensor_id": "sensor-001", "temperature": 73.1, "timestamp": "2024-01-15T10:00:02Z"},
            {"sensor_id": "sensor-003", "temperature": 75.8, "timestamp": "2024-01-15T10:00:03Z"},
            {"sensor_id": "sensor-002", "temperature": 67.9, "timestamp": "2024-01-15T10:00:04Z"},
        ]
        records = [
            {
                "Data": json.dumps(data).encode("utf-8"),
                "PartitionKey": data["sensor_id"],
            }
            for data in sensor_data
        ]
        put_response = kinesis_wrapper.put_records(stream_name, records)
        for idx, rec in enumerate(put_response.get("Records", list()), 1):
            if rec.get("ErrorCode"):
                print(
                    f"  Record {idx}: FAILED - {rec.get('ErrorCode')}: "
                    f"{rec.get('ErrorMessage')}"
                )
            else:
                print(
                    f"  Record {idx}: ShardId={rec.get('ShardId')}, "
                    f"SequenceNumber={rec.get('SequenceNumber')}"
                )
        failed = put_response.get("FailedRecordCount", 0)
        print(f"Successfully put 5 record(s) with {failed} failures.")
        print(DASHES)

        # --- Step 5: GetShardIterator and read records ---
        print("\nStep 5: Getting shard iterator (TRIM_HORIZON) and reading records...")
        shard_iterator = kinesis_wrapper.get_shard_iterator(
            stream_name, first_shard_id, "TRIM_HORIZON"
        )
        print(f"  Obtained shard iterator for {first_shard_id}.")
        print("  Reading records from the stream...")
        get_response = kinesis_wrapper.kinesis_client.get_records(
            ShardIterator=shard_iterator, Limit=10
        )
        data_records = get_response.get("Records", list())
        for idx, rec in enumerate(data_records, 1):
            payload = rec.get("Data", b"").decode("utf-8")
            print(f"  Record {idx}: {payload}")
        print(f"Read {len(data_records)} record(s) from the stream.")
        print(DASHES)

        # --- Step 6: Add Tags ---
        print("\nStep 6: Adding tags to the stream...")
        tags = {
            "Project": "kinesis-demo",
            "Environment": "development",
            "CostCenter": "12345",
        }
        kinesis_wrapper.add_tags_to_stream(stream_name, tags)
        for key, value in tags.items():
            print(f"  Added tag: {key} = {value}")
        print("Tags added successfully.")
        print(DASHES)

        # --- Step 7: List Tags ---
        print("\nStep 7: Listing tags for the stream...")
        tag_list = kinesis_wrapper.list_tags_for_stream(stream_name)
        for tag in tag_list:
            print(f"  Tag: {tag.get('Key')} = {tag.get('Value')}")
        print(f"Found {len(tag_list)} tag(s).")
        print(DASHES)

        # --- Step 8: Remove Tags ---
        print("\nStep 8: Removing the CostCenter tag from the stream...")
        kinesis_wrapper.remove_tags_from_stream(stream_name, ["CostCenter"])
        print("Tag removed successfully.")
        print("Verifying remaining tags...")
        remaining_tags = kinesis_wrapper.list_tags_for_stream(stream_name)
        for tag in remaining_tags:
            print(f"  Tag: {tag.get('Key')} = {tag.get('Value')}")
        print(f"Confirmed: {len(remaining_tags)} tag(s) remaining after removal.")
        print(DASHES)

        # --- Step 9: Register Consumer ---
        print("\nStep 9: Registering an enhanced fan-out consumer...")
        consumer = kinesis_wrapper.register_stream_consumer(
            stream_arn, consumer_name
        )
        consumer_arn = consumer.get("ConsumerARN", "")
        print(f"  Consumer Name: {consumer.get('ConsumerName')}")
        print(f"  Consumer ARN: {consumer_arn}")
        print(f"  Status: {consumer.get('ConsumerStatus')}")
        print("Waiting for consumer to become ACTIVE...")
        desc = kinesis_wrapper.wait_for_consumer_active(stream_arn, consumer_name)
        print(f"  Consumer status: {desc.get('ConsumerStatus')}")
        print("Enhanced fan-out consumer registered successfully.")
        print(DASHES)

        # --- Step 10: Deregister Consumer ---
        print("\nStep 10: Deregistering the enhanced fan-out consumer...")
        kinesis_wrapper.deregister_stream_consumer(consumer_arn)
        print(f"Consumer '{consumer_name}' deregistered successfully.")
        print("Enhanced fan-out charges will stop for this consumer.")
        consumer_arn = None
        print(DASHES)

    finally:
        # --- Cleanup ---
        print(DASHES)
        print("Cleaning up resources...")

        # Deregister consumer if still registered
        if consumer_arn:
            print("Ensuring consumer is deregistered...")
            try:
                kinesis_wrapper.deregister_stream_consumer(consumer_arn)
                print("  Consumer deregistered during cleanup.")
            except ClientError as err:
                if err.response["Error"]["Code"] == "ResourceNotFoundException":
                    print("  Consumer already deregistered.")
                else:
                    logger.error("Error deregistering consumer: %s", err)

        # Delete the stream
        print(f"Deleting stream: {stream_name}")
        try:
            kinesis_wrapper.kinesis_client.delete_stream(
                StreamName=stream_name, EnforceConsumerDeletion=True
            )
            print("Waiting for stream deletion to complete...")
            while True:
                try:
                    kinesis_wrapper.describe_stream_summary(stream_name)
                    time.sleep(2)
                except ClientError as err:
                    if err.response["Error"]["Code"] == "ResourceNotFoundException":
                        break
                    raise
            print("Stream deleted successfully.")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                print("Stream already deleted.")
            else:
                logger.error("Error deleting stream: %s", err)

        print(DASHES)
        print("Amazon Kinesis Data Streams scenario complete!")


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    wrapper = KinesisWrapper.from_client()
    run_scenario(wrapper)
