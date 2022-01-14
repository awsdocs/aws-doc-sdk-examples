# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for object_wrapper.py functions.
"""

from unittest.mock import ANY
import pytest

import boto3
from botocore.exceptions import ClientError

from object_wrapper import ObjectWrapper


@pytest.mark.parametrize('data, error_code', [
    (__file__, None),
    (b'test data', None),
    (b'test data', 'TestException')
])
def test_put(make_stubber, data, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    key = 'test-key'
    wrapper = ObjectWrapper(s3_resource.Object(bucket_name, key))

    s3_stubber.stub_put_object(bucket_name, key, error_code=error_code)
    if error_code is None:
        s3_stubber.stub_head_object(bucket_name, key)

    if error_code is None:
        wrapper.put(data)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put(data)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    key = 'test-key'
    wrapper = ObjectWrapper(s3_resource.Object(bucket_name, key))
    data = b'test-data'

    s3_stubber.stub_get_object(bucket_name, key, data, error_code=error_code)

    if error_code is None:
        got_data = wrapper.get()
        assert got_data == data
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    bucket = s3_resource.Bucket(bucket_name)
    prefix = 'test-prefix'
    keys = [f'{prefix}-{ind}' for ind in range(3)]

    s3_stubber.stub_list_objects(bucket_name, keys, prefix, error_code=error_code)

    if error_code is None:
        got_objects = ObjectWrapper.list(bucket, prefix)
        assert [o.key for o in got_objects] == keys
    else:
        with pytest.raises(ClientError) as exc_info:
            ObjectWrapper.list(bucket, prefix)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_copy(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    src_bucket_name = 'source-bucket'
    src_key = 'source-key'
    dest_bucket_name = 'dest-bucket'
    dest_key = 'dest-key'
    wrapper = ObjectWrapper(s3_resource.Object(src_bucket_name, src_key))
    dest_obj = s3_resource.Object(dest_bucket_name, dest_key)

    s3_stubber.stub_copy_object(
        src_bucket_name, src_key, dest_bucket_name, dest_key, error_code=error_code)
    if error_code is None:
        s3_stubber.stub_head_object(dest_bucket_name, dest_key)

    if error_code is None:
        wrapper.copy(dest_obj)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.copy(dest_obj)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    key = 'test-key'
    wrapper = ObjectWrapper(s3_resource.Object(bucket_name, key))

    s3_stubber.stub_delete_object(bucket_name, key, error_code=error_code)
    if error_code is None:
        s3_stubber.stub_head_object(bucket_name, key, status_code=404)

    if error_code is None:
        wrapper.delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_objects(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    bucket = s3_resource.Bucket('test-bucket')
    keys = [f'key-{ind}' for ind in range(3)]

    s3_stubber.stub_delete_objects(bucket_name, keys, error_code=error_code)

    if error_code is None:
        got_dels = ObjectWrapper.delete_objects(bucket, keys)
        assert [d['Key'] for d in got_dels['Deleted']] == keys
    else:
        with pytest.raises(ClientError) as exc_info:
            ObjectWrapper.delete_objects(bucket, keys)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_empty_bucket(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    bucket = s3_resource.Bucket(bucket_name)
    keys = [f'key-{ind}' for ind in range(3)]

    s3_stubber.stub_list_objects(bucket_name, keys)
    s3_stubber.stub_delete_objects(bucket_name, keys, error_code=error_code)

    if error_code is None:
        ObjectWrapper.empty_bucket(bucket)
    else:
        with pytest.raises(ClientError) as exc_info:
            ObjectWrapper.empty_bucket(bucket)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_acl(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    key = 'test-key'
    wrapper = ObjectWrapper(s3_resource.Object(bucket_name, key))
    mail = 'test-mail'

    s3_stubber.stub_get_object_acl(bucket_name, key)
    s3_stubber.stub_put_object_acl(bucket_name, key, mail, error_code=error_code)

    if error_code is None:
        wrapper.put_acl(mail)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_acl(mail)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_acl(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket'
    key = 'test-key'
    wrapper = ObjectWrapper(s3_resource.Object(bucket_name, key))

    s3_stubber.stub_get_object_acl(bucket_name, key, error_code=error_code)

    if error_code is None:
        got_acl = wrapper.get_acl()
        assert got_acl.owner['DisplayName'] == 'test-owner'
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_acl()
        assert exc_info.value.response['Error']['Code'] == error_code
