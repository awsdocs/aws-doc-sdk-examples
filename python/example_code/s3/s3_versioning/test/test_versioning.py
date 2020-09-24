# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for versioning.py functions.
"""

from datetime import datetime, timedelta
from operator import itemgetter
import pytest

from botocore.exceptions import ClientError
from botocore.stub import  ANY
import versioning


@pytest.mark.parametrize("error_code,stop_on_method,keep_going", [
    (None, None, True),
    ('BucketAlreadyOwnedByYou', 'stub_create_bucket', True),
    ('TestException', 'stub_create_bucket', False),
    ('TestException', 'stub_put_bucket_versioning', False),
    ('TestException', 'stub_put_bucket_lifecycle_configuration', True)
])
def test_create_versioned_bucket(
        make_stubber, make_unique_name, stub_runner,
        error_code, stop_on_method, keep_going):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_create_bucket, bucket_name,
            versioning.s3.meta.client.meta.region_name, keep_going=keep_going)
        runner.add(
            s3_stubber.stub_put_bucket_versioning, bucket_name, 'Enabled',
            keep_going=keep_going)
        runner.add(
            s3_stubber.stub_put_bucket_lifecycle_configuration, bucket_name, [{
                'Status': 'Enabled',
                'Prefix': obj_prefix,
                'NoncurrentVersionExpiration': {'NoncurrentDays': ANY}
            }], keep_going=keep_going)

    if error_code is None or keep_going:
        bucket = versioning.create_versioned_bucket(bucket_name, obj_prefix)
        assert bucket.name == bucket_name
    else:
        with pytest.raises(ClientError) as exc_info:
            versioning.create_versioned_bucket(bucket_name, obj_prefix)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("rollback_version", ["version-2", "non-existent-version"])
def test_rollback_object(
        make_stubber, make_unique_name, rollback_version):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    versions = [
        s3_stubber.make_version(
            obj_key, f'version-{index}', True,
            datetime.now() + timedelta(minutes=index))
        for index in range(5)]
    delete_markers = [
        s3_stubber.make_version(
            obj_key, f'version-{index}', True,
            datetime.now() + timedelta(minutes=index))
        for index in range(10, 15)]
    sorted_versions = \
        sorted(versions + delete_markers, key=itemgetter('LastModified'), reverse=True)

    s3_stubber.stub_list_object_versions(
        bucket_name, prefix=obj_key, versions=versions, delete_markers=delete_markers)
    if rollback_version in [ver['VersionId'] for ver in sorted_versions]:
        for version in sorted_versions:
            if version['VersionId'] != rollback_version:
                s3_stubber.stub_delete_object(
                    bucket_name, obj_key, obj_version_id=version['VersionId'])
            else:
                break
        s3_stubber.stub_head_object(bucket_name, obj_key)

    if rollback_version == 'non-existent-version':
        with pytest.raises(KeyError):
            versioning.rollback_object(
                versioning.s3.Bucket(bucket_name), obj_key, rollback_version)
    else:
        versioning.rollback_object(
            versioning.s3.Bucket(bucket_name), obj_key, rollback_version)


@pytest.mark.parametrize(
    'stop_on_method,has_delete_markers,marker_is_latest,has_versions', [
        (None, True, True, True),
        ('stub_list_object_versions', True, False, False),
        ('stub_list_object_versions', False, False, True),
        ('stub_list_object_versions', False, False, False),
        (None, True, True, False)
    ])
def test_revive_object(
        make_stubber, make_unique_name, stub_runner, stop_on_method,
        has_delete_markers, marker_is_latest, has_versions):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')
    delete_markers = [s3_stubber.make_version(
        obj_key, 'version1', marker_is_latest, datetime.now())] \
        if has_delete_markers else None
    versions = [s3_stubber.make_version(
        obj_key, 'version1', True, datetime.now())] if has_versions else None

    with stub_runner(None, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_list_object_versions, bucket_name, obj_key,
            delete_markers=delete_markers, versions=versions, max_keys=1)
        runner.add(
            s3_stubber.stub_delete_object, bucket_name, obj_key,
            obj_version_id='version1')
        runner.add(s3_stubber.stub_head_object, bucket_name, obj_key)
        runner.add(
            s3_stubber.stub_get_object, bucket_name, obj_key,
            object_data=b'Test data', version_id='version1')

    versioning.revive_object(versioning.s3.Bucket(bucket_name), obj_key)


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_permanently_delete_object(make_stubber, make_unique_name, error_code):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')

    s3_stubber.stub_list_object_versions(
        bucket_name, obj_key, delete_markers=
        [s3_stubber.make_version(obj_key, 'version1', True, datetime.now())])
    s3_stubber.stub_delete_object_versions(bucket_name,
        [s3_stubber.make_version(obj_key, 'version1')], error_code=error_code)

    if not error_code:
        versioning.permanently_delete_object(versioning.s3.Bucket(bucket_name), obj_key)
    else:
        with pytest.raises(ClientError) as exc_info:
            versioning.permanently_delete_object(versioning.s3.Bucket(bucket_name),
                                                 obj_key)
        assert exc_info.value.response['Error']['Code'] == error_code
