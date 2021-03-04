# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for lambda_basics.py functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import organizations_policies


def make_policy(index, pol_type):
    return {
        'id': f'p-123456789{index}',
        'name': f'Test Policy {index}',
        'description': f'Test policy {index} for testing.',
        'content': {
            'tags': {
                'CostCenter': {
                    'tag_key': {'@@assign': 'CostCenter'},
                    'tag_value': {'@@assign': ['AWS2', 'AWS']},
                    'enforced_for': {
                        '@@assign': ['ec2:instance', 'ec2:volume']}}}},
        'type': pol_type
    }


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_policy(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    policy = make_policy(1, 'TAG_POLICY')

    orgs_stubber.stub_create_policy(policy, error_code=error_code)

    if error_code is None:
        got_policy = organizations_policies.create_policy(
            policy['name'], policy['description'], policy['content'], policy['type'],
            orgs_client)
        assert got_policy['PolicySummary']['Id'] == policy['id']
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.create_policy(
                policy['name'], policy['description'], policy['content'],
                policy['type'], orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_policies(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    pol_type = 'TAG_POLICY'
    policies = [make_policy(index, pol_type) for index in range(5)]

    orgs_stubber.stub_list_policies(pol_type, policies, error_code=error_code)

    if error_code is None:
        got_policies = organizations_policies.list_policies(pol_type, orgs_client)
        assert [got['Id'] for got in got_policies] == \
               [had['id'] for had in policies]
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.list_policies(pol_type, orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_policy(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    policy = make_policy(1, 'TAG_POLICY')

    orgs_stubber.stub_describe_policy(policy, error_code=error_code)

    if error_code is None:
        got_policy = organizations_policies.describe_policy(policy['id'], orgs_client)
        assert got_policy['PolicySummary']['Id'] == policy['id']
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.describe_policy(policy['id'], orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code



@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_attach_policy(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    policy_id = 'p-123456789'
    target_id = 'r-987654321'

    orgs_stubber.stub_attach_policy(policy_id, target_id, error_code=error_code)

    if error_code is None:
        organizations_policies.attach_policy(policy_id, target_id, orgs_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.attach_policy(policy_id, target_id, orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detach_policy(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    policy_id = 'p-123456789'
    target_id = 'r-987654321'

    orgs_stubber.stub_detach_policy(policy_id, target_id, error_code=error_code)

    if error_code is None:
        organizations_policies.detach_policy(policy_id, target_id, orgs_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.detach_policy(policy_id, target_id, orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_policy(make_stubber, error_code):
    orgs_client = boto3.client('organizations')
    orgs_stubber = make_stubber(orgs_client)
    policy_id = 'p-123456789'

    orgs_stubber.stub_delete_policy(policy_id, error_code=error_code)

    if error_code is None:
        organizations_policies.delete_policy(policy_id, orgs_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            organizations_policies.delete_policy(policy_id, orgs_client)
        assert exc_info.value.response['Error']['Code'] == error_code
