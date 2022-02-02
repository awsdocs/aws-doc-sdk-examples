# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS STS unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import datetime
import io
import json
from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class StsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS STS Control unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 STS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_get_caller_identity(self, account_id, error_code=None):
        response = {'Account': account_id}
        self._stub_bifurcator(
            'get_caller_identity', response=response, error_code=error_code)

    def stub_assume_role(
            self, role_arn, session_name, mfa_serial_number=None, mfa_totp=None,
            key_id='test-access-key-id', secret_key='test-secret-key',
            session_token='test-session-token', error_code=None):
        expected_params = {'RoleArn': role_arn, 'RoleSessionName': session_name}
        if mfa_serial_number is not None:
            expected_params['SerialNumber'] = mfa_serial_number
        if mfa_totp is not None:
            expected_params['TokenCode'] = mfa_totp
        response = {
            'Credentials': {
                'AccessKeyId': key_id,
                'SecretAccessKey': secret_key,
                'SessionToken': session_token,
                'Expiration': datetime.datetime.now() + datetime.timedelta(minutes=5)
            }
        }
        self._stub_bifurcator(
            'assume_role', expected_params, response, error_code=error_code)

    def stub_get_session_token(
            self, serial_number, token_code, credentials=None, error_code=None):
        expected_params = {}
        if serial_number is not None:
            expected_params['SerialNumber'] = serial_number
        if token_code is not None:
            expected_params['TokenCode'] = token_code
        response = {'Credentials': {}}
        if credentials is not None:
            response['Credentials'] = {
                'AccessKeyId': credentials.id,
                'SecretAccessKey': credentials.secret,
                'SessionToken': credentials.token,
                'Expiration': datetime.datetime.now() + datetime.timedelta(seconds=10)
            }
        else:
            response['Credentials'] = {
                'AccessKeyId': 'test-key-id-plus-more',
                'SecretAccessKey': 'test-access-key-secret',
                'SessionToken': 'test-session-token',
                'Expiration': datetime.datetime.now() + datetime.timedelta(seconds=10)
            }
        self._stub_bifurcator(
            'get_session_token', expected_params, response, error_code=error_code)
