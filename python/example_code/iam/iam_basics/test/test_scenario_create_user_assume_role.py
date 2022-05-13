# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_create_user_assume_role.py.
"""

from unittest.mock import MagicMock
import pytest
import boto3
from botocore.exceptions import ClientError

import scenario_create_user_assume_role as assume_role


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_create_user'),
    ('TestException', 'stub_create_access_key'),
    ('TestException', 'stub_get_user'),
    ('TestException', 'stub_create_role'),
    ('TestException', 'stub_create_policy'),
    ('TestException', 'stub_attach_role_policy'),
    ('TestException', 'stub_get_policy'),
    ('TestException', 'stub_get_role'),
    ('TestException', 'stub_put_user_policy'),
])
def test_setup(make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    name_suffix = 'test'
    user_name = f'demo-user-{name_suffix}'
    role_name = f'demo-role-{name_suffix}'
    policy_name = f'demo-policy-{name_suffix}'
    user_policy_name = f'demo-user-policy-{name_suffix}'
    user_arn = f'arn:aws:iam:123456789012::user/{user_name}'
    role_arn = f'arn:aws:iam:123456789012::role/{role_name}'
    policy_arn = f'arn:aws:iam:123456789012::policy/{policy_name}'

    monkeypatch.setattr(assume_role, 'uuid4', lambda: name_suffix)
    monkeypatch.setattr(assume_role, 'progress_bar', lambda x: None)

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(iam_stubber.stub_create_user, user_name)
        runner.add(iam_stubber.stub_create_access_key, user_name)
        runner.add(iam_stubber.stub_get_user, user_name, user_arn)
        runner.add(iam_stubber.stub_create_role, role_name)
        runner.add(iam_stubber.stub_create_policy, policy_name, policy_arn)
        runner.add(iam_stubber.stub_attach_role_policy, role_name, policy_arn)
        runner.add(iam_stubber.stub_get_policy, policy_arn)
        runner.add(iam_stubber.stub_get_role, role_name, role_arn)
        runner.add(iam_stubber.stub_put_user_policy, user_name, user_policy_name)

    if error_code is None:
        user, user_key, role = assume_role.setup(iam_resource)
        assert user.name == user_name
        assert user_key.user_name == user_name
        assert role.name == role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            assume_role.setup(iam_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'AccessDenied', 'TestException'])
def test_show_access_denied_without_role(make_stubber, monkeypatch, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    user_key = MagicMock(id='test-access-key-id', secret='test-secret')

    def get_s3(client, aws_access_key_id, aws_secret_access_key):
        assert aws_access_key_id == user_key.id
        assert aws_secret_access_key == user_key.secret
        return s3_resource
    monkeypatch.setattr(boto3, 'resource', get_s3)

    s3_stubber.stub_list_buckets([], error_code)

    if error_code is None:
        with pytest.raises(RuntimeError):
            assume_role.show_access_denied_without_role(user_key)
    elif error_code == 'AccessDenied':
        assume_role.show_access_denied_without_role(user_key)
    elif error_code == 'TestException':
        with pytest.raises(ClientError):
            assume_role.show_access_denied_without_role(user_key)


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_assume_role'),
    ('TestException', 'stub_list_buckets')
])
def test_list_buckets_from_assumed_role(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    sts_client = boto3.client('sts')
    sts_stubber = make_stubber(sts_client)
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    user_key = MagicMock(id='test-access-key-id', secret='test-secret')
    role_arn = 'arn:aws:iam::123456789012:role/test-role'
    session_name = 'test-session'
    session_token = 'test-session-token'
    buckets = [s3_resource.Bucket('test-bucket-1'), s3_resource.Bucket('test-bucket-2')]

    def get_boto_entity(
            client, aws_access_key_id, aws_secret_access_key, aws_session_token=None):
        assert aws_access_key_id == user_key.id
        assert aws_secret_access_key == user_key.secret
        assert client in ['s3', 'sts']
        if client == 's3':
            assert aws_session_token == session_token
            return s3_resource
        elif client == 'sts':
            assert aws_session_token is None
            return sts_client

    mock_print = MagicMock()

    monkeypatch.setattr(boto3, 'client', get_boto_entity)
    monkeypatch.setattr(boto3, 'resource', get_boto_entity)
    monkeypatch.setattr('builtins.print', mock_print)

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            sts_stubber.stub_assume_role,
            role_arn, session_name, key_id=user_key.id, secret_key=user_key.secret,
            session_token=session_token)
        runner.add(s3_stubber.stub_list_buckets, buckets)

    if error_code is None:
        assume_role.list_buckets_from_assumed_role(user_key, role_arn, session_name)
        for bucket in buckets:
            mock_print.assert_any_call(bucket.name)
    else:
        with pytest.raises(ClientError) as exc_info:
            assume_role.list_buckets_from_assumed_role(user_key, role_arn, session_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_list_attached_role_policies')
])
def test_teardown(make_stubber, stub_runner, error_code, stop_on_action):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)
    role_name = 'test-role'
    role_policy = MagicMock(
        policy_name='test-role-policy',
        arn='arn:aws:iam:123456789012::policy/test-role-policy')
    user_name = 'test-user'
    user_policy = MagicMock(policy_name='test-user-policy')
    user_key_id = 'test-key-id-plus-more-characters'

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            iam_stubber.stub_list_attached_role_policies,
            role_name, {role_policy.policy_name: role_policy.arn})
        runner.add(iam_stubber.stub_get_policy, role_policy.arn)
        runner.add(iam_stubber.stub_detach_role_policy, role_name, role_policy.arn)
        runner.add(iam_stubber.stub_delete_policy, role_policy.arn)
        runner.add(iam_stubber.stub_delete_role, role_name)
        runner.add(
            iam_stubber.stub_list_user_policies, user_name, [user_policy.policy_name])
        runner.add(
            iam_stubber.stub_delete_user_policy, user_name, user_policy.policy_name)
        runner.add(iam_stubber.stub_list_access_keys, user_name, [user_key_id])
        runner.add(iam_stubber.stub_delete_access_key, user_name, user_key_id)
        runner.add(iam_stubber.stub_delete_user, user_name)

    if error_code is None:
        assume_role.teardown(iam.User(user_name), iam.Role(role_name))
    else:
        with pytest.raises(ClientError) as exc_info:
            assume_role.teardown(iam.User(user_name), iam.Role(role_name))
        assert exc_info.value.response['Error']['Code'] == error_code
