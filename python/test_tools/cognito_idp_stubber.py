# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Cognito Identity Provider unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class CognitoIdpStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Cognito Identity Provider
    unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Cognito Identity Provider client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_sign_up(
            self, client_id, user_name, password, user_email, confirmed,
            client_secret_hash=None, error_code=None):
        expected_params = {
            'ClientId': client_id, 'Username': user_name, 'Password': password,
            'UserAttributes': [{'Name': 'email', 'Value': user_email}]}
        if client_secret_hash is not None:
            expected_params['SecretHash'] = client_secret_hash
        response = {'UserConfirmed': confirmed, 'UserSub': 'test-sub'}
        self._stub_bifurcator(
            'sign_up', expected_params, response, error_code=error_code)

    def stub_admin_get_user(self, user_pool_id, user_name, status, error_code=None):
        expected_params = {'UserPoolId': user_pool_id, 'Username': user_name}
        response = {'Username': user_name, 'UserStatus': status}
        self._stub_bifurcator(
            'admin_get_user', expected_params, response, error_code=error_code)

    def stub_resend_confirmation_code(
            self, client_id, user_name, delivery, client_secret_hash=None, error_code=None):
        expected_params = {
            'ClientId': client_id, 'Username': user_name}
        if client_secret_hash is not None:
            expected_params['SecretHash'] = client_secret_hash
        response = {'CodeDeliveryDetails': delivery}
        self._stub_bifurcator(
            'resend_confirmation_code', expected_params, response, error_code=error_code)

    def stub_confirm_sign_up(
            self, client_id, user_name, conf_code, client_secret_hash=None, error_code=None):
        expected_params = {
            'ClientId': client_id, 'Username': user_name, 'ConfirmationCode': conf_code}
        if client_secret_hash is not None:
            expected_params['SecretHash'] = client_secret_hash
        response = {}
        self._stub_bifurcator(
            'confirm_sign_up', expected_params, response, error_code=error_code)

    def stub_list_users(self, user_pool_id, users, error_code=None):
        expected_params = {'UserPoolId': user_pool_id}
        response = {'Users': users}
        self._stub_bifurcator(
            'list_users', expected_params, response, error_code=error_code)

    def stub_admin_initiate_auth(
            self, user_pool_id, client_id, auth_flow, user_name, password, challenge_name,
            challenge_parameters, session, client_secret_hash=None, error_code=None):
        expected_params = {
            'UserPoolId': user_pool_id, 'ClientId': client_id, 'AuthFlow': auth_flow,
            'AuthParameters': {'USERNAME': user_name, 'PASSWORD': password}}
        if client_secret_hash is not None:
            expected_params['AuthParameters']['SECRET_HASH'] = client_secret_hash
        response = {
            'ChallengeName': challenge_name, 'ChallengeParameters': challenge_parameters,
            'Session': session}
        self._stub_bifurcator(
            'admin_initiate_auth', expected_params, response, error_code=error_code)

    def stub_associate_software_token(self, session, mfa_secret, error_code=None):
        expected_params = {'Session': session}
        response = {'SecretCode': mfa_secret}
        self._stub_bifurcator(
            'associate_software_token', expected_params, response, error_code=error_code)

    def stub_verify_software_token(self, session, user_code, status, error_code=None):
        expected_params = {'Session': session, 'UserCode': user_code}
        response = {'Status': status}
        self._stub_bifurcator(
            'verify_software_token', expected_params, response, error_code=error_code)

    def stub_admin_respond_to_auth_challenge(
            self, user_pool_id, client_id, challenge_name, session, challenge_responses,
            access_token, client_secret_hash=None, device_info=None, error_code=None):
        expected_params = {
            'UserPoolId': user_pool_id, 'ClientId': client_id, 'Session': session,
            'ChallengeName': challenge_name, 'ChallengeResponses': challenge_responses}
        if client_secret_hash is not None:
            expected_params['ChallengeResponses']['SECRET_HASH'] = client_secret_hash
        response = {'AuthenticationResult': {'AccessToken': access_token}}
        if device_info is not None:
            response['AuthenticationResult']['NewDeviceMetadata'] = device_info
        self._stub_bifurcator(
            'admin_respond_to_auth_challenge', expected_params, response, error_code=error_code)

    def stub_confirm_device(
            self, access_token, device_key, pw_verifier, salt, confirmation, error_code=None):
        expected_params = {
            'AccessToken': access_token, 'DeviceKey': device_key,
            'DeviceSecretVerifierConfig': {'PasswordVerifier': pw_verifier, 'Salt': salt}}
        response = {'UserConfirmationNecessary': confirmation}
        self._stub_bifurcator(
            'confirm_device', expected_params, response, error_code=error_code)

    def stub_initiate_auth(
            self, client_id, auth_flow, user_name, password, device_key,
            challenge_name, error_code=None):
        expected_params = {
            'ClientId': client_id, 'AuthFlow': auth_flow,
            'AuthParameters': {
                'USERNAME': user_name, 'PASSWORD': password, 'DEVICE_KEY': device_key}}
        response = {'ChallengeName': challenge_name}
        self._stub_bifurcator(
            'initiate_auth', expected_params, response, error_code=error_code)

    def stub_respond_to_auth_challenge(
            self, client_id, challenge_name, challenge_responses,
            new_challenge_name, new_challenge_params=None, access_token=None,
            error_code=None):
        expected_params = {
            'ClientId': client_id, 'ChallengeName': challenge_name,
            'ChallengeResponses': challenge_responses}
        response = {'ChallengeName': new_challenge_name}
        if new_challenge_params is not None:
            response['ChallengeParameters'] = new_challenge_params
        if access_token is not None:
            response['AuthenticationResult'] = {'AccessToken': access_token}
        self._stub_bifurcator(
            'respond_to_auth_challenge', expected_params, response, error_code=error_code)
