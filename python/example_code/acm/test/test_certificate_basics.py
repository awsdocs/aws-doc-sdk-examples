# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for certificate_basics.py
"""

import uuid

import boto3
from botocore.exceptions import ClientError
import pytest

from certificate_basics import AcmCertificate


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    certificate = {'DomainName': 'test.example.com'}

    acm_stubber.stub_describe_certificate(
        certificate_arn, certificate, error_code=error_code)

    if error_code is None:
        got_certificate = acm_certificate.describe(certificate_arn)
        assert got_certificate == certificate
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.describe(certificate_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    cert_data = {
        'Certificate': 'test-certificate-data',
        'CertificateChain': 'test-certificate-chain'
    }

    acm_stubber.stub_get_certificate(certificate_arn, cert_data, error_code=error_code)

    if error_code is None:
        got_cert_data = acm_certificate.get(certificate_arn)
        assert got_cert_data == cert_data
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.get(certificate_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('statuses,includes,error_code', [
    (None,
     {'key_usage': ['KEY_ENCIPHERMENT', 'CRL_SIGNING'],
      'extended_key_usage': None,
      'key_types': None},
     None),
    (None,
     {'key_usage': ['KEY_ENCIPHERMENT', 'CRL_SIGNING'],
      'extended_key_usage': None,
      'key_types': None},
     'TestException'),
    (['INACTIVE', 'FAILED'],
     {'key_usage': ['KEY_ENCIPHERMENT', 'CRL_SIGNING'],
      'extended_key_usage': None,
      'key_types': None},
     None),
    (None,
     {'key_usage': None,
      'extended_key_usage': ['CODE_SIGNING', 'TIME_STAMPING'],
      'key_types': None},
     None),
    (None,
     {'key_usage': None,
      'extended_key_usage': None,
      'key_types': ['RSA_1024', 'EC_secp384r1']},
     None),
    (['INACTIVE', 'FAILED'],
     {'key_usage': ['KEY_ENCIPHERMENT', 'CRL_SIGNING'],
      'extended_key_usage': ['CODE_SIGNING', 'TIME_STAMPING'],
      'key_types': ['RSA_1024', 'EC_secp384r1']},
     None),
])
def test_list(make_stubber, statuses, includes, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificates = [{
        'CertificateArn':
            f'arn:aws:acm:us-west-2:123456789012:certificate/example-{index}',
        'DomainName': f'example-{index}.com'
    } for index in range(3)]
    max_items = 10

    acm_stubber.stub_list_certificates(
        max_items, certificates, statuses, **includes, error_code=error_code)

    if error_code is None:
        got_certificates = acm_certificate.list(max_items, statuses, **includes)
        assert got_certificates == certificates
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.list(max_items, statuses, **includes)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_import_certificate(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate = b'Test certificate'
    private_key = b'Test private key'
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'

    acm_stubber.stub_import_certificate(
        certificate, private_key, certificate_arn, error_code=error_code)

    if error_code is None:
        got_certificate_arn = acm_certificate.import_certificate(
            certificate, private_key)
        assert got_certificate_arn == certificate_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.import_certificate(
                certificate, private_key)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_remove(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'

    acm_stubber.stub_delete_certificate(certificate_arn, error_code=error_code)

    if error_code is None:
        acm_certificate.remove(certificate_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.remove(certificate_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_tags(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    tags = {f'key-{index}': f'value-{index}' for index in range(3)}

    acm_stubber.stub_add_tags_to_certificate(
        certificate_arn, tags, error_code=error_code)

    if error_code is None:
        acm_certificate.add_tags(certificate_arn, tags)
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.add_tags(certificate_arn, tags)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_tags(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    tags = {f'key-{index}': f'value-{index}' for index in range(3)}

    acm_stubber.stub_list_tags_for_certificate(
        certificate_arn, tags, error_code=error_code)

    if error_code is None:
        got_tags = acm_certificate.list_tags(certificate_arn)
        assert got_tags == tags
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.list_tags(certificate_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_remove_tags(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    tags = {'key-1': 'value-1', 'key-2': None, 'key-3': 'value-3', 'key-4': None}

    acm_stubber.stub_remove_tags_from_certificate(
        certificate_arn, tags, error_code=error_code)

    if error_code is None:
        acm_certificate.remove_tags(certificate_arn, tags)
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.remove_tags(certificate_arn, tags)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('use_options,error_code', [
    (False, None),
    (False, 'TestException'),
    (True, None)])
def test_request_validation(make_stubber, use_options, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    domain = 'example.com'
    alternate_domains = ['*.test.example.com', '*.dev.example.com']
    method = 'DNS'
    validation_domains = (
        {f'sub{index}.example.com': 'example.com' for index in range(3)}
        if use_options else None)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'

    acm_stubber.stub_request_certificate(
        domain, alternate_domains, method, certificate_arn, validation_domains,
        error_code=error_code)

    if error_code is None:
        got_certificate_arn = acm_certificate.request_validation(
            domain, alternate_domains, method, validation_domains)
        assert got_certificate_arn == certificate_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.request_validation(
                domain, alternate_domains, method, validation_domains)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_resend_validation_email(make_stubber, error_code):
    acm_client = boto3.client('acm')
    acm_stubber = make_stubber(acm_client)
    acm_certificate = AcmCertificate(acm_client)
    certificate_arn = f'arn:aws:acm:us-west-2:123456789012:certificate/{uuid.uuid4()}'
    domain = 'test.example.com'
    validation_domain = 'example.com'

    acm_stubber.stub_resend_validation_email(
        certificate_arn, domain, validation_domain, error_code=error_code)

    if error_code is None:
        acm_certificate.resend_validation_email(
            certificate_arn, domain, validation_domain)
    else:
        with pytest.raises(ClientError) as exc_info:
            acm_certificate.resend_validation_email(
                certificate_arn, domain, validation_domain)
        assert exc_info.value.response['Error']['Code'] == error_code
