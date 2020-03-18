# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for demo_bucket_basics.py.
"""

import pytest
from botocore.exceptions import ClientError, ParamValidationError

import demo_bucket_basics


@pytest.mark.parametrize("region,keep", [
    ('us-west-2', False), ('us-east-2', True), ('ap-southeast-1', False)])
def test_create_and_delete_my_bucket(make_stubber, make_unique_name, region, keep):
    """Test that running the demo with various regions and arguments works as
    expected."""
    stubber = make_stubber(demo_bucket_basics, 'get_s3', region)
    s3 = demo_bucket_basics.get_s3(region)
    bucket_name = make_unique_name('bucket')

    stubber.stub_list_buckets([])
    stubber.stub_create_bucket(bucket_name, region)
    stubber.stub_head_bucket(bucket_name)
    stubber.stub_list_buckets([s3.Bucket(bucket_name)])
    if keep:
        stubber.stub_head_bucket(bucket_name)
        stubber.stub_delete_bucket(bucket_name)
    else:
        stubber.stub_delete_bucket(bucket_name)
        stubber.stub_head_bucket(bucket_name, 404)
        stubber.stub_list_buckets([])
        stubber.stub_head_bucket_error(bucket_name, 404)

    demo_bucket_basics.create_and_delete_my_bucket(bucket_name, region, keep)
    if keep:
        response = s3.meta.client.head_bucket(Bucket=bucket_name)
        assert response['ResponseMetadata']['HTTPStatusCode'] == 200
        s3.Bucket(bucket_name).delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            s3.meta.client.head_bucket(Bucket=bucket_name)
        assert exc_info.value.response['Error']['Code'] == '404'


def test_create_bucket_fails(make_stubber, make_unique_name, make_bucket):
    """Test that the demo exits gracefully when bucket creation fails."""
    stubber = make_stubber(demo_bucket_basics, 'get_s3')
    s3 = demo_bucket_basics.get_s3()
    bucket_name = make_unique_name('bucket')

    stubber.stub_create_bucket(bucket_name, stubber.region_name)
    stubber.stub_list_buckets([])
    stubber.stub_create_bucket_error(bucket_name, 'BucketAlreadyOwnedByYou',
                                     stubber.region_name)

    bucket = s3.create_bucket(
        Bucket=bucket_name,
        CreateBucketConfiguration={
            'LocationConstraint': stubber.region_name
        }
    )

    with pytest.raises(SystemExit):
        demo_bucket_basics.create_and_delete_my_bucket(
            bucket_name, stubber.region_name, False)

    if not stubber.use_stubs:
        bucket.delete()
