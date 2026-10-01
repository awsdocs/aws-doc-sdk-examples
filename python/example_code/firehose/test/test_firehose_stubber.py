# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for the Amazon Data Firehose Hello example using botocore Stubber.
These tests run offline without AWS credentials.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import boto3
import pytest
from botocore.stub import Stubber
from botocore.exceptions import ClientError

from firehose_hello import hello_firehose


class TestHelloFirehose:
    """Tests for the hello_firehose function."""

    def test_hello_firehose_success_with_streams(self, capsys):
        """
        Test that hello_firehose prints stream names when streams exist.
        Stubs ListDeliveryStreams to return two stream names.
        """
        firehose_client = boto3.client(
            "firehose", region_name="us-east-1"
        )
        stubber = Stubber(firehose_client)

        response = {
            "DeliveryStreamNames": [
                "my-s3-delivery-stream",
                "web-log-firehose",
            ],
            "HasMoreDeliveryStreams": False,
        }
        expected_params = {"Limit": 10}
        stubber.add_response("list_delivery_streams", response, expected_params)

        with stubber:
            hello_firehose(firehose_client)

        captured = capsys.readouterr()
        assert "my-s3-delivery-stream" in captured.out
        assert "web-log-firehose" in captured.out
        assert "No delivery streams found" not in captured.out

    def test_hello_firehose_success_empty(self, capsys):
        """
        Test that hello_firehose handles an empty list of streams gracefully.
        Stubs ListDeliveryStreams to return an empty list.
        """
        firehose_client = boto3.client(
            "firehose", region_name="us-east-1"
        )
        stubber = Stubber(firehose_client)

        response = {
            "DeliveryStreamNames": list(),
            "HasMoreDeliveryStreams": False,
        }
        expected_params = {"Limit": 10}
        stubber.add_response("list_delivery_streams", response, expected_params)

        with stubber:
            hello_firehose(firehose_client)

        captured = capsys.readouterr()
        assert "No delivery streams found in this account/region." in captured.out

    def test_hello_firehose_service_unavailable_error(self):
        """
        Test that hello_firehose raises ClientError with error code
        ServiceUnavailableException when the service is unavailable.
        """
        firehose_client = boto3.client(
            "firehose", region_name="us-east-1"
        )
        stubber = Stubber(firehose_client)

        stubber.add_client_error(
            "list_delivery_streams",
            service_error_code="ServiceUnavailableException",
            service_message="The service is temporarily unavailable.",
        )

        with stubber:
            with pytest.raises(ClientError) as exc_info:
                hello_firehose(firehose_client)

            assert (
                exc_info.value.response["Error"]["Code"]
                == "ServiceUnavailableException"
            )

    def test_hello_firehose_has_more_streams(self, capsys):
        """
        Test that hello_firehose prints a pagination note when
        HasMoreDeliveryStreams is True.
        """
        firehose_client = boto3.client(
            "firehose", region_name="us-east-1"
        )
        stubber = Stubber(firehose_client)

        response = {
            "DeliveryStreamNames": ["stream-alpha", "stream-beta"],
            "HasMoreDeliveryStreams": True,
        }
        expected_params = {"Limit": 10}
        stubber.add_response("list_delivery_streams", response, expected_params)

        with stubber:
            hello_firehose(firehose_client)

        captured = capsys.readouterr()
        assert "stream-alpha" in captured.out
        assert "stream-beta" in captured.out
        assert "additional delivery streams" in captured.out
