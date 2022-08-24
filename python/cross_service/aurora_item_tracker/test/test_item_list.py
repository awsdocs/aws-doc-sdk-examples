# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for item_list.py.
"""

from datetime import datetime
from unittest.mock import MagicMock
import boto3
import pytest

from item_list import ItemList, reqparse
from storage import Storage


@pytest.mark.parametrize('item_state, error_code', [
    ('active', None),
    (None, None),
    ('all', None),
    ('active', 'TestException'),
])
def test_get_item_list(make_stubber, item_state, error_code):
    rds_client = boto3.client('rds-data')
    rds_stubber = make_stubber(rds_client)
    cluster_arn = 'test-cluster'
    secret_arn = 'test-secret'
    db_name = 'test-db'
    table_name = 'test-table'
    storage = Storage(cluster_arn, secret_arn, db_name, table_name, rds_client)
    item_list = ItemList(storage)
    work_items = [{
        'id': index, 'name': f'user-{index}', 'description': f'desc-{index}',
        'guide': f'guide-{index}', 'status': f'status-{index}',
        'created_date': str(datetime.now()), 'state': True
        } for index in range(1, 5)]
    if item_state == 'active':
        sql = (f"SELECT work_item_id, created_date, username, description, guide, status, archive "
               f"FROM {table_name} WHERE archive=:archive")
        sql_params = [{'name': 'archive', 'value': {'booleanValue': item_state == 'archive'}}]
    else:
        sql = (f"SELECT work_item_id, created_date, username, description, guide, status, archive "
               f"FROM {table_name} ")
        sql_params = None

    rds_stubber.stub_execute_statement(
        cluster_arn, secret_arn, db_name, sql, sql_params,
        records=[item.values() for item in work_items], error_code=error_code)

    got_work_items, result = item_list.get(item_state)

    if error_code is None:
        assert ([got_item['id'] for got_item in got_work_items] ==
                [item['id'] for item in work_items])
        assert result == 200
    elif error_code == 'RequestTimeout':
        assert result == 408
    else:
        assert result == 400


@pytest.mark.parametrize(
    'error_code', [None, 'TestException'])
def test_post_work_item(make_stubber, monkeypatch, error_code):
    rds_client = boto3.client('rds-data')
    rds_stubber = make_stubber(rds_client)
    cluster_arn = 'test-cluster'
    secret_arn = 'test-secret'
    db_name = 'test-db'
    table_name = 'test-table'
    storage = Storage(cluster_arn, secret_arn, db_name, table_name, rds_client)
    item_list = ItemList(storage)
    work_item = {
        'name': f'user-1', 'description': f'desc-1', 'guide': f'guide-1', 'status': f'status-1'}
    item_id = 1
    sql = (f"INSERT INTO {table_name} (username, description, guide, status) " 
           f" VALUES (:username, :description, :guide, :status)")
    sql_params = [
        {'name': 'username', 'value': {'stringValue': work_item['name']}},
        {'name': 'description', 'value': {'stringValue': work_item['description']}},
        {'name': 'guide', 'value': {'stringValue': work_item['guide']}},
        {'name': 'status', 'value': {'stringValue': work_item['status']}}
    ]

    mock_parser = MagicMock(
        name='mock_parser', return_value=MagicMock(
            parse_args=MagicMock(return_value=work_item)))
    monkeypatch.setattr(reqparse, 'RequestParser', mock_parser)

    rds_stubber.stub_execute_statement(
        cluster_arn, secret_arn, db_name, sql, sql_params, generated_fields=[item_id],
        error_code=error_code)

    got_item_id, result = item_list.post()
    if error_code is None:
        assert got_item_id == item_id
        assert result == 200
    else:
        assert got_item_id is None
        assert result == 400


@pytest.mark.parametrize(
    'error_code', [None, 'TestException'])
def test_put_work_item(make_stubber, monkeypatch, error_code):
    rds_client = boto3.client('rds-data')
    rds_stubber = make_stubber(rds_client)
    cluster_arn = 'test-cluster'
    secret_arn = 'test-secret'
    db_name = 'test-db'
    table_name = 'test-table'
    storage = Storage(cluster_arn, secret_arn, db_name, table_name, rds_client)
    item_list = ItemList(storage)
    item_id = 1
    sql = f"UPDATE {table_name} SET archive=:archive WHERE work_item_id=:work_item_id"
    sql_params = [
        {'name': 'archive', 'value': {'booleanValue': True}},
        {'name': 'work_item_id', 'value': {'longValue': item_id}},
    ]

    rds_stubber.stub_execute_statement(
        cluster_arn, secret_arn, db_name, sql, sql_params, error_code=error_code)

    _, result = item_list.put(item_id)
    if error_code is None:
        assert result == 200
    else:
        assert result == 400
