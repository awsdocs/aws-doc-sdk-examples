# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon SNS unit tests.
"""

import json
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

    def stub_list_topics(self, topic_arns, error_code=None):
        expected_params = {}
        response = {'Topics': [{'TopicArn': arn} for arn in topic_arns]}
        self._stub_bifurcator(
            'list_topics', expected_params, response, error_code=error_code)

    def stub_subscribe(
            self, topic_arn, protocol, endpoint, subscription_arn, return_arn=False,
            error_code=None):
        expected_params = {
            'TopicArn': topic_arn, 'Protocol': protocol, 'Endpoint': endpoint}
        if return_arn:
            expected_params['ReturnSubscriptionArn'] = True
        response = {'SubscriptionArn': subscription_arn}
        self._stub_bifurcator(
            'subscribe', expected_params, response, error_code=error_code)
        
    def stub_set_subscription_attributes(
            self, subscription_arn, attribute_name, attribute_value, error_code=None):
        expected_params = {
            'SubscriptionArn': subscription_arn,
            'AttributeName': attribute_name,
            'AttributeValue': json.dumps(attribute_value)}
        response = {}
        self._stub_bifurcator(
            'set_subscription_attributes', expected_params, response,
            error_code=error_code)

    def stub_delete_topic(self, topic_arn, error_code=None):
        expected_params = {'TopicArn': topic_arn}
        self._stub_bifurcator(
            'delete_topic', expected_params, error_code=error_code)

    def stub_list_subscriptions(self, sub_arns, error_code=None):
        expected_params = {}
        response = {'Subscriptions': [{'SubscriptionArn': arn} for arn in sub_arns]}
        self._stub_bifurcator(
            'list_subscriptions', expected_params, response, error_code=error_code)

    def stub_list_subscriptions_by_topic(self, topic_arn, sub_arns, error_code=None):
        expected_params = {'TopicArn': topic_arn}
        response = {'Subscriptions': [{'SubscriptionArn': arn} for arn in sub_arns]}
        self._stub_bifurcator(
            'list_subscriptions_by_topic', expected_params, response,
            error_code=error_code)

    def stub_unsubscribe(self, subscription_arn, error_code=None):
        expected_params = {'SubscriptionArn': subscription_arn}
        response = {}
        self._stub_bifurcator(
            'unsubscribe', expected_params, response, error_code=error_code)

    def stub_publish(
            self, message, message_id, topic_arn=None, phone_number=None, subject=None,
            message_structure=None, message_attributes=None, error_code=None):
        expected_params = {'Message': message}
        if topic_arn is not None:
            expected_params['TopicArn'] = topic_arn
        if phone_number is not None:
            expected_params['PhoneNumber'] = phone_number
        if subject is not None:
            expected_params['Subject'] = subject
        if message_structure is not None:
            expected_params['MessageStructure'] = message_structure
        if message_attributes is not None:
            att_dict = {}
            for key, value in message_attributes.items():
                if isinstance(value, str):
                    att_dict[key] = {'DataType': 'String', 'StringValue': value}
                elif isinstance(value, bytes):
                    att_dict[key] = {'DataType': 'Binary', 'BinaryValue': value}
            expected_params['MessageAttributes'] = att_dict
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'publish', expected_params, response, error_code=error_code)
