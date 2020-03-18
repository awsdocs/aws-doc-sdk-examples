# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon S3 tests.
"""

import time
import pytest
from s3_stub_funcs import S3Stubber


def pytest_addoption(parser):
    """Add an option to run tests against a real AWS account instead of the Stubber."""
    parser.addoption(
        "--use-real-aws-may-incur-charges", action="store_true", default=False,
        help="Connect to real AWS services while testing. WARNING: THIS MAY INCUR "
             "CHARGES ON YOUR ACCOUNT!"
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
            pytest.skip("Non-stubbed AWS will fail because of test data. To run this "
                        "test under AWS, you must first substitute actual data, such "
                        "as user IDs, for test data.")


@pytest.fixture(name='make_stubber')
def fixture_make_stubber(request, monkeypatch):
    """
    Return a factory function that makes an S3Stubber object configured either
    to pass calls through to AWS or to use stubs.

    :param request: An object that contains configuration parameters.
    :param monkeypatch: The Pytest monkeypatch object.
    :return: A factory function that makes the S3Stubber object.
    """
    def _make_stubber(wrapper, resource_get_func_name, region_name=None):
        """
        Create a class that wraps the Botocore Stubber and implements a variety of
        stub functions that are used by the Amazon S3 unit tets.

        After tests complete, the S3Stubber checks that no more responses remain in its
        queue. This lets tests verify that all expected calls were actually made during
        the test.

        When tests are run against a real AWS account, the S3Stubber class does not
        set up stubs and passes all calls through to the Boto 3 S3 resource.

        :param wrapper: The wrapper class that contains the Boto 3 S3 resource.
        :param resource_get_func_name: The name of the function in the wrapper object
                                       that returns the Boto 3 S3 resource.
        :param region_name: The region to configure for the Boto 3 S3 resource.
        :return: The S3Stubber object, configured either for real AWS or for stubbing.
        """
        resource = getattr(wrapper, resource_get_func_name)(region_name)
        stubber = S3Stubber(
            resource.meta.client,
            not request.config.getoption("--use-real-aws-may-incur-charges")
        )

        monkeypatch.setattr(wrapper, resource_get_func_name,
                            lambda rgn=None: resource)

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

        :return: A unique name that can be used to create something, such as a bucket.
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
        :param region_name: The region in which to create the bucket.
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
