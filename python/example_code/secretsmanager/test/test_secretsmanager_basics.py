# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for secretsmanager_basics.py
"""

import uuid
import boto3
from botocore.exceptions import ClientError
import pytest

from secretsmanager_basics import SecretsManagerSecret


@pytest.mark.parametrize('secret_value,error_code', [
    ('test-secret', None),
    (b'test-secret', None),
    ('test-secret', 'TestException')])
def test_create(make_stubber, secret_value, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    name = 'test-name'

    secretsmanager_stubber.stub_create_secret(
        name, secret_value, error_code=error_code)

    if error_code is None:
        secret.create(name, secret_value)
        assert secret.name == name
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.create(name, secret_value)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('name,error_code', [
    ('test-name', None),
    (None, None),
    ('test-name', 'TestException')])
def test_describe(make_stubber, name, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    existing_name = 'secret-name'
    if name is None:
        secret.name = existing_name

    secretsmanager_stubber.stub_describe_secret(
        name if name is not None else existing_name, error_code=error_code)

    if error_code is None:
        secret.describe(name)
        if name is None:
            assert secret.name == existing_name
        else:
            assert secret.name == name
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.describe(name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('stage,secret_value,error_code', [
    ('test-stage', 'test-value', None),
    ('test-stage', b'test-value', None),
    (None, 'test-value', None),
    ('test-stage', 'test-value', 'TestException')])
def test_get_value(make_stubber, stage, secret_value, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    secret.name = 'test-name'

    secretsmanager_stubber.stub_get_secret_value(
        secret.name, stage, secret_value, error_code=error_code)

    if error_code is None:
        got_response = secret.get_value(stage)
        if isinstance(secret_value, str):
            assert got_response['SecretString'] == secret_value
        elif isinstance(secret_value, bytes):
            assert got_response['SecretBinary'] == secret_value
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.get_value(stage)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_random_password(make_stubber, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    pw_length = 20
    password = 'test-password'

    secretsmanager_stubber.stub_get_random_password(
        pw_length, password, error_code=error_code)

    if error_code is None:
        got_password = secret.get_random_password(pw_length)
        assert got_password == password
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.get_random_password(pw_length)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('secret_value,stages,error_code', [
    ('test-value', ['test-stage'], None),
    (b'test-value', None, None),
    ('test-value', ['test-stage'], 'TestException')])
def test_put_value(make_stubber, secret_value, stages, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    secret.name = 'test-name'

    secretsmanager_stubber.stub_put_secret_value(
        secret.name, secret_value, stages, error_code=error_code)

    if error_code is None:
        secret.put_value(secret_value, stages)
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.put_value(secret_value, stages)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_version_stage(make_stubber, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    secret.name = 'test-name'
    stage = 'test-stage'
    remove_from = str(uuid.uuid4())
    move_to = str(uuid.uuid4())

    secretsmanager_stubber.stub_update_secret_version_stage(
        secret.name, stage, remove_from, move_to, error_code=error_code)

    if error_code is None:
        secret.update_version_stage(stage, remove_from, move_to)
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.update_version_stage(stage, remove_from, move_to)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    secret.name = 'test-name'

    secretsmanager_stubber.stub_delete_secret(secret.name, error_code=error_code)

    if error_code is None:
        secret.delete(True)
    else:
        with pytest.raises(ClientError) as exc_info:
            secret.delete(True)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list(make_stubber, error_code):
    secretsmanager_client = boto3.client('secretsmanager')
    secretsmanager_stubber = make_stubber(secretsmanager_client)
    secret = SecretsManagerSecret(secretsmanager_client)
    secrets = [{'Name': f'test-name-{secid}'} for secid in range(5)]

    secretsmanager_stubber.stub_list_secrets(secrets, error_code=error_code)

    if error_code is None:
        assert secrets == list(secret.list(10))
    else:
        with pytest.raises(ClientError) as exc_info:
            list(secret.list(10))
        assert exc_info.value.response['Error']['Code'] == error_code
