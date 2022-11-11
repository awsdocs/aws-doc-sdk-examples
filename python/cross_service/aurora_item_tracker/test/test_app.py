# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the dynamodb_item_tracker example.
"""

import boto3
from botocore.stub import ANY
import pytest

from app import create_app


class MockManager:
    def __init__(self, client, stubber, ses_client, ses_stubber, stub_runner):
        self.client = client
        self.stubber = stubber
        self.ses_client = ses_client
        self.ses_stubber = ses_stubber
        self.stub_runner = stub_runner
        self.cluster_arn = 'arn:aws:rds:us-west-2:1234567890012:cluster:test-cluster'
        self.secret_arn = 'arn:aws:secretsmanager:us-west-2:123456789012:secret:test-secret'
        self.db_name = 'test-db'
        self.table_name = 'test-table'
        self.web_items = [{
            'id': str(index),
            'description': f'desc-{index}',
            'guide': f'guide-{index}',
            'status': f'status-{index}',
            'name': f'user-{index}',
            'archived': index % 2 == 0
        } for index in range(1, 5)]
        self.data_items = [{
            'iditem': index,
            'description': f'desc-{index}',
            'guide': f'guide-{index}',
            'status': f'status-{index}',
            'username': f'user-{index}',
            'archived': index % 2 == 0
        } for index in range(1, 5)]
        self.sender = 'sender@example.com'
        self.recipient = 'recipient@example.com'
        self.app = create_app({
            'TESTING': True,
            'CLUSTER_ARN': self.cluster_arn,
            'SECRET_ARN': self.secret_arn,
            'DATABASE': self.db_name,
            'TABLE_NAME': self.table_name,
            'SENDER_EMAIL': self.sender,
            'RDSDATA_CLIENT': self.client,
            'SES_CLIENT': self.ses_client})

    def setup_stubs(self, err, stop_on, sql, sql_params, report=None, **kwargs):
        err_msg = 'Communications link failure' if err == 'BadRequestException' else ''
        with self.stub_runner(err, stop_on) as runner:
            expander = 1 if (report is None or report == 'small') else 3
            runner.add(
                self.stubber.stub_execute_statement, self.cluster_arn, self.secret_arn, self.db_name, sql, sql_params,
                records=[item.values() for item in self.data_items]*expander, error_message=err_msg, **kwargs)
            if report == 'small':
                runner.add(
                    self.ses_stubber.stub_send_email, self.sender, {'ToAddresses': [self.recipient]},
                    f"Work items", ANY, ANY, 'test-msg-id')
            elif report == 'large':
                runner.add(
                    self.ses_stubber.stub_send_raw_email, self.sender, [self.recipient],
                    'test-msg-id')

    def make_query(self, kind, data):
        sql = None
        sql_params = None
        if kind == 'SELECT':
            if data is not None:
                sql = (f"SELECT iditem, description, guide, status, username, archived "
                       f"FROM {self.table_name} WHERE archived=:archived")
                sql_params = [{'name': 'archived', 'value': {'booleanValue': data == 'true'}}]
            else:
                sql = (f"SELECT iditem, description, guide, status, username, archived "
                       f"FROM {self.table_name} ")
                sql_params = None
        elif kind == 'INSERT':
            sql = (f"INSERT INTO {self.table_name} (description, guide, status, username) "
                   f" VALUES (:description, :guide, :status, :username)")
            sql_params = [
                {'name': 'description', 'value': {'stringValue': data['description']}},
                {'name': 'guide', 'value': {'stringValue': data['guide']}},
                {'name': 'status', 'value': {'stringValue': data['status']}},
                {'name': 'username', 'value': {'stringValue': data['username']}},
            ]
        elif kind == 'UPDATE':
            sql = f"UPDATE {self.table_name} SET archived=:archived WHERE iditem=:iditem"
            sql_params = [
                {'name': 'archived', 'value': {'booleanValue': True}},
                {'name': 'iditem', 'value': {'longValue': data}},
            ]
        return sql, sql_params


@pytest.fixture
def mock_mgr(make_stubber, stub_runner):
    client = boto3.client('rds-data')
    stubber = make_stubber(client)
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    return MockManager(client, stubber, ses_client, ses_stubber, stub_runner)


@pytest.mark.parametrize('archived', ['false', 'true', None])
def test_get_items(mock_mgr, archived):
    sql, sql_params = mock_mgr.make_query('SELECT', archived)

    mock_mgr.setup_stubs(None, None, sql, sql_params)

    with mock_mgr.app.test_client() as client:
        rte = '/api/items' if archived is None else f'/api/items?archived={archived}'
        rv = client.get(rte)
        assert rv.status_code == 200
        assert mock_mgr.web_items == rv.json


@pytest.mark.parametrize('error, stop_on, code, msg', [
    ('BadRequestException', 'stub_execute_statement', 503, 'Data service not ready'),
    ('TESTERROR-TestException', 'stub_execute_statement', 500, 'A storage error occurred')
])
def test_get_items_error(mock_mgr, error, stop_on, code, msg):
    sql, sql_params = mock_mgr.make_query('SELECT', None)

    mock_mgr.setup_stubs(error, stop_on, sql, sql_params)

    with mock_mgr.app.test_client() as client:
        rv = client.get('/api/items')
        assert rv.status_code == code
        assert msg in rv.json


def test_post_item(mock_mgr):
    sql, sql_params = mock_mgr.make_query('INSERT', mock_mgr.data_items[0])
    mock_mgr.setup_stubs(None, None, sql, sql_params, generated_fields=[mock_mgr.data_items[0]['iditem']])

    with mock_mgr.app.test_client() as client:
        rte = '/api/items'
        rv = client.post(rte, json=mock_mgr.web_items[0])
        assert rv.status_code == 200
        assert mock_mgr.data_items[0]['iditem'] == rv.json


@pytest.mark.parametrize('error, stop_on, code, msg', [
    ('BadRequestException', 'stub_execute_statement', 503, 'Data service not ready'),
    ('TESTERROR-TestException', 'stub_execute_statement', 500, 'A storage error occurred')
])
def test_post_item_error(mock_mgr, error, stop_on, code, msg):
    sql, sql_params = mock_mgr.make_query('INSERT', mock_mgr.data_items[0])
    mock_mgr.setup_stubs(error, stop_on, sql, sql_params, generated_fields=[mock_mgr.data_items[0]['iditem']])

    with mock_mgr.app.test_client() as client:
        rte = '/api/items'
        rv = client.post(rte, json=mock_mgr.web_items[0])
        assert rv.status_code == code
        assert msg in rv.json


def test_archive_item(mock_mgr):
    sql, sql_params = mock_mgr.make_query('UPDATE', mock_mgr.data_items[0]['iditem'])
    mock_mgr.setup_stubs(None, None, sql, sql_params)

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}:archive'
        rv = client.put(rte)
        assert rv.status_code == 200


@pytest.mark.parametrize('error, stop_on, code, msg', [
    ('BadRequestException', 'stub_execute_statement', 503, 'Data service not ready'),
    ('TESTERROR-TestException', 'stub_execute_statement', 500, 'A storage error occurred')
])
def test_archive_item_error(mock_mgr, error, stop_on, code, msg):
    sql, sql_params = mock_mgr.make_query('UPDATE', mock_mgr.data_items[0]['iditem'])
    mock_mgr.setup_stubs(error, stop_on, sql, sql_params)

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}:archive'
        rv = client.put(rte)
        assert rv.status_code == code
        assert msg in rv.json


def test_archive_item_bad_action(mock_mgr):
    with mock_mgr.app.test_client() as client:
        rte = f'/api/items/{mock_mgr.web_items[0]["id"]}:garbage'
        rv = client.put(rte)
        assert rv.status_code == 400
        assert "Unrecognized action" in rv.json


def test_report_small(mock_mgr):
    sql, sql_params = mock_mgr.make_query('SELECT', False)
    mock_mgr.setup_stubs(None, None, sql, sql_params, report='small')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 200


def test_report_large(mock_mgr):
    sql, sql_params = mock_mgr.make_query('SELECT', False)
    mock_mgr.setup_stubs(None, None, sql, sql_params, report='large')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 200


@pytest.mark.parametrize('err, stop_on, msg', [
    ('TESTERROR-stub_execute_statement', 0, 'A storage error occurred'),
    ('TESTERROR-stub_send_email', 1, 'An email error occurred')
])
def test_report_error(mock_mgr, err, stop_on, msg):
    sql, sql_params = mock_mgr.make_query('SELECT', False)
    mock_mgr.setup_stubs(err, stop_on, sql, sql_params, report='small')

    with mock_mgr.app.test_client() as client:
        rte = f'/api/items:report'
        rv = client.post(rte, json={'email': mock_mgr.recipient})
        assert rv.status_code == 500
        assert msg in rv.json
