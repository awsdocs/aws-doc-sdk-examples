# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for datasets.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from datasets import Datasets


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_dataset(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    bucket = 'test-bucket'
    object_key = 'test-object'
    manifest_file = f'{bucket}/{object_key}'
    dataset_type = 'train'
    status = 'CREATE_COMPLETE'
    message = 'Test message'

    lookoutvision_stubber.stub_create_dataset(
        project_name, dataset_type, bucket, object_key, status, message,
        error_code=error_code)
    if error_code is None:
        lookoutvision_stubber.stub_describe_dataset(
            project_name, dataset_type, status, message)

    if error_code is None:
        Datasets.create_dataset(
            lookoutvision_client, project_name, manifest_file, dataset_type)
    else:
        with pytest.raises(ClientError) as exc_info:
            Datasets.create_dataset(
                lookoutvision_client, project_name, manifest_file, dataset_type)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_manifest_file_s3(make_stubber, monkeypatch, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    image_bucket = 'image-bucket'
    image_prefix = 'image-prefix/'
    image_path = f'{image_bucket}/{image_prefix}'
    mani_bucket = 'mani-bucket'
    mani_prefix = 'mani-prefix/'
    manifest_path = f'{mani_bucket}/{mani_prefix}'

    monkeypatch.setattr(
        s3_resource.meta.client, 'upload_file',
        lambda Filename, Bucket, Key, ExtraArgs, Callback, Config: None)

    s3_stubber.stub_list_objects(
        image_bucket, [f'{image_prefix}anomaly/anomaly-test-key'],
        f"{image_prefix}anomaly/", '/')
    s3_stubber.stub_list_objects(
        image_bucket, [f'{image_prefix}normal/normal-test-key'],
        f"{image_prefix}normal/", '/',
        error_code=error_code)

    with open("temp.manifest", 'w') as mani:
        mani.write("Test manifest.")

    if error_code is None:
        Datasets.create_manifest_file_s3(s3_resource, image_path, manifest_path)
    else:
        with pytest.raises(ClientError) as exc_info:
            Datasets.create_manifest_file_s3(s3_resource, image_path, manifest_path)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_dataset(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    dataset_type = 'train'

    lookoutvision_stubber.stub_delete_dataset(
        project_name, dataset_type, error_code=error_code)

    if error_code is None:
        Datasets.delete_dataset(lookoutvision_client, project_name, dataset_type)
    else:
        with pytest.raises(ClientError) as exc_info:
            Datasets.delete_dataset(lookoutvision_client, project_name, dataset_type)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_dataset(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    dataset_type = 'train'
    status = 'CREATE_COMPLETE'
    message = 'Test message'
    image_stats = {'Total': 5, 'Labeled': 2, 'Normal': 2, 'Anomaly': 1}

    lookoutvision_stubber.stub_describe_dataset(
        project_name, dataset_type, status, message, image_stats, error_code=error_code)

    if error_code is None:
        Datasets.describe_dataset(lookoutvision_client, project_name, dataset_type)
    else:
        with pytest.raises(ClientError) as exc_info:
            Datasets.describe_dataset(lookoutvision_client, project_name, dataset_type)
        assert exc_info.value.response['Error']['Code'] == error_code

@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_dataset_entries(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    updates_file = 'test/test_manifests/updates.manifest'
    dataset_type = 'train'
    status_complete = 'UPDATE_COMPLETE'
    status_running = 'UPDATE_IN_PROGRESS'
    message = 'Test message'
    changes = ""


    with open(updates_file) as f:
            changes = f.read()

    lookoutvision_stubber.stub_update_dataset_entries(
        project_name, dataset_type, changes, status_running,
        error_code=error_code)
    if error_code is None:
        lookoutvision_stubber.stub_describe_dataset(
            project_name, dataset_type, status_complete, message)

    if error_code is None:
        Datasets.update_dataset_entries(
            lookoutvision_client, project_name,dataset_type, updates_file )
    else:
        with pytest.raises(ClientError) as exc_info:
            Datasets.update_dataset_entries(
                lookoutvision_client, project_name, dataset_type,updates_file )
        assert exc_info.value.response['Error']['Code'] == error_code