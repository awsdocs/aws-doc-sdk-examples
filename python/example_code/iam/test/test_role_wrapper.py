# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for role_wrapper.py functions.
"""

import json
import pytest
from botocore.exceptions import ClientError


import role_wrapper


@pytest.mark.parametrize("error_code", [None, "MalformedPolicyDocument"])
def test_create_role(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = make_unique_name('role-')
    allowed_services = ['lambda.amazonaws.com', 'batchoperations.s3.amazonaws.com']
    trust_policy = {
        'Version': '2012-10-17',
        'Statement': [{
                'Effect': 'Allow',
                'Principal': {'Service': service},
                'Action': 'sts:AssumeRole'
            } for service in allowed_services
        ]
    }

    iam_stubber.stub_create_role(
        role_name, json.dumps(trust_policy), error_code=error_code)

    if error_code is None:
        role = role_wrapper.create_role(role_name, allowed_services)
        assert role.name == role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.create_role(role_name, allowed_services)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "DeleteConflict"])
def test_delete_role(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = make_unique_name('role-')

    iam_stubber.stub_delete_role(role_name, error_code=error_code)

    if error_code is None:
        role_wrapper.delete_role(role_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.delete_role(role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "PolicyNotAttachable"])
def test_attach_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = make_unique_name('role-')
    policy_arn = 'arn:aws:test:::test-policy-arn'

    iam_stubber.stub_attach_role_policy(role_name, policy_arn, error_code=error_code)

    if error_code is None:
        role_wrapper.attach_policy(role_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.attach_policy(role_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_policies(make_stubber, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = 'test-role'
    policies = [f'policy-{ind}' for ind in range(3)]

    iam_stubber.stub_list_role_policies(role_name, policies, error_code=error_code)

    if error_code is None:
        role_wrapper.list_policies(role_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.list_policies(role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_attached_policies(make_stubber, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = 'test-role'
    policies = {
        f'policy-{ind}': f'arn:aws:iam::111122223333:policy/test-policy-{ind}'
        for ind in range(3)}

    iam_stubber.stub_list_attached_role_policies(role_name, policies, error_code=error_code)
    
    if error_code is None:
        role_wrapper.list_attached_policies(role_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.list_attached_policies(role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "UnmodifiableEntity"])
def test_detach_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(role_wrapper.iam.meta.client)
    role_name = make_unique_name('role-')
    policy_arn = 'arn:aws:test:::test-policy-arn'

    iam_stubber.stub_detach_role_policy(role_name, policy_arn, error_code=error_code)

    if error_code is None:
        role_wrapper.detach_policy(role_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            role_wrapper.detach_policy(role_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code
