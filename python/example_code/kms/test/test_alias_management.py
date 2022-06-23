# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for alias_management.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import alias_management


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_create_key'),
    ('TestException', 'stub_create_alias'),
    ('TestException', 'stub_list_aliases'),
    ('TestException', 'stub_update_alias'),
    ('TestException', 'stub_delete_alias'),
    ('TestException', 'stub_schedule_key_deletion'),
])
def test_alias_management(make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    kms_client = boto3.client('kms')
    kms_stubber = make_stubber(kms_client)
    key_id = 'test-key-id'
    key_id_2 = 'test-key-id-2'
    alias = 'test-alias'

    inputs = [
        'y', 'test-alias', 'y', 'y', key_id_2, 'test-alias', 'y'
    ]

    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(kms_stubber.stub_create_key, 'Alias management demo key', key_id)
        runner.add(kms_stubber.stub_create_alias, alias, key_id, raise_and_continue=True)
        if stop_on_action == 'stub_create_alias':
            inputs.insert(2, 'test-alias')
            runner.add(kms_stubber.stub_create_alias, alias, key_id, keep_going=True)
        runner.add(kms_stubber.stub_list_aliases, 10, [alias]*10, truncated=True, keep_going=True)
        runner.add(
            kms_stubber.stub_list_aliases, 10, [alias]*10, marker='test-token', raise_and_continue=True)
        runner.add(kms_stubber.stub_update_alias, alias, key_id_2, raise_and_continue=True)
        runner.add(kms_stubber.stub_delete_alias, alias, raise_and_continue=True)
        runner.add(kms_stubber.stub_schedule_key_deletion, key_id, 7, raise_and_continue=True)

    if stop_on_action != 'stub_create_key':
        alias_management.alias_management(kms_client)
    else:
        with pytest.raises(ClientError):
            alias_management.alias_management(kms_client)
