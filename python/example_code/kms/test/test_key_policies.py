# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for key_policies.py.
"""

import json

import boto3
import pytest
from botocore.exceptions import ClientError

import key_policies


@pytest.mark.parametrize(
    "error_code, stop_on_action",
    [
        (None, None),
        ("TestException", "stub_list_key_policies"),
        ("TestException", "stub_get_key_policy"),
        ("TestException", "stub_put_key_policy"),
    ],
)
def test_key_policies(
    make_stubber, stub_runner, monkeypatch, error_code, stop_on_action
):
    kms_client = boto3.client("kms")
    kms_stubber = make_stubber(kms_client)
    key_id = "test-key-id"
    user = "test-user"
    policy = {"Statement": ["test-statement"]}

    inputs = [key_id, user]
    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            kms_stubber.stub_list_key_policies,
            key_id,
            ["test-policy"] * 5,
        )
        runner.add(
            kms_stubber.stub_get_key_policy,
            key_id,
            json.dumps(policy),
        )
        if stop_on_action != "stub_get_key_policy":
            runner.add(kms_stubber.stub_put_key_policy, key_id)

    exception_raising_functions = [
        "stub_list_key_policies",
        "stub_get_key_policy",
        "stub_put_key_policy",
    ]
    if stop_on_action not in exception_raising_functions:
        key_policies.key_policies(kms_client)
    else:
        with pytest.raises(ClientError):
            key_policies.key_policies(kms_client)
