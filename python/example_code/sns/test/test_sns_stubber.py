# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for the Amazon SNS Hello example using botocore.stub.Stubber.
All tests are offline — no real AWS calls are made.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import boto3
import pytest
from botocore.stub import Stubber

from sns_hello import hello_sns


@pytest.fixture
def sns_client():
    """Create an SNS client for testing."""
    return boto3.client("sns", region_name="us-east-1")


def test_hello_sns_list_topics_success(sns_client, capsys):
    """Verify the Hello program correctly lists topics when topics exist."""
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

    result = hello_sns(sns_client)

    assert len(result) == 2
    assert result[0] == "arn:aws:sns:us-east-1:123456789012:topic-1"
    assert result[1] == "arn:aws:sns:us-east-1:123456789012:topic-2"

    captured = capsys.readouterr()
    assert "topic-1" in captured.out
    assert "topic-2" in captured.out
    assert "Found 2 topic(s)." in captured.out

    stubber.assert_no_pending_responses()
    stubber.deactivate()


def test_hello_sns_list_topics_empty(sns_client, capsys):
    """Verify the Hello program handles the case when no topics exist."""
    stubber = Stubber(sns_client)
    stubber.add_response(
        "list_topics",
        {"Topics": []},
    )
    stubber.activate()

    result = hello_sns(sns_client)

    assert len(result) == 0
    assert result == []

    captured = capsys.readouterr()
    assert "No topics found in this region." in captured.out

    stubber.assert_no_pending_responses()
    stubber.deactivate()


def test_hello_sns_list_topics_authorization_error(sns_client):
    """Verify correct handling when the caller lacks permissions."""
    stubber = Stubber(sns_client)
    stubber.add_client_error(
        "list_topics",
        service_error_code="AuthorizationError",
        service_message="Access denied",
    )
    stubber.activate()

    with pytest.raises(sns_client.exceptions.AuthorizationErrorException):
        hello_sns(sns_client)

    stubber.assert_no_pending_responses()
    stubber.deactivate()


def test_hello_sns_list_topics_internal_error(sns_client):
    """Verify correct handling of transient internal service errors."""
    stubber = Stubber(sns_client)
    stubber.add_client_error(
        "list_topics",
        service_error_code="InternalError",
        service_message="Internal service error",
    )
    stubber.activate()

    with pytest.raises(sns_client.exceptions.InternalErrorException):
        hello_sns(sns_client)

    stubber.assert_no_pending_responses()
    stubber.deactivate()


def test_hello_sns_list_topics_invalid_parameter(sns_client):
    """Verify correct handling of invalid parameter errors."""
    stubber = Stubber(sns_client)
    stubber.add_client_error(
        "list_topics",
        service_error_code="InvalidParameter",
        service_message="Invalid parameter",
    )
    stubber.activate()

    with pytest.raises(sns_client.exceptions.InvalidParameterException):
        hello_sns(sns_client)

    stubber.assert_no_pending_responses()
    stubber.deactivate()
