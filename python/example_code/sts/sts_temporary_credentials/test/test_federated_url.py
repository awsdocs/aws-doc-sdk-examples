# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for AWS Security Token Service (STS) usage functions.
"""

import json
import unittest.mock
import pytest
import requests
import boto3
from botocore.exceptions import ClientError

import federated_url


def test_setup(make_stubber, monkeypatch, unique_names):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)

    monkeypatch.setattr(federated_url, 'unique_name', lambda x: unique_names[x])
    monkeypatch.setattr(federated_url, 'progress_bar', lambda x: None)

    iam_stubber.stub_get_user(None, 'arn:aws:iam:123456789012::user/test-user')
    iam_stubber.stub_create_role(unique_names['role'])
    iam_stubber.stub_attach_role_policy(unique_names['role'], unittest.mock.ANY)

    role = federated_url.setup(iam)
    assert role is not None


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_construct_federated_url(make_stubber, monkeypatch, error_code):
    sts = boto3.client('sts')
    sts_stubber = make_stubber(sts)
    role_arn = 'arn:aws:iam::123456789012:role/test-role'
    session_name = 'test-session'
    signin_token = 'test-signin-token'

    sts_stubber.stub_assume_role(role_arn, session_name, error_code=error_code)
    monkeypatch.setattr(
        requests, 'get',
        lambda url, params: unittest.mock.MagicMock(
            text=json.dumps({'SigninToken': signin_token})))

    if error_code is None:
        fed_url = federated_url.construct_federated_url(
            role_arn, session_name, 'example.org', sts)
        assert fed_url.split('=')[-1] == signin_token
    else:
        with pytest.raises(ClientError) as exc_info:
            federated_url.construct_federated_url(
                role_arn, session_name, 'example.org', sts)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_teardown(make_stubber):
    iam = boto3.resource('iam')
    iam_stubber = make_stubber(iam.meta.client)
    role_name = 'test-role'
    policies = {'test-policy': 'arn:aws:iam:123456789012::policy/test-policy'}

    iam_stubber.stub_list_attached_role_policies(role_name, policies)
    for arn in policies.values():
        iam_stubber.stub_detach_role_policy(role_name, arn)
        iam_stubber.stub_get_policy(arn)
    iam_stubber.stub_delete_role(role_name)

    federated_url.teardown(iam.Role(role_name))
