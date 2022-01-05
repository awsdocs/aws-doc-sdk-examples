# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Pinpoint unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from test_tools.example_stubber import ExampleStubber


class PinpointStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Pinpoint unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Pinpoint client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_app(self, name):
        self.add_response(
            'create_app',
            expected_params={'CreateApplicationRequest': {'Name': name}},
            service_response={
                'ApplicationResponse': {
                    'Arn': 'arn:aws:mobiletargeting:us-west-2:111122223333:apps/d41d8cd98f00b204e9800998ecf8427e',
                    'Id': 'd41d8cd98f00b204e9800998ecf8427e',
                    'Name': name
                }
            }
        )

    def stub_create_app_error(self, name, error_code):
        self.add_client_error(
            'create_app',
            expected_params={'CreateApplicationRequest': {'Name': name}},
            service_error_code=error_code
        )

    def stub_get_apps(self, apps):
        self.add_response(
            'get_apps',
            expected_params={},
            service_response={'ApplicationsResponse': {'Item': apps}}
        )

    def stub_get_apps_error(self, error_code):
        self.add_client_error(
            'get_apps',
            expected_params={},
            service_error_code=error_code
        )

    def stub_delete_app(self, app):
        self.add_response(
            'delete_app',
            expected_params={'ApplicationId': app['Id']},
            service_response={'ApplicationResponse': app}
        )

    def stub_delete_app_error(self, app, error_code):
        self.add_client_error(
            'delete_app',
            expected_params={'ApplicationId': app['Id']},
            service_error_code=error_code
        )

    def stub_send_email_messages(
            self, app_id, sender, to_addresses, char_set, subject,
            html_message, text_message, message_ids, error_code=None):
        expected_params = {
            'ApplicationId': app_id,
            'MessageRequest': {
                'Addresses': {
                    to_address: {'ChannelType': 'EMAIL'} for to_address in to_addresses
                },
                'MessageConfiguration': {
                    'EmailMessage': {
                        'FromAddress': sender,
                        'SimpleEmail': {
                            'Subject': {'Charset': char_set, 'Data': subject},
                            'HtmlPart': {'Charset': char_set, 'Data': html_message},
                            'TextPart': {'Charset': char_set, 'Data': text_message}}}}}}
        response = {
            'MessageResponse': {
                'ApplicationId': app_id,
                'Result': {
                    to_address: {
                        'MessageId': message_id,
                        'DeliveryStatus': 'SUCCESSFUL',
                        'StatusCode': 200
                    }
                    for to_address, message_id in zip(to_addresses, message_ids)
                }
            }
        }
        self._stub_bifurcator(
            'send_messages', expected_params, response, error_code=error_code)

    def stub_send_templated_email_messages(
            self, app_id, sender, to_addresses, template_name, template_version,
            message_ids, error_code=None):
        expected_params = {
            'ApplicationId': app_id,
            'MessageRequest': {
                'Addresses': {
                    to_address: {'ChannelType': 'EMAIL'} for to_address in to_addresses
                },
                'MessageConfiguration': {'EmailMessage': {'FromAddress': sender}},
                'TemplateConfiguration': {
                    'EmailTemplate': {
                        'Name': template_name, 'Version': template_version}}}}
        response = {
            'MessageResponse': {
                'ApplicationId': app_id,
                'Result': {
                    to_address: {
                        'MessageId': message_id,
                        'DeliveryStatus': 'SUCCESSFUL',
                        'StatusCode': 200
                    }
                    for to_address, message_id in zip(to_addresses, message_ids)
                }
            }
        }
        self._stub_bifurcator(
            'send_messages', expected_params, response, error_code=error_code)

    def stub_send_sms_message(
            self, app_id, origination_number, destination_number, message, message_type,
            message_id, error_code=None):
        expected_params = {
            'ApplicationId': app_id,
            'MessageRequest': {
                'Addresses': {destination_number: {'ChannelType': 'SMS'}},
                'MessageConfiguration': {
                    'SMSMessage': {
                        'Body': message,
                        'MessageType': message_type,
                        'OriginationNumber': origination_number}}}}
        response = {'MessageResponse': {
            'ApplicationId': app_id,
            'Result': {
                destination_number: {
                    'DeliveryStatus': 'SUCCESSFUL',
                    'StatusCode': 200,
                    'MessageId': message_id}}}}
        self._stub_bifurcator(
            'send_messages', expected_params, response, error_code=error_code)

    def stub_send_templated_sms_message(
            self, app_id, origination_number, destination_number, message_type,
            template_name, template_version, message_id, error_code=None):
        expected_params = {
            'ApplicationId': app_id,
            'MessageRequest': {
                'Addresses': {destination_number: {'ChannelType': 'SMS'}},
                'MessageConfiguration': {
                    'SMSMessage': {
                        'MessageType': message_type,
                        'OriginationNumber': origination_number}},
                'TemplateConfiguration': {
                    'SMSTemplate': {
                        'Name': template_name, 'Version': template_version}}}}
        response = {'MessageResponse': {
            'ApplicationId': app_id,
            'Result': {
                destination_number: {
                    'DeliveryStatus': 'SUCCESSFUL',
                    'StatusCode': 200,
                    'MessageId': message_id}}}}
        self._stub_bifurcator(
            'send_messages', expected_params, response, error_code=error_code)
