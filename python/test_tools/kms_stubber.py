# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Key Management Service (AWS KMS) unit tests.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class KmsStubber(ExampleStubber):
    """
    Implements stub functions used by AWS KMS unit tests.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 AWS KMS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_key(self, description, key_id, error_code=None):
        expected_params = {'Description': description}
        response = {'KeyMetadata': {'KeyId': key_id, 'Description': description}}
        self._stub_bifurcator(
            'create_key', expected_params, response, error_code=error_code)

    def stub_list_keys(self, limit, key_ids, marker=None, truncated=False, error_code=None):
        expected_params = {'Limit': limit}
        if marker is not None:
            expected_params['Marker'] = marker
        response = {'Keys': [{'KeyId': kid} for kid in key_ids], 'Truncated': truncated}
        if truncated:
            response['NextMarker'] = 'test-token'
        self._stub_bifurcator(
            'list_keys', expected_params, response, error_code=error_code)

    def stub_describe_key(self, key_id, state, error_code=None):
        expected_params = {'KeyId': key_id}
        response = {'KeyMetadata': {'KeyId': key_id, 'KeyState': state}}
        self._stub_bifurcator(
            'describe_key', expected_params, response, error_code=error_code)

    def stub_generate_data_key(self, key_id, key_spec, error_code=None):
        expected_params = {'KeyId': key_id, 'KeySpec': key_spec}
        response = {}
        self._stub_bifurcator(
            'generate_data_key', expected_params, response, error_code=error_code)

    def stub_enable_key(self, key_id, error_code=None):
        expected_params = {'KeyId': key_id}
        response = {}
        self._stub_bifurcator(
            'enable_key', expected_params, response, error_code=error_code)

    def stub_disable_key(self, key_id, error_code=None):
        expected_params = {'KeyId': key_id}
        response = {}
        self._stub_bifurcator(
            'disable_key', expected_params, response, error_code=error_code)

    def stub_schedule_key_deletion(self, key_id, window, error_code=None):
        expected_params = {'KeyId': key_id, 'PendingWindowInDays': window}
        response = {}
        self._stub_bifurcator(
            'schedule_key_deletion', expected_params, response, error_code=error_code)

    def stub_create_alias(self, alias, key_id, error_code=None):
        expected_params = {'AliasName': alias, 'TargetKeyId': key_id}
        response = {}
        self._stub_bifurcator(
            'create_alias', expected_params, response, error_code=error_code)

    def stub_list_aliases(self, limit, aliases, marker=None, truncated=False, error_code=None):
        expected_params = {'Limit': limit}
        if marker is not None:
            expected_params['Marker'] = marker
        response = {'Aliases': [{'AliasName': alias} for alias in aliases], 'Truncated': truncated}
        if truncated:
            response['NextMarker'] = 'test-token'
        self._stub_bifurcator(
            'list_aliases', expected_params, response, error_code=error_code)

    def stub_update_alias(self, alias, key_id, error_code=None):
        expected_params = {'AliasName': alias, 'TargetKeyId': key_id}
        response = {}
        self._stub_bifurcator(
            'update_alias', expected_params, response, error_code=error_code)

    def stub_delete_alias(self, alias, error_code=None):
        expected_params = {'AliasName': alias}
        response = {}
        self._stub_bifurcator(
            'delete_alias', expected_params, response, error_code=error_code)

    def stub_create_grant(self, key_id, user, operations, grant, error_code=None):
        expected_params = {
            'KeyId': key_id, 'GranteePrincipal': user, 'Operations': operations}
        response = grant
        self._stub_bifurcator(
            'create_grant', expected_params, response, error_code=error_code)

    def stub_list_grants(self, key_id, grants, error_code=None):
        expected_params = {'KeyId': key_id}
        response = {'Grants': [{'GrantId': grant for grant in grants}]}
        self._stub_bifurcator(
            'list_grants', expected_params, response, error_code=error_code)

    def stub_retire_grant(self, grant_token, error_code=None):
        expected_params = {'GrantToken': grant_token}
        response = {}
        self._stub_bifurcator(
            'retire_grant', expected_params, response, error_code=error_code)

    def stub_revoke_grant(self, key_id, grant_id, error_code=None):
        expected_params = {'KeyId': key_id, 'GrantId': grant_id}
        response = {}
        self._stub_bifurcator(
            'revoke_grant', expected_params, response, error_code=error_code)

    def stub_list_key_policies(self, key_id, policy_names, error_code=None):
        expected_params = {'KeyId': key_id}
        response = {'PolicyNames': policy_names}
        self._stub_bifurcator(
            'list_key_policies', expected_params, response, error_code=error_code)

    def stub_get_key_policy(self, key_id, policy, error_code=None):
        expected_params = {'KeyId': key_id, 'PolicyName': 'default'}
        response = {'Policy': policy}
        self._stub_bifurcator(
            'get_key_policy', expected_params, response, error_code=error_code)

    def stub_put_key_policy(self, key_id, error_code=None):
        expected_params = {'KeyId': key_id, 'Policy': ANY, 'PolicyName': 'default'}
        response = {}
        self._stub_bifurcator(
            'put_key_policy', expected_params, response, error_code=error_code)

    def stub_encrypt(self, key_id, plaintext, ciphertext, error_code=None):
        expected_params = {'KeyId': key_id, 'Plaintext': plaintext}
        response = {'CiphertextBlob': ciphertext}
        self._stub_bifurcator(
            'encrypt', expected_params, response, error_code=error_code)

    def stub_decrypt(self, key_id, ciphertext, plaintext, error_code=None):
        expected_params = {'KeyId': key_id, 'CiphertextBlob': ciphertext}
        response = {'Plaintext': plaintext}
        self._stub_bifurcator(
            'decrypt', expected_params, response, error_code=error_code)

    def stub_re_encrypt(self, source_key_id, dest_key_id, ciphertext, error_code=None):
        expected_params = {
            'SourceKeyId': source_key_id, 'DestinationKeyId': dest_key_id,
            'CiphertextBlob': ciphertext}
        response = {'CiphertextBlob': ciphertext}
        self._stub_bifurcator(
            're_encrypt', expected_params, response, error_code=error_code)
