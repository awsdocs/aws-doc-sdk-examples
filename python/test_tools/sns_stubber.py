# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon SNS unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class SnsStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon SNS unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 SNS client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_topic(self, topic_name, topic_arn, error_code=None):
        expected_params = {'Name': topic_name}
        response = {f'TopicArn': topic_arn}
        self._stub_bifurcator(
            'create_topic', expected_params, response, error_code=error_code)

    def stub_subscribe(
            self, topic_arn, protocol, endpoint, subscription_arn, error_code=None):
        expected_params = {
            'TopicArn': topic_arn, 'Protocol': protocol, 'Endpoint': endpoint}
        response = {'SubscriptionArn': subscription_arn}
        self._stub_bifurcator(
            'subscribe', expected_params, response, error_code=error_code)

    def stub_delete_topic(self, topic_arn, error_code=None):
        expected_params = {'TopicArn': topic_arn}
        self._stub_bifurcator(
            'delete_topic', expected_params, error_code=error_code)
