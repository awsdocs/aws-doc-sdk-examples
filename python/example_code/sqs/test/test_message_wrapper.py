# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
#
# You may not use this file except in compliance with the License. A copy of
# the License is located at http://aws.amazon.com/apache2.0/.
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

"""
Purpose
    Unit tests for message_wrapper.py functions.

Running the tests
    Tests can be run in two modes. By default, tests use the botocore Stubber.
    This captures requests before they are sent to AWS and returns a mocked response.
    You can also run the tests against your AWS account. In this case, they will
    create and manipulate AWS resources, which may incur charges on your account.

    To run the tests for this module with the botocore Stubber, run the following in
    your <GitHub root>/python/example_code/sqs folder.

        python -m pytest -o log_cli=1 --log-cli-level=INFO test/test_message_wrapper.py

    The '-o log_cli=1 --log-cli-level=INFO' flags configure pytest to output
    logs to stdout during the test run. Without them, pytest captures logs and prints
    them only when the test fails.

    To run the tests using your AWS account and default shared credentials, include the
    '--use-real-aws-may-incur-charges' flag.

        python -m pytest -o log_cli=1 --log-cli-level=INFO --use-real-aws-may-incur-charges test/test_message_wrapper.py

    Note that this may incur charges to your AWS account. When run in this mode,
    a best effort is made to clean up any resources created during the test. But it's
    your responsibility to verify that all resources have actually been cleaned up.
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
def test_send_message(sqs_stubber, queue_stub, body, attributes):
    """Test that sending a message returns a message ID."""
    sqs_stubber.add_response(
        'send_message',
        expected_params={
            'QueueUrl': queue_stub.url,
            'MessageBody': body,
            'MessageAttributes': attributes
        },
        service_response={'MessageId': '1234-5678'}
    )

    response = message_wrapper.send_message(queue_stub, body, attributes)
    assert response['MessageId']


def test_send_message_no_body(sqs_stubber, queue_stub):
    """Test that sending a message with no body raises an exception."""
    sqs_stubber.add_client_error(
        'send_message',
        expected_params={
            'QueueUrl': queue_stub.url,
            'MessageBody': '',
            'MessageAttributes': {}
        },
        service_error_code='MissingParameter'
    )

    with pytest.raises(ClientError):
        message_wrapper.send_message(queue_stub, '')


@pytest.mark.parametrize("body_template,attributes,count", [
    ("This is body template {}!", {}, 5),
    ("Message {}, now with attributes.", {
        'slogan': {'StringValue': 'New and improved!', 'DataType': 'String'},
        'discount': {'StringValue': '50%', 'DataType': 'String'}
    }, 10),
    ("Just {} message.", {}, 1)
])
def test_send_messages(sqs_stubber, queue_stub, body_template, attributes, count):
    """Test that sending various batches of messages returns the expected list of
    successful sends."""
    messages = [{
        'body': body_template.format(ind),
        'attributes': attributes
    } for ind in range(1, count+1)]

    sqs_stubber.add_response(
        'send_message_batch',
        expected_params={
            'QueueUrl': queue_stub.url,
            'Entries': [{
                'Id': str(ind),
                'MessageBody': msg['body'],
                'MessageAttributes': msg['attributes']
            } for ind, msg in enumerate(messages)]
        },
        service_response={
            'Successful': [{
                'Id': str(ind),
                'MessageId': f'{ind}-1234',
                'MD5OfMessageBody': 'Test-MD5-Body',
            } for ind in range(0, len(messages))],
            'Failed': []
        }
    )

    response = message_wrapper.send_messages(queue_stub, messages)
    assert len(response['Successful']) == count


@pytest.mark.parametrize("count", [0, 20])
def test_send_messages_wrong_size(sqs_stubber, queue_stub, count):
    """Test that sending batches of messages that are too big or too small
    raises exceptions."""
    messages = [{
        'body': f'Another body {ind}',
        'attributes': {}
    } for ind in range(0, count)]

    sqs_stubber.add_client_error(
        'send_message_batch',
        expected_params={
            'QueueUrl': queue_stub.url,
            'Entries': [{
                'Id': str(ind),
                'MessageBody': msg['body'],
                'MessageAttributes': msg['attributes']
            } for ind, msg in enumerate(messages)]
        },
        service_error_code='AWS.SimpleQueueService.EmptyBatchRequest' if count == 0
            else 'AWS.SimpleQueueService.TooManyEntriesInBatchRequest'
    )

    if count == 0:
        with pytest.raises(sqs_stubber.client.exceptions.EmptyBatchRequest):
            message_wrapper.send_messages(queue_stub, messages)
    elif count > 10:
        with pytest.raises(sqs_stubber.client.exceptions.TooManyEntriesInBatchRequest):
            message_wrapper.send_messages(queue_stub, messages)


@pytest.mark.parametrize("send_count,receive_count,wait_time", [
    (5, 3, 5), (2, 10, 0), (1, 1, 1), (0, 5, 0)
])
def test_receive_messages(sqs_stubber, queue_stub, send_count, receive_count,
                          wait_time):
    """Test that receiving various numbers of messages returns the expected
    number of messages."""
    sent_messages = [{
        'body': f"I have several bodies. This is #{ind}.",
        'attributes': {}
    } for ind in range(0, send_count)]
    if send_count > 0:
        sqs_stubber.add_response(
            'send_message_batch',
            expected_params={
                'QueueUrl': queue_stub.url,
                'Entries': [{
                    'Id': str(ind),
                    'MessageBody': msg['body'],
                    'MessageAttributes': msg['attributes']
                } for ind, msg in enumerate(sent_messages)]
            },
            service_response={
                'Successful': [{
                    'Id': str(ind),
                    'MessageId': f'{ind}-1234',
                    'MD5OfMessageBody': 'Test-MD5-Body',
                } for ind in range(0, len(sent_messages))],
                'Failed': []
            }
        )

        message_wrapper.send_messages(queue_stub, sent_messages)

    sqs_stubber.add_response(
        'receive_message',
        expected_params={
            'QueueUrl': queue_stub.url,
            'MessageAttributeNames': ['All'],
            'MaxNumberOfMessages': receive_count,
            'WaitTimeSeconds': wait_time
        },
        service_response={
            'Messages': [{
                'MessageId': f'{ind}-1234',
                'Body': msg['body'],
                'MD5OfBody': 'Test-MD5-Body',
                'ReceiptHandle': f'Receipt-{ind}'
            } for ind, msg in enumerate(sent_messages) if ind < receive_count]
        }
    )

    received_messages = message_wrapper.receive_messages(
        queue_stub,
        receive_count,
        wait_time
    )

    if send_count > 0:
        assert received_messages
        assert len(received_messages) <= receive_count
    else:
        assert not received_messages


@pytest.mark.parametrize("receive_count", [0, 20])
def test_receive_messages_bad_params(sqs_stubber, queue_stub, receive_count):
    """Test that trying to receive a number of messages that is too large or too small
    raises an exception."""
    sqs_stubber.add_client_error(
        'receive_message',
        expected_params={
            'QueueUrl': queue_stub.url,
            'MessageAttributeNames': ['All'],
            'MaxNumberOfMessages': receive_count,
            'WaitTimeSeconds': 1
        },
        service_error_code='InvalidParameterValue'
    )

    with pytest.raises(ClientError):
        message_wrapper.receive_messages(queue_stub, receive_count, 1)


@pytest.mark.parametrize("message_count", [1, 5, 10])
def test_delete_messages(sqs_stubber, queue_stub, message_count):
    """Test that deleting a single message or a batch of messages returns
    the expected success response."""
    body = "I'm not long for this world."
    wait_time = 1

    sqs_stubber.add_response(
        'send_message_batch',
        expected_params={
            'QueueUrl': queue_stub.url,
            'Entries': [{
                'Id': str(ind),
                'MessageBody': body,
                'MessageAttributes': {}
            } for ind in range(0, message_count)]
        },
        service_response={
            'Successful': [{
                'Id': str(ind),
                'MessageId': f'{ind}-1234',
                'MD5OfMessageBody': 'Test-MD5-Body',
            } for ind in range(0, message_count)],
            'Failed': []
        }
    )
    sqs_stubber.add_response(
        'receive_message',
        expected_params={
            'QueueUrl': queue_stub.url,
            'MessageAttributeNames': ['All'],
            'MaxNumberOfMessages': message_count,
            'WaitTimeSeconds': wait_time
        },
        service_response={
            'Messages': [{
                'MessageId': f'{ind}-1234',
                'Body': body,
                'MD5OfBody': 'Test-MD5-Body',
                'ReceiptHandle': f'Receipt-{ind}'
            } for ind in range(0, message_count)]
        }
    )

    message_wrapper.send_messages(queue_stub,
                                  [{'body': body, 'attributes': {}}]*message_count)
    messages = message_wrapper.receive_messages(queue_stub, message_count, wait_time)

    if message_count == 1:
        sqs_stubber.add_response(
            'delete_message',
            expected_params={
                'QueueUrl': queue_stub.url,
                'ReceiptHandle': messages[0].receipt_handle
            },
            service_response={}
        )

        messages[0].delete()
    else:
        sqs_stubber.add_response(
            'delete_message_batch',
            expected_params={
                'QueueUrl': queue_stub.url,
                'Entries': [{
                    'Id': str(ind),
                    'ReceiptHandle': msg.receipt_handle
                } for ind, msg in enumerate(messages)]
            },
            service_response={
                'Successful': [{
                    'Id': str(ind)
                } for ind in range(0, message_count)],
                'Failed': []
            }
        )

        message_wrapper.delete_messages(queue_stub, messages)


def test_delete_message_not_exist(sqs_stubber, queue_stub):
    """Test that deleting a message that doesn't exist raises an exception."""
    message = queue_stub.Message(receipt_handle='fake-handle')

    sqs_stubber.add_client_error(
        'delete_message',
        expected_params={
            'QueueUrl': message.queue_url,
            'ReceiptHandle': message.receipt_handle
        },
        service_error_code='ReceiptHandleIsInvalid'
    )

    with pytest.raises(sqs_stubber.client.exceptions.ReceiptHandleIsInvalid):
        message.delete()


def test_delete_messages_not_exist(sqs_stubber, queue_stub):
    """Test that deleting a batch of messages that don't exist succeeds
    and returns the expected list of failed messages."""
    messages = [
        queue_stub.Message(receipt_handle=f'fake-handle-{ind}')
        for ind in range(0, 5)
    ]

    sqs_stubber.add_response(
        'delete_message_batch',
        expected_params={
            'QueueUrl': queue_stub.url,
            'Entries': [{
                'Id': str(ind),
                'ReceiptHandle': msg.receipt_handle
            } for ind, msg in enumerate(messages)]
        },
        service_response={
            'Successful': [],
            'Failed': [{
                'Id': str(ind),
                'Code': 'ReceiptHandleIsInvalid',
                'SenderFault': False
            } for ind in range(0, len(messages))]
        }
    )

    response = message_wrapper.delete_messages(queue_stub, messages)

    assert len(response['Failed']) == len(messages)
    assert all([failed['Code'] == 'ReceiptHandleIsInvalid'
                for failed in response['Failed']])
