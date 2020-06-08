# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for AWS Security Token Service (STS) usage functions.
"""

import unittest.mock
import webbrowser
import pytest
import boto3
from botocore.exceptions import ClientError

import assume_role_mfa


def test_setup(make_stubber, monkeypatch, unique_names):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    user_arn = f'arn:aws:iam:123456789012::user/{unique_names["user"]}'
    role_arn = f'arn:aws:iam:123456789012::role/{unique_names["role"]}'
    policy_arn = f'arn:aws:iam:123456789012::policy/{unique_names["policy"]}'
    mock_mfa = unittest.mock.MagicMock(
        device_name=unique_names['mfa'],
        serial_number='test-serial-number',
        qr_code_png=b'png')
    mock_code = '123456'

    monkeypatch.setattr(assume_role_mfa, 'unique_name', lambda x: unique_names[x])
    monkeypatch.setattr(assume_role_mfa, 'progress_bar', lambda x: None)
    monkeypatch.setattr(webbrowser, 'open', lambda x: None)
    monkeypatch.setattr('builtins.input', lambda x: mock_code)

    iam_stubber.stub_create_user(unique_names['user'])
    iam_stubber.stub_create_virtual_mfa_device(mock_mfa)
    iam_stubber.stub_enable_mfa_device(
        unique_names['user'], mock_mfa.serial_number, mock_code, mock_code)
    iam_stubber.stub_create_access_key(unique_names['user'])
    iam_stubber.stub_get_user(unique_names['user'], user_arn)
    iam_stubber.stub_create_role(unique_names['role'])
    iam_stubber.stub_create_policy(unique_names['policy'], policy_arn)
    iam_stubber.stub_attach_role_policy(unique_names['role'], policy_arn)
    iam_stubber.stub_get_policy(policy_arn)
    iam_stubber.stub_get_role(unique_names['role'], role_arn)
    iam_stubber.stub_put_user_policy(unique_names['user'], unique_names['user-policy'])

    user, user_key, v_mfa, role = assume_role_mfa.setup(iam_resource)
    assert user is not None
    assert user_key is not None
    assert v_mfa is not None
    assert role is not None


@pytest.mark.parametrize('error_code', [None, 'AccessDenied', 'TestException'])
def test_try_to_assume_role_without_mfa(make_stubber, error_code):
    sts = boto3.client('sts')
    sts_stubber = make_stubber(sts)
    role_arn = 'arn:aws:iam:123456789012::role/test-role'
    session_name = 'test-session'

    sts_stubber.stub_assume_role(role_arn, session_name, error_code=error_code)

    if error_code is None:
        with pytest.raises(RuntimeError):
            assume_role_mfa.try_to_assume_role_without_mfa(role_arn, session_name, sts)
    elif error_code == 'AccessDenied':
        assume_role_mfa.try_to_assume_role_without_mfa(role_arn, session_name, sts)
    elif error_code == 'TestException':
        with pytest.raises(ClientError):
            assume_role_mfa.try_to_assume_role_without_mfa(role_arn, session_name, sts)


def test_list_buckets_from_assumed_role_with_mfa(make_stubber, monkeypatch):
    sts = boto3.client('sts')
    sts_stubber = make_stubber(sts)
    s3 = boto3.resource('s3')
    s3_stubber = make_stubber(s3.meta.client)
    role_arn = 'arn:aws:iam::123456789012:role/test-role'
    session_name = 'test-session'
    mfa_serial_number = 'arn:aws:iam::123456789012:mfa/test-mfa'
    mfa_totp = '123456'
    buckets = [unittest.mock.MagicMock(), unittest.mock.MagicMock()]
    for b in buckets:
        b.name = 'test-bucket'

    monkeypatch.setattr(
        boto3, 'resource',
        lambda x, aws_access_key_id, aws_secret_access_key, aws_session_token: s3)

    sts_stubber.stub_assume_role(
        role_arn, session_name, mfa_serial_number=mfa_serial_number, mfa_totp=mfa_totp)
    s3_stubber.stub_list_buckets(buckets)

    assume_role_mfa.list_buckets_from_assumed_role_with_mfa(
        role_arn, session_name, mfa_serial_number, mfa_totp, sts)


def test_teardown(make_stubber):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)
    role_name = 'test-role'
    role_policies = [unittest.mock.MagicMock(
        policy_name='test-role-policy',
        arn='arn:aws:iam:123456789012::policy/test-role-policy')]
    user_name = 'test-user'
    user_policies = [unittest.mock.MagicMock(policy_name='test-user-policy')]
    user_key_ids = ['test-key-id-plus-more-characters']
    mfa_serials = ['test-serial']

    iam_stubber.stub_list_attached_role_policies(
        unittest.mock.ANY, {pol.policy_name: pol.arn for pol in role_policies})
    for pol in role_policies:
        iam_stubber.stub_get_policy(pol.arn)
        iam_stubber.stub_detach_role_policy(role_name, pol.arn)
        iam_stubber.stub_delete_policy(pol.arn)
    iam_stubber.stub_delete_role(role_name)
    iam_stubber.stub_list_user_policies(
        user_name, [pol.policy_name for pol in user_policies])
    for pol in user_policies:
        iam_stubber.stub_delete_user_policy(user_name, pol.policy_name)
    iam_stubber.stub_list_access_keys(user_name, user_key_ids)
    for key_id in user_key_ids:
        iam_stubber.stub_delete_access_key(user_name, key_id)
    iam_stubber.stub_list_mfa_devices(user_name, mfa_serials)
    for mfa_serial in mfa_serials:
        iam_stubber.stub_deactivate_mfa_device(user_name, mfa_serial)
        iam_stubber.stub_delete_virtual_mfa_device(mfa_serial)
    iam_stubber.stub_delete_user(user_name)

    assume_role_mfa.teardown(
        iam.User(user_name), iam.VirtualMfaDevice(mfa_serials[0]), iam.Role(role_name))
