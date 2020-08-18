# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Organizations unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import json

from test_tools.example_stubber import ExampleStubber


class OrganizationsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS Organizations unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Organizations client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _make_policy_summary(policy):
        return {
            'Id': policy['id'],
            'Arn': f'arn:aws:organizations::111111111111:policy/{policy["name"]}',
            'Name': policy['name'],
            'Description': policy['description'],
            'Type': policy['type'],
            'AwsManaged': False
        }

    def stub_create_policy(self, policy, error_code=None):
        expected_parameters = {
            'Name': policy['name'],
            'Description': policy['description'],
            'Content': json.dumps(policy['content']),
            'Type': policy['type']
        }
        response = {'Policy': {
            'PolicySummary': self._make_policy_summary(policy),
            'Content': json.dumps(policy['content'])
        }}
        self._stub_bifurcator(
            'create_policy', expected_parameters, response, error_code=error_code)

    def stub_list_policies(self, policy_filter, policies, error_code=None):
        expected_parameters = {'Filter': policy_filter}
        response = {'Policies': [self._make_policy_summary(pol) for pol in policies]}
        self._stub_bifurcator(
            'list_policies', expected_parameters, response, error_code=error_code)

    def stub_describe_policy(self, policy, error_code=None):
        expected_parameters = {'PolicyId': policy['id']}
        response = {'Policy': {
            'PolicySummary': self._make_policy_summary(policy),
            'Content': json.dumps(policy['content'])
        }}
        self._stub_bifurcator(
            'describe_policy', expected_parameters, response, error_code=error_code)

    def stub_attach_policy(self, policy_id, target_id, error_code=None):
        expected_parameters = {'PolicyId': policy_id, 'TargetId': target_id}
        self._stub_bifurcator(
            'attach_policy', expected_parameters, error_code=error_code)

    def stub_detach_policy(self, policy_id, target_id, error_code=None):
        expected_parameters = {'PolicyId': policy_id, 'TargetId': target_id}
        self._stub_bifurcator(
            'detach_policy', expected_parameters, error_code=error_code)

    def stub_delete_policy(self, policy_id, error_code=None):
        expected_parameters = {'PolicyId': policy_id}
        self._stub_bifurcator(
            'delete_policy', expected_parameters, error_code=error_code)
