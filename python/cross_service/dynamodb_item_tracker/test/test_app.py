from contextlib import contextmanager
from datetime import datetime

import boto3
from boto3.dynamodb.conditions import Attr
from botocore.stub import ANY
from flask import appcontext_pushed, g
import pytest

from app import create_app
from storage import Storage


_TABLE_NAME = 'test-table'


@contextmanager
def storage_set(app, storage):
    def handler(sender, **kwargs):
        g.storage = storage
    with appcontext_pushed.connected_to(handler, app):
        yield


def test_no_table():
    app = create_app({'TESTING': True, 'TABLE_NAME': _TABLE_NAME})
    with app.test_client() as client:
        rv = client.get('/')
        assert b"Couldn't get items from table" in rv.data


@pytest.mark.parametrize('item_count, status_filter, error_code', [
    (5, None, None),
    (3, 'All', None),
    (1, 'Open', None),
    (0, None, None),
    (3, None, 'TestException')
])
def test_items(make_stubber, item_count, status_filter, error_code):
    ddb_resource = boto3.resource('dynamodb')
    ddb_stubber = make_stubber(ddb_resource.meta.client)
    table = ddb_resource.Table(_TABLE_NAME)
    storage = Storage(table, ddb_resource)
    app = create_app({'TESTING': True, 'TABLE_NAME': _TABLE_NAME})

    items = [{
        'item_id': f'id-{ind}',
        'name': f'Name {ind}',
        'description': f'Description {ind}!',
        'created_date': str(datetime.now()),
        'status': f'Open'
    } for ind in range(5)]

    fex = ANY if status_filter is not None and status_filter != 'All' else None

    ddb_stubber.stub_scan(
        _TABLE_NAME, items, filter_expression=fex, error_code=error_code)

    with storage_set(app, storage):
        with app.test_client() as client:
            rte = '/' if status_filter is None else f'/?status_filter={status_filter}'
            rv = client.get(rte)
            if error_code is None:
                for item in items:
                    assert item['name'].encode() in rv.data
                    assert item['description'].encode() in rv.data
                    assert datetime.fromisoformat(
                        item['created_date']).strftime('%b %d %Y').encode() in rv.data
                    assert item['status'].encode() in rv.data
                    assert b'<div class="alert' not in rv.data
                if not items:
                    assert b'No items found in table' in rv.data
            else:
                for item in items:
                    assert item['name'].encode() not in rv.data
                    assert b"Couldn't get items from table" in rv.data
                    assert b"TestException" in rv.data


@pytest.mark.parametrize('item_id, error_code', [
    (None, None),
    ('id-1', None),
    ('id-1', 'TestException')
])
def test_item_get(make_stubber, item_id, error_code):
    ddb_resource = boto3.resource('dynamodb')
    ddb_stubber = make_stubber(ddb_resource.meta.client)
    table = ddb_resource.Table(_TABLE_NAME)
    storage = Storage(table, ddb_resource)
    app = create_app({'TESTING': True, 'TABLE_NAME': _TABLE_NAME})

    item = {
        'item_id': item_id,
        'name': f'Name test',
        'description': f'Description test!',
        'created_date': str(datetime.now()),
        'status': f'Open'}

    if item_id is not None:
        ddb_stubber.stub_get_item(_TABLE_NAME, {'item_id': item_id}, item, error_code=error_code)

    with storage_set(app, storage):
        with app.test_client() as client:
            rte = '/item/' if item_id is None else f'/item/{item_id}'
            rv = client.get(rte)
            if error_code is None:
                if item_id is not None:
                    assert item['item_id'].encode() in rv.data
                    assert item['name'].encode() in rv.data
                    assert item['description'].encode() in rv.data
                    assert datetime.fromisoformat(
                        item['created_date']).strftime('%b %d %Y').encode() in rv.data
                    assert item['status'].encode() in rv.data
                    assert b'Update</button>' in rv.data
                else:
                    assert item['name'].encode() not in rv.data
                    assert item['description'].encode() not in rv.data
                    assert datetime.fromisoformat(
                        item['created_date']).strftime('%b %d %Y').encode() not in rv.data
                    assert b'Add</button>' in rv.data
                assert b'<div class="alert' not in rv.data
            else:
                assert item['name'].encode() not in rv.data
                assert b"Couldn't get item"
                assert b"TestException" in rv.data


@pytest.mark.parametrize('item_id, error_code', [
    (None, None),
    ('id-1', None),
    ('id-1', 'TestException')
])
def test_item_post(make_stubber, item_id, error_code):
    ddb_resource = boto3.resource('dynamodb')
    ddb_stubber = make_stubber(ddb_resource.meta.client)
    table = ddb_resource.Table(_TABLE_NAME)
    storage = Storage(table, ddb_resource)
    app = create_app({'TESTING': True, 'TABLE_NAME': _TABLE_NAME})

    attribs = {
        'name': f'Name test',
        'description': f'Description test!',
        'status': f'Open'}

    if item_id is None:
        item = {'item_id': ANY, 'created_date': ANY}
        item.update(attribs)
        ddb_stubber.stub_put_item(_TABLE_NAME, item, error_code=error_code)
    else:
        ddb_stubber.stub_update_item_attr_update(
            _TABLE_NAME, {'item_id': item_id}, attribs, error_code=error_code)
    if error_code is None:
        item = {'item_id': 'id-1', 'created_date': str(datetime.now())}
        item.update(attribs)
        ddb_stubber.stub_scan(_TABLE_NAME, [item])

    with storage_set(app, storage):
        with app.test_client() as client:
            rte = '/item/' if item_id is None else f'/item/{item_id}'
            rv = client.post(rte, data=attribs, follow_redirects=True)
            if error_code is None:
                assert _TABLE_NAME.encode() in rv.data
                assert b'<table' in rv.data
                assert attribs['name'].encode() in rv.data
                assert attribs['description'].encode() in rv.data
                assert attribs['status'].encode() in rv.data
                assert b'<div class="alert' not in rv.data
            else:
                if item_id is not None:
                    assert item_id.encode() in rv.data
                assert attribs['name'].encode() in rv.data
                assert b"Couldn't add or update item"
                assert b"TestException" in rv.data
                assert b'<div class="alert' in rv.data


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_item_delete(make_stubber, error_code):
    ddb_resource = boto3.resource('dynamodb')
    ddb_stubber = make_stubber(ddb_resource.meta.client)
    table = ddb_resource.Table(_TABLE_NAME)
    storage = Storage(table, ddb_resource)
    app = create_app({'TESTING': True, 'TABLE_NAME': _TABLE_NAME})
    item_id = 'test_id'
    out_item = {
        'item_id': 'another-id',
        'name': f'Name test',
        'description': f'Description test!',
        'created_date': str(datetime.now()),
        'status': f'Open'}

    ddb_stubber.stub_delete_item(_TABLE_NAME, {'item_id': item_id}, error_code=error_code)
    ddb_stubber.stub_scan(_TABLE_NAME, [out_item])

    with storage_set(app, storage):
        with app.test_client() as client:
            rte = f'/items/delete/{item_id}'
            rv = client.get(rte, follow_redirects=True)
            if error_code is None:
                assert out_item['name'].encode() in rv.data
                assert out_item['description'].encode() in rv.data
                assert out_item['status'].encode() in rv.data
                assert b'<div class="alert' not in rv.data
            else:
                assert b"Couldn't delete item"
                assert b"TestException" in rv.data
                assert b'<div class="alert' in rv.data
