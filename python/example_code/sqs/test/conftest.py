# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon SQS tests.
"""

import sys
import pytest
# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *


@pytest.fixture(name='make_queue')
def fixture_make_queue(request, make_unique_name):
    """
    Return a factory function that can be used to make a queue for testing.

    :param request: The Pytest request object that contains configuration data.
    :param make_unique_name: A fixture that returns a unique name.
    :return: The factory function to make a test queue.
    """
    def _make_queue(sqs_stubber, sqs_resource):
        """
        Make a queue that can be used for testing. When stubbing is used, a stubbed
        queue is created. When AWS services are used, the queue is deleted after
        the test completes.

        :param sqs_stubber: The SqsStubber object, configured for stubbing or AWS.
        :param sqs_resource: The SQS resource, used to create the queue.
        :return: The test queue.
        """
        queue_name = make_unique_name('queue')
        sqs_stubber.add_response(
            'create_queue',
            expected_params={
                'QueueName': queue_name,
                'Attributes': {}
            },
            service_response={'QueueUrl': 'url-' + queue_name}
        )
        queue = sqs_resource.create_queue(QueueName=queue_name, Attributes={})

        def fin():
            if not sqs_stubber.use_stubs:
                queue.delete()
        request.addfinalizer(fin)

        return queue

    return _make_queue
