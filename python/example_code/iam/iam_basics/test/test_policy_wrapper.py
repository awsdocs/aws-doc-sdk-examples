# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for policy_wrapper.py functions.
"""

import datetime
import json

import pytest
from botocore.exceptions import ClientError

import policy_wrapper


@pytest.mark.parametrize("error_code", [None, "MalformedPolicyDocument"])
def test_create_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    policy_name = make_unique_name('policy-')
    policy_description = 'Just a test.'
    actions = ['test:JustTest', 'test:AlsoTest']
    resource_arn = 'arn:aws:test:::test/resource'
    policy_arn = 'arn:aws:iam:::test/policy'

    policy_doc = json.dumps({
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": actions,
                "Resource": resource_arn
            }
        ]
    })

    iam_stubber.stub_create_policy(
        policy_name, policy_arn, policy_doc, description=policy_description,
        error_code=error_code)

    if error_code is None:
        policy = policy_wrapper.create_policy(
            policy_name, policy_description, actions, resource_arn)
        assert policy.arn == policy_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.create_policy(
                policy_name, policy_description, actions, resource_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "DeleteConflict"])
def test_delete_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    policy_arn = 'arn:aws:test:::test-policy'

    iam_stubber.stub_delete_policy(policy_arn, error_code)

    if error_code is None:
        policy_wrapper.delete_policy(policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.delete_policy(policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "MalformedPolicyDocument"])
def test_create_policy_version(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    policy_arn = 'arn:aws:iam:::test/policy'
    actions = ['test:JustTest', 'test:AlsoTest']
    resource_arn = 'arn:aws:test:::test/resource'
    policy_version_id = 'test-policy-version'
    set_as_default = True

    policy_doc = json.dumps({
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": actions,
                "Resource": resource_arn
            }
        ]
    })

    iam_stubber.stub_create_policy_version(
        policy_arn, policy_version_id, policy_doc=policy_doc,
        set_as_default=set_as_default, error_code=error_code)

    if error_code is None:
        policy_version = policy_wrapper.create_policy_version(
            policy_arn, actions, resource_arn, True)
        assert policy_version.version_id == policy_version_id
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.create_policy_version(
                policy_arn, actions, resource_arn, True)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_policies(make_stubber, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    scope = 'Local'
    policies = {
        f'test-policy-{index}': f'arn:aws:iam:::test/policy-{index}'
        for index in range(1, 5)
    }

    iam_stubber.stub_list_policies(scope, policies, error_code=error_code)

    if error_code is None:
        got_policies = policy_wrapper.list_policies(scope)
        assert len(got_policies) == len(policies)
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.list_policies(scope)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_get_default_version_statement(make_stubber, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    policy_name = 'test-policy'
    policy_arn = f'arn:aws:iam:::{policy_name}'
    policy_version_id = 'test-version-id'
    policy_doc = {
        'Version': '2012-10-17',
        'Statement': [
            {
                'Effect': 'Allow',
                'Action': ['test:DoSomething', 'test:DoSomethingElse'],
                'Resource': 'arn:aws:test:::test-resource'
            }
        ]
    }

    iam_stubber.stub_get_policy(
        policy_arn, policy_version_id, error_code=error_code)
    if error_code is None:
        iam_stubber.stub_get_policy_version(
            policy_arn, policy_version_id, json.dumps(policy_doc), error_code=error_code)

    if error_code is None:
        got_statement = policy_wrapper.get_default_policy_statement(policy_arn)
        assert got_statement == policy_doc['Statement']
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.get_default_policy_statement(policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    "version_count,default_index,list_error_code,default_error_code",
    [(4, 3, None, None), (0, None, None, None), (3, 0, None, None),
     (2, None, None, None), (4, 3, "NoSuchEntity", None),
     (4, 3, None, "NoSuchEntity")])
def test_rollback_policy_version(
        make_stubber, version_count, default_index, list_error_code,
        default_error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    policy_arn = 'arn:aws:iam:::test-policy'
    policy_versions = [{
        'document': f'test-doc-{index+1}',
        'id': f'v{index+1}',
        'is_default': index == default_index,
        'create_date': datetime.datetime.now() + datetime.timedelta(days=index)
    } for index in range(version_count)]

    iam_stubber.stub_list_policy_versions(
        policy_arn, policy_versions, error_code=list_error_code)
    if list_error_code is None\
            and version_count > 0\
            and default_index is not None\
            and default_index > 0:
        iam_stubber.stub_set_default_policy_version(
            policy_arn, policy_versions[default_index-1]['id'],
            error_code=default_error_code)
        if default_error_code is None:
            iam_stubber.stub_delete_policy_version(
                policy_arn, policy_versions[default_index]['id'])

    if list_error_code is None and default_error_code is None:
        got_version = policy_wrapper.rollback_policy_version(policy_arn)
        if version_count == 0 or default_index is None \
                or default_index == 0:
            assert got_version is None
        else:
            assert got_version.version_id == policy_versions[default_index-1]['id']
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.rollback_policy_version(policy_arn)
        if list_error_code is not None:
            assert exc_info.value.response['Error']['Code'] == list_error_code
        else:
            assert exc_info.value.response['Error']['Code'] == default_error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_attach_to_role(make_stubber, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    role_name = 'test-role'
    policy_arn = 'arn:aws:iam:::test-policy'

    iam_stubber.stub_attach_role_policy(role_name, policy_arn, error_code)

    if error_code is None:
        policy_wrapper.attach_to_role(role_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.attach_to_role(role_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_detach_from_role(make_stubber, error_code):
    iam_stubber = make_stubber(policy_wrapper.iam.meta.client)
    role_name = 'test-role'
    policy_arn = 'arn:aws:iam:::test-policy'

    iam_stubber.stub_detach_role_policy(role_name, policy_arn, error_code)

    if error_code is None:
        policy_wrapper.detach_from_role(role_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            policy_wrapper.detach_from_role(role_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code
