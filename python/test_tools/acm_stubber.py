# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Certificate Manager (ACM) unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class AcmStubber(ExampleStubber):
    """
    A class that implements stub functions used by ACM unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 ACM client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_describe_certificate(self, certificate_arn, certificate, error_code=None):
        expected_params = {'CertificateArn': certificate_arn}
        response = {'Certificate': certificate}
        self._stub_bifurcator(
            'describe_certificate', expected_params, response, error_code=error_code)

    def stub_get_certificate(self, certificate_arn, cert_data, error_code=None):
        expected_params = {'CertificateArn': certificate_arn}
        response = cert_data
        self._stub_bifurcator(
            'get_certificate', expected_params, response, error_code=error_code)

    def stub_list_certificates(
            self, max_items, certificates, statuses=None, key_usage=None,
            extended_key_usage=None, key_types=None, error_code=None):
        expected_params = {'MaxItems': max_items}
        if statuses is not None:
            expected_params['CertificateStatuses'] = statuses
        includes = {}
        if key_usage is not None:
            includes['keyUsage'] = key_usage
        if extended_key_usage is not None:
            includes['extendedKeyUsage'] = extended_key_usage
        if key_types is not None:
            includes['keyTypes'] = key_types
        if includes:
            expected_params['Includes'] = includes
        response = {'CertificateSummaryList': certificates}
        self._stub_bifurcator(
            'list_certificates', expected_params, response, error_code=error_code)

    def stub_import_certificate(
            self, certificate, private_key, certificate_arn, error_code=None):
        expected_params = {'Certificate': certificate, 'PrivateKey': private_key}
        response = {'CertificateArn': certificate_arn}
        self._stub_bifurcator(
            'import_certificate', expected_params, response, error_code=error_code)

    def stub_delete_certificate(self, certificate_arn, error_code=None):
        expected_params = {'CertificateArn': certificate_arn}
        response = {}
        self._stub_bifurcator(
            'delete_certificate', expected_params, response, error_code=error_code)


    def stub_add_tags_to_certificate(self, certificate_arn, tags, error_code=None):
        expected_params = {
            'CertificateArn': certificate_arn,
            'Tags': [{'Key': key, 'Value': value} for key, value in tags.items()]}
        response = {}
        self._stub_bifurcator(
            'add_tags_to_certificate', expected_params, response, error_code=error_code)

    def stub_list_tags_for_certificate(self, certificate_arn, tags, error_code=None):
        expected_params = {'CertificateArn': certificate_arn}
        response = {
            'Tags': [{'Key': key, 'Value': value} for key, value in tags.items()]}
        self._stub_bifurcator(
            'list_tags_for_certificate', expected_params, response,
            error_code=error_code)

    def stub_remove_tags_from_certificate(self, certificate_arn, tags, error_code=None):
        expected_params = {'CertificateArn': certificate_arn}
        tag_list = []
        for key, value in tags.items():
            tag = {'Key': key}
            if value is not None:
                tag['Value'] = value
            tag_list.append(tag)
        if tag_list:
            expected_params['Tags'] = tag_list
        response = {}
        self._stub_bifurcator(
            'remove_tags_from_certificate', expected_params, response,
            error_code=error_code)

    def stub_request_certificate(
            self, domain, alternate_domains, method, certificate_arn,
            validation_domains=None, error_code=None):
        expected_params = {
            'DomainName': domain,
            'ValidationMethod': method,
            'SubjectAlternativeNames': alternate_domains}
        if validation_domains is not None:
            expected_params['DomainValidationOptions'] = [{
                'DomainName': key,
                'ValidationDomain': value
            } for key, value in validation_domains.items()]
        response = {'CertificateArn': certificate_arn}
        self._stub_bifurcator(
            'request_certificate', expected_params, response, error_code=error_code)

    def stub_resend_validation_email(
            self, certificate_arn, domain, validation_domain, error_code=None):
        expected_params = {
            'CertificateArn': certificate_arn,
            'Domain': domain,
            'ValidationDomain': validation_domain}
        response = {}
        self._stub_bifurcator(
            'resend_validation_email', expected_params, response, error_code=error_code)
