# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Identity and Access Management (IAM) unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from datetime import datetime
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
            'CreateDate': datetime.now()
        }
        return response

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
                           error_code=None):
        expected_params = {
            'PolicyName': policy_name,
            'PolicyDocument': policy_document
        }
        if not error_code:
            self.add_response(
                'create_policy',
                expected_params=expected_params,
                service_response={
                    'Policy': {
                        'PolicyName': policy_name,
                        'Arn': policy_arn
                    }
                }
            )
        else:
            self.add_client_error(
                'create_policy',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_get_policy(self, policy_arn, status_code=200, error_code=None):
        expected_params = {'PolicyArn': policy_arn}
        if not error_code:
            self.add_response(
                'get_policy',
                expected_params=expected_params,
                service_response= {
                    'ResponseMetadata': {'HTTPStatusCode': status_code},
                    'Policy': {'PolicyName': policy_arn.split(':')[-1]}
                }
            )
        else:
            self.add_client_error(
                'get_policy',
                expected_params=expected_params,
                service_error_code=error_code
            )
            
    def stub_delete_policy(self, policy_arn, error_code=None):
        self._stub_bifurcator(
            'delete_policy',
            expected_params={'PolicyArn': policy_arn},
            error_code=error_code)

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
