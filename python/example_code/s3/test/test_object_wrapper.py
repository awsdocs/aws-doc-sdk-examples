# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for object_wrapper.py functions.
"""

from unittest.mock import ANY
import pytest

from botocore.exceptions import ClientError

import bucket_wrapper
import object_wrapper


@pytest.mark.parametrize("object_data", [b"Test data", __file__])
def test_put_get_delete_object(make_stubber, make_unique_name, make_bucket,
                               object_data):
    """Test that put, get, delete of an object into a test bucket works as expected."""
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')
    bucket = make_bucket(stubber, bucket_wrapper, bucket_name, stubber.region_name)
    object_key = make_unique_name('object')

    stubber.stub_put_object(bucket.name, object_key)
    stubber.stub_head_object(bucket.name, object_key)
    stubber.stub_get_object(bucket.name, object_key, object_data)
    stubber.stub_delete_object(bucket.name, object_key)
    stubber.stub_head_object(bucket.name, object_key, 404)
    stubber.stub_get_object_error(bucket.name, object_key, 'NoSuchKey')

    object_wrapper.put_object(bucket, object_key, object_data)
    data = object_wrapper.get_object(bucket, object_key)
    if isinstance(object_data, bytes):
        assert data == object_data
    else:
        with open(object_data, 'rb') as file:
            assert file.read() == data
    object_wrapper.delete_object(bucket, object_key)
    with pytest.raises(ClientError) as exc_info:
        object_wrapper.get_object(bucket, object_key)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchKey'


def test_put_not_file_expect_error(make_stubber, make_unique_name, make_bucket):
    """Test that putting an object using a string that is not a filename
    raises an error."""
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')
    bucket = make_bucket(stubber, bucket_wrapper, bucket_name, stubber.region_name)
    object_key = make_unique_name('object')

    with pytest.raises(IOError):
        object_wrapper.put_object(bucket, object_key, "neither-file-nor-binary")


@pytest.mark.parametrize("object_count", [5, 1, 0])
def test_list_objects_empty_bucket(make_stubber, make_unique_name, make_bucket,
                                   object_count):
    """Test that listing the objects in a test bucket returns the expected list
    and that the bucket can be emptied of objects."""
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')
    bucket = make_bucket(stubber, bucket_wrapper, bucket_name, stubber.region_name)
    object_key = make_unique_name('object')

    put_keys = []
    for obj_suffix in range(0, object_count):
        key = f"{object_key}-{obj_suffix}"
        data = bytes(f"Test data-{obj_suffix}", 'utf-8')
        stubber.stub_put_object(bucket.name, key)
        stubber.stub_head_object(bucket.name, key)
        object_wrapper.put_object(bucket, key, data)
        put_keys.append(key)

    stubber.stub_list_objects(bucket_name, put_keys)
    stubber.stub_list_objects(bucket_name, put_keys, "object")
    stubber.stub_list_objects(bucket_name, put_keys)
    if put_keys:
        stubber.stub_delete_objects(bucket_name, put_keys)
    stubber.stub_list_objects(bucket_name)

    all_objects = object_wrapper.list_objects(bucket)
    assert set(put_keys).issubset([o.key for o in all_objects])
    filtered_objects = object_wrapper.list_objects(bucket, "object")
    assert put_keys == [o.key for o in filtered_objects]
    object_wrapper.empty_bucket(bucket)
    assert [] == object_wrapper.list_objects(bucket)


def test_copy_object(make_stubber, make_unique_name, make_bucket):
    """Test that copying an object from one bucket to another works as expected."""
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    src_bucket = make_bucket(stubber, bucket_wrapper, make_unique_name('src-bucket'))
    dest_bucket = make_bucket(stubber, bucket_wrapper, make_unique_name('dest-bucket'))
    src_object_key = make_unique_name('src-object')
    dest_object_key = make_unique_name('dest-object')

    data = b"Some test data!"

    stubber.stub_put_object(src_bucket.name, src_object_key)
    stubber.stub_head_object(src_bucket.name, src_object_key)
    stubber.stub_copy_object(src_bucket.name, src_object_key,
                             dest_bucket.name, dest_object_key)
    stubber.stub_head_object(dest_bucket.name, dest_object_key)
    stubber.stub_get_object(dest_bucket.name, dest_object_key, data)
    stubber.stub_list_objects(dest_bucket.name, [dest_object_key])
    stubber.stub_delete_objects(dest_bucket.name, [dest_object_key])
    stubber.stub_list_objects(src_bucket.name, [src_object_key])
    stubber.stub_delete_objects(src_bucket.name, [src_object_key])

    object_wrapper.put_object(src_bucket, src_object_key, data)
    copied_obj = object_wrapper.copy_object(src_bucket, src_object_key,
                                            dest_bucket, dest_object_key)
    assert data == copied_obj.get()['Body'].read()
    object_wrapper.empty_bucket(dest_bucket)
    object_wrapper.empty_bucket(src_bucket)


def test_delete_objects(make_stubber, make_unique_name, make_bucket):
    """Test that deleting objects from a bucket works as expected."""
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper, make_unique_name('bucket'))
    object_key = make_unique_name('object')

    put_keys = []
    for suffix in range(0, 5):
        put_key = f"{object_key}-{suffix}"
        stubber.stub_put_object(bucket.name, put_key)
        stubber.stub_head_object(bucket.name, put_key)
        object_wrapper.put_object(bucket, put_key, b"Test data!")
        put_keys.append(put_key)

    delete_keys = put_keys[3:]
    keep_keys = put_keys[:3]

    stubber.stub_delete_objects(bucket.name, delete_keys)
    stubber.stub_list_objects(bucket.name, keep_keys)
    stubber.stub_list_objects(bucket.name)

    response = object_wrapper.delete_objects(bucket, delete_keys)
    assert set(delete_keys) == set(obj['Key'] for obj in response['Deleted'])
    assert set(keep_keys) == set(obj.key for obj in object_wrapper.list_objects(bucket))

    object_wrapper.empty_bucket(bucket)


@pytest.mark.skip_if_real_aws
def test_put_get_acl(make_stubber, make_unique_name, make_bucket):
    """
    Test that put and get of an object ACL works as expected.
    To run this test against the non-stubbed AWS service, you must replace the
    email and canonical_user values with an existing AWS user or the test will fail.
    """
    stubber = make_stubber(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper, make_unique_name('bucket'))
    object_key = make_unique_name('object')

    email = 'arnav@example.net'
    canonical_user = 'arnav'
    stubber.stub_put_object(bucket.name, object_key)
    stubber.stub_head_object(bucket.name, object_key)
    stubber.stub_get_object_acl(bucket.name, object_key)
    stubber.stub_put_object_acl(bucket.name, object_key, email)
    stubber.stub_get_object_acl(bucket.name, object_key, email)
    stubber.stub_list_objects(bucket.name)

    object_wrapper.put_object(bucket, object_key, b"Test data")
    object_wrapper.put_acl(bucket, object_key, email)
    acl = object_wrapper.get_acl(bucket, object_key)
    email_grantee = {
        'Grantee': {
            'Type': 'CanonicalUser',
            'DisplayName': canonical_user,
            'ID': ANY
        },
        'Permission': 'READ'
    }
    assert email_grantee in acl.grants
    owner_grantee = {
        'Grantee': {
            'Type': 'CanonicalUser',
            'ID': acl.owner['ID'],
            'DisplayName': acl.owner['DisplayName']
        },
        'Permission': 'FULL_CONTROL'
    }
    assert owner_grantee in acl.grants

    object_wrapper.empty_bucket(bucket)
