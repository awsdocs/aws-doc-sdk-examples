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
    Unit tests for queue_wrapper.py functions.

Running the tests
    Tests can be run in two modes. By default, the tests use the botocore Stubber.
    This captures requests before they are sent to AWS and returns a mocked response.
    You can also run the tests against your AWS account. In this case, they will
    create and manipulate AWS resources, which may incur charges on your account.

    To run the tests for this module with the botocore Stubber, run the following in
    your <GitHub root>/python/example_code/sqs folder.

        python -m pytest -o log_cli=1 --log-cli-level=INFO test/test_queue_wrapper.py

    The '-o log_cli=1 --log-cli-level=INFO' flags configure pytest to output
    logs to stdout during the test run. Without them, pytest captures logs and prints
    them only when the test fails.

    To run the tests using your AWS account and default shared credentials, include the
    '--use-real-aws-may-incur-charges' flag.

        python -m pytest -o log_cli=1 --log-cli-level=INFO --use-real-aws-may-incur-charges test/test_queue_wrapper.py

    Note that this may incur charges to your AWS account. When run in this mode,
    a best effort is made to clean up any resources created during the test. But it's
    your responsibility to verify that all resources have actually been cleaned up.
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
def test_create_standard_queue(sqs_stubber, unique_queue_name, attributes):
    """Test that creating a standard queue returns a queue with the expected
     form of URL."""

    sqs_stubber.add_response(
        'create_queue',
        expected_params={
            'QueueName': unique_queue_name,
            'Attributes': attributes
        },
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )

    queue = queue_wrapper.create_queue(unique_queue_name, attributes)
    assert unique_queue_name in queue.url

    if sqs_stubber.needs_cleanup:
        queue_wrapper.remove_queue(queue)


@pytest.mark.parametrize("attributes", [
    ({}),
    ({
        'MaximumMessageSize': str(1024),
        'ReceiveMessageWaitTimeSeconds': str(20)
    })
])
def test_create_fifo_queue(sqs_stubber, unique_queue_name, attributes):
    """Test that creating a FIFO queue returns a queue with the expected form of URL."""

    # FIFO queues require a '.fifo' suffix on the queue name.
    name = unique_queue_name + '.fifo'
    attributes['FifoQueue'] = str(True)

    sqs_stubber.add_response(
        'create_queue',
        expected_params={
            'QueueName': name,
            'Attributes': attributes
        },
        service_response={'QueueUrl': 'url' + name}
    )

    queue = queue_wrapper.create_queue(name, attributes)
    assert name in queue.url

    if sqs_stubber.needs_cleanup:
        queue_wrapper.remove_queue(queue)


def test_create_dead_letter_queue(sqs_stubber, unique_queue_name):
    """
    Test that creating a queue with an associated dead-letter queue results in
    the source queue being listed in the dead-letter queue's source queue list.

    A dead-letter queue is any queue that is designated as a dead-letter target
    by another queue's redrive policy.
    """
    dl_queue_name = unique_queue_name + '_my_lost_messages'
    sqs_stubber.add_response(
        'create_queue',
        expected_params={
            'QueueName': dl_queue_name,
            'Attributes': {}
        },
        service_response={'QueueUrl': 'url' + dl_queue_name}
    )
    dl_queue = queue_wrapper.create_queue(dl_queue_name)

    sqs_stubber.add_response(
        'get_queue_attributes',
        expected_params={
            'AttributeNames': ['All'],
            'QueueUrl': 'url' + dl_queue_name
        },
        service_response={'Attributes': {'QueueArn': 'arn:' + unique_queue_name}}
    )

    # The redrive policy must be in JSON format. Note that its attribute names
    # start with lowercase letters.
    attributes = {
        'RedrivePolicy': json.dumps({
            'deadLetterTargetArn': dl_queue.attributes['QueueArn'],
            'maxReceiveCount': str(5)
        })
    }

    sqs_stubber.add_response(
        'create_queue',
        expected_params={
            'QueueName': unique_queue_name,
            'Attributes': attributes
        },
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )
    sqs_stubber.add_response(
        'list_dead_letter_source_queues',
        expected_params={'QueueUrl': 'url' + dl_queue_name},
        service_response={'queueUrls': ['url' + unique_queue_name]}
    )

    queue = queue_wrapper.create_queue(unique_queue_name, attributes)
    sources = list(dl_queue.dead_letter_source_queues.all())
    assert queue in sources

    if sqs_stubber.needs_cleanup:
        queue_wrapper.remove_queue(queue)
        queue_wrapper.remove_queue(dl_queue)


def test_create_queue_bad_name():
    """Test that creating a queue with invalid characters in the name raises
    an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue("Queue names cannot contain spaces!")


def test_create_standard_queue_with_fifo_extension(unique_queue_name):
    """Test that creating a standard queue with the '.fifo' extension
    raises an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue(unique_queue_name + '.fifo')


def test_create_fifo_queue_without_extension(unique_queue_name):
    """Test that creating a FIFO queue without the '.fifo' extension
    raises an exception."""
    with pytest.raises(ClientError):
        queue_wrapper.create_queue(
            unique_queue_name,
            attributes={'FifoQueue': str(True)})


def test_get_queue(sqs_stubber, unique_queue_name):
    """Test that creating a queue and then getting it by name returns the same queue."""
    sqs_stubber.add_response(
        'create_queue',
        expected_params={'QueueName': unique_queue_name, 'Attributes': {}},
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )
    sqs_stubber.add_response(
        'get_queue_url',
        expected_params={'QueueName': unique_queue_name},
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )

    created = queue_wrapper.create_queue(unique_queue_name)
    gotten = queue_wrapper.get_queue(unique_queue_name)
    assert created == gotten

    if sqs_stubber.needs_cleanup:
        queue_wrapper.remove_queue(created)


def test_get_queue_nonexistent(sqs_stubber, unique_queue_name):
    """Test that getting a nonexistent queue raises an exception."""
    sqs_stubber.add_client_error(
        'get_queue_url',
        expected_params={'QueueName': unique_queue_name},
        service_error_code='AWS.SimpleQueueService.NonExistentQueue'
    )

    with pytest.raises(sqs_stubber.client.exceptions.QueueDoesNotExist):
        queue_wrapper.get_queue(unique_queue_name)


def test_get_queues(sqs_stubber, unique_queue_name):
    """
    Test that creating some queues, then retrieving all the queues, returns a list
    that contains at least all of the newly created queues.
    """
    created_queues = []
    for ind in range(1, 4):
        name = unique_queue_name + str(ind)
        sqs_stubber.add_response(
            'create_queue',
            expected_params={'QueueName': name, 'Attributes': {}},
            service_response={'QueueUrl': 'url' + name}
        )
        created_queues.append(queue_wrapper.create_queue(name))

    sqs_stubber.add_response(
        'list_queues',
        expected_params={},
        service_response={'QueueUrls': [q.url for q in created_queues]}
    )

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

    if sqs_stubber.needs_cleanup:
        for queue in created_queues:
            queue_wrapper.remove_queue(queue)


def test_get_queues_prefix(sqs_stubber, unique_queue_name):
    """
    Test that creating some queues with a unique prefix, then retrieving a list of
    queues that have that unique prefix, returns a list that contains exactly the
    newly created queues.
    """
    created_queues = []
    for ind in range(1, 4):
        name = unique_queue_name + str(ind)
        sqs_stubber.add_response(
            'create_queue',
            expected_params={'QueueName': name, 'Attributes': {}},
            service_response={'QueueUrl': 'url' + name}
        )
        created_queues.append(queue_wrapper.create_queue(name))

    sqs_stubber.add_response(
        'list_queues',
        expected_params={'QueueNamePrefix': unique_queue_name},
        service_response={'QueueUrls': [q.url for q in created_queues]}
    )

    gotten_queues = queue_wrapper.get_queues(unique_queue_name)

    assert created_queues == gotten_queues

    if sqs_stubber.needs_cleanup:
        for queue in created_queues:
            queue_wrapper.remove_queue(queue)


def test_get_queues_expect_none(sqs_stubber, unique_queue_name):
    """Test that getting queues with a random prefix returns an empty list
     and doesn't raise an exception."""
    sqs_stubber.add_response(
        'list_queues',
        expected_params={'QueueNamePrefix': unique_queue_name},
        service_response={}
    )

    queues = queue_wrapper.get_queues(unique_queue_name)
    assert not queues


def test_remove_queue(sqs_stubber, unique_queue_name):
    """Test that creating a queue, deleting it, then trying to get it raises
     an exception because the queue no longer exists."""
    sqs_stubber.add_response(
        'create_queue',
        expected_params={'QueueName': unique_queue_name, 'Attributes': {}},
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )
    sqs_stubber.add_response(
        'delete_queue',
        expected_params={'QueueUrl': 'url' + unique_queue_name},
        service_response={}
    )
    sqs_stubber.add_client_error(
        'get_queue_url',
        expected_params={'QueueName': unique_queue_name},
        service_error_code='AWS.SimpleQueueService.NonExistentQueue'
    )

    queue = queue_wrapper.create_queue(unique_queue_name)
    queue_wrapper.remove_queue(queue)
    with pytest.raises(sqs_stubber.client.exceptions.QueueDoesNotExist):
        queue_wrapper.get_queue(unique_queue_name)
