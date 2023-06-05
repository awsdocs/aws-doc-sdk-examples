# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for user_wrapper.py functions.
"""

import pytest

from botocore.exceptions import ClientError

import user_wrapper


@pytest.mark.parametrize("error_code", [None, "EntityAlreadyExists"])
def test_create_user(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')

    iam_stubber.stub_create_user(user_name, error_code=error_code)

    if error_code is None:
        user = user_wrapper.create_user(user_name)
        assert user.name == user_name
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.create_user(user_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "DeleteConflict"])
def test_delete_user(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')

    iam_stubber.stub_delete_user(user_name, error_code=error_code)

    if error_code is None:
        user_wrapper.delete_user(user_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.delete_user(user_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    "user_count,error_code", [(5, None), (0, None), (3, "TestException")])
def test_list_users(make_stubber, user_count, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    user_count = 5

    iam_stubber.stub_list_users(user_count, error_code=error_code)

    if error_code is None:
        got_users = user_wrapper.list_users()
        assert len(got_users) == user_count
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.list_users()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    "error_code", [None, "EntityTemporarilyUnmodifiable"])
def test_update_user(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    old_name = make_unique_name('user-')
    new_name = make_unique_name('user-')

    iam_stubber.stub_update_user(old_name, new_name, error_code=error_code)

    if error_code is None:
        got_update = user_wrapper.update_user(old_name, new_name)
        assert got_update.name == new_name
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.update_user(old_name, new_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "PolicyNotAttachable"])
def test_attach_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')
    policy_arn = 'arn:aws:iam:::test/policy'

    iam_stubber.stub_attach_user_policy(user_name, policy_arn, error_code=error_code)

    if error_code is None:
        user_wrapper.attach_policy(user_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.attach_policy(user_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_detach_policy(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(user_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')
    policy_arn = 'arn:aws:iam:::test/policy'

    iam_stubber.stub_detach_user_policy(user_name, policy_arn, error_code=error_code)

    if error_code is None:
        user_wrapper.detach_policy(user_name, policy_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            user_wrapper.detach_policy(user_name, policy_arn)
        assert exc_info.value.response['Error']['Code'] == error_code

