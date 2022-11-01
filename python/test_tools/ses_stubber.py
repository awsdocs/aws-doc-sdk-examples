# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Simple Email Service (Amazon SES) unit tests.
"""

import json
from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class SesStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon SES unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Rekognition client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_verify_domain_identity(self, domain_name, token, error_code=None):
        expected_params = {'Domain': domain_name}
        response = {'VerificationToken': token}
        self._stub_bifurcator(
            'verify_domain_identity', expected_params, response, error_code=error_code)

    def stub_verify_email_identity(self, email_address, error_code=None):
        expected_params = {'EmailAddress': email_address}
        self._stub_bifurcator(
            'verify_email_identity', expected_params, error_code=error_code)

    def stub_get_identity_verification_attributes(
            self, identities, statuses, error_code=None):
        expected_params = {'Identities': identities}
        response = {'VerificationAttributes': {
            ident: {'VerificationStatus': status}
        } for ident, status in zip(identities, statuses)}
        self._stub_bifurcator(
            'get_identity_verification_attributes', expected_params, response,
            error_code=error_code)

    def stub_delete_identity(self, identity, error_code=None):
        expected_params = {'Identity': identity}
        self._stub_bifurcator(
            'delete_identity', expected_params, error_code=error_code)

    def stub_list_identities(
            self, identity_type, max_items, identities, error_code=None):
        expected_params = {}
        if identity_type is not None:
            expected_params['IdentityType'] = identity_type
        if max_items is not None:
            expected_params['MaxItems'] = max_items
        response = {'Identities': identities}
        self._stub_bifurcator(
            'list_identities', expected_params, response, error_code=error_code)

    def stub_create_receipt_filter(
            self, filter_name, ip_address_or_range, allow, error_code=None):
        expected_params = {
            'Filter': {
                'Name': filter_name,
                'IpFilter': {
                    'Cidr': ip_address_or_range,
                    'Policy': 'Allow' if allow else 'Block'}}}
        self._stub_bifurcator(
            'create_receipt_filter', expected_params, error_code=error_code)

    def stub_list_receipt_filters(self, filters, error_code=None):
        response = {'Filters': filters}
        self._stub_bifurcator(
            'list_receipt_filters', response=response, error_code=error_code)

    def stub_delete_receipt_filter(self, filter_name, error_code=None):
        expected_params = {'FilterName': filter_name}
        self._stub_bifurcator(
            'delete_receipt_filter', expected_params, error_code=error_code)

    def stub_create_receipt_rule_set(self, rule_set_name, error_code=None):
        expected_params = {'RuleSetName': rule_set_name}
        self._stub_bifurcator(
            'create_receipt_rule_set', expected_params, error_code=error_code)

    def stub_create_receipt_rule(
            self, rule_set_name, rule_name, recipients, actions, error_code=None):
        expected_params = {
            'RuleSetName': rule_set_name,
            'Rule': {
                'Name': rule_name,
                'Enabled': True,
                'Recipients': recipients,
                'Actions': actions
            }}
        self._stub_bifurcator(
            'create_receipt_rule', expected_params, error_code=error_code)

    def stub_describe_receipt_rule_set(
            self, rule_set_name, rule_name, recipients, actions, error_code=None):
        expected_params = {'RuleSetName': rule_set_name}
        response = {
            'Metadata': {'Name': rule_set_name},
            'Rules': [{
                'Name': rule_name,
                'Enabled': True,
                'Recipients': recipients,
                'Actions': actions
            }]
        }
        self._stub_bifurcator(
            'describe_receipt_rule_set', expected_params, response,
            error_code=error_code)

    def stub_delete_receipt_rule(self, rule_set_name, rule_name, error_code=None):
        expected_params = {'RuleSetName': rule_set_name, 'RuleName': rule_name}
        self._stub_bifurcator(
            'delete_receipt_rule', expected_params, error_code=error_code)

    def stub_delete_receipt_rule_set(self, rule_set_name, error_code=None):
        expected_params = {'RuleSetName': rule_set_name}
        self._stub_bifurcator(
            'delete_receipt_rule_set', expected_params, error_code=error_code)

    def stub_create_template(self, name, subject, text, html, error_code=None):
        expected_params = {
            'Template': {
                'TemplateName': name,
                'SubjectPart': subject,
                'TextPart': text,
                'HtmlPart': html}}
        self._stub_bifurcator(
            'create_template', expected_params, error_code=error_code)

    def stub_delete_template(self, name, error_code=None):
        expected_params = {'TemplateName': name}
        self._stub_bifurcator(
            'delete_template', expected_params, error_code=error_code)

    def stub_get_template(self, name, subject, text, html, error_code=None):
        expected_params = {'TemplateName': name}
        response = {
            'Template': {
                'TemplateName': name,
                'SubjectPart': subject,
                'TextPart': text,
                'HtmlPart': html}}
        self._stub_bifurcator(
            'get_template', expected_params, response, error_code=error_code)

    def stub_list_templates(self, names, error_code=None):
        response = {'TemplatesMetadata': [{'Name': name} for name in names]}
        self._stub_bifurcator(
            'list_templates', response=response, error_code=error_code)

    def stub_update_template(self, name, subject, text, html, error_code=None):
        expected_params = {
            'Template': {
                'TemplateName': name,
                'SubjectPart': subject,
                'TextPart': text,
                'HtmlPart': html}}
        self._stub_bifurcator(
            'update_template', expected_params, error_code=error_code)

    def stub_send_email(
            self, source, destination, subject, text, html, message_id, reply_tos=None,
            error_code=None):
        expected_params = {
            'Source': source,
            'Destination': destination,
            'Message': {
                'Subject': {'Data': subject},
                'Body': {'Text': {'Data': text}, 'Html': {'Data': html}}}}
        if reply_tos is not None:
            expected_params['ReplyToAddresses'] = reply_tos
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_email', expected_params, response, error_code=error_code)

    def stub_send_raw_email(self, source, destinations, message_id, msg=ANY, error_code=None):
        expected_params = {
            'Source': source, 'Destinations': destinations, 'RawMessage': {'Data': msg}}
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_raw_email', expected_params, response, error_code=error_code)

    def stub_send_templated_email(
            self, source, destination, template_name, template_data, message_id,
            reply_tos=None, error_code=None):
        expected_params = {
            'Source': source,
            'Destination': destination,
            'Template': template_name,
            'TemplateData': json.dumps(template_data)}
        if reply_tos is not None:
            expected_params['ReplyToAddresses'] = reply_tos
        response = {'MessageId': message_id}
        self._stub_bifurcator(
            'send_templated_email', expected_params, response, error_code=error_code)

    def stub_verify_domain_dkim(self, domain, tokens, error_code=None):
        expected_params = {'Domain': domain}
        response = {'DkimTokens': tokens}
        self._stub_bifurcator(
            'verify_domain_dkim', expected_params, response, error_code=error_code)

    def stub_set_identity_notification_topic(
            self, identity, topic, topic_arn, error_code=None):
        expected_params = {
            'Identity': identity, 'NotificationType': topic, 'SnsTopic': topic_arn}
        self._stub_bifurcator(
            'set_identity_notification_topic', expected_params, error_code=error_code)
