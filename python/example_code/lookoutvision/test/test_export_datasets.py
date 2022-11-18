# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for datasets.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from export_datasets import export_datasets
from export_datasets import copy_file


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_describe_project'),
    ('TestException', 'stub_list_dataset_entries'),
    ('TestException', 'stub_copy_object'),
    ('TestPutException', 'stub_put_object'),
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
    dest_key = 'stubber_test/train/cookies/Anomaly/anomaly-1.jpg'
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
