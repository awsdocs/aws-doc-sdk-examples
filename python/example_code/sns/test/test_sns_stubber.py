# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for hello_sns.py using botocore Stubber.
These tests run offline — no AWS credentials or network access required.
"""

import boto3
import pytest
from botocore.stub import Stubber
from botocore.exceptions import ClientError

from hello_sns import hello_sns


@pytest.mark.unit
class TestHelloSns:
    """Tests for the hello_sns function."""

    def test_hello_sns_topics_exist(self, capsys):
        """Test that hello_sns prints topic ARNs when topics exist."""
        sns_client = boto3.client("sns", region_name="us-east-1")
        stubber = Stubber(sns_client)
        stubber.add_response(
            "list_topics",
            {
                "Topics": [
                    {"TopicArn": "arn:aws:sns:us-east-1:123456789012:topic-1"},
                    {"TopicArn": "arn:aws:sns:us-east-1:123456789012:topic-2"},
                ]
            },
        )
        stubber.activate()
        try:
            topics = hello_sns(sns_client)
            captured = capsys.readouterr()
            assert len(topics) == 2
            assert topics[0]["TopicArn"] == "arn:aws:sns:us-east-1:123456789012:topic-1"
            assert topics[1]["TopicArn"] == "arn:aws:sns:us-east-1:123456789012:topic-2"
            assert "topic-1" in captured.out
            assert "topic-2" in captured.out
        finally:
            stubber.deactivate()

    def test_hello_sns_no_topics(self, capsys):
        """Test that hello_sns handles an empty topic list gracefully."""
        sns_client = boto3.client("sns", region_name="us-east-1")
        stubber = Stubber(sns_client)
        stubber.add_response(
            "list_topics",
            {"Topics": []},
        )
        stubber.activate()
        try:
            topics = hello_sns(sns_client)
            captured = capsys.readouterr()
            assert len(topics) == 0
            assert "no SNS topics" in captured.out
        finally:
            stubber.deactivate()

    def test_hello_sns_authorization_error(self):
        """Test that hello_sns raises ClientError on AuthorizationError."""
        sns_client = boto3.client("sns", region_name="us-east-1")
        stubber = Stubber(sns_client)
        stubber.add_client_error(
            "list_topics",
            service_error_code="AuthorizationError",
            service_message="User is not authorized to perform sns:ListTopics",
        )
        stubber.activate()
        try:
            with pytest.raises(ClientError) as exc_info:
                hello_sns(sns_client)
            assert exc_info.value.response["Error"]["Code"] == "AuthorizationError"
        finally:
            stubber.deactivate()
