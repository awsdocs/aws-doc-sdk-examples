# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon DynamoDB TryDax code example.
"""

import importlib

import boto3
from boto3.dynamodb.conditions import Key

# import_module is needed because the file names are not valid Python identifiers.
create_table = importlib.import_module('01-create-table')
write_data = importlib.import_module('02-write-data')
getitem_test = importlib.import_module('03-getitem-test')
query_test = importlib.import_module('04-query-test')
scan_test = importlib.import_module('05-scan-test')
delete_table = importlib.import_module('06-delete-table')

TRY_DAX_TABLE = 'TryDaxTable'


def test_create_dax_table(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)

    dyn_stubber.stub_create_table(
        TRY_DAX_TABLE, [
            {'name': 'partition_key', 'type': 'N', 'key_type': 'HASH'},
            {'name': 'sort_key', 'type': 'N', 'key_type': 'RANGE'}
        ],
        {'read': 10, 'write': 10}
    )
    dyn_stubber.stub_describe_table(TRY_DAX_TABLE)

    table = create_table.create_dax_table(dyn)
    assert table.name == TRY_DAX_TABLE


def test_write_data_to_dax_table(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    key_count = 7
    item_size = 42
    data = 'X'*item_size

    for partition in range(1, key_count + 1):
        for sort in range(1, key_count + 1):
            dyn_stubber.stub_put_item(
                TRY_DAX_TABLE, {
                    'partition_key': partition,
                    'sort_key': sort,
                    'some_data': data
                })

    write_data.write_data_to_dax_table(key_count, item_size, dyn)


def test_getitem_test(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    key_count = 9
    iterations = 35
    data = 'X'*100

    for _ in range(iterations):
        for partition in range(1, key_count + 1):
            for sort in range(1, key_count + 1):
                dyn_stubber.stub_get_item(
                    TRY_DAX_TABLE, {
                        'partition_key': partition,
                        'sort_key': sort
                    }, {
                        'partition_key': partition,
                        'sort_key': sort,
                        'some_data': data
                    }
                )

    start, end = getitem_test.get_item_test(key_count, iterations, dyn)
    assert end > start


def test_query_test(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    partition = 3
    sorts = (5, 10)
    iterations = 15

    for _ in range(iterations):
        dyn_stubber.stub_query(TRY_DAX_TABLE, [{
                'partition_key': partition,
                'sort_key': sort,
                'some_data': 'X'*100
            } for sort in range(sorts[0], sorts[1])],
            key_condition=
                Key('partition_key').eq(partition) &
                Key('sort_key').between(*sorts)
        )

    start, end = query_test.query_test(partition, sorts, iterations, dyn)
    assert end > start


def test_scan_test(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)
    iterations = 13

    for _ in range(iterations):
        dyn_stubber.stub_scan(TRY_DAX_TABLE, [{
                'partition_key': key,
                'sort_key': key,
                'some_data': 'X' * 100
            } for key in range(1, 10)])

    start, end = scan_test.scan_test(iterations, dyn)
    assert end > start


def test_delete_table(make_stubber):
    dyn = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn.meta.client)

    dyn_stubber.stub_delete_table(TRY_DAX_TABLE)
    # The table_not_exists waiter waits until this error is returned.
    dyn_stubber.stub_describe_table(
        TRY_DAX_TABLE, error_code='ResourceNotFoundException')

    delete_table.delete_dax_table(dyn)
