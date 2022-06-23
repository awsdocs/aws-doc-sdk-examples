# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Common test fixtures that can be used throughout the unit tests for all Python
code examples.
"""

import contextlib
import time
import pytest

from test_tools.stubber_factory import stubber_factory


def pytest_configure(config):
    config.addinivalue_line(
        "markers", "integ: integration test that requires and uses AWS resources. "
                   "Use of this tag is likely to incur charges on your account."
    )


@pytest.fixture(name='make_stubber')
def fixture_make_stubber(request, monkeypatch):
    """
    Return a factory function that makes an object configured either
    to pass calls through to AWS or to use stubs.

    :param request: An object that contains configuration parameters.
    :param monkeypatch: The Pytest monkeypatch object.
    :return: A factory function that makes the stubber object.
    """
    def _make_stubber(service_client):
        """
        Create a class that wraps the botocore Stubber and implements a variety of
        stub functions that can be used in unit tests for the specified service client.

        After tests complete, the stubber checks that no more responses remain in its
        queue. This lets tests verify that all expected calls were actually made during
        the test.

        When tests are run against an actual AWS account, the stubber does not
        set up stubs and passes all calls through to the Boto 3 client.

        :param service_client: The Boto 3 service client to stub.
        :return: The stubber object, configured either for actual AWS or for stubbing.
        """
        fact = stubber_factory(service_client.meta.service_model.service_name)
        stubber = fact(service_client)

        def fin():
            stubber.assert_no_pending_responses()
            stubber.deactivate()
        request.addfinalizer(fin)
        stubber.activate()

        return stubber

    return _make_stubber


@pytest.fixture(name='make_unique_name')
def fixture_make_unique_name():
    """
    Return a factory function that can be used to create a unique name.

    :return: The function to create a unique name.
    """
    def _make_unique_name(prefix):
        """
        Creates a unique name based on a prefix and the current time in nanoseconds.

        :return: A unique name that can be used to create something, such as
                 an Amazon S3 bucket.
        """
        return f"{prefix}{time.time_ns()}"
    return _make_unique_name


@pytest.fixture(name='make_bucket')
def fixture_make_bucket(request, make_unique_name):
    """
    Return a factory function that can be used to make a bucket for testing.

    :param request: The Pytest request object that contains configuration data.
    :param make_unique_name: A function that creates a unique name.
    :return: The factory function to make a test bucket.
    """
    def _make_bucket(s3_stubber, s3_resource, region_name=None):
        """
        Make a bucket that can be used for testing. When stubbing is used, a stubbed
        bucket is created. When AWS services are used, the bucket is deleted after
        the test completes.

        :param s3_stub: The S3Stubber object, configured for stubbing or AWS.
        :param region_name: The AWS Region in which to create the bucket.
        :return: The test bucket.
        """
        bucket_name = make_unique_name('bucket')
        if not region_name:
            region_name = s3_resource.meta.client.meta.region_name
        s3_stubber.stub_create_bucket(bucket_name, region_name)

        bucket = s3_resource.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': region_name
            }
        )

        def fin():
            if not s3_stubber.use_stubs:
                bucket.delete()
        request.addfinalizer(fin)

        return bucket

    return _make_bucket


class StubRunner:
    """
    Adds stubbed responses until a specified method name is encountered.
    Stubbers and stub functions can be added from any stubber. The error code
    is automatically added to the specified method.
    """
    def __init__(self, error_code, stop_on_method):
        self.stubs = []
        self.error_code = error_code
        self.stop_on_method = stop_on_method

    def add(
            self, func, *func_args, keep_going=False, raise_and_continue=False,
            **func_kwargs):
        """
        Adds a stubbed function response to the list.

        :param stubber: The stubber that implements the specified function.
        :param func: The name of the stub function.
        :param keep_going: When True, continue to process stub responses that follow
                           this function. Otherwise, stop on this function.
        :param func_args: The positional arguments of the stub function.
        :param func_kwargs: The keyword arguments of the stub function.
        """
        self.stubs.append({
            'func': func, 'keep_going': keep_going,
            'raise_and_continue': raise_and_continue,
            'func_args': func_args, 'func_kwargs': func_kwargs
        })

    def run(self):
        """
        Adds stubbed responses until the specified method is encountered. The
        specified error code is added to the kwargs for the final method and no
        more responses are added to the stubbers. When no method is specified, all
        responses are added and the error code is not used.
        In this way, flexible tests can be written that both run successfully through
        all stubbed calls or raise errors and exit early.
        """
        for stub in self.stubs:
            if self.stop_on_method == stub['func'].__name__ and not stub['keep_going']:
                stub['func_kwargs']['error_code'] = self.error_code
            stub['func'](*stub['func_args'], **stub['func_kwargs'])
            if (self.stop_on_method == stub['func'].__name__ and not stub['keep_going']
                    and not stub['raise_and_continue']):
                break


@pytest.fixture
def stub_runner():
    """
    Encapsulates the StubRunner in a context manager so its run function is called
    when the context is exited.
    """
    @contextlib.contextmanager
    def _runner(err, stop):
        runner = StubRunner(err, stop)
        yield runner
        runner.run()
    return _runner
