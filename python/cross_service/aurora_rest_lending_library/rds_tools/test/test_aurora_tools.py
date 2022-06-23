# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for aurora_tools.py functions.
"""

import json
import boto3
from botocore.exceptions import ClientError
import pytest
import aurora_tools


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_db_cluster(make_stubber, error_code):
    rds_client = boto3.client('rds')
    rds_stubber = make_stubber(rds_client)
    cluster_name = 'test-cluster'
    db_name = 'test-db'
    admin_name = 'test-admin'
    admin_password = 'test-password'

    rds_stubber.stub_create_db_cluster(
        cluster_name, db_name, admin_name, admin_password, error_code=error_code)

    if error_code is None:
        got_cluster = aurora_tools.create_db_cluster(
            cluster_name, db_name, admin_name, admin_password, rds_client)
        assert got_cluster['DBClusterIdentifier'] == cluster_name
        assert got_cluster['DatabaseName'] == db_name
    else:
        with pytest.raises(ClientError) as exc_info:
            aurora_tools.create_db_cluster(
                cluster_name, db_name, admin_name, admin_password, rds_client)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_db_cluster(make_stubber, error_code):
    rds_client = boto3.client('rds')
    rds_stubber = make_stubber(rds_client)
    cluster_name = 'test-cluster'

    rds_stubber.stub_delete_db_cluster(cluster_name, error_code=error_code)

    if error_code is None:
        aurora_tools.delete_db_cluster(cluster_name, rds_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            aurora_tools.delete_db_cluster(cluster_name, rds_client)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_aurora_secret(make_stubber, error_code):
    secret_client = boto3.client('secretsmanager')
    secret_stubber = make_stubber(secret_client)
    secret_name = 'test-secret'
    secret_args = {
        'username': 'test-user',
        'password': 'test-password',
        'engine': 'test-engine',
        'host': 'test-host',
        'port': 1111,
        'dbClusterIdentifier': 'test-cluster'}

    secret_stubber.stub_create_secret(
        secret_name, json.dumps(secret_args), error_code=error_code)

    if error_code is None:
        got_secret = aurora_tools.create_aurora_secret(
            secret_name, *secret_args.values(), secret_client)
        assert got_secret['Name'] == secret_name
    else:
        with pytest.raises(ClientError) as exc_info:
            aurora_tools.create_aurora_secret(
                secret_name, *secret_args.values(), secret_client)
            assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_secret(make_stubber, error_code):
    secret_client = boto3.client('secretsmanager')
    secret_stubber = make_stubber(secret_client)
    secret_name = 'test-secret'

    secret_stubber.stub_delete_secret(secret_name, error_code=error_code)

    if error_code is None:
        aurora_tools.delete_secret(secret_name, secret_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            aurora_tools.delete_secret(secret_name, secret_client)
            assert exc_info.value.response['Error']['Code'] == error_code
