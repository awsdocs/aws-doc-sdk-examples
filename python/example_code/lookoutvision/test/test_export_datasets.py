# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for export_datasets.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from export_datasets import export_datasets
from export_datasets import copy_file
from export_datasets import upload_manifest_file
from export_datasets import get_dataset_types
from export_datasets import process_json_line
from export_datasets import write_manifest_file


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_project'),
    ('TestException', 'stub_list_dataset_entries'),
    ('TestException', 'stub_copy_object'),
    ('TestPutException', 'stub_put_object')
])
def test_export_datasets(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'
    s3_path = 's3://dest-bucket/stubber_test/'
    dataset_type = 'train'
    json_lines_file = 'test/test_manifests/updates.manifest'

    dataset = {'DatasetType': 'train', 'Status': 'CREATE_COMPLETE',
               'StatusMessage': 'The model is hosted.'}

    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    src_bucket_name = 'bucket'
    src_key = 'cookies/Anomaly/anomaly-1.jpg'
    dest_bucket_name = 'dest-bucket'
    dest_key = 'stubber_test/train/images/cookies/Anomaly/anomaly-1.jpg'
    manifest_key = 'stubber_test/datasets/train.manifest'

    with open(json_lines_file, encoding='utf-8') as json_file:
        json_lines = json_file.read()

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(lookoutvision_stubber.stub_describe_project,
                   project_name, [dataset])
        runner.add(lookoutvision_stubber.stub_list_dataset_entries,
                   project_name, dataset_type,
                   json_lines)
        runner.add(s3_stubber.stub_copy_object,
                   src_bucket_name, src_key, dest_bucket_name, dest_key)
        if error_code is None or error_code == 'TestPutException':
            runner.add(s3_stubber.stub_head_object, dest_bucket_name, dest_key)
        runner.add(s3_stubber.stub_put_object, dest_bucket_name, manifest_key)
        if error_code is None:
            runner.add(s3_stubber.stub_head_object,
                       dest_bucket_name, manifest_key)

    if error_code is None:
        export_datasets(
            lookoutvision_client, s3_resource, project_name, s3_path)
    else:
        with pytest.raises(ClientError) as exc_info:
            export_datasets(
                lookoutvision_client, s3_resource, project_name,  s3_path)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_copy_object')

])
def test_copy_file(make_stubber, stub_runner, error_code, stop_on_method):

    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    source_file = "s3://bucket/folder/image.jpg"
    destination_file = "s3://bucket2/folder/image.jpg"
    src_bucket_name = "bucket"
    src_key = "folder/image.jpg"
    dest_bucket_name = "bucket2"
    dest_key = "folder/image.jpg"

    with stub_runner(error_code, stop_on_method) as runner:

        runner.add(s3_stubber.stub_copy_object,
                   src_bucket_name, src_key, dest_bucket_name, dest_key)
        if error_code is None:
            runner.add(s3_stubber.stub_head_object, dest_bucket_name, dest_key)

    if error_code is None:
        copy_file(
            s3_resource, source_file, destination_file)
    else:
        with pytest.raises(ClientError) as exc_info:
            copy_file(
                s3_resource, source_file, destination_file)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_put_object')

])
def test_upload_manifest_file(make_stubber, stub_runner, error_code, stop_on_method):

    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)

    dest_bucket_name = 'dest-bucket'
    manifest_folder = 'stubber_test/datasets/'
    manifest_file = "train.manifest"
    destination = f"s3://{dest_bucket_name}/{manifest_folder}/"

    with stub_runner(error_code, stop_on_method) as runner:

        runner.add(s3_stubber.stub_put_object, dest_bucket_name,
                   manifest_folder + "/" + manifest_file)
        if error_code is None:
            runner.add(s3_stubber.stub_head_object,
                       dest_bucket_name, manifest_folder + "/" + manifest_file)

    if error_code is None:
        upload_manifest_file(
            s3_resource, manifest_file, destination)
    else:
        with pytest.raises(ClientError) as exc_info:
            upload_manifest_file(
                s3_resource, manifest_file, destination)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_project')
])
def test_get_dataset_types(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'

    dataset = {'DatasetType': 'train', 'Status': 'CREATE_COMPLETE',
               'StatusMessage': 'The model is hosted.'}

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(lookoutvision_stubber.stub_describe_project,
                   project_name, [dataset])

    if error_code is None:
        get_dataset_types(
            lookoutvision_client, project_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            get_dataset_types(
                lookoutvision_client, project_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_copy_object')

])
def test_process_json_line(make_stubber, stub_runner, error_code, stop_on_method):

    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)

    destination_folder = "s3://bucket2/folder/"
    src_bucket_name = "bucket"
    src_key = "cookies/Anomaly/anomaly-1.jpg"
    dest_bucket_name = "dest-bucket"
    dest_key = "files/train/images/cookies/Anomaly/anomaly-1.jpg"
    destination_folder = f"s3://{dest_bucket_name}/files/"

    json_lines_file = 'test/test_manifests/updates.manifest'

    with open(json_lines_file, encoding='utf-8') as json_file:
        json_line = json_file.read()

    with stub_runner(error_code, stop_on_method) as runner:

        runner.add(s3_stubber.stub_copy_object,
                   src_bucket_name, src_key, dest_bucket_name, dest_key)
        if error_code is None:
            runner.add(s3_stubber.stub_head_object, dest_bucket_name, dest_key)

    if error_code is None:
        process_json_line(
            s3_resource, json_line, "train",  destination_folder)
    else:
        with pytest.raises(ClientError) as exc_info:
            process_json_line(
            s3_resource, json_line, "train",  destination_folder)
        assert exc_info.value.response['Error']['Code'] == error_code

@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_dataset_entries'),
    ('TestException', 'stub_copy_object'),
    ('TestPutException', 'stub_put_object')
])
def test_write_manifest_file(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'
    s3_path = 's3://dest-bucket/stubber_test/'
    dataset_type = 'train'
    json_lines_file = 'test/test_manifests/updates.manifest'

    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    src_bucket_name = 'bucket'
    src_key = 'cookies/Anomaly/anomaly-1.jpg'
    dest_bucket_name = 'dest-bucket'
    dest_key = 'stubber_test/train/images/cookies/Anomaly/anomaly-1.jpg'
    manifest_key = 'stubber_test/datasets/train.manifest'

    with open(json_lines_file, encoding='utf-8') as json_file:
        json_lines = json_file.read()

    with stub_runner(error_code, stop_on_method) as runner:

        runner.add(lookoutvision_stubber.stub_list_dataset_entries,
                   project_name, dataset_type,
                   json_lines)
        runner.add(s3_stubber.stub_copy_object,
                   src_bucket_name, src_key, dest_bucket_name, dest_key)
        if error_code is None or error_code == 'TestPutException':
            runner.add(s3_stubber.stub_head_object, dest_bucket_name, dest_key)
        runner.add(s3_stubber.stub_put_object, dest_bucket_name, manifest_key)
        if error_code is None:
            runner.add(s3_stubber.stub_head_object,
                       dest_bucket_name, manifest_key)

    if error_code is None:
        write_manifest_file(
            lookoutvision_client, s3_resource, project_name, 'train', s3_path)
    else:
        with pytest.raises(ClientError) as exc_info:
            write_manifest_file(
                lookoutvision_client, s3_resource, project_name, 'train', s3_path)
        assert exc_info.value.response['Error']['Code'] == error_code
