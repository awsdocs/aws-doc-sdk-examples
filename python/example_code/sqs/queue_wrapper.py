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
    Demonstrate basic queue operations in Amazon Simple Queue Service (Amazon SQS).
    Learn how to create, get, and remove standard, FIFO, and dead-letter queues.
    Usage is shown in the test/test_queue_wrapper.py file.

Prerequisites
    - You must have an AWS account, and have your default credentials and AWS Region
      configured as described in the [AWS Tools and SDKs Shared Configuration and
      Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
    - Python 3.6 or later
    - Boto 3 1.11.10 or later
    - PyTest 5.3.5 or later (to run unit tests)

Running the tests
    The best way to learn how to use this service is to run the tests.
    For instructions on testing, see the docstring in test/test_queue_wrapper.py.

Running the code
    Run individual functions in the Python shell to make calls to your AWS account.

        > python
        >>> import queue_wrapper
        >>> queue_wrapper.create_queue("My-test-queue")
        sqs.Queue(url='https://us-west-2.queue.amazonaws.com/1234EXAMPLE/My-test-queue')
        >>> queue = queue_wrapper.get_queue("My-test-queue")
        >>> queue_wrapper.remove_queue(queue)

Additional information
    Running this code might result in charges to your AWS account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
sqs = boto3.resource('sqs')


def create_queue(name, attributes=None):
    """
    Creates an SQS queue.

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
