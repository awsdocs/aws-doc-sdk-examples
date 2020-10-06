# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Secrets Manager unit tests.
"""

from test_tools.example_stubber import ExampleStubber

class SecretsManagerStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS Secrets Manager unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Secrets Manager client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_secret(self, secret_name, secret_string, error_code=None):
        expected_params = {'Name': secret_name, 'SecretString': secret_string}
        response = {'Name': secret_name}
        self._stub_bifurcator(
            'create_secret', expected_params, response, error_code=error_code)

    def stub_delete_secret(self, secret_name, error_code=None):
        expected_params = {'SecretId': secret_name, 'ForceDeleteWithoutRecovery': True}
        self._stub_bifurcator(
            'delete_secret', expected_params, error_code=error_code)
