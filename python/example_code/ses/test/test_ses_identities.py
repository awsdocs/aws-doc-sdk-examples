# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for ses_identities.py.
"""

import boto3
from botocore.exceptions import ClientError, WaiterError
import pytest

from ses_identities import SesIdentity


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_verify_domain_identity(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    domain_name = 'example.com'
    token = 'test-token'

    ses_stubber.stub_verify_domain_identity(domain_name, token, error_code=error_code)

    if error_code is None:
        got_token = ses_identity.verify_domain_identity(domain_name)
        assert token == got_token
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_identity.verify_domain_identity(domain_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_verify_email_identity(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    email = 'test@example.com'

    ses_stubber.stub_verify_email_identity(email, error_code=error_code)

    if error_code is None:
        ses_identity.verify_email_identity(email)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_identity.verify_email_identity(email)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_wait_until_identity_exists(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    email = 'test@example.com'

    ses_stubber.stub_get_identity_verification_attributes(
        [email], ['Success'], error_code=error_code)

    if error_code is None:
        ses_identity.wait_until_identity_exists(email)
    else:
        with pytest.raises(WaiterError):
            ses_identity.wait_until_identity_exists(email)


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_get_identity_status(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    email = 'test@example.com'
    status = 'Pending'

    ses_stubber.stub_get_identity_verification_attributes(
        [email], [status], error_code=error_code)

    if error_code is None:
        got_status = ses_identity.get_identity_status(email)
        assert got_status == status
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_identity.get_identity_status(email)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_identity(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    email = 'test@example.com'

    ses_stubber.stub_delete_identity(email, error_code=error_code)

    if error_code is None:
        ses_identity.delete_identity(email)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_identity.delete_identity(email)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_list_identities(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_identity = SesIdentity(ses_client)
    identity_type = 'EmailAddress'
    max_items = 5
    identities = ['test@example.com', 'anotherone@example.org']

    ses_stubber.stub_list_identities(
        identity_type, max_items, identities, error_code=error_code)

    if error_code is None:
        got_identities = ses_identity.list_identities(identity_type, max_items)
        assert got_identities == identities
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_identity.list_identities(identity_type, max_items)
        assert exc_info.value.response['Error']['Code'] == error_code
