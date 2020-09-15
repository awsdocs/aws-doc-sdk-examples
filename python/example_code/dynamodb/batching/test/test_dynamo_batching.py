# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon DynamoDB batching code example.
"""

import time
import unittest.mock
from botocore.exceptions import ClientError
import pytest
import dynamo_batching


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_table')])
def test_create_table(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    dyn_stubber = make_stubber(dynamo_batching.dynamodb.meta.client)
    table_name = make_unique_name('table-')
    schema = [
        {'name': 'hash_item', 'type': 'N', 'key_type': 'HASH'},
        {'name': 'range_item', 'type': 'S', 'key_type': 'RANGE'}]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            dyn_stubber, 'stub_create_table', table_name, schema,
            {'read': 10, 'write': 10})
        runner.add(dyn_stubber, 'stub_describe_table', table_name)

    if error_code is None:
        got_table = dynamo_batching.create_table(table_name, schema)
        assert got_table.name == table_name
    else:
        with pytest.raises(ClientError) as exc_info:
            dynamo_batching.create_table(table_name, schema)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_do_batch_get(make_stubber, monkeypatch):
    dyn_stubber = make_stubber(dynamo_batching.dynamodb.meta.client)
    item_count = 5
    request_keys = {
        'test-table1': {
            'Keys': [{'test': {'S': f'test-{index}'}} for index in range(item_count)]},
        'test-table2': {
            'Keys': [{'test': {'S': f'test-{index}'}} for index in range(item_count)]}
    }
    response_items = {
        'test-table1':
            [{'test': {'S': f'test-{index}' for index in range(item_count)}}],
        'test-table2':
            [{'test': {'S': f'test-{index}' for index in range(item_count)}}],
    }

    monkeypatch.setattr(time, 'sleep', lambda x: None)

    dyn_stubber.stub_batch_get_item(request_keys, unprocessed_keys=request_keys)
    dyn_stubber.stub_batch_get_item(request_keys, response_items=response_items)

    got_data = dynamo_batching.do_batch_get(request_keys)
    for key in request_keys:
        assert got_data[key] == response_items[key]


@pytest.mark.parametrize(
    'item_count,error_code',
    [(0, None),
     (10, None),
     (25, None),
     (100, None),
     (13, 'TestException')])
def test_fill_table(make_stubber, item_count, error_code):
    dyn_stubber = make_stubber(dynamo_batching.dynamodb.meta.client)
    table = dynamo_batching.dynamodb.Table('test-table')
    table_data = [{'test': f'test-{index}'} for index in range(item_count)]

    max_batch_size = 25  # Amazon DynamoDB limit
    data_index = 0
    while data_index < item_count:
        dyn_stubber.stub_batch_write_item({
            table.name: [{
                'PutRequest': {'Item': item}}
                for item in table_data[data_index:data_index+max_batch_size]]
        }, error_code=error_code)
        data_index += max_batch_size

    if error_code is None:
        dynamo_batching.fill_table(table, table_data)
    else:
        with pytest.raises(ClientError) as exc_info:
            dynamo_batching.fill_table(table, table_data)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    'item_count,error_code',
    [(10, None),
     (500, None),
     (dynamo_batching.MAX_GET_SIZE, None),
     (13, 'TestException')])
def test_get_batch_data(monkeypatch, item_count, error_code):
    movie_table = unittest.mock.MagicMock()
    movie_table.name = 'movie-test'
    movie_list = [(index, f'title-{index}') for index in range(item_count)]
    actor_table = unittest.mock.MagicMock()
    actor_table.name = 'actor-test'
    actor_list = [f'actor-{index}' for index in range(item_count)]
    test_data = {movie_table.name: movie_list, actor_table.name: actor_list}

    def mock_do_batch_get(batch_keys):
        if error_code is not None:
            raise ClientError({'Error': {'Code': error_code}}, 'test_op')
        assert len(batch_keys[movie_table.name]['Keys']) == len(movie_list)
        assert len(batch_keys[actor_table.name]['Keys']) == len(actor_list)
        return test_data
    monkeypatch.setattr(dynamo_batching, 'do_batch_get', mock_do_batch_get)

    if error_code is None:
        got_data = dynamo_batching.get_batch_data(
            movie_table, movie_list, actor_table, actor_list)
        assert got_data == test_data
    else:
        with pytest.raises(ClientError) as exc_info:
            dynamo_batching.get_batch_data(
                movie_table, movie_list, actor_table, actor_list)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('item_count,error_code,stop_on_method', [
    (20, None, None),
    (10, 'TestException', 'stub_create_table'),
    (10, 'TestException', 'stub_batch_write_item'),
])
def test_archive_movies(
        make_stubber, stub_runner, item_count, error_code, stop_on_method):
    dyn_stubber = make_stubber(dynamo_batching.dynamodb.meta.client)
    movie_table = dynamo_batching.dynamodb.Table('movie-test')
    movie_list = [
        {'year': index, 'title': f'title-{index}'} for index in range(item_count)]
    table_schema = [
        {'name': 'year', 'type': 'N', 'key_type': 'HASH'},
        {'name': 'title', 'type': 'S', 'key_type': 'RANGE'}]
    archive_table_name = f'{movie_table.name}-archive'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            dyn_stubber, 'stub_describe_table', movie_table.name, schema=table_schema,
            provisioned_throughput={'ReadCapacityUnits': 10, 'WriteCapacityUnits': 10})
        runner.add(
            dyn_stubber, 'stub_create_table', archive_table_name, table_schema,
            {'read': 10, 'write': 10})
        runner.add(dyn_stubber, 'stub_describe_table', archive_table_name)
        runner.add(
            dyn_stubber, 'stub_batch_write_item', {
                archive_table_name: [{
                    'PutRequest': {'Item': item}} for item in movie_list]},
            error_code='ValidationException')
        runner.add(
            dyn_stubber, 'stub_batch_write_item', {
                archive_table_name: [{
                    'PutRequest': {'Item': item}} for item in movie_list]})
        runner.add(
            dyn_stubber, 'stub_batch_write_item', {
                movie_table.name: [{
                    'DeleteRequest': {'Key': item}} for item in movie_list]})

    if error_code is None:
        got_table = dynamo_batching.archive_movies(movie_table, movie_list)
        assert got_table.name == archive_table_name
    else:
        with pytest.raises(ClientError) as exc_info:
            dynamo_batching.archive_movies(movie_table, movie_list)
        assert exc_info.value.response['Error']['Code'] == error_code
