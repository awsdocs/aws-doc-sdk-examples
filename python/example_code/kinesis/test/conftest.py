import sys
import os

# Add the kinesis root to the path so tests can import modules like
# 'streams.kinesis_stream', 'kinesis_wrapper', etc.
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from botocore.stub import Stubber


class KinesisStubber:
    """Helper that wraps botocore Stubber with Kinesis-specific stub methods."""

    def __init__(self, client):
        self.client = client
        self.stubber = Stubber(client)
        self.stubber.activate()

    def stub_create_stream(self, stream_name, error_code=None):
        expected_params = {"StreamName": stream_name, "ShardCount": 1}
        if error_code:
            self.stubber.add_client_error(
                "create_stream",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response("create_stream", {}, expected_params)

    def stub_describe_stream(self, stream_name, stream_arn, stream_status, error_code=None):
        expected_params = {"StreamName": stream_name}
        if error_code:
            self.stubber.add_client_error(
                "describe_stream",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "describe_stream",
                {
                    "StreamDescription": {
                        "StreamName": stream_name,
                        "StreamARN": stream_arn,
                        "StreamStatus": stream_status,
                        "Shards": [
                            {
                                "ShardId": "shardId-000000000000",
                                "HashKeyRange": {
                                    "StartingHashKey": "0",
                                    "EndingHashKey": "340282366920938463463374607431768211455",
                                },
                                "SequenceNumberRange": {
                                    "StartingSequenceNumber": "49590338271490256608559692540925702759324208523137515522",
                                },
                            }
                        ],
                        "HasMoreShards": False,
                        "RetentionPeriodHours": 24,
                        "StreamCreationTimestamp": "2021-01-01T00:00:00+00:00",
                        "EnhancedMonitoring": [],
                        "EncryptionType": "NONE",
                    }
                },
                expected_params,
            )

    def stub_delete_stream(self, stream_name, error_code=None):
        expected_params = {"StreamName": stream_name}
        if error_code:
            self.stubber.add_client_error(
                "delete_stream",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response("delete_stream", {}, expected_params)

    def stub_put_record(self, stream_name, data, partition_key, error_code=None):
        import json

        expected_params = {
            "StreamName": stream_name,
            "Data": json.dumps(data),
            "PartitionKey": partition_key,
        }
        if error_code:
            self.stubber.add_client_error(
                "put_record",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "put_record",
                {
                    "ShardId": "shardId-000000000000",
                    "SequenceNumber": "49590338271490256608559692538361571095921575989136588898",
                },
                expected_params,
            )

    def stub_put_records(self, stream_name, records, partition_key, error_code=None):
        import json

        kinesis_records = [
            {"Data": json.dumps(r), "PartitionKey": partition_key} for r in records
        ]
        expected_params = {"StreamName": stream_name, "Records": kinesis_records}
        if error_code:
            self.stubber.add_client_error(
                "put_records",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "put_records",
                {
                    # Omit FailedRecordCount when 0 — botocore enforces min value of 1
                    "Records": [
                        {
                            "ShardId": "shardId-000000000000",
                            "SequenceNumber": "49590338271490256608559692538361571095921575989136588898",
                        }
                        for _ in records
                    ],
                },
                expected_params,
            )

    def stub_get_shard_iterator(self, stream_name, shard_id, shard_iter, error_code=None):
        expected_params = {
            "StreamName": stream_name,
            "ShardId": shard_id,
            "ShardIteratorType": "LATEST",
        }
        if error_code:
            self.stubber.add_client_error(
                "get_shard_iterator",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "get_shard_iterator",
                {"ShardIterator": shard_iter},
                expected_params,
            )

    def stub_get_records(self, shard_iter, count, records, error_code=None):
        expected_params = {"ShardIterator": shard_iter, "Limit": 10}
        if error_code:
            self.stubber.add_client_error(
                "get_records",
                service_error_code=error_code,
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "get_records",
                {
                    "Records": [
                        {
                            "SequenceNumber": str(i),
                            "ApproximateArrivalTimestamp": "2021-01-01T00:00:00+00:00",
                            # Store data as-is (bytes or str); the real Kinesis API
                            # returns the raw bytes that were put in.
                            "Data": r if isinstance(r, (bytes, bytearray)) else str(r).encode(),
                            "PartitionKey": "partitionkey",
                        }
                        for i, r in enumerate(records)
                    ],
                    "NextShardIterator": shard_iter,
                    "MillisBehindLatest": 0,
                },
                expected_params,
            )


@pytest.fixture
def make_stubber():
    """
    Returns a factory function that creates a KinesisStubber for a given client.
    Tracks all created stubbers so their contexts can be verified after each test.
    """
    stubbers = []

    def _make_stubber(client):
        stubber = KinesisStubber(client)
        stubbers.append(stubber)
        return stubber

    yield _make_stubber

    for s in stubbers:
        s.stubber.assert_no_pending_responses()
        s.stubber.deactivate()
