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
Contains common test fixtures used to run SQS tests.
"""

import time
from unittest.mock import MagicMock
import pytest
from botocore.stub import Stubber

import queue_wrapper

def pytest_addoption(parser):
    """Add an option to run tests against a real AWS account instead of the Stubber."""
    parser.addoption(
        "--use-real-aws-may-incur-charges", action="store_true", default=False,
        help="Connect to real AWS services while testing. WARNING: THIS MAY INCUR "
             "CHARGES ON YOUR ACCOUNT!"
    )


@pytest.fixture(name='sqs_stubber')
def fixture_sqs_stubber(request):
    """
    Create a botocore Stubber that can be used to intercept requests and return mocked
    responses.

    When tests are run against a real AWS account, create a mocked Stubber. This
    causes test code that sets up the Stubber to be ignored, so that requests
    are sent through to AWS instead of being intercepted by the Stubber.

    After tests complete, the Stubber checks that no more responses remain in its
    queue. This lets tests verify that all expected calls to the Stubber were
    actually made during the test.

    :param request: An object that contains configuration parameters.
    :return: The Stubber or mocked Stubber object.
    """
    if request.config.getoption("--use-real-aws-may-incur-charges"):
        stubber = MagicMock(unsafe=True)
        stubber.client = queue_wrapper.sqs.meta.client
        stubber.needs_cleanup = True
    else:
        stubber = Stubber(queue_wrapper.sqs.meta.client)
        stubber.needs_cleanup = False

    stubber.activate()
    yield stubber
    stubber.assert_no_pending_responses()
    stubber.deactivate()


@pytest.fixture(name='unique_queue_name')
def fixture_unique_queue_name():
    """
    Creates a unique queue name based on the current time in nanoseconds.

    :return: A unique name that can be used to create a queue.
    """
    name = f"queue{time.time_ns()}"
    print(f"unique_queue_name={name}")
    return name


@pytest.fixture(name='queue_stub')
def fixture_queue_stub(sqs_stubber, unique_queue_name):
    """Create a standard queue to use for message testing. In non-stubbed
    scenarios, delete the queue after the test completes."""
    sqs_stubber.add_response(
        'create_queue',
        expected_params={
            'QueueName': unique_queue_name,
            'Attributes': {}
        },
        service_response={'QueueUrl': 'url' + unique_queue_name}
    )
    queue = queue_wrapper.create_queue(unique_queue_name)
    yield queue
    if sqs_stubber.needs_cleanup:
        queue.delete()
