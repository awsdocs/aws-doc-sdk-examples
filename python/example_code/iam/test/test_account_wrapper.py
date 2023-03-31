# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for account_wrapper.py functions.
"""

import pytest
from botocore.exceptions import ClientError

import account_wrapper


@pytest.mark.parametrize("error_code", [None, "NoSuchEntity"])
def test_create_alias(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    alias = make_unique_name('alias-')

    iam_stubber.stub_create_account_alias(alias, error_code=error_code)

    if error_code is None:
        account_wrapper.create_alias(alias)
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.create_alias(alias)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_alias(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    alias = make_unique_name('alias-')

    iam_stubber.stub_delete_account_alias(alias, error_code=error_code)

    if error_code is None:
        account_wrapper.delete_alias(alias)
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.delete_alias(alias)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_aliases(make_stubber, make_unique_name, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    alias = make_unique_name('alias-')

    iam_stubber.stub_list_account_aliases([alias], error_code=error_code)

    if error_code is None:
        got_aliases = account_wrapper.list_aliases()
        assert got_aliases == [alias]
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.list_aliases()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_authorization_details(make_stubber, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    response_filter = ['User']
    response_count = 3

    iam_stubber.stub_get_account_authorization_details(
        response_filter, response_count, error_code=error_code)

    if error_code is None:
        got_details = account_wrapper.get_authorization_details(response_filter)
        assert len(got_details['UserDetailList']) == response_count
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.get_authorization_details(response_filter)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_account_summary(make_stubber, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    summary = {f'test-key-{index}': index for index in range(1, 5)}

    iam_stubber.stub_get_account_summary(summary, error_code=error_code)

    if error_code is None:
        got_summary = account_wrapper.get_summary()
        assert got_summary == summary
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.get_summary()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_generate_credential_report(make_stubber, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    state = 'STARTED'

    iam_stubber.stub_generate_credential_report(state, error_code=error_code)

    if error_code is None:
        response = account_wrapper.generate_credential_report()
        assert response['State'] == state
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.generate_credential_report()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_credential_report(make_stubber, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    report = b'This is a nice report.'

    iam_stubber.stub_get_credential_report(report, error_code=error_code)

    if error_code is None:
        got_report = account_wrapper.get_credential_report()
        assert got_report == report
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.get_credential_report()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException', 'NoSuchEntity'])
def test_get_account_password_policy(make_stubber, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)

    iam_stubber.stub_get_account_password_policy(error_code=error_code)
    
    if error_code is None or error_code == 'NoSuchEntity':
        got_policy = account_wrapper.print_password_policy()
        assert got_policy if error_code is None else not got_policy
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.print_password_policy()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('count, error_code', [
    (3, None),
    (0, None),
    (3, 'TestException')])
def test_list_saml_providers(make_stubber, count, error_code):
    iam_stubber = make_stubber(account_wrapper.iam.meta.client)
    providers = [
        f"arn:aws:iam::1111222333:saml-provider/provider-{ind}" for ind in range(3)]

    iam_stubber.stub_list_saml_providers(providers, error_code=error_code)

    if error_code is None:
        account_wrapper.list_saml_providers(3)
    else:
        with pytest.raises(ClientError) as exc_info:
            account_wrapper.list_saml_providers(3)
        assert exc_info.value.response['Error']['Code'] == error_code
