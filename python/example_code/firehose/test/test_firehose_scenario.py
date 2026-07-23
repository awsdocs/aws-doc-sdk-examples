# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the Amazon Data Firehose Basics Scenario using stub clients.

These tests use botocore's Stubber to mock all AWS API calls, so they
run without any AWS credentials or network access.

Run with: pytest test_firehose_scenario.py -v
"""

import sys
from datetime import datetime, timezone
from unittest.mock import patch

import boto3
import pytest
from botocore.stub import Stubber

sys.path.append("..")
from firehose_wrapper import FirehoseWrapper

STREAM_NAME = "test-firehose-stream"
STREAM_ARN = (
    "arn:aws:firehose:us-east-1:123456789012:deliverystream/test-firehose-stream"
)
ROLE_ARN = "arn:aws:iam::123456789012:role/firehose-test-role"
BUCKET_ARN = "arn:aws:s3:::test-firehose-bucket"
STACK_NAME = "test-firehose-stack"


@pytest.fixture
def firehose_client_and_stubber():
    """Creates a Firehose client with a Stubber attached."""
    client = boto3.client("firehose", region_name="us-east-1")
    stubber = Stubber(client)
    stubber.activate()
    yield client, stubber
    stubber.deactivate()


@pytest.fixture
def wrapper(firehose_client_and_stubber):
    """Creates a FirehoseWrapper with a stubbed client."""
    client, stubber = firehose_client_and_stubber
    return FirehoseWrapper(client), stubber


class TestFirehoseWrapper:
    """Tests for individual FirehoseWrapper methods."""

    def test_create_delivery_stream(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "create_delivery_stream",
            {"DeliveryStreamARN": STREAM_ARN},
            {
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamType": "DirectPut",
                "ExtendedS3DestinationConfiguration": {
                    "RoleARN": ROLE_ARN,
                    "BucketARN": BUCKET_ARN,
                    "BufferingHints": {
                        "SizeInMBs": 1,
                        "IntervalInSeconds": 60,
                    },
                },
            },
        )

        result = firehose_wrapper.create_delivery_stream(
            stream_name=STREAM_NAME,
            role_arn=ROLE_ARN,
            bucket_arn=BUCKET_ARN,
            buffer_size_mb=1,
            buffer_interval_seconds=60,
        )
        assert result == STREAM_ARN
        stubber.assert_no_pending_responses()

    def test_describe_delivery_stream(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "describe_delivery_stream",
            {
                "DeliveryStreamDescription": {
                    "DeliveryStreamName": STREAM_NAME,
                    "DeliveryStreamARN": STREAM_ARN,
                    "DeliveryStreamStatus": "ACTIVE",
                    "DeliveryStreamType": "DirectPut",
                    "VersionId": "1",
                    "CreateTimestamp": datetime(2024, 1, 1, tzinfo=timezone.utc),
                    "Destinations": [],
                    "HasMoreDestinations": False,
                }
            },
            {"DeliveryStreamName": STREAM_NAME},
        )

        result = firehose_wrapper.describe_delivery_stream(STREAM_NAME)
        assert result["DeliveryStreamStatus"] == "ACTIVE"
        assert result["DeliveryStreamName"] == STREAM_NAME
        stubber.assert_no_pending_responses()

    def test_list_delivery_streams(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "list_delivery_streams",
            {
                "DeliveryStreamNames": [STREAM_NAME, "another-stream"],
                "HasMoreDeliveryStreams": False,
            },
            {"DeliveryStreamType": "DirectPut"},
        )

        result = firehose_wrapper.list_delivery_streams(stream_type="DirectPut")
        assert STREAM_NAME in result
        assert len(result) == 2
        stubber.assert_no_pending_responses()

    def test_tag_delivery_stream(self, wrapper):
        firehose_wrapper, stubber = wrapper
        tags = [
            {"Key": "Environment", "Value": "Test"},
            {"Key": "Project", "Value": "FirehoseBasics"},
        ]
        stubber.add_response(
            "tag_delivery_stream",
            {},
            {"DeliveryStreamName": STREAM_NAME, "Tags": tags},
        )

        firehose_wrapper.tag_delivery_stream(STREAM_NAME, tags)
        stubber.assert_no_pending_responses()

    def test_list_tags_for_delivery_stream(self, wrapper):
        firehose_wrapper, stubber = wrapper
        tags = [
            {"Key": "Environment", "Value": "Test"},
            {"Key": "Project", "Value": "FirehoseBasics"},
        ]
        stubber.add_response(
            "list_tags_for_delivery_stream",
            {"Tags": tags, "HasMoreTags": False},
            {"DeliveryStreamName": STREAM_NAME},
        )

        result = firehose_wrapper.list_tags_for_delivery_stream(STREAM_NAME)
        assert len(result) == 2
        assert result[0]["Key"] == "Environment"
        stubber.assert_no_pending_responses()

    def test_start_delivery_stream_encryption(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "start_delivery_stream_encryption",
            {},
            {
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamEncryptionConfigurationInput": {
                    "KeyType": "AWS_OWNED_CMK",
                },
            },
        )

        firehose_wrapper.start_delivery_stream_encryption(STREAM_NAME)
        stubber.assert_no_pending_responses()

    def test_put_record(self, wrapper):
        firehose_wrapper, stubber = wrapper
        data = '{"sensor_id": "sensor-001", "temperature": 72.5}\n'
        stubber.add_response(
            "put_record",
            {"RecordId": "test-record-id-12345", "Encrypted": True},
            {
                "DeliveryStreamName": STREAM_NAME,
                "Record": {"Data": data.encode("utf-8")},
            },
        )

        result = firehose_wrapper.put_record(STREAM_NAME, data)
        assert result["RecordId"] == "test-record-id-12345"
        assert result["Encrypted"] is True
        stubber.assert_no_pending_responses()

    def test_put_record_batch(self, wrapper):
        firehose_wrapper, stubber = wrapper
        records = [
            '{"sensor_id": "sensor-001", "temperature": 73.2}\n',
            '{"sensor_id": "sensor-002", "temperature": 68.9}\n',
            '{"sensor_id": "sensor-003", "temperature": 75.1}\n',
        ]
        stubber.add_response(
            "put_record_batch",
            {
                "FailedPutCount": 0,
                "Encrypted": True,
                "RequestResponses": [{"RecordId": f"record-id-{i}"} for i in range(3)],
            },
            {
                "DeliveryStreamName": STREAM_NAME,
                "Records": [{"Data": r.encode("utf-8")} for r in records],
            },
        )

        result = firehose_wrapper.put_record_batch(STREAM_NAME, records)
        assert result["FailedPutCount"] == 0
        assert len(result["RequestResponses"]) == 3
        stubber.assert_no_pending_responses()

    def test_stop_delivery_stream_encryption(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "stop_delivery_stream_encryption",
            {},
            {"DeliveryStreamName": STREAM_NAME},
        )

        firehose_wrapper.stop_delivery_stream_encryption(STREAM_NAME)
        stubber.assert_no_pending_responses()

    def test_delete_delivery_stream(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_response(
            "delete_delivery_stream",
            {},
            {"DeliveryStreamName": STREAM_NAME},
        )

        firehose_wrapper.delete_delivery_stream(STREAM_NAME)
        stubber.assert_no_pending_responses()


class TestFirehoseScenario:
    """Tests for the full FirehoseScenario using stubbed clients."""

    def _make_active_description(self):
        return {
            "DeliveryStreamDescription": {
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamARN": STREAM_ARN,
                "DeliveryStreamStatus": "ACTIVE",
                "DeliveryStreamType": "DirectPut",
                "VersionId": "1",
                "CreateTimestamp": datetime(2024, 1, 1, tzinfo=timezone.utc),
                "Destinations": [],
                "HasMoreDestinations": False,
            }
        }

    def _make_encryption_description(self, status):
        desc = self._make_active_description()
        desc["DeliveryStreamDescription"]["DeliveryStreamEncryptionConfiguration"] = {
            "Status": status,
            "KeyType": "AWS_OWNED_CMK",
        }
        return desc

    def test_full_scenario(self, capsys):
        """Test the full scenario with all steps stubbed."""
        self._test_scenario_steps(capsys)

    def _test_scenario_steps(self, capsys):
        """Test scenario by running each step with proper stubs."""
        firehose_client = boto3.client("firehose", region_name="us-east-1")
        stubber = Stubber(firehose_client)
        stubber.activate()

        firehose_wrapper = FirehoseWrapper(firehose_client)

        # Step 1: Create stream
        stubber.add_response(
            "create_delivery_stream",
            {"DeliveryStreamARN": STREAM_ARN},
            {
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamType": "DirectPut",
                "ExtendedS3DestinationConfiguration": {
                    "RoleARN": ROLE_ARN,
                    "BucketARN": BUCKET_ARN,
                    "BufferingHints": {"SizeInMBs": 1, "IntervalInSeconds": 60},
                },
            },
        )
        arn = firehose_wrapper.create_delivery_stream(
            STREAM_NAME, ROLE_ARN, BUCKET_ARN, 1, 60
        )
        assert arn == STREAM_ARN

        # Step 2: Wait for active
        stubber.add_response(
            "describe_delivery_stream",
            self._make_active_description(),
            {"DeliveryStreamName": STREAM_NAME},
        )
        desc = firehose_wrapper.wait_for_stream_active(STREAM_NAME, timeout=10)
        assert desc["DeliveryStreamStatus"] == "ACTIVE"

        # Step 3: List streams
        stubber.add_response(
            "list_delivery_streams",
            {"DeliveryStreamNames": [STREAM_NAME], "HasMoreDeliveryStreams": False},
            {"DeliveryStreamType": "DirectPut"},
        )
        streams = firehose_wrapper.list_delivery_streams("DirectPut")
        assert STREAM_NAME in streams

        # Step 4: Tag stream
        tags = [{"Key": "Env", "Value": "Test"}]
        stubber.add_response(
            "tag_delivery_stream",
            {},
            {"DeliveryStreamName": STREAM_NAME, "Tags": tags},
        )
        firehose_wrapper.tag_delivery_stream(STREAM_NAME, tags)

        # Step 5: List tags
        stubber.add_response(
            "list_tags_for_delivery_stream",
            {"Tags": tags, "HasMoreTags": False},
            {"DeliveryStreamName": STREAM_NAME},
        )
        result_tags = firehose_wrapper.list_tags_for_delivery_stream(STREAM_NAME)
        assert result_tags == tags

        # Step 6: Enable encryption
        stubber.add_response(
            "start_delivery_stream_encryption",
            {},
            {
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamEncryptionConfigurationInput": {
                    "KeyType": "AWS_OWNED_CMK"
                },
            },
        )
        firehose_wrapper.start_delivery_stream_encryption(STREAM_NAME)

        # Wait for encryption enabled
        stubber.add_response(
            "describe_delivery_stream",
            self._make_encryption_description("ENABLED"),
            {"DeliveryStreamName": STREAM_NAME},
        )
        firehose_wrapper.wait_for_encryption_status(STREAM_NAME, "ENABLED", timeout=10)

        # Step 7: Put record
        record_data = '{"sensor": "test"}\n'
        stubber.add_response(
            "put_record",
            {"RecordId": "rec-123", "Encrypted": True},
            {
                "DeliveryStreamName": STREAM_NAME,
                "Record": {"Data": record_data.encode("utf-8")},
            },
        )
        result = firehose_wrapper.put_record(STREAM_NAME, record_data)
        assert result["RecordId"] == "rec-123"

        # Step 8: Put record batch
        batch_records = ['{"id": 1}\n', '{"id": 2}\n']
        stubber.add_response(
            "put_record_batch",
            {
                "FailedPutCount": 0,
                "Encrypted": True,
                "RequestResponses": [
                    {"RecordId": "batch-1"},
                    {"RecordId": "batch-2"},
                ],
            },
            {
                "DeliveryStreamName": STREAM_NAME,
                "Records": [{"Data": r.encode("utf-8")} for r in batch_records],
            },
        )
        result = firehose_wrapper.put_record_batch(STREAM_NAME, batch_records)
        assert result["FailedPutCount"] == 0

        # Step 9: Stop encryption
        stubber.add_response(
            "stop_delivery_stream_encryption",
            {},
            {"DeliveryStreamName": STREAM_NAME},
        )
        firehose_wrapper.stop_delivery_stream_encryption(STREAM_NAME)

        # Wait for encryption disabled
        stubber.add_response(
            "describe_delivery_stream",
            self._make_encryption_description("DISABLED"),
            {"DeliveryStreamName": STREAM_NAME},
        )
        firehose_wrapper.wait_for_encryption_status(STREAM_NAME, "DISABLED", timeout=10)

        # Cleanup: Delete stream
        stubber.add_response(
            "delete_delivery_stream",
            {},
            {"DeliveryStreamName": STREAM_NAME},
        )
        firehose_wrapper.delete_delivery_stream(STREAM_NAME)

        stubber.assert_no_pending_responses()
        stubber.deactivate()


class TestFirehoseHello:
    """Tests for the Hello Firehose example."""

    def test_hello_firehose(self, capsys):
        """Test hello_firehose with stubbed client."""
        client = boto3.client("firehose", region_name="us-east-1")
        stubber = Stubber(client)
        stubber.add_response(
            "list_delivery_streams",
            {
                "DeliveryStreamNames": ["stream-1", "stream-2"],
                "HasMoreDeliveryStreams": False,
            },
            {"DeliveryStreamType": "DirectPut"},
        )
        stubber.activate()

        with patch("boto3.client", return_value=client):
            from firehose_hello import hello_firehose

            hello_firehose()

        captured = capsys.readouterr()
        assert "stream-1" in captured.out
        assert "stream-2" in captured.out
        stubber.assert_no_pending_responses()
        stubber.deactivate()


class TestFirehoseWrapperErrors:
    """Tests for error handling in FirehoseWrapper."""

    def test_create_stream_already_exists(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_client_error(
            "create_delivery_stream",
            service_error_code="ResourceInUseException",
            service_message="Stream already exists",
            expected_params={
                "DeliveryStreamName": STREAM_NAME,
                "DeliveryStreamType": "DirectPut",
                "ExtendedS3DestinationConfiguration": {
                    "RoleARN": ROLE_ARN,
                    "BucketARN": BUCKET_ARN,
                    "BufferingHints": {"SizeInMBs": 1, "IntervalInSeconds": 60},
                },
            },
        )

        from botocore.exceptions import ClientError

        with pytest.raises(ClientError) as exc_info:
            firehose_wrapper.create_delivery_stream(
                STREAM_NAME, ROLE_ARN, BUCKET_ARN, 1, 60
            )
        assert exc_info.value.response["Error"]["Code"] == "ResourceInUseException"

    def test_describe_stream_not_found(self, wrapper):
        firehose_wrapper, stubber = wrapper
        stubber.add_client_error(
            "describe_delivery_stream",
            service_error_code="ResourceNotFoundException",
            service_message="Stream not found",
            expected_params={"DeliveryStreamName": "nonexistent-stream"},
        )

        from botocore.exceptions import ClientError

        with pytest.raises(ClientError) as exc_info:
            firehose_wrapper.describe_delivery_stream("nonexistent-stream")
        assert exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"

    def test_wait_for_stream_timeout(self, wrapper):
        """Test that wait_for_stream_active raises TimeoutError."""
        firehose_wrapper, stubber = wrapper
        # Return CREATING status to trigger timeout
        stubber.add_response(
            "describe_delivery_stream",
            {
                "DeliveryStreamDescription": {
                    "DeliveryStreamName": STREAM_NAME,
                    "DeliveryStreamARN": STREAM_ARN,
                    "DeliveryStreamStatus": "CREATING",
                    "DeliveryStreamType": "DirectPut",
                    "VersionId": "1",
                    "CreateTimestamp": datetime(2024, 1, 1, tzinfo=timezone.utc),
                    "Destinations": [],
                    "HasMoreDestinations": False,
                }
            },
            {"DeliveryStreamName": STREAM_NAME},
        )

        with pytest.raises(TimeoutError):
            firehose_wrapper.wait_for_stream_active(STREAM_NAME, timeout=1, interval=1)
