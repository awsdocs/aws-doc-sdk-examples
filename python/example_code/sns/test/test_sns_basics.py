# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for sns_basics.py
"""

import json
import boto3
from botocore.exceptions import ClientError
import pytest

from sns_basics import SnsWrapper

TOPIC_ARN = 'arn:aws:sns:REGION:123456789012:topic/test-name'
SUBSCRIPTION_ARN = 'arn:aws:sns:REGION:123456789012:subscription/sub-name'

@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_topic(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    name = 'test-name'

    sns_stubber.stub_create_topic(name, TOPIC_ARN, error_code=error_code)

    if error_code is None:
        got_topic = sns_wrapper.create_topic(name)
        assert got_topic.arn == TOPIC_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.create_topic(name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_topics(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic_arns = [f'{TOPIC_ARN}-{index}' for index in range(3)]

    sns_stubber.stub_list_topics(topic_arns, error_code=error_code)

    if error_code is None:
        got_topics = sns_wrapper.list_topics()
        assert [gt.arn for gt in got_topics] == topic_arns
    else:
        with pytest.raises(ClientError) as exc_info:
            got_topics = sns_wrapper.list_topics()
            list(got_topics)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_topic(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic = sns_resource.Topic(TOPIC_ARN)

    sns_stubber.stub_delete_topic(topic.arn, error_code=error_code)

    if error_code is None:
        sns_wrapper.delete_topic(topic)
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.delete_topic(topic)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_subscribe(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic = sns_resource.Topic(TOPIC_ARN)
    protocol = 'email'
    endpoint = 'test@example.com'

    sns_stubber.stub_subscribe(
        topic.arn, protocol, endpoint, SUBSCRIPTION_ARN, return_arn=True,
        error_code=error_code)

    if error_code is None:
        got_subscription = sns_wrapper.subscribe(topic, protocol, endpoint)
        assert got_subscription.arn == SUBSCRIPTION_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.subscribe(topic, protocol, endpoint)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_subscription_filter(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    subscription = sns_resource.Subscription(SUBSCRIPTION_ARN)
    attributes = {'test': 'howdy'}
    att_policy = {'test': ['howdy']}

    sns_stubber.stub_set_subscription_attributes(
        subscription.arn, 'FilterPolicy', att_policy, error_code=error_code)

    if error_code is None:
        sns_wrapper.add_subscription_filter(subscription, attributes)
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.add_subscription_filter(subscription, attributes)
        assert exc_info.value.response['Error']['Code'] == error_code



@pytest.mark.parametrize('topic_arn,error_code', [
    (None, None),
    (TOPIC_ARN, None),
    (TOPIC_ARN, 'TestException')])
def test_list_subscriptions(make_stubber, topic_arn, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic = sns_resource.Topic(topic_arn) if topic_arn else None
    sub_arns = [f'{SUBSCRIPTION_ARN}-{index}' for index in range(3)]

    if topic is None:
        sns_stubber.stub_list_subscriptions(sub_arns, error_code=error_code)
    else:
        sns_stubber.stub_list_subscriptions_by_topic(
            topic_arn, sub_arns, error_code=error_code)

    if error_code is None:
        got_sub_arns = sns_wrapper.list_subscriptions(topic)
        assert [gsa.arn for gsa in got_sub_arns] == sub_arns
    else:
        with pytest.raises(ClientError) as exc_info:
            got_sub_arns = sns_wrapper.list_subscriptions(topic)
            list(got_sub_arns)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_subscription(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    subscription = sns_resource.Subscription(SUBSCRIPTION_ARN)

    sns_stubber.stub_unsubscribe(SUBSCRIPTION_ARN, error_code=error_code)

    if error_code is None:
        sns_wrapper.delete_subscription(subscription)
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.delete_subscription(subscription)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_publish_message(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic = sns_resource.Topic(TOPIC_ARN)
    message = 'test-message'
    attributes = {'test': 'string', 'bintest': b'binary'}
    message_id = 'msg-id'

    sns_stubber.stub_publish(
        message, message_id, topic_arn=topic.arn, message_attributes=attributes,
        error_code=error_code)

    if error_code is None:
        got_message_id = sns_wrapper.publish_message(topic, message, attributes)
        assert got_message_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.publish_message(topic, message, attributes)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_publish_text_message(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    phone_number = 'test-phone_number'
    message = 'test message'
    message_id = 'msg-id'

    sns_stubber.stub_publish(
        message, message_id, phone_number=phone_number, error_code=error_code)

    if error_code is None:
        got_message_id = sns_wrapper.publish_text_message(phone_number, message)
        assert got_message_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.publish_text_message(phone_number, message)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_publish_multi_message(make_stubber, error_code):
    sns_resource = boto3.resource('sns')
    sns_stubber = make_stubber(sns_resource.meta.client)
    sns_wrapper = SnsWrapper(sns_resource)
    topic = sns_resource.Topic(TOPIC_ARN)
    subject = 'test-subject'
    message = {prot: f'{prot} message' for prot in ['default', 'sms', 'email']}
    message_id = 'msg-id'

    sns_stubber.stub_publish(
        json.dumps(message), message_id, topic_arn=topic.arn, subject=subject,
        message_structure='json', error_code=error_code)

    if error_code is None:
        got_message_id = sns_wrapper.publish_multi_message(
            topic, subject, *message.values())
        assert got_message_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            sns_wrapper.publish_multi_message(
                topic, subject, *message.values())
        assert exc_info.value.response['Error']['Code'] == error_code
