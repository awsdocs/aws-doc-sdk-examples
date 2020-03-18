# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Test fixtures used to run Amazon S3 file transfer tests.
"""

import time
import pytest


def pytest_addoption(parser):
    """Add an option to run tests against a real AWS account instead of the Stubber
    or mocks."""
    parser.addoption(
        "--use-real-aws-may-incur-charges", action="store_true", default=False,
        help="Connect to real AWS services while testing. WARNING: THIS MAY INCUR "
             "CHARGES ON YOUR ACCOUNT!"
    )


@pytest.fixture(name="use_real_aws")
def fixture_use_real_aws(request):
    """Indicates whether the 'use real AWS' option is on or off."""
    return request.config.getoption("--use-real-aws-may-incur-charges")


@pytest.fixture(name='make_unique_name')
def fixture_make_unique_name():
    """
    Creates a unique name based on the current time in nanoseconds.

    :return: A unique name that can be used to create something.
    """
    def _make_unique_name(prefix):
        return f"{prefix}{time.time_ns()}"
    return _make_unique_name


@pytest.fixture(name='make_bucket')
def fixture_make_bucket(request):
    """Makes an Amazon S3 bucket for testing. Empties the bucket and deletes it
    after the test completes."""
    def _make_bucket(resource, bucket_name, region_name=None):
        if not region_name:
            region_name = resource.meta.client.meta.region_name

        bucket = resource.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': region_name
            }
        )

        def fin():
            bucket.objects.delete()
            bucket.delete()
        request.addfinalizer(fin)

        return bucket

    return _make_bucket
