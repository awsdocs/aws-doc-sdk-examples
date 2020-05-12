# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrate basic message operations in Amazon Simple Queue Service (Amazon SQS).
    Learn how to send, receive, and delete messages from a queue.
    Usage is shown in the test/test_message_wrapper.py file.

Prerequisites
    - You must have an AWS account, and have your default credentials and AWS Region
      configured as described in the [AWS Tools and SDKs Shared Configuration and
      Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
    - Python 3.6 or later
    - Boto 3 1.11.10 or later
    - PyTest 5.3.5 or later (to run unit tests)

Running the tests
    The best way to learn how to use this service is to run the tests.
    For instructions on testing, see the docstring in test/test_message_wrapper.py.

Running the code
    Run individual functions in the Python shell to make requests to your AWS account.

        > python
        >>> import queue_wrapper
        >>> queue = queue_wrapper.create_queue("My-test-queue")
        >>> message_wrapper.send_message(queue, "Test message")
        >>> messages = message_wrapper.receive_messages(queue, 10, 10)
        >>> messages[0].body
        'Test message'
        >>> queue_wrapper.remove_queue(queue)

Additional information
    Running this code might result in charges to your AWS account.
"""

import logging
import sys

import boto3
from botocore.exceptions import ClientError

import queue_wrapper

logger = logging.getLogger(__name__)
sqs = boto3.resource('sqs')


def send_message(queue, message_body, message_attributes=None):
    """
    Send a message to an Amazon SQS queue.

    Usage is shown in usage_demo at the end of this module.

    :param queue: The queue that receives the message.
    :param message_body: The body text of the message.
    :param message_attributes: Custom attributes of the message. These are key-value
                               pairs that can be whatever you want.
    :return: The response from SQS that contains the assigned message ID.
    """
    if not message_attributes:
        message_attributes = {}

    try:
        response = queue.send_message(
            MessageBody=message_body,
            MessageAttributes=message_attributes
        )
    except ClientError as error:
        logger.exception("Send message failed: %s", message_body)
        raise error
    else:
        return response


def send_messages(queue, messages):
    """
    Send a batch of messages in a single request to an SQS queue.
    This request may return overall success even when some messages were not sent.
    The caller must inspect the Successful and Failed lists in the response and
    resend any failed messages.

    Usage is shown in usage_demo at the end of this module.

    :param queue: The queue to receive the messages.
    :param messages: The messages to send to the queue. These are simplified to
                     contain only the message body and attributes.
    :return: The response from SQS that contains the list of successful and failed
             messages.
    """
    try:
        entries = [{
            'Id': str(ind),
            'MessageBody': msg['body'],
            'MessageAttributes': msg['attributes']
        } for ind, msg in enumerate(messages)]
        response = queue.send_messages(Entries=entries)
        if 'Successful' in response:
            for msg_meta in response['Successful']:
                logger.info(
                    "Message sent: %s: %s",
                    msg_meta['MessageId'],
                    messages[int(msg_meta['Id'])]['body']
                )
        if 'Failed' in response:
            for msg_meta in response['Failed']:
                logger.warning(
                    "Failed to send: %s: %s",
                    msg_meta['MessageId'],
                    messages[int(msg_meta['Id'])]['body']
                )
    except ClientError as error:
        logger.exception("Send messages failed to queue: %s", queue)
        raise error
    else:
        return response


def receive_messages(queue, max_number, wait_time):
    """
    Receive a batch of messages in a single request from an SQS queue.

    Usage is shown in usage_demo at the end of this module.

    :param queue: The queue from which to receive messages.
    :param max_number: The maximum number of messages to receive. The actual number
                       of messages received might be less.
    :param wait_time: The maximum time to wait (in seconds) before returning. When
                      this number is greater than zero, long polling is used. This
                      can result in reduced costs and fewer false empty responses.
    :return: The list of Message objects received. These each contain the body
             of the message and metadata and custom attributes.
    """
    try:
        messages = queue.receive_messages(
            MessageAttributeNames=['All'],
            MaxNumberOfMessages=max_number,
            WaitTimeSeconds=wait_time
        )
        for msg in messages:
            logger.info("Received message: %s: %s", msg.message_id, msg.body)
    except ClientError as error:
        logger.exception("Couldn't receive messages from queue: %s", queue)
        raise error
    else:
        return messages


def delete_message(message):
    """
    Delete a message from a queue. Clients must delete messages after they
    are received and processed to remove them from the queue.

    Usage is shown in usage_demo at the end of this module.

    :param message: The message to delete. The message's queue URL is contained in
                    the message's metadata.
    :return: None
    """
    try:
        message.delete()
        logger.info("Deleted message: %s", message.message_id)
    except ClientError as error:
        logger.exception("Couldn't delete message: %s", message.message_id)
        raise error


def delete_messages(queue, messages):
    """
    Delete a batch of messages from a queue in a single request.

    Usage is shown in usage_demo at the end of this module.

    :param queue: The queue from which to delete the messages.
    :param messages: The list of messages to delete.
    :return: The response from SQS that contains the list of successful and failed
             message deletions.
    """
    try:
        entries = [{
            'Id': str(ind),
            'ReceiptHandle': msg.receipt_handle
        } for ind, msg in enumerate(messages)]
        response = queue.delete_messages(Entries=entries)
        if 'Successful' in response:
            for msg_meta in response['Successful']:
                logger.info("Deleted %s", messages[int(msg_meta['Id'])].receipt_handle)
        if 'Failed' in response:
            for msg_meta in response['Failed']:
                logger.warning(
                    "Could not delete %s",
                    messages[int(msg_meta['Id'])].receipt_handle
                )
    except ClientError:
        logger.exception("Couldn't delete messages from queue %s", queue)
    else:
        return response


def usage_demo():
    """
    Demonstrates some ways to use the functions in this module.

    This demonstration reads the lines from this Python file and sends the lines in
    batches of 10 as messages to a queue. It then receives the messages in batches
    until the queue is empty. It reassembles the lines of the file and verifies
    they match the original file.
    """
    def pack_message(msg_path, msg_body, msg_line):
        return {
            'body': msg_body,
            'attributes': {
                'path': {'StringValue': msg_path, 'DataType': 'String'},
                'line': {'StringValue': str(msg_line), 'DataType': 'String'}
            }
        }

    def unpack_message(msg):
        return (msg.message_attributes['path']['StringValue'],
                msg.body,
                int(msg.message_attributes['line']['StringValue']))

    queue = queue_wrapper.create_queue('sqs-usage-demo-message-wrapper')

    with open(__file__) as file:
        lines = file.readlines()

    line = 0
    batch_size = 10
    received_lines = [None]*len(lines)
    print(f"Sending file lines in batches of {batch_size} as messages.")
    while line < len(lines):
        messages = [pack_message(__file__, lines[index], index)
                    for index in range(line, min(line + batch_size, len(lines)))]
        line = line + batch_size
        send_messages(queue, messages)
        print('.', end='')
        sys.stdout.flush()
    print(f"Done. Sent {len(lines) - 1} messages.")

    print(f"Receiving, handling, and deleting messages in batches of {batch_size}.")
    more_messages = True
    while more_messages:
        received_messages = receive_messages(queue, batch_size, 2)
        print('.', end='')
        sys.stdout.flush()
        for message in received_messages:
            path, body, line = unpack_message(message)
            received_lines[line] = body
        if received_messages:
            delete_messages(queue, received_messages)
        else:
            more_messages = False
    print('Done.')

    if all([lines[index] == received_lines[index] for index in range(len(lines))]):
        print(f"Successfully reassembled all file lines!")
    else:
        print(f"Uh oh, some lines were missed!")

    queue.delete()


def main():
    go = input("Running the usage demonstration uses your default AWS account "
               "credentials and might incur charges on your account. Do you want "
               "to continue (y/n)? ")
    if go.lower() == 'y':
        print("Starting the usage demo. Enjoy!")
        usage_demo()
    else:
        print("Thanks anyway!")


if __name__ == '__main__':
    main()
