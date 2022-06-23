# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for grant_management.py.
"""

import boto3
import pytest

import grant_management


@pytest.mark.parametrize('error_code, stop_on_action, delete_choice', [
    (None, None, 'retire'),
    ('TestException', 'stub_create_grant', None),
    ('TestException', 'stub_list_grants', 'revoke'),
    ('TestException', 'stub_retire_grant', 'retire'),
    ('TestException', 'stub_revoke_grant', 'revoke'),
])
def test_grant_management(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_action, delete_choice):
    kms_client = boto3.client('kms')
    kms_stubber = make_stubber(kms_client)
    key_id = 'test-key-id'
    user = 'test-user'
    grant = {'GrantToken': 'test-grant-token', 'GrantId': 'test-grant-id'}

    inputs = [
        key_id, user, 'y', delete_choice
    ]

    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            kms_stubber.stub_create_grant, key_id, user, ['GenerateDataKey'], grant,
            raise_and_continue=True)
        runner.add(
            kms_stubber.stub_list_grants, key_id, ['test-grant']*5, raise_and_continue=True)
        if delete_choice == 'retire':
            runner.add(kms_stubber.stub_retire_grant, 'test-grant-token', raise_and_continue=True)
        elif delete_choice == 'revoke':
            runner.add(
                kms_stubber.stub_revoke_grant, key_id, 'test-grant-id', raise_and_continue=True)

    grant_management.grant_management(kms_client)
