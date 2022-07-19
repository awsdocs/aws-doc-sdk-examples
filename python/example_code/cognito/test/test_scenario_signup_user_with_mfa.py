# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_signup_user_with_mfa.py.
"""

from datetime import datetime
from unittest.mock import MagicMock, ANY
import boto3
from botocore.exceptions import ClientError
import pytest
import qrcode
import webbrowser

import scenario_signup_user_with_mfa as scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_sign_up'),
    ('TestException', 'stub_resend_confirmation_code'),
    ('TestException', 'stub_confirm_sign_up'),
    ('TestException', 'stub_list_users'),
    ('TestException', 'stub_admin_initiate_auth'),
    ('TestException', 'stub_associate_software_token'),
    ('TestException', 'stub_verify_software_token'),
    ('TestException', 'stub_admin_respond_to_auth_challenge'),
    ('TestException', 'stub_confirm_device'),
    ('TestException', 'stub_initiate_auth'),
    ('TestException', 'stub_respond_to_auth_challenge'),
])
def test_scenario(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    cognito_idp_client = boto3.client('cognito-idp')
    cognito_idp_stubber = make_stubber(cognito_idp_client)
    user_pool_id = 'test-user-pool-id'
    client_id = 'test-client-id'
    user_name = 'test-user_name'
    password = 'test-pass'
    email = 'test@example.com'
    delivery = {'DeliveryMedium': 'test-medium', 'Destination': 'test-dest'}
    conf_code = '1234'
    users = [{}, {}]
    session = 'test-session-test-session'
    device_key = 'test-device-key'
    mfa_secret = 'test-secret-test-secret'
    user_code = '123456'
    status = 'SUCCEEDED'
    device_group_key = 'test-group-key'
    device_and_pw_hash = 'test-hash'
    salt = '123456'
    verifier = '567890'
    access_token = 'test-token'
    aws_srp = MagicMock()
    aws_srp.hash_sha256 = lambda x: device_and_pw_hash
    pad_hex_results = [salt, verifier]
    aws_srp.pad_hex = lambda x: pad_hex_results.pop(0)
    aws_srp.hex_to_long = lambda x: 12345467890
    aws_srp.AWSSRP = MagicMock
    aws_srp.AWSSRP.get_auth_params = lambda s: {
        'USERNAME': user_name, 'SRP_A': 'test-srp-a', 'DEVICE_KEY': device_key}
    tstamp = str(datetime.utcnow())
    aws_srp.AWSSRP.process_challenge = lambda s, x: {
        'TIMESTAMP': tstamp, 'USERNAME': user_name,
        'PASSWORD_CLAIM_SECRET_BLOCK': 'test-secret-block',
        'PASSWORD_CLAIM_SIGNATURE': 'test-signature',
        'DEVICE_KEY': device_key}

    inputs = [
        user_name, password, email, '', 'y', conf_code, '', '', user_code, user_code,
        '', '']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))
    monkeypatch.setattr(scenario, 'aws_srp', aws_srp)
    monkeypatch.setattr(qrcode, 'make', lambda x: MagicMock())
    monkeypatch.setattr(webbrowser, 'open', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            cognito_idp_stubber.stub_sign_up, client_id, user_name, password, email, False)
        runner.add(
            cognito_idp_stubber.stub_resend_confirmation_code, client_id, user_name, delivery)
        runner.add(
            cognito_idp_stubber.stub_confirm_sign_up, client_id, user_name, conf_code)
        runner.add(cognito_idp_stubber.stub_list_users, user_pool_id, users)
        runner.add(
            cognito_idp_stubber.stub_admin_initiate_auth, user_pool_id, client_id,
            'ADMIN_USER_PASSWORD_AUTH', user_name, password, 'MFA_SETUP',
            {'MFAS_CAN_SETUP': 'SOFTWARE_TOKEN_MFA'}, session)
        runner.add(
            cognito_idp_stubber.stub_associate_software_token, session, mfa_secret)
        runner.add(cognito_idp_stubber.stub_verify_software_token, session, user_code, status)
        runner.add(
            cognito_idp_stubber.stub_admin_initiate_auth, user_pool_id, client_id,
            'ADMIN_USER_PASSWORD_AUTH', user_name, password, 'SOFTWARE_TOKEN_MFA',
            {}, session)
        runner.add(
            cognito_idp_stubber.stub_admin_respond_to_auth_challenge, user_pool_id, client_id,
            'SOFTWARE_TOKEN_MFA', session,
            {'USERNAME': user_name, 'SOFTWARE_TOKEN_MFA_CODE': user_code}, access_token,
            device_info={'DeviceGroupKey': device_group_key, 'DeviceKey': device_key})
        runner.add(
            cognito_idp_stubber.stub_confirm_device, access_token, device_key, ANY, ANY, False)
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
        scenario.run_scenario(cognito_idp_client, user_pool_id, client_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run_scenario(cognito_idp_client, user_pool_id, client_id)
        assert exc_info.value.response['Error']['Code'] == error_code
