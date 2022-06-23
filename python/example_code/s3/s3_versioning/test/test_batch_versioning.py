# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for batch_versioning.py functions.
"""

import time
from unittest.mock import MagicMock
from urllib import parse

import pytest

from botocore.exceptions import ClientError
import versioning
import batch_versioning


def test_custom_retry():
    def callback():
        return 'Success!'
    response = batch_versioning.custom_retry(callback, 'test-error', 3)
    assert response == 'Success!'


@pytest.fixture(scope='module')
def monkey_module():
    from _pytest.monkeypatch import MonkeyPatch
    mpatch = MonkeyPatch()
    yield mpatch
    mpatch.undo()


@pytest.fixture(scope='module', autouse=True)
def sleepless(monkey_module):
    monkey_module.setattr(time, 'sleep', lambda x: None)


@pytest.mark.parametrize('error_code', ['test-error', 'garbage-error'])
def test_custom_retry_failure(error_code):
    def callback():
        raise ClientError({'Error': {'Code': 'test-error'}}, 'test-op')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.custom_retry(callback, error_code, 3)
    assert exc_info.value.response['Error']['Code'] == 'test-error'


def test_create_iam_role(make_stubber, make_unique_name):
    iam_stubber = make_stubber(batch_versioning.iam.meta.client)
    role_name = make_unique_name('role')
    policy_name = f'{role_name}-policy'
    policy_arn = f'aws:arn:EXAMPLE:{policy_name}'

    iam_stubber.stub_create_role(role_name)
    iam_stubber.stub_get_role(role_name)
    iam_stubber.stub_create_policy(policy_name, policy_arn)
    iam_stubber.stub_get_policy(policy_arn)
    iam_stubber.stub_attach_role_policy(role_name, policy_arn)

    role = batch_versioning.create_iam_role(role_name)
    assert role.name == role_name


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_role'),
    ('TestException', 'stub_create_policy'),
    ('TestException', 'stub_attach_role_policy')])
def test_create_iam_role_failures(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    iam_stubber = make_stubber(batch_versioning.iam.meta.client)
    role_name = make_unique_name('role')
    policy_name = f'{role_name}-policy'
    policy_arn = f'aws:arn:EXAMPLE:{policy_name}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(iam_stubber.stub_create_role, role_name)
        runner.add(iam_stubber.stub_get_role, role_name)
        runner.add(iam_stubber.stub_create_policy, policy_name, policy_arn)
        runner.add(iam_stubber.stub_get_policy, policy_arn)
        runner.add(iam_stubber.stub_attach_role_policy, role_name, policy_arn)

    if error_code is None:
        got_role = batch_versioning.create_iam_role(role_name)
        assert got_role.name == role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            batch_versioning.create_iam_role(role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_create_lambda_function(make_stubber, make_unique_name):
    lambda_stubber = make_stubber(batch_versioning.aws_lambda)
    function_name = make_unique_name('function')
    function_arn = f'aws:arn:EXAMPLE:{function_name}'
    mock_role = MagicMock()
    mock_role.arn = 'aws:arn:EXAMPLE:iam_role'
    handler = 'test_batch_versioning.test_function'

    lambda_stubber.stub_create_function(
        function_name, function_arn, mock_role.arn, handler)

    returned_arn = batch_versioning.create_lambda_function(
        mock_role, function_name, __file__, handler, 'test lambda function')
    assert returned_arn == function_arn


def test_create_lambda_function_failure(make_stubber, make_unique_name):
    lambda_stubber = make_stubber(batch_versioning.aws_lambda)
    function_name = make_unique_name('function')
    function_arn = f'aws:arn:EXAMPLE:{function_name}'
    mock_role = MagicMock()
    mock_role.arn = 'aws:arn:EXAMPLE:iam_role'
    handler = 'test_batch_versioning.test_function'

    lambda_stubber.stub_create_function(
        function_name, function_arn, mock_role.arn, handler, error_code='TestException')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.create_lambda_function(
            mock_role, function_name, __file__, handler, 'test lambda function')
    assert exc_info.value.response['Error']['Code'] == 'TestException'


def test_create_lambda_function_bad_file():
    with pytest.raises(IOError):
        batch_versioning.create_lambda_function(
            'role', 'func', 'not_a_file.py', 'nonexistent.no_handler', 'None at all')


def test_create_and_fill_bucket(make_stubber, make_unique_name, monkeypatch):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    monkeypatch.setattr(versioning, 'create_versioned_bucket',
                        lambda x, y: batch_versioning.s3.Bucket(bucket_name))
    obj_prefix = 'test-prefix'

    with open(__file__) as file:
        stanzas = file.read().split('\n\n')

    for index in range(len(stanzas)):
        s3_stubber.stub_put_object(bucket_name, f"{obj_prefix}stanza-{index}")

    bucket, stanza_objects = \
        batch_versioning.create_and_fill_bucket(__file__, bucket_name, obj_prefix)
    assert bucket.name == bucket_name
    assert len(stanza_objects) == len(stanzas)


def test_create_and_fill_bucket_failure(make_stubber, make_unique_name, monkeypatch):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    monkeypatch.setattr(versioning, 'create_versioned_bucket',
                        lambda x, y: batch_versioning.s3.Bucket(bucket_name))
    obj_prefix = 'test-prefix'

    s3_stubber.stub_put_object(bucket_name, f"{obj_prefix}stanza-0",
                               error_code='TestException')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.create_and_fill_bucket(__file__, bucket_name, obj_prefix)
    assert exc_info.value.response['Error']['Code'] == 'TestException'


def test_prepare_for_random_revisions():
    manifest_lines = batch_versioning.prepare_for_random_revisions(
        MagicMock(), [MagicMock(), MagicMock(), MagicMock()])
    assert len(manifest_lines) == 5 * 3


def test_prepare_for_revival(make_stubber, make_unique_name):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'

    # include a space in the key to test url-encoding/decoding
    delete_markers = [s3_stubber.make_version(f'key {index}', f'version-{index}', True)
                      for index in range(5)]

    s3_stubber.stub_list_object_versions(
        bucket_name, f'{obj_prefix}stanza', delete_markers=delete_markers)

    manifest_lines = batch_versioning.prepare_for_revival(
         batch_versioning.s3.Bucket(bucket_name), obj_prefix)
    assert manifest_lines == [
        f"{bucket_name},{parse.quote(ver['Key'])},{ver['VersionId']}"
        for ver in delete_markers]


def test_prepare_for_revival_failure(make_stubber, make_unique_name):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'

    s3_stubber.stub_list_object_versions(
        bucket_name, f'{obj_prefix}stanza', error_code='TestException')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.prepare_for_revival(
            batch_versioning.s3.Bucket(bucket_name), obj_prefix)
    assert exc_info.value.response['Error']['Code'] == 'TestException'


def test_prepare_for_cleanup(make_stubber, make_unique_name):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'

    versions = [s3_stubber.make_version(f'key {index}', f'version-{index}', True)
                for index in range(5)]
    delete_markers = [s3_stubber.make_version(f'key {index}', f'version-{index}', True)
                      for index in range(5)]
    s3_stubber.stub_list_object_versions(
        bucket_name, f'{obj_prefix}stanza', versions=versions,
        delete_markers=delete_markers)

    manifest_lines = batch_versioning.prepare_for_cleanup(
        batch_versioning.s3.Bucket(bucket_name), obj_prefix,
        [MagicMock(), MagicMock(), MagicMock()]
    )
    assert manifest_lines == [
        f"{bucket_name},{parse.quote(ver['Key'])},{ver['VersionId']}"
        for ver in delete_markers]


@pytest.mark.parametrize("fail_at", ["put_object", "list_versions"])
def test_prepare_for_cleanup_failure(make_stubber, make_unique_name, fail_at):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'
    stanzas = [MagicMock(), MagicMock(), MagicMock()]

    if fail_at == 'put_object':
        stanzas[0].put = MagicMock(side_effect=ClientError(
            {'Error': {'Code': 'TestException', 'Message': 'hi'}}, 'test-op'))
    elif fail_at == 'list_versions':
        s3_stubber.stub_list_object_versions(
            bucket_name, f'{obj_prefix}stanza', error_code='TestException')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.prepare_for_cleanup(
            batch_versioning.s3.Bucket(bucket_name), obj_prefix, stanzas)
    assert exc_info.value.response['Error']['Code'] == 'TestException'


def test_create_batch_job(make_stubber, make_unique_name):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    s3control_stubber = make_stubber(batch_versioning.s3control)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'
    manifest = {
        'name': 'test-manifest',
        'lines': 'bucket,obj\nbucket,obj\n',
        'key': f'{obj_prefix}test-manifest.csv',
        'e_tag': 'test-e-tag',
        'bucket': batch_versioning.s3.Bucket(bucket_name),
        'obj_prefix': obj_prefix,
        'has_versions': False
    }
    job = {
        'id': 'test-job-id',
        'description': 'test this function',
        'account_id': 'test-account-id',
        'role_arn': 'test-role-arn',
        'function_arn': 'test-function-arn'
    }

    s3_stubber.stub_put_object(bucket_name, manifest['key'], e_tag=manifest['e_tag'])
    s3control_stubber.stub_create_job(
        job['account_id'], job['role_arn'], job['function_arn'],
        bucket_name, manifest['key'], manifest['e_tag'], job['id'])

    returned_job_id = batch_versioning.create_batch_job(job, manifest)
    assert returned_job_id == job['id']


@pytest.mark.parametrize("error_code,stop_on_method", [
    (None, None),
    ('TestException', 'stub_put_object'),
    ('TestException', 'stub_create_job')])
def test_create_batch_job_failure(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    s3control_stubber = make_stubber(batch_versioning.s3control)
    bucket_name = make_unique_name('bucket')
    obj_prefix = 'test-prefix'
    manifest = {
        'lines': 'bucket,obj\nbucket,obj\n',
        'key': f'{obj_prefix}test-manifest.csv',
        'e_tag': 'test-e-tag',
        'bucket': batch_versioning.s3.Bucket(bucket_name),
        'obj_prefix': obj_prefix,
        'has_versions': False
    }
    job = {
        'id': 'test-job-id',
        'description': 'test this function',
        'account_id': 'test-account-id',
        'role_arn': 'test-role-arn',
        'function_arn': 'test-function-arn'
    }

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_put_object, bucket_name, manifest['key'],
            e_tag=manifest['e_tag'])
        runner.add(
            s3control_stubber.stub_create_job, job['account_id'], job['role_arn'],
            job['function_arn'], bucket_name, manifest['key'], manifest['e_tag'],
            job['id'])

    if error_code is None:
        got_job_id = batch_versioning.create_batch_job(job, manifest)
        assert got_job_id == job['id']
    else:
        with pytest.raises(ClientError) as exc_info:
            batch_versioning.create_batch_job(job, manifest)
        assert exc_info.value.response['Error']['Code'] == 'TestException'


@pytest.mark.parametrize('done_status', ['Complete', 'Failed', 'Cancelled'])
def test_report_job_status(make_stubber, done_status):
    s3control_stubber = make_stubber(batch_versioning.s3control)
    account_id = 'test-account-id'
    job_id = 'test-job-id'

    s3control_stubber.stub_describe_job(account_id, job_id, status='Preparing')
    s3control_stubber.stub_describe_job(account_id, job_id, status='Preparing')
    s3control_stubber.stub_describe_job(account_id, job_id, status=done_status)

    batch_versioning.report_job_status(account_id, job_id)


def test_report_job_status_failure(make_stubber):
    s3control_stubber = make_stubber(batch_versioning.s3control)
    account_id = 'test-account-id'
    job_id = 'test-job-id'

    s3control_stubber.stub_describe_job(account_id, job_id, error_code='TestException')

    with pytest.raises(ClientError) as exc_info:
        batch_versioning.report_job_status(account_id, job_id)
    assert exc_info.value.response['Error']['Code'] == 'TestException'


def test_setup_demo(monkeypatch):
    role_name = 'test-role'
    mock_role = MagicMock()
    mock_role.name = role_name
    function_info = {
        'test_function': {
            'file_name': 'test_file.py',
            'handler': 'test_file.lambda_handler',
            'description': 'testing stuff',
            'arn': None
        }
    }
    test_stanzas = ['stanza1', 'stanza2']
    monkeypatch.setattr(
        batch_versioning, 'create_iam_role', lambda x: mock_role)
    monkeypatch.setattr(
        batch_versioning, 'create_lambda_function',
        lambda a, b, c, d, e: 'test-arn'
    )
    monkeypatch.setattr(
        batch_versioning, 'create_and_fill_bucket',
        lambda x, y, z: ('bucket', test_stanzas)
    )

    role, bucket, stanza_objects = \
        batch_versioning.setup_demo(role_name, 'bucket', function_info, 'test-prefix')
    assert role.name == role_name
    assert bucket == 'bucket'
    assert stanza_objects == test_stanzas
    assert function_info['test_function']['arn'] == 'test-arn'


@pytest.mark.parametrize(
    "error_code,stop_on_method,stop_index", [
        (None, None, None),
        ('TestException', 'stub_list_objects', 1),
        ('TestException', 'stub_list_objects', 2),
        ('TestException', 'stub_list_objects', 3),
        ('TestException', 'stub_list_object_versions', None)])
def test_usage_demo_batch_operations(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_method,
        stop_index):
    sts_stubber = make_stubber(batch_versioning.sts)
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket = batch_versioning.s3.Bucket('bucket')
    obj_prefix = 'test-prefix-'
    stanza_prefix = f'{obj_prefix}stanza'
    stanza_key = f'{stanza_prefix}-test'
    versions = [s3_stubber.make_version(f'key-{index}', f'version-{index}', True)
                for index in range(5)]
    delete_markers = [s3_stubber.make_version(f'key-{index}', f'version-{index}', True)
                      for index in range(5)]

    monkeypatch.setattr(
        batch_versioning, 'prepare_for_random_revisions',
        lambda x, y: 'test-revision-manifest'
    )
    monkeypatch.setattr(
        batch_versioning, 'prepare_for_revival', lambda x, y: 'test-revival-manifest')
    monkeypatch.setattr(
        batch_versioning, 'prepare_for_cleanup',
        lambda x, y, z: 'test-cleanup-manifest')
    monkeypatch.setattr(
        batch_versioning, 'create_batch_job', lambda x, y: 'test-job-id')
    monkeypatch.setattr(batch_versioning, 'report_job_status', lambda x, y: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(sts_stubber.stub_get_caller_identity, 'test-account-id')
        runner.add(
            s3_stubber.stub_list_objects, bucket.name, prefix=stanza_prefix,
            object_keys=[stanza_key], keep_going=(stop_index != 1))
        runner.add(
            s3_stubber.stub_list_objects, bucket.name, prefix=stanza_prefix,
            object_keys=[stanza_key], keep_going=(stop_index != 2))
        runner.add(
            s3_stubber.stub_get_object, bucket.name, stanza_key,
            object_data=b'Hey there.')
        runner.add(
            s3_stubber.stub_list_objects, bucket.name, prefix=stanza_prefix,
            object_keys=[stanza_key], keep_going=(stop_index != 3))
        runner.add(
            s3_stubber.stub_list_object_versions, bucket.name, stanza_prefix,
            versions=versions, delete_markers=delete_markers)

    if error_code is None:
        batch_versioning.usage_demo_batch_operations(
            'test-role-arn',
            {
                'revise_stanza': {'arn': 'test-arn'},
                'remove_delete_marker': {'arn': 'test-arn'}
            },
            bucket, ['stanza1', 'stanza2'], obj_prefix)
    else:
        with pytest.raises(ClientError) as exc_info:
            batch_versioning.usage_demo_batch_operations(
                'test-role-arn',
                {
                    'revise_stanza': {'arn': 'test-arn'},
                    'remove_delete_marker': {'arn': 'test-arn'}
                },
                bucket, ['stanza1', 'stanza2'], obj_prefix)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_delete_role'),
    ('TestException', 'stub_delete_function'),
    ('TestException', 'stub_delete_object_versions'),
    ('TestException', 'stub_delete_bucket')])
def test_teardown_demo(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    iam_stubber = make_stubber(batch_versioning.iam.meta.client)
    lambda_stubber = make_stubber(batch_versioning.aws_lambda)
    s3_stubber = make_stubber(batch_versioning.s3.meta.client)
    bucket_name = make_unique_name('bucket')
    role_name = 'test-role'
    policy_arn = 'test-arn-must-be-20-characters'
    function_name = 'test-function'
    function_info = {function_name: 'test-info'}
    versions = [{
        'Key': f'key-{index}',
        'VersionId': f'version-{index}'
    } for index in range(5)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            iam_stubber.stub_list_attached_role_policies, role_name,
            policies={'test-policy': policy_arn})
        runner.add(iam_stubber.stub_get_policy, policy_arn)
        runner.add(iam_stubber.stub_detach_role_policy, role_name, policy_arn)
        runner.add(iam_stubber.stub_delete_policy, policy_arn)
        runner.add(iam_stubber.stub_delete_role, role_name, keep_going=True)
        runner.add(
            lambda_stubber.stub_delete_function, function_name, keep_going=True)
        runner.add(
            s3_stubber.stub_list_object_versions, bucket_name,
            prefix=None, versions=versions)
        runner.add(
            s3_stubber.stub_delete_object_versions, bucket_name, versions,
            keep_going=True)
        runner.add(s3_stubber.stub_delete_bucket, bucket_name, keep_going=True)

    batch_versioning.teardown_demo(role_name, function_info, bucket_name)
