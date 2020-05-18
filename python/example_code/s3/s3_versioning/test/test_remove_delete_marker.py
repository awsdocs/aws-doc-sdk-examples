# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for remove_delete_marker.py functions.
"""

from urllib import parse
import pytest

import remove_delete_marker


def test_remove_delete_marker(make_stubber, make_unique_name, make_event, make_result):
    s3_stubber = make_stubber(remove_delete_marker.s3)
    bucket_name = make_unique_name('bucket')
    # include a space in the object key to verify url-encoding/decoding
    obj_key = make_unique_name('prefix object')
    version_id = 'test-version-id'

    event = make_event(bucket_name, parse.quote(obj_key), version_id=version_id)

    s3_stubber.stub_head_object(
        bucket_name, obj_key, obj_version_id=version_id, error_code='405',
        response_meta={'HTTPHeaders': {'x-amz-delete-marker': 'true'}}
    )
    s3_stubber.stub_delete_object(bucket_name, obj_key, obj_version_id=version_id)

    result = remove_delete_marker.lambda_handler(event, None)
    assert result == make_result('Succeeded')


def test_remove_delete_marker_not_deleted(
        make_stubber, make_unique_name, make_event, make_result):
    s3_stubber = make_stubber(remove_delete_marker.s3)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    version_id = 'test-version-id'

    event = make_event(bucket_name, obj_key, version_id=version_id)

    s3_stubber.stub_head_object(bucket_name, obj_key, obj_version_id=version_id)

    result = remove_delete_marker.lambda_handler(event, None)
    assert result == make_result('PermanentFailure')


def test_remove_delete_marker_no_delete_marker(
    make_stubber, make_unique_name, make_event, make_result):
    s3_stubber = make_stubber(remove_delete_marker.s3)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    version_id = 'test-version-id'

    event = make_event(bucket_name, obj_key, version_id=version_id)

    s3_stubber.stub_head_object(
        bucket_name, obj_key, obj_version_id=version_id, error_code='405',
        response_meta={'HTTPHeaders': {'some-other-header': 'nonsense'}}
    )

    result = remove_delete_marker.lambda_handler(event, None)
    assert result == make_result('PermanentFailure')


@pytest.mark.parametrize('error_code', ['TestException', 'RequestTimeout'])
def test_remove_delete_marker_delete_fails(
        make_stubber, make_unique_name, make_event, make_result, error_code):
    s3_stubber = make_stubber(remove_delete_marker.s3)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    version_id = 'test-version-id'

    event = make_event(bucket_name, obj_key, version_id=version_id)

    s3_stubber.stub_head_object(
        bucket_name, obj_key, obj_version_id=version_id, error_code='405',
        response_meta={'HTTPHeaders': {'x-amz-delete-marker': 'true'}}
    )
    s3_stubber.stub_delete_object(bucket_name, obj_key, obj_version_id=version_id,
                                  error_code=error_code)

    result = remove_delete_marker.lambda_handler(event, None)
    if error_code == 'RequestTimeout':
        assert result == make_result('TemporaryFailure')
    else:
        assert result == make_result('PermanentFailure')
