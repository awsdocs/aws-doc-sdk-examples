# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Kinesis Data Streams Basics Scenario

This scenario demonstrates:
1. Creating a Kinesis data stream (ON_DEMAND mode)
2. Waiting for the stream to become ACTIVE
3. Putting a single record (IoT sensor telemetry)
4. Putting multiple records in a batch
5. Listing shards in the stream
6. Reading records from shards
7. Tagging the stream
8. Verifying tags
9. Cleaning up by deleting the stream
"""

import json
import logging
import time
from datetime import datetime, timezone

import boto3

from kinesis_wrapper import KinesisWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisScenario]
class KinesisScenario:
    """Demonstrates Amazon Kinesis Data Streams basics."""

    def __init__(self, kinesis_wrapper: KinesisWrapper):
        """
        :param kinesis_wrapper: An instance of KinesisWrapper.
        """
        self.kinesis_wrapper = kinesis_wrapper

    def run_scenario(self):
        """
        Runs the Kinesis basics scenario end-to-end.
        """
        stream_name = f"sdk-example-stream-{int(time.time())}"

        print("=" * 60)
        print("Amazon Kinesis Data Streams - Basics Scenario")
        print("=" * 60)

        try:
            # Step 1: Create a stream
            print(f"\n1. Creating stream: {stream_name}")
            self.kinesis_wrapper.create_stream(stream_name)
            print(f"   Stream '{stream_name}' creation initiated.")

            # Step 2: Wait for ACTIVE
            print("\n2. Waiting for stream to become ACTIVE...")
            description = self.kinesis_wrapper.wait_for_stream_active(stream_name)
            print(f"   Stream ARN: {description['StreamARN']}")
            print(f"   Stream Status: {description['StreamStatus']}")

            # Step 3: Put a single record
            print("\n3. Putting a single sensor record...")
            sensor_data = {
                "sensor_id": "sensor-1",
                "temperature": 72.5,
                "humidity": 45.2,
                "timestamp": datetime.now(timezone.utc).isoformat(),
            }
            result = self.kinesis_wrapper.put_record(
                stream_name, sensor_data, "sensor-1"
            )
            print(f"   Written to shard: {result['ShardId']}")
            print(f"   Sequence number: {result['SequenceNumber']}")

            # Step 4: Put records in batch
            print("\n4. Putting batch of 5 sensor records...")
            batch_records = list()
            for i in range(1, 6):
                batch_records.append(
                    {
                        "Data": {
                            "sensor_id": f"sensor-{i}",
                            "temperature": 68.0 + i * 2.5,
                            "humidity": 40.0 + i * 1.5,
                            "timestamp": datetime.now(timezone.utc).isoformat(),
                        },
                        "PartitionKey": f"sensor-{i}",
                    }
                )
            batch_result = self.kinesis_wrapper.put_records(stream_name, batch_records)
            print(
                f"   Successfully wrote {5 - batch_result['FailedRecordCount']} records."
            )
            if batch_result["FailedRecordCount"] > 0:
                print(f"   Failed records: {batch_result['FailedRecordCount']}")

            # Step 5: List shards
            print("\n5. Listing shards...")
            shards = self.kinesis_wrapper.list_shards(stream_name)
            print(f"   Found {len(shards)} shard(s):")
            for shard in shards:
                print(f"     - {shard['ShardId']}")

            # Step 6: Read records from shards
            print("\n6. Reading records from shards...")
            all_records = list()
            # ON_DEMAND streams may have multiple shards; read from all to find records
            for shard in shards:
                shard_id = shard["ShardId"]
                shard_iterator = self.kinesis_wrapper.get_shard_iterator(
                    stream_name, shard_id, "TRIM_HORIZON"
                )
                # Retry to allow propagation delay
                for attempt in range(5):
                    response = self.kinesis_wrapper.get_records(shard_iterator)
                    records = response["Records"]
                    if records:
                        all_records.extend(records)
                        break
                    next_iter = response.get("NextShardIterator")
                    if next_iter is None:
                        break
                    shard_iterator = next_iter
                    time.sleep(1)

            print(f"   Retrieved {len(all_records)} record(s):")
            for record in all_records[:5]:
                data = json.loads(record["Data"])
                print(
                    f"     - Partition: {record['PartitionKey']}, "
                    f"Data: {data}"
                )
            if len(all_records) > 5:
                print(f"     ... and {len(all_records) - 5} more record(s).")

            # Step 7: Add tags
            print("\n7. Adding tags to stream...")
            tags = {"Environment": "Development", "Project": "IoTSensorDemo"}
            self.kinesis_wrapper.add_tags_to_stream(stream_name, tags)
            print("   Tags added successfully.")

            # Step 8: List tags
            print("\n8. Verifying tags...")
            stream_tags = self.kinesis_wrapper.list_tags_for_stream(stream_name)
            for tag in stream_tags:
                print(f"     {tag['Key']}: {tag['Value']}")

            print("\n" + "=" * 60)
            print("Scenario completed successfully!")
            print("=" * 60)

        finally:
            # Step 9: Cleanup - Delete stream
            print(f"\n9. Cleaning up: Deleting stream '{stream_name}'...")
            try:
                self.kinesis_wrapper.delete_stream(stream_name)
                print("   Stream deletion initiated.")
            except Exception as cleanup_error:
                logger.error("Error during cleanup: %s", cleanup_error)
                print(f"   Warning: Cleanup error: {cleanup_error}")


# snippet-end:[python.example_code.kinesis.KinesisScenario]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    wrapper = KinesisWrapper.from_client()
    scenario = KinesisScenario(wrapper)
    scenario.run_scenario()