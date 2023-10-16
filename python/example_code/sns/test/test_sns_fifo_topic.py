# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for sns_fifo_topic.py
"""

import json
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

from sns_fifo_topic import FifoTopicWrapper

TOPIC_ARN = "arn:aws:sns:REGION:123456789012:topic/test-name"
SUBSCRIPTION_ARN = "arn:aws:sns:REGION:123456789012:subscription/sub-name"
QUEUE_URL = "https://REGION.amazonaws.com/123456789012/queue-name"
QUEUE_ARN = "arn:aws:sqs:REGION:123456789012:queue/queue-name"


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_fifo_topic(make_stubber, error_code):
    sns_resource = boto3.resource("sns")
    sns_stubber = make_stubber(sns_resource.meta.client)
    fifo_topic_wrapper = FifoTopicWrapper(sns_resource)
    name = "test-name.fifo"
    attributes = {"ContentBasedDeduplication": "False", "FifoTopic": "True"}

    sns_stubber.stub_create_topic(
        name, TOPIC_ARN, topic_attributes=attributes, error_code=error_code
    )

    if error_code is None:
        got_topic = fifo_topic_wrapper.create_fifo_topic(name)
        assert got_topic.arn == TOPIC_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            fifo_topic_wrapper.create_fifo_topic(name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_subscribe_queue_to_topic(make_stubber, error_code):
    sns_resource = boto3.resource("sns")
    sns_stubber = make_stubber(sns_resource.meta.client)
    topic = sns_resource.Topic(TOPIC_ARN)
    fifo_topic_wrapper = FifoTopicWrapper(sns_resource)

    sns_stubber.stub_subscribe(
        topic.arn, "sqs", QUEUE_ARN, SUBSCRIPTION_ARN, False, error_code=error_code
    )

    if error_code is None:
        got_subscription = fifo_topic_wrapper.subscribe_queue_to_topic(topic, QUEUE_ARN)
        assert got_subscription.arn == SUBSCRIPTION_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            fifo_topic_wrapper.subscribe_queue_to_topic(topic, QUEUE_ARN)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_publish_price_update(make_stubber, error_code):
    sns_resource = boto3.resource("sns")
    sns_stubber = make_stubber(sns_resource.meta.client)
    fifo_topic_wrapper = FifoTopicWrapper(sns_resource)
    topic = sns_resource.Topic(TOPIC_ARN)
    message = "test message"
    message_id = "msg-id"
    group_id = "group-id"
    attributes = {"business": "wholesale"}
    dedup_id = "dedup-id"
    subject = "Price Update"

    sns_stubber.stub_publish(
        message,
        message_id,
        topic_arn=TOPIC_ARN,
        subject=subject,
        group_id=group_id,
        dedup_id=dedup_id,
        message_attributes=attributes,
        error_code=error_code,
    )

    if error_code is None:
        got_message_id = fifo_topic_wrapper.publish_price_update(
            topic, message, group_id
        )
        assert got_message_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            fifo_topic_wrapper.publish_price_update(topic, message, group_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_queue(make_stubber, error_code):
    sqs_resource = boto3.resource("sqs")
    sns_resource = boto3.resource("sns")
    sqs_stubber = make_stubber(sqs_resource.meta.client)
    queue = sqs_resource.Queue(QUEUE_URL)
    fifo_topic_wrapper = FifoTopicWrapper(sns_resource)

    sqs_stubber.stub_delete_queue(QUEUE_URL, error_code=error_code)

    if error_code is None:
        fifo_topic_wrapper.delete_queue(queue)
    else:
        with pytest.raises(ClientError) as exc_info:
            fifo_topic_wrapper.delete_queue(queue)
        assert exc_info.value.response["Error"]["Code"] == error_code
