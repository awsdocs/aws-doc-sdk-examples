# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for kinesis_stream.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from streams.kinesis_stream import KinesisStream


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create(make_stubber, error_code):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    stream = KinesisStream(kinesis_client)
    stream_name = 'test-stream'
    stream_arn = f'arn:aws:kinesis:REGION:123456789012:stream/{stream_name}'
    stream_status = 'ACTIVE'

    kinesis_stubber.stub_create_stream(stream_name, error_code=error_code)
    if error_code is None:
        kinesis_stubber.stub_describe_stream(stream_name, stream_arn, stream_status)
        kinesis_stubber.stub_describe_stream(stream_name, stream_arn, stream_status)

    if error_code is None:
        stream.create(stream_name)
        assert stream.name == stream_name
        assert stream.details['StreamStatus'] == stream_status
    else:
        with pytest.raises(ClientError) as exc_info:
            stream.create(stream_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe(make_stubber, error_code):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    stream = KinesisStream(kinesis_client)
    details = {
        'StreamName': 'test-stream',
        'StreamARN': 'arn:aws:kinesis:REGION:123456789012:stream/{stream_name}'}

    kinesis_stubber.stub_describe_stream(
        details['StreamName'], details['StreamARN'], 'ACTIVE', error_code=error_code)

    if error_code is None:
        stream.describe(details['StreamName'])
        assert stream.name == details['StreamName']
        assert stream.arn() == details['StreamARN']
    else:
        with pytest.raises(ClientError) as exc_info:
            stream.describe(details['StreamName'])
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    stream = KinesisStream(kinesis_client)
    stream.name = 'test-stream'

    kinesis_stubber.stub_delete_stream(stream.name, error_code=error_code)

    if error_code is None:
        stream.delete()
        assert stream.name is None
    else:
        with pytest.raises(ClientError) as exc_info:
            stream.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_record(make_stubber, error_code):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    stream = KinesisStream(kinesis_client)
    stream.name = 'test-stream'
    data = 'test-data'
    partition_key = 'test-key'

    kinesis_stubber.stub_put_record(
        stream.name, data, partition_key, error_code=error_code)

    if error_code is None:
        got_response = stream.put_record(data, partition_key)
        assert 'ShardId' in got_response
    else:
        with pytest.raises(ClientError) as exc_info:
            stream.put_record(data, partition_key)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None,'TestException'])
def test_get_records(make_stubber, error_code):
    kinesis_client = boto3.client('kinesis')
    kinesis_stubber = make_stubber(kinesis_client)
    stream = KinesisStream(kinesis_client)
    stream.name = 'test-stream'
    shard_id = 'test-shard-id'
    stream.details = {'Shards': [{'ShardId': shard_id}]}
    max_records = 50
    page_size = 10
    shard_iter = 'test-shard-iter'
    records = [f'test-data-{index}' for index in range(page_size)]

    kinesis_stubber.stub_get_shard_iterator(
        stream.name, shard_id, shard_iter, error_code=error_code)
    if error_code is None:
        for _ in range(0, max_records, page_size):
            kinesis_stubber.stub_get_records(shard_iter, page_size, records)

    if error_code is None:
        for got_records in stream.get_records(max_records):
            assert [record['Data'] for record in got_records] == records
    else:
        with pytest.raises(ClientError) as exc_info:
            for _ in stream.get_records(max_records):
                pass
        assert exc_info.value.response['Error']['Code'] == error_code
