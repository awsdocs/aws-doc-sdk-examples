# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Identity and Access Management (IAM) unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import datetime
import random
import string
from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


def random_string(length):
    return ''.join([random.choice(string.ascii_lowercase) for _ in range(length)])


class IamStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    IAM unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 IAM client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _add_role(response, role_name):
        response['Role'] = {
            'RoleName': role_name,
            'Path': '/',
            'RoleId': random_string(16),
            'Arn': random_string(20),
            'CreateDate': datetime.datetime.now()
        }
        return response

    def stub_create_account_alias(self, alias, error_code=None):
        expected_params = {'AccountAlias': alias}
        self._stub_bifurcator(
            'create_account_alias', expected_params, error_code=error_code)

    def stub_delete_account_alias(self, alias, error_code=None):
        expected_params = {'AccountAlias': alias}
        self._stub_bifurcator(
            'delete_account_alias', expected_params, error_code=error_code)

    def stub_list_account_aliases(self, aliases, error_code=None):
        response = {'AccountAliases': aliases}
        self._stub_bifurcator(
            'list_account_aliases', {}, response, error_code=error_code)

    def stub_get_account_authorization_details(
            self, response_filter, response_count, error_code=None):
        expected_params = {'Filter': response_filter}
        response = {'UserDetailList': [{
            'Path': 'test-path',
            'UserName': f'user-{index}',
            'UserId': 'testid-' + str(index)*16,
            'Arn': 'test-arn-' + str(index)*20,
            'CreateDate': datetime.datetime.now()
        } for index in range(1, response_count + 1)]}
        self._stub_bifurcator(
            'get_account_authorization_details', expected_params, response,
            error_code=error_code)

    def stub_get_account_summary(self, summary, error_code=None):
        response = {'SummaryMap': summary}
        self._stub_bifurcator(
            'get_account_summary', response=response, error_code=error_code)

    def stub_generate_credential_report(self, state, error_code=None):
        response = {'State': state, 'Description': "It's only a test."}
        self._stub_bifurcator(
            'generate_credential_report', response=response, error_code=error_code)

    def stub_get_credential_report(self, report, error_code=None):
        response = {
            'Content': report,
            'ReportFormat': 'text/csv',
            'GeneratedTime': datetime.datetime.now()
        }
        self._stub_bifurcator(
            'get_credential_report', response=response, error_code=error_code)

    def stub_create_role(self, role_name, assume_role_policy=ANY, error_code=None):
        expected_params = {
            'RoleName': role_name,
            'AssumeRolePolicyDocument': assume_role_policy
        }

        if not error_code:
            self.add_response(
                'create_role',
                expected_params=expected_params,
                service_response=self._add_role({}, role_name)
            )
        else:
            self.add_client_error(
                'create_role',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_get_role(self, role_name, status_code=200, error_code=None):
        expected_params = {'RoleName': role_name}
        if not error_code:
            self.add_response(
                'get_role',
                expected_params=expected_params,
                service_response=self._add_role({
                    'ResponseMetadata': {'HTTPStatusCode': status_code}
                }, role_name)
            )
        else:
            self.add_client_error(
                'get_role',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_delete_role(self, role_name, error_code=None):
        self._stub_bifurcator(
            'delete_role',
            expected_params={'RoleName': role_name},
            error_code=error_code)

    def stub_create_policy(self, policy_name, policy_arn, policy_document=ANY,
                           description=None, error_code=None):
        expected_params = {
            'PolicyName': policy_name,
            'PolicyDocument': policy_document
        }
        if description is not None:
            expected_params['Description'] = description
        response = {
            'Policy': {
                'PolicyName': policy_name,
                'Arn': policy_arn
            }
        }
        self._stub_bifurcator(
            'create_policy', expected_params, response, error_code=error_code)

    def stub_get_policy(
            self, policy_arn, default_version_id=None, status_code=200,
            error_code=None):
        expected_params = {'PolicyArn': policy_arn}
        response = {
            'ResponseMetadata': {'HTTPStatusCode': status_code},
            'Policy': {
                'PolicyName': policy_arn.split(':')[-1],
                'Arn': policy_arn
            }
        }
        if default_version_id is not None:
            response['Policy']['DefaultVersionId'] = default_version_id
        self._stub_bifurcator(
            'get_policy', expected_params, response, error_code=error_code)

    def stub_delete_policy(self, policy_arn, error_code=None):
        self._stub_bifurcator(
            'delete_policy',
            expected_params={'PolicyArn': policy_arn},
            error_code=error_code)

    def stub_list_policies(self, scope, policies, error_code=None):
        expected_params = {'Scope': scope}
        response = {
            'Policies': [{
                'PolicyName': poli_key,
                'Arn': poli_val
            } for poli_key, poli_val in policies.items()]
        }
        self._stub_bifurcator(
            'list_policies', expected_params, response, error_code=error_code)

    def stub_create_policy_version(
            self, policy_arn, policy_version_id, policy_doc=ANY, set_as_default=True,
            error_code=None):
        expected_params = {
            'PolicyArn': policy_arn,
            'PolicyDocument': policy_doc,
            'SetAsDefault': set_as_default
        }
        response = {
            'PolicyVersion':
                {'VersionId': policy_version_id, 'IsDefaultVersion': set_as_default}}
        self._stub_bifurcator(
            'create_policy_version', expected_params, response, error_code=error_code)

    def stub_get_policy_version(
            self, policy_arn, policy_version_id, policy_doc, error_code=None):
        expected_params = {'PolicyArn': policy_arn, 'VersionId': policy_version_id}
        response = {
            'PolicyVersion': {
                'Document': policy_doc,
                'VersionId': policy_version_id
            }
        }
        self._stub_bifurcator(
            'get_policy_version', expected_params, response, error_code=error_code)

    def stub_list_policy_versions(self, policy_arn, policy_versions, error_code=None):
        expected_params = {'PolicyArn': policy_arn}
        response = {
            'Versions': [{
                'Document': ver['document'],
                'VersionId': ver['id'],
                'IsDefaultVersion': ver['is_default'],
                'CreateDate': ver['create_date']
            } for ver in policy_versions]
        }
        self._stub_bifurcator(
            'list_policy_versions', expected_params, response, error_code=error_code)

    def stub_set_default_policy_version(
            self, policy_arn, policy_version, error_code=None):
        expected_params= {'PolicyArn': policy_arn, 'VersionId': policy_version}
        self._stub_bifurcator(
            'set_default_policy_version', expected_params, error_code=error_code)

    def stub_delete_policy_version(self, policy_arn, policy_version, error_code=None):
        expected_params= {'PolicyArn': policy_arn, 'VersionId': policy_version}
        self._stub_bifurcator(
            'delete_policy_version', expected_params, error_code=error_code)

    def stub_attach_role_policy(self, role_name, policy_arn, error_code=None):
        expected_params = {
            'RoleName': role_name,
            'PolicyArn': policy_arn
        }
        if not error_code:
            self.add_response(
                'attach_role_policy',
                expected_params=expected_params,
                service_response={}
            )
        else:
            self.add_client_error(
                'attach_role_policy',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_list_attached_role_policies(self, role_name, policies=None,
                                         error_code=None):
        expected_params = {'RoleName': role_name}
        if not error_code:
            self.add_response(
                'list_attached_role_policies',
                expected_params=expected_params,
                service_response={
                    'AttachedPolicies': [{
                        'PolicyName': name,
                        'PolicyArn': arn
                    } for name, arn in policies.items()]
                }
            )
        else:
            self.add_client_error(
                'list_attached_role_policies',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_detach_role_policy(self, role_name, policy_arn, error_code=None):
        self._stub_bifurcator(
            'detach_role_policy',
            expected_params={'RoleName': role_name, 'PolicyArn': policy_arn},
            error_code=error_code
        )

    def stub_create_access_key(self, user_name, error_code=None):
        expected_params = {'UserName': user_name}
        response = {
            'AccessKey': {
                'UserName': user_name,
                'AccessKeyId': 'test-id-plus-more-characters',
                'Status': 'Active',
                'SecretAccessKey': 'test-secret-plus-more-characters    ',
                'CreateDate': datetime.datetime.now()
            }
        }
        self._stub_bifurcator(
            'create_access_key', expected_params, response, error_code=error_code)

    def stub_delete_access_key(self, user_name, key_id, error_code=None):
        expected_params = {'UserName': user_name, 'AccessKeyId': key_id}
        self._stub_bifurcator(
            'delete_access_key', expected_params, error_code=error_code)

    def stub_get_access_key_last_used(self, key_id, user_name, error_code=None):
        expected_params = {'AccessKeyId': key_id}
        response = {
            'UserName': user_name,
            'AccessKeyLastUsed': {
                'LastUsedDate': datetime.datetime.now(),
                'ServiceName': 'test-svc',
                'Region': 'test-region'
            }
        }
        self._stub_bifurcator(
            'get_access_key_last_used', expected_params, response,
            error_code=error_code)

    def stub_list_access_keys(self, user_name, key_ids, error_code=None):
        expected_params = {'UserName': user_name}
        response = {
            'AccessKeyMetadata': [{
                'UserName': user_name,
                'AccessKeyId': key_id,
                'Status': 'Active',
                'CreateDate': datetime.datetime.now()
            } for key_id in key_ids]
        }
        self._stub_bifurcator(
            'list_access_keys', expected_params, response, error_code=error_code)

    def stub_update_access_key(self, user_name, key_id, activate, error_code=None):
        expected_params = {
            'UserName': user_name,
            'AccessKeyId': key_id,
            'Status': 'Active' if activate else 'Inactive'
        }
        self._stub_bifurcator(
            'update_access_key', expected_params, error_code=error_code)

    def stub_create_user(self, user_name, error_code=None):
        expected_params = {'UserName': user_name}
        response = {
            'User': {
                'UserName': user_name,
                'UserId': 'test-id-plus-more-characters',
                'Arn': 'arn:aws:iam:::test-user',
                'Path': 'test-path',
                'CreateDate': datetime.datetime.now()
            }
        }
        self._stub_bifurcator(
            'create_user', expected_params, response, error_code=error_code)

    def stub_delete_user(self, user_name, error_code=None):
        expected_params = {'UserName': user_name}
        self._stub_bifurcator(
            'delete_user', expected_params, error_code=error_code)

    def stub_list_users(self, user_count, error_code=None):
        response = {
            'Users': [{
                'UserName': f'test-user-{index}',
                'UserId': f'test-id-plus-more-characters-{index}',
                'Arn': f'arn:aws:iam:::test-user-{index}',
                'Path': f'test-path-{index}',
                'CreateDate': datetime.datetime.now() + datetime.timedelta(days=index)
            } for index in range(1, user_count+1)]
        }
        self._stub_bifurcator(
            'list_users', response=response, error_code=error_code)

    def stub_update_user(self, current_name, new_name, error_code=None):
        expected_params = {'UserName': current_name, 'NewUserName': new_name}
        self._stub_bifurcator(
            'update_user', expected_params, error_code=error_code)

    def stub_attach_user_policy(self, user_name, policy_arn, error_code=None):
        expected_params = {'UserName': user_name, 'PolicyArn': policy_arn}
        self._stub_bifurcator(
            'attach_user_policy', expected_params, error_code=error_code)

    def stub_detach_user_policy(self, user_name, policy_arn, error_code=None):
        expected_params = {'UserName': user_name, 'PolicyArn': policy_arn}
        self._stub_bifurcator(
            'detach_user_policy', expected_params, error_code=error_code)
