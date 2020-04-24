# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Common test fixtures that can be used throughout the unit tests for all Python
code examples.
"""

import time
import pytest

from test_tools.stubber_factory import stubber_factory


def pytest_addoption(parser):
    """Add an option to run tests against an actual AWS account instead of
    the Stubber."""
    parser.addoption(
        "--use-real-aws-may-incur-charges", action="store_true", default=False,
        help="Connect to real AWS services while testing. **Warning: this might incur "
             "charges on your account!**"
    )


def pytest_configure(config):
    """Register the skip_if_real_aws marker with Pytest."""
    config.addinivalue_line(
        "markers", "skip_if_real_aws: mark test to run only when stubbed."
    )


def pytest_runtest_setup(item):
    """Handle the custom marker skip_if_real_aws, which skips a test when it is
    run against actual AWS services."""
    skip_if_real_aws = 'skip_if_real_aws' in [m.name for m in item.iter_markers()]
    if skip_if_real_aws:
        if item.config.getoption("--use-real-aws-may-incur-charges"):
            pytest.skip("When run with actual AWS services instead of stub functions, "
                        "this test will fail because it uses test data. To run this "
                        "test with AWS services, you must first substitute actual "
                        "data, such as user IDs, for test data.")


@pytest.fixture(name="use_real_aws")
def fixture_use_real_aws(request):
    """Indicates whether the 'use_real_aws' option is on or off."""
    return request.config.getoption("--use-real-aws-may-incur-charges")


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
        stubber = fact(
            service_client,
            not request.config.getoption("--use-real-aws-may-incur-charges")
        )

        if stubber.use_stubs:
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
def fixture_make_bucket(request):
    """
    Return a factory function that can be used to make a bucket for testing.

    :param request: The Pytest request object that contains configuration data.
    :return: The factory function to make a test bucket.
    """
    def _make_bucket(s3_stub, wrapper, bucket_name, region_name=None):
        """
        Make a bucket that can be used for testing. When stubbing is used, a stubbed
        bucket is created. When AWS services are used, the bucket is deleted after
        the test completes.

        :param s3_stub: The S3Stubber object, configured for stubbing or AWS.
        :param wrapper: The bucket wrapper object, used to create the bucket.
        :param bucket_name: The unique name for the bucket.
        :param region_name: The AWS Region in which to create the bucket.
        :return: The test bucket.
        """
        if not region_name:
            region_name = s3_stub.region_name
        s3_stub.stub_create_bucket(bucket_name, region_name)
        # Bucket.wait_until_exists calls head_bucket on a timer until it returns 200.
        s3_stub.stub_head_bucket(bucket_name)

        bucket = wrapper.create_bucket(bucket_name, region_name)

        def fin():
            if not s3_stub.use_stubs and wrapper.bucket_exists(bucket_name):
                bucket.delete()
        request.addfinalizer(fin)

        return bucket

    return _make_bucket
