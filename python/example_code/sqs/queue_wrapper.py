# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Demonstrate basic queue operations in Amazon Simple Queue Service (Amazon SQS).
Learn how to create, get, and remove standard, FIFO, and dead-letter queues.
Usage is shown in the test/test_queue_wrapper.py file.
"""

# snippet-start:[python.example_code.sqs.queue_wrapper_imports]
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
sqs = boto3.resource('sqs')
# snippet-end:[python.example_code.sqs.queue_wrapper_imports]


# snippet-start:[python.example_code.sqs.CreateQueue]
def create_queue(name, attributes=None):
    """
    Creates an Amazon SQS queue.

    :param name: The name of the queue. This is part of the URL assigned to the queue.
    :param attributes: The attributes of the queue, such as maximum message size or
                       whether it's a FIFO queue.
    :return: A Queue object that contains metadata about the queue and that can be used
             to perform queue operations like sending and receiving messages.
    """
    if not attributes:
        attributes = {}

    try:
        queue = sqs.create_queue(
            QueueName=name,
            Attributes=attributes
        )
        logger.info("Created queue '%s' with URL=%s", name, queue.url)
    except ClientError as error:
        logger.exception("Couldn't create queue named '%s'.", name)
        raise error
    else:
        return queue
# snippet-end:[python.example_code.sqs.CreateQueue]


# snippet-start:[python.example_code.sqs.GetQueueUrl]
def get_queue(name):
    """
    Gets an SQS queue by name.

    :param name: The name that was used to create the queue.
    :return: A Queue object.
    """
    try:
        queue = sqs.get_queue_by_name(QueueName=name)
        logger.info("Got queue '%s' with URL=%s", name, queue.url)
    except ClientError as error:
        logger.exception("Couldn't get queue named %s.", name)
        raise error
    else:
        return queue
# snippet-end:[python.example_code.sqs.GetQueueUrl]


# snippet-start:[python.example_code.sqs.ListQueues]
def get_queues(prefix=None):
    """
    Gets a list of SQS queues. When a prefix is specified, only queues with names
    that start with the prefix are returned.

    :param prefix: The prefix used to restrict the list of returned queues.
    :return: A list of Queue objects.
    """
    if prefix:
        queue_iter = sqs.queues.filter(QueueNamePrefix=prefix)
    else:
        queue_iter = sqs.queues.all()
    queues = list(queue_iter)
    if queues:
        logger.info("Got queues: %s", ', '.join([q.url for q in queues]))
    else:
        logger.warning("No queues found.")
    return queues
# snippet-end:[python.example_code.sqs.ListQueues]


# snippet-start:[python.example_code.sqs.DeleteQueue]
def remove_queue(queue):
    """
    Removes an SQS queue. When run against an AWS account, it can take up to
    60 seconds before the queue is actually deleted.

    :param queue: The queue to delete.
    :return: None
    """
    try:
        queue.delete()
        logger.info("Deleted queue with URL=%s.", queue.url)
    except ClientError as error:
        logger.exception("Couldn't delete queue with URL=%s!", queue.url)
        raise error
# snippet-end:[python.example_code.sqs.DeleteQueue]


# snippet-start:[python.example_code.sqs.Scenario_ManageQueues]
def usage_demo():
    """Shows how to create, list, and delete queues."""
    print('-'*88)
    print("Welcome to the Amazon Simple Queue Service (Amazon SQS) demo!")
    print('-'*88)

    prefix = 'sqs-usage-demo-'
    river_queue = create_queue(
        prefix + 'peculiar-river',
        {
            'MaximumMessageSize': str(1024),
            'ReceiveMessageWaitTimeSeconds': str(20)
        }
    )
    print(f"Created queue with URL: {river_queue.url}.")

    lake_queue = create_queue(
        prefix + 'strange-lake.fifo',
        {
            'MaximumMessageSize': str(4096),
            'ReceiveMessageWaitTimeSeconds': str(10),
            'VisibilityTimeout': str(300),
            'FifoQueue': str(True),
            'ContentBasedDeduplication': str(True)
        }
    )
    print(f"Created queue with URL: {lake_queue.url}.")

    stream_queue = create_queue(prefix + 'boring-stream')
    print(f"Created queue with URL: {stream_queue.url}.")

    alias_queue = get_queue(prefix + 'peculiar-river')
    print(f"Got queue with URL: {alias_queue.url}.")

    remove_queue(stream_queue)
    print(f"Removed queue with URL: {stream_queue.url}.", )

    queues = get_queues(prefix=prefix)
    print(f"Got {len(queues)} queues.")
    for queue in queues:
        remove_queue(queue)
        print(f"Removed queue with URL: {queue.url}.")

    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.sqs.Scenario_ManageQueues]


if __name__ == '__main__':
    usage_demo()
