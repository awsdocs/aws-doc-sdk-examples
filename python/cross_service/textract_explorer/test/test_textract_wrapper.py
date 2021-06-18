# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for textract_wrapper.py.
"""

from io import BytesIO
import json
from unittest.mock import patch, mock_open
import boto3
from botocore.exceptions import ClientError
import pytest

from textract_wrapper import TextractWrapper


@pytest.mark.parametrize('func_kwargs,doc_bytes,error_code', [
    ({'document_file_name': 'test-doc-file'}, b'test-bytes', None),
    ({'document_bytes': b'test-bytes'}, b'test-bytes', None),
    ({'document_bytes': b'test-bytes'}, b'test-bytes', 'TestException')])
def test_detect_file_text(
        make_stubber, monkeypatch, func_kwargs, doc_bytes, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    blocks = [{'BlockType': 'TEST'}]

    textract_stubber.stub_detect_document_text(
        doc_bytes, blocks, error_code=error_code)

    if error_code is None:
        if list(func_kwargs.keys())[0] == 'document_file_name':
            with patch('builtins.open', mock_open(read_data=doc_bytes)) as mock_file:
                got_blocks = twrapper.detect_file_text(**func_kwargs)
            mock_file.assert_called_once_with(func_kwargs['document_file_name'], 'rb')
        else:
            got_blocks = twrapper.detect_file_text(**func_kwargs)
        assert got_blocks['Blocks'] == blocks
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.detect_file_text(**func_kwargs)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('func_kwargs,doc_bytes,error_code', [
    ({'document_file_name': 'test-doc-file'}, b'test-bytes', None),
    ({'document_bytes': b'test-bytes'}, b'test-bytes', None),
    ({'document_bytes': b'test-bytes'}, b'test-bytes', 'TestException')])
def test_analyze_file(make_stubber, func_kwargs, doc_bytes, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    feature_types = ['TABLES', 'FORMS']
    blocks = [{'BlockType': 'TEST'}]

    textract_stubber.stub_analyze_document(
        doc_bytes, feature_types, blocks, error_code=error_code)

    if error_code is None:
        if list(func_kwargs.keys())[0] == 'document_file_name':
            with patch('builtins.open', mock_open(read_data=doc_bytes)) as mock_file:
                got_blocks = twrapper.analyze_file(feature_types, **func_kwargs)
            mock_file.assert_called_once_with(func_kwargs['document_file_name'], 'rb')
        else:
            got_blocks = twrapper.analyze_file(feature_types, **func_kwargs)
        assert got_blocks['Blocks'] == blocks
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.analyze_file(feature_types, **func_kwargs)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_prepare_job(monkeypatch, error_code):
    s3_resource = boto3.resource('s3')
    twrapper = TextractWrapper(None, s3_resource, None)
    bucket_name = 'test-bucket_name'
    document_name = 'test-document_name'
    doc_bytes = BytesIO(b'test-doc-bytes')

    def mock_upload(Fileobj, Bucket, Key, ExtraArgs, Callback, Config):
        assert Bucket == bucket_name
        assert Fileobj == doc_bytes
        assert Key == document_name
        if error_code is not None:
            raise ClientError({'Error': {'Code': error_code}}, 'test-op')

    monkeypatch.setattr(s3_resource.meta.client, 'upload_fileobj', mock_upload)

    if error_code is None:
        twrapper.prepare_job(bucket_name, document_name, doc_bytes)
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.prepare_job(bucket_name, document_name, doc_bytes)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_check_job_queue(make_stubber, error_code):
    sqs_resource = boto3.resource('sqs')
    sqs_stubber = make_stubber(sqs_resource.meta.client)
    twrapper = TextractWrapper(None, None, sqs_resource)
    queue_url = 'test-queue_url'
    job_id = 'test-job_id'
    status = 'test-status'
    messages = [{
        'body': json.dumps({
            'Message': json.dumps({'JobId': job_id, 'Status': status})})}]

    sqs_stubber.stub_receive_messages(
        queue_url, messages, None, omit_wait_time=True, message_attributes=None,
        error_code=error_code)
    if error_code is None:
        sqs_stubber.stub_delete_message(queue_url, receipt_handle='Receipt-0')

    got_status = twrapper.check_job_queue(queue_url, job_id)
    if error_code is None:
        assert got_status == status
    else:
        assert got_status is None


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_detection_job(make_stubber, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    bucket_name = 'test-bucket_name'
    file_name = 'test-file'
    topic_arn = 'arn:aws:sns:REGION:123456789012:topic/test-topic'
    role_arn = 'arn:aws:iam:REGION:123456789012:role/test-role'
    job_id = 'test-job-id'

    textract_stubber.stub_start_document_text_detection(
        bucket_name, file_name, job_id, topic_arn=topic_arn, role_arn=role_arn,
        error_code=error_code)

    if error_code is None:
        got_job_id = twrapper.start_detection_job(
            bucket_name, file_name, topic_arn, role_arn)
        assert got_job_id == job_id
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.start_detection_job(bucket_name, file_name, topic_arn, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_detection_job(make_stubber, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    job_id = 'test-job_id'
    job_status = 'SUCCEEDED'

    textract_stubber.stub_get_document_text_detection(
        job_id, job_status, error_code=error_code)

    if error_code is None:
        got_job_status = twrapper.get_detection_job(job_id)
        assert got_job_status['JobStatus'] == job_status
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.get_detection_job(job_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_analysis_job(make_stubber, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    bucket_name = 'test-bucket_name'
    file_name = 'test-file_name'
    feature_types = ['TABLES', 'FORMS']
    topic_arn = 'arn:aws:sns:REGION:123456789012:topic/test-topic'
    role_arn = 'arn:aws:iam:REGION:123456789012:role/test-role'
    job_id = 'test-job_id'

    textract_stubber.stub_start_document_analysis(
        bucket_name, file_name, feature_types, job_id, topic_arn=topic_arn,
        role_arn=role_arn, error_code=error_code)

    if error_code is None:
        got_job_id = twrapper.start_analysis_job(
            bucket_name, file_name, feature_types, topic_arn, role_arn)
        assert got_job_id == job_id
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.start_analysis_job(
                bucket_name, file_name, feature_types, topic_arn, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_analysis_job(make_stubber, error_code):
    textract_client = boto3.client('textract')
    textract_stubber = make_stubber(textract_client)
    twrapper = TextractWrapper(textract_client, None, None)
    job_id = 'test-job_id'
    job_status = 'SUCCEEDED'

    textract_stubber.stub_get_document_analysis(
        job_id, job_status, error_code=error_code)

    if error_code is None:
        got_job_status = twrapper.get_analysis_job(job_id)
        assert got_job_status['JobStatus'] == job_status
    else:
        with pytest.raises(ClientError) as exc_info:
            twrapper.get_analysis_job(job_id)
        assert exc_info.value.response['Error']['Code'] == error_code


test_input = [
    {'Id': '1', 'BlockType': 'PAGE',
     'Relationships': [{'Type': 'CHILD', 'Ids': ['1-1', '1-2']}]},
    {'Id': '2', 'BlockType': 'PAGE',
     'Relationships': [{'Type': 'CHILD', 'Ids': ['2-1']}]},
    {'Id': '1-1', 'BlockType': 'LINE',
     'Relationships': [{'Type': 'CHILD', 'Ids': ['1-1-1', '1-1-2', '1-1-3']}]},
    {'Id': '1-2', 'BlockType': 'LINE',
     'Relationships': [{'Type': 'CHILD', 'Ids': ['1-2-1']}]},
    {'Id': '2-1', 'BlockType': 'LINE'},
    {'Id': '1-1-1', 'BlockType': 'WORD'},
    {'Id': '1-1-2', 'BlockType': 'WORD'},
    {'Id': '1-1-3', 'BlockType': 'WORD'},
    {'Id': '1-2-1', 'BlockType': 'WORD'}]

test_hierarchy = {
    'Children': [{
        'Id': '1', 'BlockType': 'PAGE',
        'Relationships': [{'Type': 'CHILD', 'Ids': ['1-1', '1-2']}],
        'Children': [{
            'Id': '1-1', 'BlockType': 'LINE',
            'Relationships': [{'Type': 'CHILD', 'Ids': ['1-1-1', '1-1-2', '1-1-3']}],
            'Children': [
                {'Id': '1-1-1', 'BlockType': 'WORD'},
                {'Id': '1-1-2', 'BlockType': 'WORD'},
                {'Id': '1-1-3', 'BlockType': 'WORD'}
            ]}, {
            'Id': '1-2', 'BlockType': 'LINE',
            'Relationships': [{'Type': 'CHILD', 'Ids': ['1-2-1']}],
            'Children': [
                {'Id': '1-2-1', 'BlockType': 'WORD'},
            ]}]}, {
        'Id': '2', 'BlockType': 'PAGE',
        'Relationships': [{'Type': 'CHILD', 'Ids': ['2-1']}],
        'Children': [{
            'Id': '2-1', 'BlockType': 'LINE'}]
    }]}


def test_make_page_hierarchy():
    got_blocks = TextractWrapper.make_page_hierarchy(test_input)
    assert got_blocks == test_hierarchy
