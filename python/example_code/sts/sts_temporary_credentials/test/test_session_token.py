# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for AWS Security Token Service (STS) usage functions.
"""

import json
import unittest.mock
import webbrowser
import pytest
import requests
import boto3
from botocore.exceptions import ClientError

import session_token


def test_setup(make_stubber, monkeypatch, unique_names):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)
    mock_mfa = unittest.mock.MagicMock(
        device_name=unique_names['mfa'],
        serial_number='test-serial-number',
        qr_code_png=b'png')
    mock_code = '123456'

    monkeypatch.setattr(session_token, 'unique_name', lambda x: unique_names[x])
    monkeypatch.setattr(session_token, 'progress_bar', lambda x: None)
    monkeypatch.setattr(webbrowser, 'open', lambda x: None)
    monkeypatch.setattr('builtins.input', lambda x: mock_code)

    iam_stubber.stub_create_user(unique_names['user'])
    iam_stubber.stub_create_virtual_mfa_device(mock_mfa)
    iam_stubber.stub_enable_mfa_device(
        unique_names['user'], mock_mfa.serial_number, mock_code, mock_code)
    iam_stubber.stub_create_access_key(unique_names['user'])
    iam_stubber.stub_put_user_policy(unique_names['user'], unique_names['user-policy'])

    user, user_key, v_mfa = session_token.setup(iam)
    assert user is not None
    assert user_key is not None
    assert v_mfa is not None


def test_list_buckets_from_assumed_role(make_stubber, monkeypatch):
    sts = boto3.client('sts')
    sts_stubber = make_stubber(sts)
    s3 = boto3.resource('s3')
    s3_stubber = make_stubber(s3.meta.client)
    mfa_serial_number = 'arn:aws:iam::123456789012:mfa/test-mfa'
    mfa_totp = '123456'
    buckets = [unittest.mock.MagicMock(), unittest.mock.MagicMock()]
    for b in buckets:
        b.name = 'test-bucket'

    monkeypatch.setattr(
        boto3, 'resource',
        lambda x, aws_access_key_id, aws_secret_access_key, aws_session_token: s3)

    sts_stubber.stub_get_session_token(mfa_serial_number, mfa_totp)
    s3_stubber.stub_list_buckets(buckets)

    session_token.list_buckets_with_session_token_with_mfa(
        mfa_serial_number, mfa_totp, sts)


def test_teardown(make_stubber):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)
    user_name = 'test-user'
    user_policies = [unittest.mock.MagicMock(policy_name='test-user-policy')]
    user_key_ids = ['test-key-id-plus-more-characters']
    mfa_serials = ['test-serial']

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

    session_token.teardown(
        iam.User(user_name), iam.VirtualMfaDevice(mfa_serials[0]))
