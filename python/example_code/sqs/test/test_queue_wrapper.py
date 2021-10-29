# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for queue_wrapper.py functions.
"""

import json
import time
import pytest

from botocore.exceptions import ClientError

import queue_wrapper


@pytest.mark.parametrize("attributes", [
    ({}),
    ({
        'MaximumMessageSize': str(4096),
        'ReceiveMessageWaitTimeSeconds': str(10),
        'VisibilityTimeout': str(300)
    })
])
def test_create_standard_queue(make_stubber, make_unique_name, attributes):
    """Test that creating a standard queue returns a queue with the expected
     form of URL."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')
    url = 'url-' + queue_name

    sqs_stubber.stub_create_queue(queue_name, attributes, url)

    queue = queue_wrapper.create_queue(queue_name, attributes)
    assert queue_name in queue.url

    if not sqs_stubber.use_stubs:
        queue_wrapper.remove_queue(queue)


@pytest.mark.parametrize("attributes", [
    ({}),
    ({
        'MaximumMessageSize': str(1024),
        'ReceiveMessageWaitTimeSeconds': str(20)
    })
])
def test_create_fifo_queue(make_stubber, make_unique_name, attributes):
    """Test that creating a FIFO queue returns a queue with the expected form of URL."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    # FIFO queues require a '.fifo' suffix on the queue name.
    queue_name = make_unique_name('queue') + '.fifo'
    attributes['FifoQueue'] = str(True)
    url = 'url-' + queue_name

    sqs_stubber.stub_create_queue(queue_name, attributes, url)

    queue = queue_wrapper.create_queue(queue_name, attributes)
    assert queue.url.endswith(queue_name)

    if not sqs_stubber.use_stubs:
        queue_wrapper.remove_queue(queue)


def test_create_dead_letter_queue(make_stubber, make_unique_name):
    """
    Test that creating a queue with an associated dead-letter queue results in
    the source queue being listed in the dead-letter queue's source queue list.

    A dead-letter queue is any queue that is designated as a dead-letter target
    by another queue's redrive policy.
    """
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    dl_queue_name = make_unique_name('queue') + '_my_lost_messages'
    dl_url = 'url-' + dl_queue_name
    queue_name = make_unique_name('queue')
    url = 'url-' + queue_name

    # Create a dead-letter queue.
    sqs_stubber.stub_create_queue(dl_queue_name, {}, dl_url)
    dl_queue = queue_wrapper.create_queue(dl_queue_name)

    sqs_stubber.stub_get_queue_attributes(dl_url, 'arn:' + dl_queue_name)

    # Create the source queue that sends dead-letter messages to the dead-letter queue.
    # The redrive policy must be in JSON format. Note that its attribute names
    # start with lowercase letters.
    redrive_attributes = {
        'RedrivePolicy': json.dumps({
            'deadLetterTargetArn': dl_queue.attributes['QueueArn'],
            'maxReceiveCount': str(5)
        })
    }
    sqs_stubber.stub_create_queue(queue_name, redrive_attributes, url)
    sqs_stubber.stub_list_dead_letter_source_queues(dl_url, [url])

    queue = queue_wrapper.create_queue(queue_name, redrive_attributes)
    sources = list(dl_queue.dead_letter_source_queues.all())
    assert queue in sources

    if not sqs_stubber.use_stubs:
        queue_wrapper.remove_queue(queue)
        queue_wrapper.remove_queue(dl_queue)


def test_create_queue_bad_name():
    """Test that creating a queue with invalid characters in the name raises
    an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue("Queue names cannot contain spaces!")


def test_create_standard_queue_with_fifo_extension(make_unique_name):
    """Test that creating a standard queue with the '.fifo' extension
    raises an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue(make_unique_name('queue') + '.fifo')


def test_create_fifo_queue_without_extension(make_unique_name):
    """Test that creating a FIFO queue without the '.fifo' extension
    raises an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue(
            make_unique_name('queue'),
            attributes={'FifoQueue': str(True)})


def test_get_queue(make_stubber, make_unique_name):
    """Test that creating a queue and then getting it by name returns the same queue."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')
    url = 'url-' + queue_name

    sqs_stubber.stub_create_queue(queue_name, {}, url)
    sqs_stubber.stub_get_queue_url(queue_name, url)

    created = queue_wrapper.create_queue(queue_name)
    gotten = queue_wrapper.get_queue(queue_name)
    assert created == gotten

    if not sqs_stubber.use_stubs:
        queue_wrapper.remove_queue(created)


def test_get_queue_nonexistent(make_stubber, make_unique_name):
    """Test that getting a nonexistent queue raises an exception."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')
    url = 'url-' + queue_name

    sqs_stubber.stub_get_queue_url(
        queue_name, url, error_code='AWS.SimpleQueueService.NonExistentQueue')

    with pytest.raises(ClientError) as exc_info:
        queue_wrapper.get_queue(queue_name)
    assert exc_info.value.response['Error']['Code'] == \
        'AWS.SimpleQueueService.NonExistentQueue'


def test_get_queues(make_stubber, make_unique_name):
    """
    Test that creating some queues, then retrieving all the queues, returns a list
    that contains at least all of the newly created queues.
    """
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')

    created_queues = []
    for ind in range(1, 4):
        name = queue_name + str(ind)
        sqs_stubber.stub_create_queue(name, {}, 'url-' + name)
        created_queues.append(queue_wrapper.create_queue(name))

    sqs_stubber.stub_list_queues([q.url for q in created_queues])

    # When running against the real AWS service, SQS may not return new queues right
    # away. Use a simple exponential backoff to retry until it succeeds.
    retries = 0
    intersect_queues = []
    while not intersect_queues and retries <= 5:
        time.sleep(min(retries * 2, 32))
        gotten_queues = queue_wrapper.get_queues()
        intersect_queues = [cq for cq in created_queues if cq in gotten_queues]
        retries += 1

    assert created_queues == intersect_queues

    if not sqs_stubber.use_stubs:
        for queue in created_queues:
            queue_wrapper.remove_queue(queue)


def test_get_queues_prefix(make_stubber, make_unique_name):
    """
    Test that creating some queues with a unique prefix, then retrieving a list of
    queues that have that unique prefix, returns a list that contains exactly the
    newly created queues.
    """
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')

    created_queues = []
    for ind in range(1, 4):
        name = queue_name + str(ind)
        sqs_stubber.stub_create_queue(name, {}, 'url-' + name)
        created_queues.append(queue_wrapper.create_queue(name))

    sqs_stubber.stub_list_queues([q.url for q in created_queues], queue_name)

    gotten_queues = queue_wrapper.get_queues(queue_name)
    assert created_queues == gotten_queues

    if not sqs_stubber.use_stubs:
        for queue in created_queues:
            queue_wrapper.remove_queue(queue)


def test_get_queues_expect_none(make_stubber, make_unique_name):
    """Test that getting queues with a random prefix returns an empty list
     and doesn't raise an exception."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')

    sqs_stubber.stub_list_queues([], queue_name)

    queues = queue_wrapper.get_queues(queue_name)
    assert not queues


def test_remove_queue(make_stubber, make_unique_name):
    """Test that creating a queue, deleting it, then trying to get it raises
     an exception because the queue no longer exists."""
    sqs_stubber = make_stubber(queue_wrapper.sqs.meta.client)
    queue_name = make_unique_name('queue')
    url = 'url-' + queue_name

    sqs_stubber.stub_create_queue(queue_name, {}, url)
    sqs_stubber.stub_delete_queue(url)
    sqs_stubber.stub_get_queue_url(
        queue_name, url, error_code='AWS.SimpleQueueService.NonExistentQueue')

    queue = queue_wrapper.create_queue(queue_name)
    queue_wrapper.remove_queue(queue)
    with pytest.raises(ClientError) as exc_info:
        queue_wrapper.get_queue(queue_name)
    assert exc_info.value.response['Error']['Code'] == \
        'AWS.SimpleQueueService.NonExistentQueue'
