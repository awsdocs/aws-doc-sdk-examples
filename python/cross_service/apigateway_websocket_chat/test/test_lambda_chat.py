# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for lambda_chat.py.
"""

import json
import boto3
import pytest

import lambda_chat


@pytest.mark.parametrize('error_code,status_code', [
    (None, 200),
    ('TestException', 503)])
def test_handle_connect(make_stubber, error_code, status_code):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    table = dynamodb_resource.Table('test-table')
    user_name = 'test-user_name'
    connection_id = 'test-connection_id'

    dynamodb_stubber.stub_put_item(
        table.name, {'connection_id': connection_id, 'user_name': user_name},
        error_code=error_code)

    got_status_code = lambda_chat.handle_connect(user_name, table, connection_id)
    assert got_status_code == status_code


@pytest.mark.parametrize('error_code,status_code', [
    (None, 200),
    ('TestException', 503)])
def test_handle_disconnect(make_stubber, error_code, status_code):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    table = dynamodb_resource.Table('test-table')
    connection_id = 'test-connection_id'

    dynamodb_stubber.stub_delete_item(
        table.name, {'connection_id': connection_id}, error_code=error_code)

    got_status_code = lambda_chat.handle_disconnect(table, connection_id)
    assert got_status_code == status_code


@pytest.mark.parametrize('error_code,error_method,status_code', [
    (None, None, 200),
    ('TestException', 'stub_get_item', 200),
    ('TestException', 'stub_scan', 404),
    ('TestException', 'stub_post_to_connection', 200)])
def test_handle_message(
        make_stubber, monkeypatch, error_code, error_method, status_code):
    dynamodb_resource = boto3.resource('dynamodb')
    dynamodb_stubber = make_stubber(dynamodb_resource.meta.client)
    apig_management_client = boto3.client('apigatewaymanagementapi')
    apig_management_stubber = make_stubber(apig_management_client)
    table = dynamodb_resource.Table('test-table')
    connection_id = 'test-connection_id'
    user_name = 'test-user'
    other_connection_id = 'other_conn'
    msg = 'test-msg'

    dynamodb_stubber.stub_get_item(
        table.name,
        {'connection_id': connection_id},
        {'connection_id': connection_id, 'user_name': user_name},
        error_code=error_code if error_method == 'stub_get_item' else None)
    if error_method == 'stub_get_item':
        user_name = 'guest'
    dynamodb_stubber.stub_scan(
        table.name,
        [{'connection_id': connection_id}, {'connection_id': other_connection_id}],
        projection_expression='connection_id',
        error_code=error_code if error_method == 'stub_scan' else None)
    if error_method != 'stub_scan':
        apig_management_stubber.stub_post_to_connection(
            f'{user_name}: {msg}'.encode('utf-8'), other_connection_id,
            error_code=error_code if error_method == 'stub_post_to_connection'
            else None)

    got_status_code = lambda_chat.handle_message(
        table, connection_id, {'msg': msg}, apig_management_client)
    assert got_status_code == status_code


@pytest.mark.parametrize(
    'table_name,route,connection_id,user_name,msg_body,domain,stage,status_code', [
        ('test-table', '$connect', 'test-conn', 'Tester', '{"msg": "Yo"}',
         'test-domain', 'test-stage', 200),
        ('test-table', '$connect', 'conn', None, None, None, None, 200),
        ('test-table', '$disconnect', 'test-conn', 'Tester', '{"msg": "Yo"}',
         'test-domain', 'test-stage', 200),
        ('test-table', '$disconnect', 'conn', None, None, None, None, 200),
        ('test-table', 'sendmessage', 'test-conn', 'Tester', '{"msg": "Yo"}',
         'test-domain', 'test-stage', 200),
        ('test-table', 'sendmessage', 'test-conn', None, None, 'dom', 'stg', 200),
        ('test-table', 'sendmessage', 'test-conn', None, None, None, None, 400),
        ('test-table', 'garbage', 'test-conn', None, None, None, None, 404),
        (None, None, None, None, None, None, None, 400),
    ])
def test_lambda_handler(
        monkeypatch, table_name, route, connection_id, user_name, msg_body, domain,
        stage, status_code):
    def verify_handle_connect(uname, tbl, conn):
        assert uname == user_name
        assert tbl.name == table_name
        assert conn == connection_id
        return status_code

    def verify_handle_disconnect(tbl, conn):
        assert tbl.name == table_name
        assert conn == connection_id
        return status_code

    def verify_handle_message(tbl, conn, body, apig):
        assert tbl.name == table_name
        assert conn == connection_id
        assert body == json.loads(msg_body if msg_body is not None else '{"msg": ""}')
        assert apig.meta.endpoint_url == f'https://{domain}/{stage}'
        return status_code

    monkeypatch.setenv('table_name', 'test-table')
    monkeypatch.setattr(lambda_chat, 'handle_connect', verify_handle_connect)
    monkeypatch.setattr(lambda_chat, 'handle_disconnect', verify_handle_disconnect)
    monkeypatch.setattr(lambda_chat, 'handle_message', verify_handle_message)

    event = {
        'requestContext': {
            'routeKey': route,
            'connectionId': connection_id,
            'domainName': domain,
            'stage': stage
        },
        'queryStringParameters': {'name': user_name},
        'body': msg_body
    }

    got_response = lambda_chat.lambda_handler(event, None)
    assert got_response['statusCode'] == status_code
