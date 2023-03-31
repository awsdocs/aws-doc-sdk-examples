# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for user_wrapper.py functions.
"""

import time
import pytest

from botocore.exceptions import ClientError
from botocore.stub import ANY

import service_linked_roles


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_service_linked_role'),
    ('TestException', 'stub_list_attached_role_policies'),
    ('TestException', 'stub_get_policy'),
    ('TestException', 'stub_get_policy_version'),
    ('TestException', 'stub_delete_service_linked_role'),
])
def test_create_service_linked_role(
        make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    iam_stubber = make_stubber(service_linked_roles.iam.meta.client)

    service_name = 'test-service-name'
    role_name = 'test-role'
    policy = {'test-policy': 'arn:aws:iam::111122223333:policy/test-policy'}
    policy_ver_id = 'test-version'
    task_id = 'test-task'

    inputs = [service_name, 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))
    monkeypatch.setattr(time, 'sleep', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            iam_stubber.stub_create_service_linked_role, service_name, ANY, role_name)
        runner.add(iam_stubber.stub_list_attached_role_policies, role_name, policy)
        runner.add(iam_stubber.stub_get_policy, policy['test-policy'], policy_ver_id)
        runner.add(
            iam_stubber.stub_get_policy_version, policy['test-policy'], policy_ver_id,
            "test-document")
        runner.add(iam_stubber.stub_delete_service_linked_role, role_name, task_id)
        runner.add(
            iam_stubber.stub_get_service_linked_role_deletion_status, task_id, 'IN_PROGRESS')
        runner.add(
            iam_stubber.stub_get_service_linked_role_deletion_status, task_id, 'SUCCEEDED')

    if error_code is None:
        service_linked_roles.usage_demo()
    else:
        with pytest.raises(ClientError) as exc_info:
            service_linked_roles.usage_demo()
        assert exc_info.value.response['Error']['Code'] == error_code
