# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for key_management.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import key_management


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_create_key'),
    ('TestException', 'stub_list_keys'),
    ('TestException', 'stub_describe_key'),
    ('TestException', 'stub_disable_key'),
    ('TestException', 'stub_enable_key'),
    ('TestException', 'stub_generate_data_key'),
    ('TestException', 'stub_schedule_key_deletion'),
])
def test_key_management(make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    kms_client = boto3.client('kms')
    kms_stubber = make_stubber(kms_client)
    key_id = 'test-key-id'
    description = 'test-description'

    inputs = [
        description, 'n', 'y', key_id, 'y', 'y', 'y'
    ]

    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(kms_stubber.stub_create_key, description, key_id)
        runner.add(kms_stubber.stub_list_keys, 10, [key_id]*10, truncated=True, keep_going=True)
        runner.add(
            kms_stubber.stub_list_keys, 10, [key_id]*10, marker='test-token',
            raise_and_continue=True)
        runner.add(kms_stubber.stub_describe_key, key_id, 'CREATED', raise_and_continue=True)
        runner.add(kms_stubber.stub_disable_key, key_id, raise_and_continue=True)
        if stop_on_action != 'stub_disable_key':
            runner.add(kms_stubber.stub_describe_key, key_id, 'DISABLED', keep_going=True)
        runner.add(kms_stubber.stub_enable_key, key_id, raise_and_continue=True)
        if stop_on_action != 'stub_enable_key':
            runner.add(kms_stubber.stub_describe_key, key_id, 'ENABLED', keep_going=True)
        runner.add(kms_stubber.stub_generate_data_key, key_id, 'AES_256', raise_and_continue=True)
        runner.add(kms_stubber.stub_schedule_key_deletion, key_id, 7, raise_and_continue=True)

    if stop_on_action != 'stub_create_key':
        key_management.key_management(kms_client)
    else:
        with pytest.raises(ClientError):
            key_management.key_management(kms_client)



