# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Kinesis unit tests.
"""

import datetime
import json
from test_tools.example_stubber import ExampleStubber


class KinesisStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Kinesis unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Kinesis client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_stream(
        self, stream_name, shard_count=1, stream_mode=None, error_code=None
    ):
        expected_params = {"StreamName": stream_name, "ShardCount": shard_count}
        if stream_mode is not None:
            expected_params["StreamModeDetails"] = {"StreamMode": stream_mode}
        response = {}
        self._stub_bifurcator(
            "create_stream", expected_params, response, error_code=error_code
        )

    def stub_list_streams(self, limit, stream_names, error_code=None):
        expected_params = {"Limit": limit}
        response = {
            "StreamNames": stream_names,
            "HasMoreStreams": False,
            "StreamSummaries": [
                {
                    "StreamName": name,
                    "StreamARN": (
                        f"arn:aws:kinesis:us-east-1:123456789012:stream/{name}"
                    ),
                    "StreamStatus": "ACTIVE",
                    "StreamModeDetails": {"StreamMode": "PROVISIONED"},
                    "StreamCreationTimestamp": datetime.datetime.now(),
                }
                for name in stream_names
            ],
        }
        self._stub_bifurcator(
            "list_streams", expected_params, response, error_code=error_code
        )

    def stub_describe_stream_summary(
        self, stream_name, stream_arn, status, open_shard_count, error_code=None
    ):
        expected_params = {"StreamName": stream_name}
        response = {
            "StreamDescriptionSummary": {
                "StreamName": stream_name,
                "StreamARN": stream_arn,
                "StreamStatus": status,
                "StreamModeDetails": {"StreamMode": "PROVISIONED"},
                "RetentionPeriodHours": 24,
                "StreamCreationTimestamp": datetime.datetime.now(),
                "EnhancedMonitoring": [],
                "EncryptionType": "NONE",
                "OpenShardCount": open_shard_count,
            }
        }
        self._stub_bifurcator(
            "describe_stream_summary", expected_params, response, error_code=error_code
        )

    def stub_update_shard_count(
        self,
        stream_name,
        target_shard_count,
        current_shard_count,
        scaling_type="UNIFORM_SCALING",
        error_code=None,
    ):
        expected_params = {
            "StreamName": stream_name,
            "TargetShardCount": target_shard_count,
            "ScalingType": scaling_type,
        }
        response = {
            "StreamName": stream_name,
            "CurrentShardCount": current_shard_count,
            "TargetShardCount": target_shard_count,
        }
        self._stub_bifurcator(
            "update_shard_count", expected_params, response, error_code=error_code
        )

    def stub_describe_stream(self, stream_name, stream_arn, status, error_code=None):
        expected_params = {"StreamName": stream_name}
        response = {
            "StreamDescription": {
                "StreamName": stream_name,
                "StreamARN": stream_arn,
                "StreamStatus": status,
                "Shards": [],
                "HasMoreShards": False,
                "RetentionPeriodHours": 10,
                "StreamCreationTimestamp": datetime.datetime.now(),
                "EnhancedMonitoring": [],
            }
        }
        self._stub_bifurcator(
            "describe_stream", expected_params, response, error_code=error_code
        )

    def stub_delete_stream(
        self, stream_name, enforce_consumer_deletion=None, error_code=None
    ):
        expected_params = {"StreamName": stream_name}
        if enforce_consumer_deletion is not None:
            expected_params["EnforceConsumerDeletion"] = enforce_consumer_deletion
        response = {}
        self._stub_bifurcator(
            "delete_stream", expected_params, response, error_code=error_code
        )

    def stub_put_record(self, stream, data, partition_key, error_code=None):
        expected_params = {
            "StreamName": stream,
            "Data": json.dumps(data),
            "PartitionKey": partition_key,
        }
        response = {"ShardId": "test-id", "SequenceNumber": "test-number"}
        self._stub_bifurcator(
            "put_record", expected_params, response, error_code=error_code
        )

    def stub_put_records(self, stream, batch, partition_key, error_code=None):
        expected_params = {
            "StreamName": stream,
            "Records": [
                {"Data": json.dumps(record), "PartitionKey": partition_key}
                for record in batch
            ],
        }
        response = {
            "Records": [{"ShardId": "test-id", "SequenceNumber": "test-number"}]
        }
        self._stub_bifurcator(
            "put_records", expected_params, response, error_code=error_code
        )

    def stub_put_records_batch(self, stream, records, error_code=None):
        """
        Stubs put_records where each record carries its own partition key.

        :param records: A list of dicts, each with 'Data' (dict) and
                        'PartitionKey' (str), matching the wrapper input.
        """
        expected_params = {
            "StreamName": stream,
            "Records": [
                {
                    "Data": json.dumps(record["Data"]),
                    "PartitionKey": record["PartitionKey"],
                }
                for record in records
            ],
        }
        # NOTE: The PutRecords API models FailedRecordCount with a minimum
        # value of 1, so the botocore Stubber rejects FailedRecordCount=0.
        # It is omitted here; wrapper code reads it via .get(..., 0).
        response = {
            "Records": [
                {"ShardId": "test-id", "SequenceNumber": f"seq-{i}"}
                for i in range(len(records))
            ]
        }
        self._stub_bifurcator(
            "put_records", expected_params, response, error_code=error_code
        )

    def stub_get_shard_iterator(
        self,
        stream_name,
        shard_id,
        shard_iter,
        iterator_type="LATEST",
        error_code=None,
    ):
        expected_params = {
            "StreamName": stream_name,
            "ShardId": shard_id,
            "ShardIteratorType": iterator_type,
        }
        response = {"ShardIterator": shard_iter}
        self._stub_bifurcator(
            "get_shard_iterator", expected_params, response, error_code=error_code
        )

    def stub_get_records(self, shard_iter, limit, records, error_code=None):
        expected_params = {"ShardIterator": shard_iter, "Limit": limit}
        response = {
            "NextShardIterator": shard_iter,
            "Records": [
                {"Data": record, "SequenceNumber": "1", "PartitionKey": "partition_key"}
                for record in records
            ],
        }
        self._stub_bifurcator(
            "get_records", expected_params, response, error_code=error_code
        )
