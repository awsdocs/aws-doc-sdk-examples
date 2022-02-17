# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for access_key_wrapper.py functions.
"""

import pytest
from botocore.exceptions import ClientError

import access_key_wrapper


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_create_key(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(access_key_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')

    iam_stubber.stub_create_access_key(user_name, error_code=error_code)

    if error_code is None:
        key = access_key_wrapper.create_key(user_name)
        assert key.user_name == user_name
        assert key.id is not None
        assert key.secret is not None
    else:
        with pytest.raises(ClientError) as exc_info:
            access_key_wrapper.create_key(user_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_delete_key(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(access_key_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')
    key_id = 'test-key-id-plus-extra-words'

    iam_stubber.stub_delete_access_key(user_name, key_id, error_code=error_code)

    if error_code is None:
        access_key_wrapper.delete_key(user_name, key_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            access_key_wrapper.delete_key(user_name, key_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_get_last_use(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(access_key_wrapper.iam.meta.client)
    key_id = 'test-key-id-plus-extra-words'
    user_name = make_unique_name('user-')

    iam_stubber.stub_get_access_key_last_used(key_id, user_name, error_code=error_code)

    if error_code is None:
        response = access_key_wrapper.get_last_use(key_id)
        assert response['UserName'] == user_name
    else:
        with pytest.raises(ClientError) as exc_info:
            access_key_wrapper.get_last_use(key_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_list_keys(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(access_key_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')
    key_ids = [f'test-key-id-plus-extra-words-{index}' for index in range(1, 5)]

    iam_stubber.stub_list_access_keys(user_name, key_ids, error_code=error_code)

    if error_code is None:
        got_keys = access_key_wrapper.list_keys(user_name)
        assert [key.id for key in got_keys] == key_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            access_key_wrapper.list_keys(user_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize(
    "activate,error_code", [(True, None), (False, None), (True, "NoSuchEntity")])
def test_update_key(make_stubber, make_unique_name, activate, error_code):
    iam_stubber = make_stubber(access_key_wrapper.iam.meta.client)
    user_name = make_unique_name('user-')
    key_id = 'test-key-id-plus-extra-words'

    iam_stubber.stub_update_access_key(
        user_name, key_id, activate, error_code=error_code)

    if error_code is None:
        access_key_wrapper.update_key(user_name, key_id, activate)
    else:
        with pytest.raises(ClientError) as exc_info:
            access_key_wrapper.update_key(user_name, key_id, activate)
        assert exc_info.value.response['Error']['Code'] == error_code
