# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for hello_sns.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from hello_sns import hello_sns

TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:topic"


@pytest.mark.parametrize("error_code", [None, "AuthorizationError"])
def test_hello_sns(make_stubber, capsys, error_code):
    sns_client = boto3.client("sns", region_name="us-east-1")
    sns_stubber = make_stubber(sns_client)
    topic_arns = [f"{TOPIC_ARN}-{index}" for index in range(3)]

    sns_stubber.stub_list_topics(topic_arns, error_code=error_code)

    if error_code is None:
        got_topics = hello_sns(sns_client)
        assert [topic["TopicArn"] for topic in got_topics] == topic_arns
        captured = capsys.readouterr()
        for arn in topic_arns:
            assert arn in captured.out
    else:
        with pytest.raises(ClientError) as exc_info:
            hello_sns(sns_client)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_hello_sns_no_topics(make_stubber, capsys):
    sns_client = boto3.client("sns", region_name="us-east-1")
    sns_stubber = make_stubber(sns_client)

    sns_stubber.stub_list_topics([])

    got_topics = hello_sns(sns_client)
    assert got_topics == []
    captured = capsys.readouterr()
    assert "no SNS topics" in captured.out
