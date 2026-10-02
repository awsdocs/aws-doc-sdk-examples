# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for hello_sqs.py using botocore.stub.Stubber.

These tests run offline without AWS credentials.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import pytest

import boto3
from botocore.exceptions import ClientError
from botocore.stub import Stubber

from hello_sqs import hello_sqs


class TestHelloSqs:
    """Tests for the hello_sqs function."""

    def test_hello_sqs_list_queues_success(self, capsys):
        """
        Test that hello_sqs prints queue URLs when queues exist.
        Stubs list_queues to return two queue URLs.
        """
        sqs_client = boto3.client("sqs", region_name="us-east-1")
        stubber = Stubber(sqs_client)

        expected_urls = [
            "https://sqs.us-east-1.amazonaws.com/123456789012/MyQueue1",
            "https://sqs.us-east-1.amazonaws.com/123456789012/MyQueue2",
        ]

        stubber.add_response(
            "list_queues",
            {"QueueUrls": expected_urls},
            {},
        )

        with stubber:
            hello_sqs(sqs_client)

        captured = capsys.readouterr()
        assert "Found 2 queue(s):" in captured.out
        for url in expected_urls:
            assert url in captured.out

    def test_hello_sqs_list_queues_empty(self, capsys):
        """
        Test that hello_sqs handles empty queue list gracefully.
        Stubs list_queues to return an empty QueueUrls list.
        """
        sqs_client = boto3.client("sqs", region_name="us-east-1")
        stubber = Stubber(sqs_client)

        stubber.add_response(
            "list_queues",
            {"QueueUrls": []},
            {},
        )

        with stubber:
            hello_sqs(sqs_client)

        captured = capsys.readouterr()
        assert "No queues found in this account/region." in captured.out

    def test_hello_sqs_list_queues_throttled(self):
        """
        Test that hello_sqs raises ClientError when RequestThrottled occurs.
        Stubs list_queues with a RequestThrottled service error.
        """
        sqs_client = boto3.client("sqs", region_name="us-east-1")
        stubber = Stubber(sqs_client)

        stubber.add_client_error(
            "list_queues",
            service_error_code="RequestThrottled",
            service_message="Rate exceeded",
        )

        with stubber:
            with pytest.raises(ClientError) as exc_info:
                hello_sqs(sqs_client)

            assert exc_info.value.response["Error"]["Code"] == "RequestThrottled"
