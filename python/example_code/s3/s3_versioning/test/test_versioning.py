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


@pytest.mark.parametrize("fail_func,error_code,stop_on_error", [
    (None, None, False),
    ('stub_create_bucket', 'BucketAlreadyOwnedByYou', False),
    ('stub_create_bucket', 'TestException', True),
    ('stub_put_bucket_versioning', 'TestException', True),
    ('stub_put_bucket_lifecycle_configuration', 'TestException', False)
])
def test_create_versioned_bucket(
        make_stubber, make_unique_name, stub_controller,
        fail_func, error_code, stop_on_error):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'

    stub_controller.add(
        s3_stubber.stub_create_bucket,
        (bucket_name, versioning.s3.meta.client.meta.region_name))
    stub_controller.add(s3_stubber.stub_put_bucket_versioning, (bucket_name, 'Enabled'))
    stub_controller.add(s3_stubber.stub_put_bucket_lifecycle_configuration, (
        bucket_name, [{
            'Status': 'Enabled',
            'Prefix': obj_prefix,
            'NoncurrentVersionExpiration': {'NoncurrentDays': ANY}
        }],))

    stub_controller.run(fail_func, error_code, stop_on_error)

    if error_code and stop_on_error:
        with pytest.raises(ClientError) as exc_info:
            versioning.create_versioned_bucket(bucket_name, obj_prefix)
        assert exc_info.value.response['Error']['Code'] == error_code
    else:
        bucket = versioning.create_versioned_bucket(bucket_name, obj_prefix)
        assert bucket.name == bucket_name


@pytest.mark.parametrize("rollback_version", ["version-2", "non-existent-version"])
def test_rollback_object(
        make_stubber, make_unique_name, stub_controller, rollback_version):
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

    stub_controller.add(
        s3_stubber.stub_list_object_versions, (bucket_name,),
        kwargs={'prefix': obj_key, 'versions': versions,
                'delete_markers': delete_markers})
    if rollback_version in [ver['VersionId'] for ver in sorted_versions]:
        for version in sorted_versions:
            if version['VersionId'] != rollback_version:
                stub_controller.add(
                    s3_stubber.stub_delete_object, (bucket_name, obj_key),
                    {'obj_version_id': version['VersionId']})
            else:
                break
        stub_controller.add(
            s3_stubber.stub_head_object, (bucket_name, obj_key))

    stub_controller.run()

    if rollback_version == 'non-existent-version':
        with pytest.raises(KeyError):
            versioning.rollback_object(
                versioning.s3.Bucket(bucket_name), obj_key, rollback_version)
    else:
        versioning.rollback_object(
            versioning.s3.Bucket(bucket_name), obj_key, rollback_version)


@pytest.mark.parametrize(
    'code_path', ['happy', 'not_latest', 'no_deletes', 'no_versions'])
def test_revive_object(make_stubber, make_unique_name, stub_controller, code_path):
    s3_stubber = make_stubber(versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_key = make_unique_name('object')

    if code_path == 'not_latest':
        stub_controller.add(
            s3_stubber.stub_list_object_versions,
            (bucket_name, obj_key),
            {'delete_markers':
                 [s3_stubber.make_version(obj_key, 'version1', False, datetime.now())],
             'max_keys': 1})
    elif code_path == 'no_deletes':
        stub_controller.add(
            s3_stubber.stub_list_object_versions, (bucket_name, obj_key),
            {'versions':
                 [s3_stubber.make_version(obj_key, 'version1', True, datetime.now())],
             'max_keys': 1})
    elif code_path == 'no_versions':
        stub_controller.add(
            s3_stubber.stub_list_object_versions, (bucket_name, obj_key),
            {'max_keys': 1})
    elif code_path == 'happy':
        stub_controller.add(
            s3_stubber.stub_list_object_versions,
            (bucket_name, obj_key),
            {'delete_markers':
                 [s3_stubber.make_version(obj_key, 'version1', True, datetime.now())],
             'max_keys': 1})
        stub_controller.add(
            s3_stubber.stub_delete_object, (bucket_name, obj_key),
            {'obj_version_id': 'version1'})
        stub_controller.add(s3_stubber.stub_head_object, (bucket_name, obj_key))
        stub_controller.add(
            s3_stubber.stub_get_object, (bucket_name, obj_key),
            {'object_data': b'Test data', 'version_id': 'version1'})

    stub_controller.run()

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
