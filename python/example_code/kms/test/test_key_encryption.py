# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for key_encryption.py.
"""

import boto3
import pytest

import key_encryption


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_encrypt'),
    ('TestException', 'stub_decrypt'),
    ('TestException', 'stub_re_encrypt'),
])
def test_key_encryption(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    kms_client = boto3.client('kms')
    kms_stubber = make_stubber(kms_client)
    key_id = 'test-key-id'
    key_id_2 = 'test-key-id-2'
    plain_text = 'test text'
    cipher_text = b'test cipher text'

    inputs = [key_id, plain_text, 'y', key_id_2]
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            kms_stubber.stub_encrypt, key_id, plain_text.encode(), cipher_text)
        runner.add(
            kms_stubber.stub_decrypt, key_id, cipher_text, plain_text.encode(),
            raise_and_continue=True)
        runner.add(
            kms_stubber.stub_re_encrypt, key_id, key_id_2, cipher_text,
            raise_and_continue=True)

    key_encryption.key_encryption(kms_client)
