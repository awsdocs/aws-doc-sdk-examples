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

    def stub_create_stream(self, stream_name, error_code=None):
        expected_params = {"StreamName": stream_name, "ShardCount": 1}
        response = {}
        self._stub_bifurcator(
            "create_stream", expected_params, response, error_code=error_code
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

    def stub_delete_stream(self, stream_name, error_code=None):
        expected_params = {"StreamName": stream_name}
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

    def stub_get_shard_iterator(
        self, stream_name, shard_id, shard_iter, error_code=None
    ):
        expected_params = {
            "StreamName": stream_name,
            "ShardId": shard_id,
            "ShardIteratorType": "LATEST",
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

    def stub_put_records_batch(self, stream_name, records, error_code=None):
        expected_params = {"StreamName": stream_name, "Records": records}
        response = {
            "Records": [
                {"ShardId": "shardId-000000000000", "SequenceNumber": str(index)}
                for index, _ in enumerate(records)
            ],
        }
        self._stub_bifurcator(
            "put_records", expected_params, response, error_code=error_code
        )

    def stub_list_streams(self, stream_names, limit=100, error_code=None):
        expected_params = {"Limit": limit}
        response = {
            "StreamNames": stream_names,
            "HasMoreStreams": False,
            "StreamSummaries": [
                {
                    "StreamName": name,
                    "StreamARN": f"arn:aws:kinesis:us-east-1:123456789012:stream/{name}",
                    "StreamStatus": "ACTIVE",
                }
                for name in stream_names
            ],
        }
        self._stub_bifurcator(
            "list_streams", expected_params, response, error_code=error_code
        )

    def stub_describe_stream_summary(
        self, stream_name, stream_arn, status="ACTIVE", error_code=None
    ):
        expected_params = {"StreamName": stream_name}
        response = {
            "StreamDescriptionSummary": {
                "StreamName": stream_name,
                "StreamARN": stream_arn,
                "StreamStatus": status,
                "RetentionPeriodHours": 24,
                "StreamCreationTimestamp": datetime.datetime.now(),
                "EnhancedMonitoring": [],
                "OpenShardCount": 1,
            }
        }
        self._stub_bifurcator(
            "describe_stream_summary", expected_params, response, error_code=error_code
        )

    def stub_list_shards(self, stream_name, shard_ids, error_code=None):
        expected_params = {"StreamName": stream_name}
        response = {
            "Shards": [
                {
                    "ShardId": shard_id,
                    "HashKeyRange": {
                        "StartingHashKey": "0",
                        "EndingHashKey": "340282366920938463463374607431768211455",
                    },
                    "SequenceNumberRange": {"StartingSequenceNumber": "0"},
                }
                for shard_id in shard_ids
            ],
        }
        self._stub_bifurcator(
            "list_shards", expected_params, response, error_code=error_code
        )

    def stub_add_tags_to_stream(self, stream_name, tags, error_code=None):
        expected_params = {"StreamName": stream_name, "Tags": tags}
        response = {}
        self._stub_bifurcator(
            "add_tags_to_stream", expected_params, response, error_code=error_code
        )

    def stub_list_tags_for_stream(
        self, stream_name, tags, has_more=False, error_code=None
    ):
        expected_params = {"StreamName": stream_name}
        response = {
            "Tags": [{"Key": k, "Value": v} for k, v in tags.items()],
            "HasMoreTags": has_more,
        }
        self._stub_bifurcator(
            "list_tags_for_stream", expected_params, response, error_code=error_code
        )

    def stub_remove_tags_from_stream(self, stream_name, tag_keys, error_code=None):
        expected_params = {"StreamName": stream_name, "TagKeys": tag_keys}
        response = {}
        self._stub_bifurcator(
            "remove_tags_from_stream", expected_params, response, error_code=error_code
        )

    def stub_register_stream_consumer(
        self,
        stream_arn,
        consumer_name,
        consumer_arn,
        status="CREATING",
        error_code=None,
    ):
        expected_params = {"StreamARN": stream_arn, "ConsumerName": consumer_name}
        response = {
            "Consumer": {
                "ConsumerName": consumer_name,
                "ConsumerARN": consumer_arn,
                "ConsumerStatus": status,
                "ConsumerCreationTimestamp": datetime.datetime.now(),
            }
        }
        self._stub_bifurcator(
            "register_stream_consumer",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_deregister_stream_consumer(self, consumer_arn, error_code=None):
        expected_params = {"ConsumerARN": consumer_arn}
        response = {}
        self._stub_bifurcator(
            "deregister_stream_consumer",
            expected_params,
            response,
            error_code=error_code,
        )
