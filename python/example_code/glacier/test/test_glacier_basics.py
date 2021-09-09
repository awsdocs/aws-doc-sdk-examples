# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for glacier_basics.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from glacier_basics import GlacierWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_vault(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault_name = 'test-vault_name'
    vault_uri = f'/123456789012/vaults/{vault_name}'

    glacier_stubber.stub_create_vault(vault_name, vault_uri, error_code=error_code)

    if error_code is None:
        got_vault = glacier.create_vault(vault_name)
        assert got_vault.name == vault_name
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.create_vault(vault_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_vaults(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault_names = [f'test-vault_name-{index}' for index in range(3)]

    glacier_stubber.stub_list_vaults(vault_names, error_code=error_code)

    if error_code is None:
        glacier.list_vaults()
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.list_vaults()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_upload_archive(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault_name')
    archive_description = 'test description'
    archive_file = b'test file contents'
    archive_id = 'EXAMPLEID11111111'

    glacier_stubber.stub_upload_archive(
        vault.name, archive_description, archive_file, archive_id,
        error_code=error_code)

    if error_code is None:
        got_archive = glacier.upload_archive(vault, archive_description, archive_file)
        assert got_archive.id == archive_id
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.upload_archive(vault, archive_description, archive_file)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_initiate_job')])
def test_initiate_inventory_retrieval(
        make_stubber, stub_runner, error_code, stop_on_method):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault_name')
    job_id = 'test-job_id'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            glacier_stubber.stub_initiate_job, vault.name, 'inventory-retrieval',
            job_id, error_code=error_code)
        runner.add(
            glacier_stubber.stub_describe_job, vault.name, job_id, 'InventoryRetrieval',
            error_code=error_code)

    if error_code is None:
        got_job = glacier.initiate_inventory_retrieval(vault)
        assert got_job.id == job_id
        assert got_job.action == 'InventoryRetrieval'
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.initiate_inventory_retrieval(vault)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('job_type,error_code', [
    ('all', None),
    ('in_progress', None),
    ('completed', None),
    ('succeeded', None),
    ('failed', None),
    ('all', 'TestException')])
def test_list_jobs(make_stubber, job_type, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault')
    status_code = (
        'InProgress' if job_type == 'in_progress'
        else 'Succeeded' if job_type == 'succeeded'
        else 'Failed' if job_type == 'failed'
        else None)
    completed = True if job_type == 'completed' else None
    job_ids = [f'job-{index}' for index in range(3)]

    glacier_stubber.stub_list_jobs(
        vault.name, status_code, completed, job_ids, error_code=error_code)

    if error_code is None:
        got_jobs = glacier.list_jobs(vault, job_type)
        assert [j.id for j in got_jobs] == job_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.list_jobs(vault, job_type)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_vault(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault')

    glacier_stubber.stub_delete_vault(vault.name, error_code=error_code)

    if error_code is None:
        glacier.delete_vault(vault)
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.delete_vault(vault)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_initiate_job')])
def test_initiate_archive_retrieval(
        make_stubber, stub_runner, error_code, stop_on_method):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    archive = glacier_resource.Archive('-', 'test-vault', 'test-archive-id')
    job_id = 'test-job-id'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            glacier_stubber.stub_initiate_job, archive.vault_name, 'archive-retrieval',
            job_id, archive_id=archive.id, error_code=error_code)
        runner.add(
            glacier_stubber.stub_describe_job, archive.vault_name, job_id,
            'ArchiveRetrieval', error_code=error_code)

    if error_code is None:
        got_job = glacier.initiate_archive_retrieval(archive)
        assert got_job.id == job_id
        assert got_job.action == 'ArchiveRetrieval'
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.initiate_archive_retrieval(archive)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_archive(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    archive = glacier_resource.Archive('-', 'test-vault', 'test-archive-id')

    glacier_stubber.stub_delete_archive(
        archive.vault_name, archive.id, error_code=error_code)

    if error_code is None:
        glacier.delete_archive(archive)
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.delete_archive(archive)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_job_status(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    job = glacier_resource.Job('-', 'test-vault', 'test-job-id')
    job_action = 'test-action'
    job_status_code = 'test-status'

    glacier_stubber.stub_describe_job(
        job.vault_name, job.id, job_action, job_status_code, error_code=error_code)

    if error_code is None:
        got_job_status = glacier.get_job_status(job)
        assert got_job_status == job_status_code
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.get_job_status(job)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_job_output(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    job = glacier_resource.Job('-', 'test-vault', 'test-job-id')
    archive_desc = 'Test archive description'
    out_bytes = b'These are test bytes!'

    glacier_stubber.stub_get_job_output(
        job.vault_name, job.id, out_bytes, archive_desc=archive_desc,
        error_code=error_code)

    if error_code is None:
        got_out_bytes = glacier.get_job_output(job)
        assert got_out_bytes == out_bytes
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.get_job_output(job)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_set_notifications(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault')
    topic_arn = 'arn:aws:sns:us-east-2:123456789012:TestTopic'

    glacier_stubber.stub_set_vault_notifications(
        vault.name,
        topic_arn, ['ArchiveRetrievalCompleted', 'InventoryRetrievalCompleted'])
    glacier_stubber.stub_get_vault_notifications(
        vault.name,
        topic_arn, ['ArchiveRetrievalCompleted', 'InventoryRetrievalCompleted'],
        error_code=error_code)

    if error_code is None:
        got_notification = glacier.set_notifications(vault, topic_arn)
        assert got_notification.sns_topic == topic_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.set_notifications(vault, topic_arn)
        assert exc_info.value.response['Error']['Code'] == error_code

@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_notification(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    vault = glacier_resource.Vault('-', 'test-vault')
    topic_arn = 'arn:aws:sns:us-east-2:123456789012:TestTopic'
    events = ['TestEvent']

    glacier_stubber.stub_get_vault_notifications(
        vault.name, topic_arn, events, error_code=error_code)

    if error_code is None:
        got_notification = glacier.get_notification(vault)
        assert got_notification.sns_topic == topic_arn
        assert got_notification.events == events
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.get_notification(vault)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_stop_notifications(make_stubber, error_code):
    glacier_resource = boto3.resource('glacier')
    glacier_stubber = make_stubber(glacier_resource.meta.client)
    glacier = GlacierWrapper(glacier_resource)
    notification = glacier_resource.Notification('-', 'vault_name')

    glacier_stubber.stub_delete_vault_notifications(
        notification.vault_name, error_code=error_code)

    if error_code is None:
        glacier.stop_notifications(notification)
    else:
        with pytest.raises(ClientError) as exc_info:
            glacier.stop_notifications(notification)
        assert exc_info.value.response['Error']['Code'] == error_code
