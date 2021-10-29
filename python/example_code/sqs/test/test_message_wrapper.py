# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for message_wrapper.py functions.
"""

import pytest

from botocore.exceptions import ClientError

import message_wrapper


@pytest.mark.parametrize("body,attributes", [
    ('Not a mess but a message.', {}),
    ('This message is about music.', {
        'genre': {'StringValue': 'Power Metal', 'DataType': 'String'},
        'key': {'StringValue': 'D Minor', 'DataType': 'String'}
    })
])
def test_send_message(make_stubber, make_queue, body, attributes):
    """Test that sending a message returns a message ID."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)
    message_id = '1234-5678'

    sqs_stubber.stub_send_message(queue.url, body, attributes, message_id)

    response = message_wrapper.send_message(queue, body, attributes)
    if sqs_stubber.use_stubs:
        assert response['MessageId'] == message_id
    else:
        assert response['MessageId']


def test_send_message_no_body(make_stubber, make_queue):
    """Test that sending a message with no body raises an exception."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    sqs_stubber.stub_send_message(queue.url, '', {}, '', error_code='MissingParameter')

    with pytest.raises(ClientError) as exc_info:
        message_wrapper.send_message(queue, '')
    assert exc_info.value.response['Error']['Code'] == 'MissingParameter'


@pytest.mark.parametrize("body_template,attributes,count", [
    ("This is body template {}!", {}, 5),
    ("Message {}, now with attributes.", {
        'slogan': {'StringValue': 'New and improved!', 'DataType': 'String'},
        'discount': {'StringValue': '50%', 'DataType': 'String'}
    }, 10),
    ("Just {} message.", {}, 1)
])
def test_send_messages(make_stubber, make_queue, body_template, attributes, count):
    """Test that sending various batches of messages returns the expected list of
    successful sends."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    messages = [{
        'body': body_template.format(ind),
        'attributes': attributes
    } for ind in range(1, count+1)]

    sqs_stubber.stub_send_message_batch(queue.url, messages)

    response = message_wrapper.send_messages(queue, messages)
    assert len(response['Successful']) == count


@pytest.mark.parametrize("count", [0, 20])
def test_send_messages_wrong_size(make_stubber, make_queue, count):
    """Test that sending batches of messages that are too big or too small
    raises exceptions."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    messages = [{
        'body': f'Another body {ind}',
        'attributes': {}
    } for ind in range(0, count)]

    sqs_stubber.stub_send_message_batch(
        queue.url,
        messages,
        'AWS.SimpleQueueService.EmptyBatchRequest' if count == 0
        else 'AWS.SimpleQueueService.TooManyEntriesInBatchRequest')

    with pytest.raises(ClientError) as exc_info:
        message_wrapper.send_messages(queue, messages)
    if count == 0:
        assert exc_info.value.response['Error']['Code'] == \
               'AWS.SimpleQueueService.EmptyBatchRequest'
    else:
        assert exc_info.value.response['Error']['Code'] == \
               'AWS.SimpleQueueService.TooManyEntriesInBatchRequest'


@pytest.mark.parametrize("send_count,receive_count,wait_time", [
    (5, 3, 5), (2, 10, 0), (1, 1, 1), (0, 5, 0)
])
def test_receive_messages(make_stubber, make_queue, send_count, receive_count,
                          wait_time):
    """Test that receiving various numbers of messages returns the expected
    number of messages."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    sent_messages = [{
        'body': f"I have several bodies. This is #{ind}.",
        'attributes': {}
    } for ind in range(0, send_count)]
    if send_count > 0:
        sqs_stubber.stub_send_message_batch(queue.url, sent_messages)
        message_wrapper.send_messages(queue, sent_messages)

    sqs_stubber.stub_receive_messages(queue.url, sent_messages, receive_count)

    received_messages = message_wrapper.receive_messages(
        queue, receive_count, wait_time)

    if send_count > 0:
        assert received_messages
        assert len(received_messages) <= receive_count
    else:
        assert not received_messages


@pytest.mark.parametrize("receive_count", [0, 20])
def test_receive_messages_bad_params(make_stubber, make_queue, receive_count):
    """Test that trying to receive a number of messages that is too large or too small
    raises an exception."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    sqs_stubber.stub_receive_messages(
        queue.url, [], receive_count, 'InvalidParameterValue')

    with pytest.raises(ClientError):
        message_wrapper.receive_messages(queue, receive_count, 1)


@pytest.mark.parametrize("message_count", [1, 5, 10])
def test_delete_messages(make_stubber, make_queue, message_count):
    """Test that deleting a single message or a batch of messages returns
    the expected success response."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)

    body = "I'm not long for this world."
    wait_time = 1

    messages = [{'body': body, 'attributes': {}}]*message_count

    sqs_stubber.stub_send_message_batch(queue.url, messages)
    sqs_stubber.stub_receive_messages(queue.url, messages, message_count)

    message_wrapper.send_messages(queue, messages)
    messages = message_wrapper.receive_messages(queue, message_count, wait_time)

    if message_count == 1:
        sqs_stubber.stub_delete_message(queue.url, messages[0])
        messages[0].delete()
    else:
        sqs_stubber.stub_delete_message_batch(queue.url, messages, len(messages), 0)
        message_wrapper.delete_messages(queue, messages)


def test_delete_message_not_exist(make_stubber, make_queue):
    """Test that deleting a message that doesn't exist raises an exception."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)
    message = queue.Message(receipt_handle='fake-handle')

    sqs_stubber.stub_delete_message(queue.url, message,
                                    error_code='ReceiptHandleIsInvalid')

    with pytest.raises(ClientError) as exc_info:
        message.delete()
    assert exc_info.value.response['Error']['Code'] == 'ReceiptHandleIsInvalid'


def test_delete_messages_not_exist(make_stubber, make_queue):
    """Test that deleting a batch of messages that don't exist succeeds
    and returns the expected list of failed messages."""
    sqs_stubber = make_stubber(message_wrapper.sqs.meta.client)
    queue = make_queue(sqs_stubber, message_wrapper.sqs)
    messages = [
        queue.Message(receipt_handle=f'fake-handle-{ind}')
        for ind in range(0, 5)
    ]

    sqs_stubber.stub_delete_message_batch(queue.url, messages, 0, len(messages))

    response = message_wrapper.delete_messages(queue, messages)

    assert len(response['Failed']) == len(messages)
    assert all([failed['Code'] == 'ReceiptHandleIsInvalid'
                for failed in response['Failed']])
