# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon EventBridge unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class EventBridgeStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon EventBridge unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 EventBridge client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_put_rule(
            self, event_rule_name, event_schedule, event_rule_arn, error_code=None):
        expected_params = {
            'Name': event_rule_name, 'ScheduleExpression': event_schedule}
        response = {'RuleArn': event_rule_arn}
        self._stub_bifurcator(
            'put_rule', expected_params, response, error_code=error_code)

    def stub_put_targets(
            self, event_rule_name, targets, failed_count=0, error_code=None):
        expected_params = {'Rule': event_rule_name, 'Targets': targets}
        response = {'FailedEntryCount': failed_count}
        self._stub_bifurcator(
            'put_targets', expected_params, response, error_code=error_code)

    def stub_enable_rule(self, event_rule_name, error_code=None):
        expected_params = {'Name': event_rule_name}
        self._stub_bifurcator(
            'enable_rule', expected_params, error_code=error_code)

    def stub_disable_rule(self, event_rule_name, error_code=None):
        expected_params = {'Name': event_rule_name}
        self._stub_bifurcator(
            'disable_rule', expected_params, error_code=error_code)

    def stub_describe_rule(self, event_rule_name, state, error_code=None):
        expected_params = {'Name': event_rule_name}
        response = {'State': state}
        self._stub_bifurcator(
            'describe_rule', expected_params, response, error_code=error_code)

    def stub_remove_targets(self, event_rule_name, target_ids, error_code=None):
        expected_params = {'Rule': event_rule_name, 'Ids': target_ids}
        self._stub_bifurcator(
            'remove_targets', expected_params, error_code=error_code)

    def stub_delete_rule(self, event_rule_name, error_code=None):
        expected_params = {'Name': event_rule_name}
        self._stub_bifurcator(
            'delete_rule', expected_params, error_code=error_code)
