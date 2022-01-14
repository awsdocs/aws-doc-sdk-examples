# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for demo_bucket_basics.py.
"""

import pytest

import boto3
from botocore.exceptions import ClientError

import demo_bucket_basics


@pytest.mark.parametrize("keep, error_code, stop_on_method", [
    (True, None, None),
    (False, None, None),
    (False, 'TestException', 'stub_list_buckets'),
    (False, 'TestException', 'stub_create_bucket'),
    (False, 'TestException', 'stub_delete_bucket'),
])
def test_create_and_delete_my_bucket(
        make_stubber, stub_runner, keep, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    region = s3_resource.meta.client.meta.region_name

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(s3_stubber.stub_list_buckets, [])
        runner.add(s3_stubber.stub_create_bucket, bucket_name, region)
        runner.add(s3_stubber.stub_head_bucket, bucket_name)
        runner.add(s3_stubber.stub_list_buckets, [s3_resource.Bucket(bucket_name)])
        if not keep:
            runner.add(s3_stubber.stub_delete_bucket, bucket_name)
            runner.add(s3_stubber.stub_head_bucket, bucket_name, 404)
            runner.add(s3_stubber.stub_list_buckets, [])

    if error_code is None:
        demo_bucket_basics.create_and_delete_my_bucket(s3_resource, bucket_name, keep)
    else:
        with pytest.raises(ClientError) as exc_info:
            demo_bucket_basics.create_and_delete_my_bucket(s3_resource, bucket_name, keep)
        assert exc_info.value.response['Error']['Code'] == error_code
