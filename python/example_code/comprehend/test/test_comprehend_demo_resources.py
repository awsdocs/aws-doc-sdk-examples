# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for comprehend_demo_resources.py
"""

from io import BytesIO
import json
import tarfile
import time
from unittest.mock import MagicMock
import uuid
import boto3
from botocore.exceptions import ClientError
import pytest

from comprehend_demo_resources import ComprehendDemoResources


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_bucket')])
def test_setup(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    demo_resources = ComprehendDemoResources(s3_resource, iam_resource)
    demo_name = 'test-name'
    bucket_name = 'doc-example-bucket-test-uuid'
    role_name = f'{demo_name}-role'
    policy_name = f'{demo_name}-policy'
    policy_arn = f'arn:aws:iam:REGION:123456789012:policy/{policy_name}'

    monkeypatch.setattr(uuid, 'uuid4', lambda: 'test-uuid')
    monkeypatch.setattr(time, 'sleep', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_create_bucket, bucket_name,
            s3_resource.meta.client.meta.region_name)
        runner.add(iam_stubber.stub_create_role, role_name)
        runner.add(iam_stubber.stub_get_role, role_name)
        runner.add(iam_stubber.stub_create_policy, policy_name, policy_arn)
        runner.add(iam_stubber.stub_get_policy, policy_arn)
        runner.add(iam_stubber.stub_attach_role_policy, role_name, policy_arn)

    if error_code is None:
        demo_resources.setup(demo_name)
        assert demo_resources.bucket.name == bucket_name
        assert demo_resources.data_access_role.name == role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            demo_resources.setup(demo_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,file_name,file_contents,output', [
    (None, 'name1.jsonl',
     [json.dumps('content1'), json.dumps('content2')],
     ['content1', 'content2']),
    (None, 'name1.csv',
     ['field1,field2', 'value1-1,value1-2', 'value2-1,value2-2'],
     [{'field1': 'value1-1', 'field2': 'value1-2'},
      {'field1': 'value2-1', 'field2': 'value2-2'}]),
    ('TestException', 'name1.jsonl', [], [])])
def test_extract_job_output(monkeypatch, error_code, file_name, file_contents, output):
    demo_resources = ComprehendDemoResources(None, None)
    demo_resources.bucket = MagicMock()
    demo_resources.bucket.name = 'test-bucket'
    job = {'OutputDataConfig': {
        'S3Uri': f's3://{demo_resources.bucket.name}/test-key'}}

    def mock_output(output_key, output_bytes):
        assert output_key == 'test-key'
        output_bytes.write(b'test-content')

    demo_resources.bucket.download_fileobj = mock_output
    if error_code is not None:
        demo_resources.bucket.download_fileobj.side_effect = ClientError(
            {'Error': {'Code': error_code}}, 'test-op')

    def mock_extract_file(name):
        return BytesIO('\n'.join(file_contents).encode())

    monkeypatch.setattr(
        tarfile, 'open', lambda fileobj, mode: MagicMock(
            extractfile=mock_extract_file, getnames=lambda: [file_name]))

    got_output = demo_resources.extract_job_output(job)
    if error_code is None:
        assert got_output[file_name]['data'] == output


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_cleanup(make_stubber, monkeypatch, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    demo_resources = ComprehendDemoResources(s3_resource, iam_resource)
    bucket_name = 'doc-example-bucket-test-uuid'
    role_name = 'comprehend-classifier-demo-role'
    policy_name = 'comprehend-classifier-demo-policy'
    policy_arn = 'arn:aws:iam:REGION:123456789012:policy/test-policy'
    demo_resources.data_access_role = iam_resource.Role(role_name)
    demo_resources.bucket = s3_resource.Bucket(bucket_name)

    iam_stubber.stub_list_attached_role_policies(role_name, {policy_name: policy_arn})
    iam_stubber.stub_detach_role_policy(role_name, policy_arn)
    iam_stubber.stub_delete_policy(policy_arn)
    iam_stubber.stub_delete_role(role_name, error_code=error_code)
    s3_stubber.stub_list_objects(bucket_name, ['key1'])
    s3_stubber.stub_delete_objects(bucket_name, ['key1'])
    s3_stubber.stub_delete_bucket(bucket_name, error_code=error_code)

    demo_resources.cleanup()
