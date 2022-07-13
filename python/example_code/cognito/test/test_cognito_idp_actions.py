# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for cognito_idp_actions.py.
"""

from datetime import datetime
from unittest.mock import MagicMock
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

from cognito_idp_actions import CognitoIdentityProviderWrapper


@pytest.mark.parametrize('client_secret,error_code', [
    (None, None),
    ('test-secret', None),
    (None, 'TestException'),
    (None, 'UsernameExistsException')
])
def test_sign_up_user(make_stubber, client_secret, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    user_pool_id = 'test-user-pool-id'
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(
        cognito_idp_client, user_pool_id, client_id, client_secret)
    user_name = 'test-user_name'
    password = 'test-password'
    email = 'test@example.com'
    confirmed = True

    cognito_idp_stubber.stub_sign_up(
        client_id, user_name, password, email, confirmed,
        None if client_secret is None else ANY, error_code=error_code)
    if error_code == 'UsernameExistsException':
        cognito_idp_stubber.stub_admin_get_user(user_pool_id, user_name, 'CONFIRMED')

    if error_code is None or error_code == 'UsernameExistsException':
        got_confirmed = wrapper.sign_up_user(user_name, password, email)
        assert got_confirmed == confirmed
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.sign_up_user(user_name, password, email)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('client_secret,error_code', [
    (None, None),
    ('test-secret', None),
    (None, 'TestException')
])
def test_resend_confirmation(make_stubber, client_secret, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, '', client_id, client_secret)
    user_name = 'test-user_name'
    delivery = {'DeliveryMedium': 'test-medium', 'Destination': 'test-dest'}

    cognito_idp_stubber.stub_resend_confirmation_code(
        client_id, user_name, delivery, None if client_secret is None else ANY,
        error_code=error_code)

    if error_code is None:
        got_delivery = wrapper.resend_confirmation(user_name)
        assert got_delivery == delivery
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.resend_confirmation(user_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('client_secret,error_code', [
    (None, None),
    ('test-secret', None),
    (None, 'TestException')
])
def test_confirm_user_sign_up(make_stubber, client_secret, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, '', client_id, client_secret)
    user_name = 'test-user_name'
    conf_code = '1234'
    success = True

    cognito_idp_stubber.stub_confirm_sign_up(
        client_id, user_name, conf_code, None if client_secret is None else ANY,
        error_code=error_code)

    if error_code is None:
        got_success = wrapper.confirm_user_sign_up(user_name, conf_code)
        assert got_success == success
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.confirm_user_sign_up(user_name, conf_code)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_users(make_stubber, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    user_pool_id = 'test-pool-id'
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, user_pool_id, '')
    users = [{}, {}]

    cognito_idp_stubber.stub_list_users(user_pool_id, users, error_code=error_code)

    if error_code is None:
        got_users = wrapper.list_users()
        assert got_users == users
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_users()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code, stop_on_method, client_secret', [
    (None, None, None),
    (None, None, 'test-secret'),
    ('TestException', 'stub_admin_initiate_auth', None),
    ('TestException', 'stub_associate_software_token', None)
])
def test_start_sign_in(
        make_stubber, stub_runner, error_code, stop_on_method, client_secret):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    user_pool_id = 'test-pool-id'
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(
        cognito_idp_client, user_pool_id, client_id, client_secret)
    user_name = 'test-user_name'
    password = 'test-pass'
    challenge_name = 'MFA_SETUP'
    session = 'test-session-test-session'
    mfa_secret = 'test-secret-test-secret'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            cognito_idp_stubber.stub_admin_initiate_auth, user_pool_id, client_id,
            'ADMIN_USER_PASSWORD_AUTH', user_name, password, challenge_name,
            {'MFAS_CAN_SETUP': 'SOFTWARE_TOKEN_MFA'}, session,
            client_secret_hash=None if client_secret is None else ANY)
        runner.add(
            cognito_idp_stubber.stub_associate_software_token, session, mfa_secret)

    if error_code is None:
        got_response = wrapper.start_sign_in(user_name, password)
        assert got_response['SecretCode'] == mfa_secret
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_sign_in(user_name, password)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_verify_mfa(make_stubber, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, '', '')
    session = 'test-session-test-session'
    user_code = '123456'
    status = 'SUCCEEDED'

    cognito_idp_stubber.stub_verify_software_token(
        session, user_code, status, error_code=error_code)

    if error_code is None:
        got_status = wrapper.verify_mfa(session, user_code)
        assert got_status['Status'] == status
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.verify_mfa(session, user_code)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('client_secret,error_code', [
    (None, None),
    ('test-secret', None),
    (None, 'TestException')
])
def test_respond_to_mfa_challenge(make_stubber, error_code, client_secret):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    user_pool_id = 'test-pool-id'
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(
        cognito_idp_client, user_pool_id, client_id, client_secret)
    user_name = 'test-user_name'
    challenge_name = 'SOFTWARE_TOKEN_MFA'
    session = 'test-session-test-session'
    mfa_code = '123456'
    challenge_responses = {
        'USERNAME': user_name, 'SOFTWARE_TOKEN_MFA_CODE': mfa_code}
    access_token = 'test-token'

    cognito_idp_stubber.stub_admin_respond_to_auth_challenge(
        user_pool_id, client_id, challenge_name, session, challenge_responses,
        access_token, None if client_secret is None else ANY, error_code=error_code)

    if error_code is None:
        got_access_token = wrapper.respond_to_mfa_challenge(user_name, session, mfa_code)
        assert got_access_token['AccessToken'] == access_token
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.respond_to_mfa_challenge(user_name, session, mfa_code)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_confirm_mfa_device(make_stubber, error_code):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, '', client_id)
    user_name = 'test-user_name'
    device_key = 'test-device-key'
    device_group_key = 'test-group-key'
    device_password = 'test-device-password'
    access_token = 'test-token'
    device_and_pw_hash = 'test-hash'
    salt = '123456'
    verifier = '567890'
    aws_srp = MagicMock()
    aws_srp.hash_sha256 = lambda x: device_and_pw_hash
    pad_hex_results = [salt, verifier]
    aws_srp.pad_hex = lambda x: pad_hex_results.pop(0)
    aws_srp.hex_to_long = lambda x: 12345467890
    confirmation = False

    cognito_idp_stubber.stub_confirm_device(
        access_token, device_key, ANY, ANY, confirmation, error_code=error_code)

    if error_code is None:
        got_confirmation = wrapper.confirm_mfa_device(
            user_name, device_key, device_group_key, device_password, access_token, aws_srp)
        assert got_confirmation == confirmation
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.confirm_mfa_device(
                user_name, device_key, device_group_key, device_password, access_token, aws_srp)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_initiate_auth'),
    ('TestException', 'stub_respond_to_auth_challenge')
])
def test_sign_in_with_tracked_device(make_stubber, stub_runner, error_code, stop_on_method):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    client_id = 'test-client-id'
    wrapper = CognitoIdentityProviderWrapper(cognito_idp_client, '', client_id)
    user_name = 'test-user_name'
    password = 'test-pass'
    device_key = 'test-device-key'
    device_group_key = 'test-group-key'
    device_password = 'test-device-password'
    access_token = 'test-token'
    aws_srp = MagicMock()
    aws_srp.AWSSRP = MagicMock
    aws_srp.AWSSRP.get_auth_params = lambda s: {
        'USERNAME': user_name, 'SRP_A': 'test-srp-a', 'DEVICE_KEY': device_key}
    tstamp = str(datetime.utcnow())
    aws_srp.AWSSRP.process_challenge = lambda s, x: {
        'TIMESTAMP': tstamp, 'USERNAME': user_name,
        'PASSWORD_CLAIM_SECRET_BLOCK': 'test-secret-block',
        'PASSWORD_CLAIM_SIGNATURE': 'test-signature',
        'DEVICE_KEY': device_key}

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            cognito_idp_stubber.stub_initiate_auth, client_id, 'USER_PASSWORD_AUTH',
            user_name, password, device_key, 'DEVICE_SRP_AUTH')
        runner.add(
            cognito_idp_stubber.stub_respond_to_auth_challenge, client_id,
            'DEVICE_SRP_AUTH', aws_srp.AWSSRP.get_auth_params('s'), 'DEVICE_PASSWORD_VERIFIER',
            {})
        runner.add(
            cognito_idp_stubber.stub_respond_to_auth_challenge, client_id,
            'DEVICE_PASSWORD_VERIFIER', aws_srp.AWSSRP.process_challenge('s', True),
            '', access_token=access_token)
    if error_code is None:
        got_access_token = wrapper.sign_in_with_tracked_device(
            user_name, password, device_key, device_group_key, device_password, aws_srp)
        assert got_access_token['AccessToken'] == access_token
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.sign_in_with_tracked_device(
                user_name, password, device_key, device_group_key, device_password, aws_srp)
        assert exc_info.value.response['Error']['Code'] == error_code
