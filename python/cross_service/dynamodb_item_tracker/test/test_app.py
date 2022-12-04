# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the dynamodb_item_tracker example.
"""

import json
import boto3
from botocore.stub import ANY
import pytest

from app import create_app
import storage
from storage import Storage


class MockManager:
    def __init__(self, resource, stubber, ses_client, ses_stubber, stub_runner):
        self.resource = resource
        self.stubber = stubber
        self.ses_client = ses_client
        self.ses_stubber = ses_stubber
        self.stub_runner = stub_runner
        self.table = resource.Table('test-table')
        self.storage = Storage(self.table)
        self.web_items = [{
            'id': f'id-{index}',
            'description': f'desc-{index}',
            'guide': f'guide-{index}',
            'status': f'status-{index}',
            'name': f'user-{index}',
            'archived': index % 2 == 0
        } for index in range(1, 5)]
        self.data_items = [{
            'iditem': f'id-{index}',
            'description': f'desc-{index}',
            'guide': f'guide-{index}',
            'status': f'status-{index}',
            'username': f'user-{index}',
            'archived': index % 2 == 0
        } for index in range(1, 5)]
        self.sender = 'sender@example.com'
        self.recipient = 'recipient@example.com'
        self.app = create_app({
            'TESTING': True, 'TABLE_NAME': self.table.name, 'SENDER_EMAIL': self.sender,
            'DYNAMODB_RESOURCE': resource, 'SES_CLIENT': ses_client})


@pytest.fixture
def mock_mgr(make_stubber, stub_runner):
    resource = boto3.resource('dynamodb')
    stubber = make_stubber(resource.meta.client)
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    return MockManager(resource, stubber, ses_client, ses_stubber, stub_runner)


@pytest.mark.parametrize('archived, filter_ex', [
    ('false', ANY),
    ('true', ANY),
    (None, None),
])
def test_get_items(mock_mgr, archived, filter_ex):
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(mock_mgr.stubber.stub_scan, mock_mgr.table.name, mock_mgr.data_items, filter_expression=filter_ex)

    with mock_mgr.app.test_client() as client:
        rte = '/api/items' if archived is None else f'/api/items?archived={archived}'
        rv = client.get(rte)
        assert rv.status_code == 200
        assert mock_mgr.web_items == rv.json


def test_get_items_error(mock_mgr):
    with mock_mgr.stub_runner('TestException', 'stub_scan') as runner:
        runner.add(mock_mgr.stubber.stub_scan, mock_mgr.table.name, mock_mgr.data_items)

    with mock_mgr.app.test_client() as client:
        rv = client.get('/api/items')
        assert rv.status_code == 500
        assert "A storage error occurred" in rv.json


def test_get_item(mock_mgr):
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(
            mock_mgr.stubber.stub_get_item, mock_mgr.table.name,
            {'iditem': mock_mgr.data_items[0]['iditem']},
            mock_mgr.data_items[0])

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}'
        rv = client.get(rte)
        assert rv.status_code == 200
        assert [mock_mgr.web_items[0]] == rv.json


def test_get_item_error(mock_mgr):
    with mock_mgr.stub_runner('TestException', 0) as runner:
        runner.add(
            mock_mgr.stubber.stub_get_item, mock_mgr.table.name,
            {'iditem': mock_mgr.data_items[0]['iditem']},
            mock_mgr.data_items[0])

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}'
        rv = client.get(rte)
        assert rv.status_code == 500


def test_post_item(mock_mgr, monkeypatch):
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(
            mock_mgr.stubber.stub_put_item, mock_mgr.table.name, mock_mgr.data_items[1])

    post_item = mock_mgr.web_items[1].copy()
    del post_item['id']

    monkeypatch.setattr(storage, 'uuid4', lambda: mock_mgr.data_items[1]['iditem'])

    with mock_mgr.app.test_client() as client:
        rte = '/api/items'
        rv = client.post(rte, json=post_item)
        assert rv.status_code == 200
        assert mock_mgr.web_items[1]['id'] == rv.json


def test_post_item_error(mock_mgr, monkeypatch):
    with mock_mgr.stub_runner('TestException', 0) as runner:
        runner.add(
            mock_mgr.stubber.stub_put_item, mock_mgr.table.name, mock_mgr.data_items[1])

    post_item = mock_mgr.web_items[1].copy()
    del post_item['id']

    monkeypatch.setattr(storage, 'uuid4', lambda: mock_mgr.data_items[1]['iditem'])

    with mock_mgr.app.test_client() as client:
        rte = '/api/items'
        rv = client.post(rte, json=post_item)
        assert rv.status_code == 500


def test_put_item(mock_mgr, monkeypatch):
    data_item = mock_mgr.data_items[0].copy()
    del data_item['iditem']

    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(
            mock_mgr.stubber.stub_update_item_attr_update,
            mock_mgr.table.name, {'iditem': mock_mgr.data_items[0]['iditem']},
            data_item)

    monkeypatch.setattr(storage, 'uuid4', lambda: mock_mgr.data_items[0]['iditem'])

    put_item = mock_mgr.web_items[0].copy()
    del put_item['id']

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}'
        rv = client.put(rte, json=put_item)
        assert rv.status_code == 200
        assert mock_mgr.data_items[0]['iditem'] == rv.json


def test_put_item_error(mock_mgr, monkeypatch):
    data_item = mock_mgr.data_items[0].copy()
    del data_item['iditem']

    with mock_mgr.stub_runner('TestException', 0) as runner:
        runner.add(
            mock_mgr.stubber.stub_update_item_attr_update,
            mock_mgr.table.name, {'iditem': mock_mgr.data_items[0]['iditem']},
            data_item)

    monkeypatch.setattr(storage, 'uuid4', lambda: mock_mgr.data_items[0]['iditem'])

    put_item = mock_mgr.web_items[0].copy()
    del put_item['id']

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}'
        rv = client.put(rte, json=put_item)
        assert rv.status_code == 500


def test_archive_item(mock_mgr, monkeypatch):
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(
            mock_mgr.stubber.stub_update_item_attr_update,
            mock_mgr.table.name, {'iditem': mock_mgr.data_items[0]['iditem']},
            {'archived': True})

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}:archive'
        rv = client.put(rte)
        assert rv.status_code == 200


def test_archive_item_error(mock_mgr, monkeypatch):
    with mock_mgr.stub_runner('TestException', 0) as runner:
        runner.add(
            mock_mgr.stubber.stub_update_item_attr_update,
            mock_mgr.table.name, {'iditem': mock_mgr.data_items[0]['iditem']},
            {'archived': True})

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}:archive'
        rv = client.put(rte)
        assert rv.status_code == 500


def test_report_small(mock_mgr, monkeypatch):
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(mock_mgr.stubber.stub_scan, mock_mgr.table.name, mock_mgr.data_items, filter_expression=ANY)
        runner.add(
            mock_mgr.ses_stubber.stub_send_email, mock_mgr.sender, {'ToAddresses': [mock_mgr.recipient]},
            f"Work items", ANY, ANY, 'test-msg-id')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 200


def test_report_large(mock_mgr, monkeypatch):
    work_items = mock_mgr.data_items * 3
    with mock_mgr.stub_runner(None, None) as runner:
        runner.add(mock_mgr.stubber.stub_scan, mock_mgr.table.name, work_items, filter_expression=ANY)
        runner.add(
            mock_mgr.ses_stubber.stub_send_raw_email, mock_mgr.sender, [mock_mgr.recipient],
            'test-msg-id')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 200


@pytest.mark.parametrize('err, stop_on', [
    ('TESTERROR-stub_scan', 0),
    ('TESTERROR-stub_send_email', 1)
])
def test_report_error(mock_mgr, monkeypatch, err, stop_on):
    with mock_mgr.stub_runner(err, stop_on) as runner:
        runner.add(mock_mgr.stubber.stub_scan, mock_mgr.table.name, mock_mgr.data_items, filter_expression=ANY)
        runner.add(
            mock_mgr.ses_stubber.stub_send_email, mock_mgr.sender, {'ToAddresses': [mock_mgr.recipient]},
            f"Work items", ANY, ANY, 'test-msg-id')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 500
