# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Kinesis Data Streams
to run a basics scenario that demonstrates the full lifecycle of a data stream:

1. List existing streams.
2. Create a new provisioned-mode stream with 2 shards.
3. Wait for the stream to become ACTIVE.
4. Put a single record.
5. Put a batch of 5 records.
6. Get a shard iterator and read records back.
7. Describe the stream summary.
8. Scale the stream from 2 to 4 shards.
9. Delete the stream.
"""

import logging
import os
import random
import string
import sys
import time
from datetime import datetime, timezone

from botocore.exceptions import ClientError

# Ensure the parent directory (python/example_code/kinesis) is importable so
# this scenario runs regardless of the working directory it is launched from.
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from kinesis_wrapper import KinesisStreamWrapper  # noqa: E402

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisScenario]
class KinesisScenario:
    """Runs an interactive scenario that demonstrates Kinesis Data Streams basics."""

    def __init__(self, wrapper: KinesisStreamWrapper):
        """
        :param wrapper: A KinesisStreamWrapper instance that wraps Kinesis operations.
        """
        self.wrapper = wrapper
        self.stream_name = None

    def _generate_stream_name(self) -> str:
        """Generates a unique stream name for the scenario."""
        suffix = "".join(random.choices(string.ascii_lowercase + string.digits, k=8))
        return f"sdk-example-stream-{suffix}"

    def run(self) -> None:
        """Runs the Kinesis Data Streams basics scenario."""
        print("-" * 70)
        print("Welcome to the Amazon Kinesis Data Streams Basics Scenario!")
        print("-" * 70)

        self.stream_name = self._generate_stream_name()
        stream_created = False

        try:
            # Step 1: List existing streams
            self._list_streams()

            # Step 2: Create a data stream
            self._create_stream()
            stream_created = True

            # Step 3: Wait for stream to become ACTIVE
            details = self._wait_for_active()

            # Step 4: Put a single record
            self._put_single_record()

            # Step 5: Put a batch of records
            self._put_batch_records()

            # Step 6: Read records back
            self._read_records(details)

            # Step 7: Describe stream summary
            self._describe_summary()

            # Step 8: Scale stream
            self._scale_stream()

        except Exception:
            logger.exception("An error occurred during the scenario.")
        finally:
            # Step 9: Cleanup
            if stream_created:
                self._cleanup()

        print("-" * 70)
        print("Amazon Kinesis Data Streams Basics Scenario complete!")
        print("-" * 70)

    def _list_streams(self) -> None:
        """Step 1: List existing streams."""
        print("\nListing existing streams...")
        response = self.wrapper.list_streams(limit=10)
        stream_names = response.get("StreamNames", list())
        if stream_names:
            print(f"  Found {len(stream_names)} stream(s): {', '.join(stream_names)}")
        else:
            print("  No existing streams found.")

    def _create_stream(self) -> None:
        """Step 2: Create a data stream."""
        print(f"\nCreating stream '{self.stream_name}' with 2 shards...")
        self.wrapper.create_stream(self.stream_name, shard_count=2)
        print("  Stream creation initiated.")

    def _wait_for_active(self) -> dict:
        """Step 3: Wait for stream to become ACTIVE."""
        print("  Waiting for ACTIVE status...")
        details = self.wrapper.wait_for_stream_active(
            self.stream_name, max_wait_seconds=60
        )
        stream_arn = details.get("StreamARN", "N/A")
        shards = details.get("Shards", list())
        retention = details.get("RetentionPeriodHours", "N/A")
        print(f"  Stream is ACTIVE. ARN: {stream_arn}")
        print(f"  Shards: {len(shards)} | Retention: {retention} hours")
        return details

    def _put_single_record(self) -> None:
        """Step 4: Put a single record."""
        print("\nPutting a single record (sensor-1 reading)...")
        payload = {
            "sensor_id": "sensor-1",
            "temperature": 22.5,
            "timestamp": datetime.now(timezone.utc).isoformat(),
        }
        response = self.wrapper.put_record(
            stream_name=self.stream_name,
            data=payload,
            partition_key="sensor-1",
        )
        print(
            f"  Record delivered to {response.get('ShardId', 'N/A')}, "
            f"sequence: {response.get('SequenceNumber', 'N/A')}"
        )

    def _put_batch_records(self) -> None:
        """Step 5: Put multiple records."""
        print("\nPutting a batch of 5 records...")
        sensor_ids = ["sensor-1", "sensor-2", "sensor-3", "sensor-1", "sensor-2"]
        temperatures = [23.1, 18.7, 19.8, 24.0, 17.5]
        records = list()
        for sid, temp in zip(sensor_ids, temperatures):
            records.append(
                {
                    "Data": {
                        "sensor_id": sid,
                        "temperature": temp,
                        "timestamp": datetime.now(timezone.utc).isoformat(),
                    },
                    "PartitionKey": sid,
                }
            )
        response = self.wrapper.put_records(
            stream_name=self.stream_name, records=records
        )
        failed = response.get("FailedRecordCount", 0)
        result_records = response.get("Records", list())
        print(
            f"  All {len(result_records)} records submitted " f"({failed} failure(s))."
        )
        for i, rec in enumerate(result_records):
            if rec.get("ErrorCode"):
                print(
                    f"    Record {i + 1}: FAILED — {rec.get('ErrorCode')}: "
                    f"{rec.get('ErrorMessage', '')}"
                )
            else:
                print(
                    f"    Record {i + 1}: shard={rec.get('ShardId', 'N/A')}, "
                    f"seq={rec.get('SequenceNumber', 'N/A')}"
                )

    def _read_records(self, details: dict) -> None:
        """Step 6: Read records from the first shard."""
        shards = details.get("Shards", list())
        if not shards:
            print("\n  No shards available to read from.")
            return
        shard_id = shards[0]["ShardId"]
        print(f"\nReading records from {shard_id} (TRIM_HORIZON)...")

        shard_iterator = self.wrapper.get_shard_iterator(
            stream_name=self.stream_name,
            shard_id=shard_id,
            iterator_type="TRIM_HORIZON",
        )

        all_records = list()
        attempts = 0
        max_attempts = 3
        millis_behind = 0
        while attempts < max_attempts:
            response = self.wrapper.get_records(shard_iterator=shard_iterator, limit=10)
            fetched = response.get("Records", list())
            all_records.extend(fetched)
            millis_behind = response.get("MillisBehindLatest", 0)
            if fetched:
                break
            # Records may not yet be available; retry with the next iterator.
            shard_iterator = response.get("NextShardIterator", "")
            if not shard_iterator:
                break
            attempts += 1
            logger.info("No records yet, retrying... (attempt %d)", attempts)
            time.sleep(1)

        if all_records:
            for idx, record in enumerate(all_records, start=1):
                data = record.get("Data", b"")
                if isinstance(data, bytes):
                    data = data.decode("utf-8")
                print(f"  Record {idx}: {record.get('PartitionKey', '')} | {data}")
            print(f"  MillisBehindLatest: {millis_behind}")
        else:
            print("  No records retrieved from this shard.")

    def _describe_summary(self) -> None:
        """Step 7: Describe stream summary."""
        print("\nDescribing stream summary...")
        summary = self.wrapper.describe_stream_summary(self.stream_name)
        mode = summary.get("StreamModeDetails", dict()).get("StreamMode", "UNKNOWN")
        print(f"  Stream: {summary.get('StreamName', 'N/A')}")
        print(f"  ARN: {summary.get('StreamARN', 'N/A')}")
        print(f"  Status: {summary.get('StreamStatus', 'N/A')}")
        print(
            f"  Mode: {mode} | Open Shards: "
            f"{summary.get('OpenShardCount', 'N/A')} | "
            f"Retention: {summary.get('RetentionPeriodHours', 'N/A')}h"
        )
        print(f"  Encryption: {summary.get('EncryptionType', 'NONE')}")
        creation_ts = summary.get("StreamCreationTimestamp", None)
        if creation_ts:
            print(f"  Created: {creation_ts}")

    def _scale_stream(self) -> None:
        """Step 8: Scale stream from 2 to 4 shards."""
        print("\nScaling stream from 2 to 4 shards...")
        response = self.wrapper.update_shard_count(
            stream_name=self.stream_name,
            target_shard_count=4,
        )
        print(
            f"  UpdateShardCount: current={response.get('CurrentShardCount', 'N/A')}, "
            f"target={response.get('TargetShardCount', 'N/A')}"
        )
        print("  Waiting for stream to return to ACTIVE...")
        self.wrapper.wait_for_stream_active(self.stream_name, max_wait_seconds=120)
        print("  Stream is ACTIVE with updated shard configuration.")

    def _cleanup(self) -> None:
        """Step 9: Delete the stream."""
        print(f"\nCleaning up: deleting stream '{self.stream_name}'...")
        try:
            self.wrapper.delete_stream(self.stream_name)
            print("  Stream deletion initiated. Goodbye!")
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                print("  Stream already deleted or not found — nothing to clean up.")
            else:
                logger.exception("Error during cleanup.")


def main():
    """Entry point for the Kinesis Data Streams basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    wrapper = KinesisStreamWrapper.from_client()
    scenario = KinesisScenario(wrapper)
    scenario.run()


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.kinesis.KinesisScenario]
