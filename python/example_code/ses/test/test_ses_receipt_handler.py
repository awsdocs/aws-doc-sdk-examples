# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for ses_receipt_handler.py.
"""

import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

from ses_receipt_handler import SesReceiptHandler


@pytest.mark.parametrize("allow,error_code", [
    (True, None),
    (False, None),
    (True, 'TestException')])
def test_create_receipt_filter(make_stubber, allow, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    filter_name = 'test-filter'
    ip_address_or_range = '0.0.0.0'

    ses_stubber.stub_create_receipt_filter(
        filter_name, ip_address_or_range, allow, error_code=error_code)

    if error_code is None:
        ses_receipt.create_receipt_filter(filter_name, ip_address_or_range, allow)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.create_receipt_filter(filter_name, ip_address_or_range, allow)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_list_receipt_filters(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    filters = [{
        'Name': f'test_filter-{index}',
        'IpFilter': {'Cidr': '0.0.0.0', 'Policy': 'Allow'}
    } for index in range(3)]

    ses_stubber.stub_list_receipt_filters(filters, error_code=error_code)

    if error_code is None:
        got_filters = ses_receipt.list_receipt_filters()
        assert got_filters == filters
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.list_receipt_filters()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_receipt_filter(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    filter_name = 'test-filter'

    ses_stubber.stub_delete_receipt_filter(filter_name, error_code=error_code)

    if error_code is None:
        ses_receipt.delete_receipt_filter(filter_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.delete_receipt_filter(filter_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_create_receipt_rule_set(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    rule_set_name = 'test-rule-set'

    ses_stubber.stub_create_receipt_rule_set(rule_set_name, error_code=error_code)

    if error_code is None:
        ses_receipt.create_receipt_rule_set(rule_set_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.create_receipt_rule_set(rule_set_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code,stop_on_method", [
    (None, None),
    ('TestException', 'stub_create_bucket'),
    ('TestException', 'stub_put_bucket_policy')])
def test_create_bucket_for_copy(
        make_stubber, stub_runner, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    ses_receipt = SesReceiptHandler(None, s3_resource)
    bucket_name = 'doc-example-bucket'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            s3_stubber.stub_create_bucket, bucket_name,
            region_name=s3_resource.meta.client.meta.region_name)
        runner.add(s3_stubber.stub_head_bucket, bucket_name)
        runner.add(s3_stubber.stub_put_bucket_policy, bucket_name, ANY)

    if stop_on_method == 'stub_put_bucket_policy':
        s3_stubber.stub_delete_bucket(bucket_name)

    if error_code is None:
        got_bucket = ses_receipt.create_bucket_for_copy(bucket_name)
        assert got_bucket.name == bucket_name
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.create_bucket_for_copy(bucket_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_create_s3_copy_rule(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    rule_set_name = 'test-rule-set'
    rule_name = 'test-rule'
    recipients = ['me', 'myself', 'I']
    bucket_name = 'doc-example-bucket'
    prefix = 'mymails/'
    actions = [{
        'S3Action': {'BucketName': bucket_name, 'ObjectKeyPrefix': prefix}
    }]

    ses_stubber.stub_create_receipt_rule(
        rule_set_name, rule_name, recipients, actions, error_code=error_code)

    if error_code is None:
        ses_receipt.create_s3_copy_rule(
            rule_set_name, rule_name, recipients, bucket_name, prefix)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.create_s3_copy_rule(
                rule_set_name, rule_name, recipients, bucket_name, prefix)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_describe_receipt_rule_set(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    rule_set_name = 'test-rule-set'
    rule_name = 'test-rule'
    recipients = ['me', 'myself', 'I']
    bucket_name = 'doc-example-bucket'
    prefix = 'mymails/'
    actions = [{
        'S3Action': {'BucketName': bucket_name, 'ObjectKeyPrefix': prefix}
    }]

    ses_stubber.stub_describe_receipt_rule_set(
        rule_set_name, rule_name, recipients, actions, error_code=error_code)

    if error_code is None:
        response = ses_receipt.describe_receipt_rule_set(rule_set_name)
        assert response['Metadata']['Name'] == rule_set_name
        rule = response['Rules'][0]
        assert rule['Name'] == rule_name
        assert rule['Recipients'] == recipients
        assert rule['Actions'] == actions
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.describe_receipt_rule_set(rule_set_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_receipt_rule(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    rule_set_name = 'test-rule-set'
    rule_name = 'test-rule'

    ses_stubber.stub_delete_receipt_rule(
        rule_set_name, rule_name, error_code=error_code)

    if error_code is None:
        ses_receipt.delete_receipt_rule(rule_set_name, rule_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.delete_receipt_rule(rule_set_name, rule_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_receipt_rule_set(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_receipt = SesReceiptHandler(ses_client, None)
    rule_set_name = 'test-rule-set'

    ses_stubber.stub_delete_receipt_rule_set(rule_set_name, error_code=error_code)

    if error_code is None:
        ses_receipt.delete_receipt_rule_set(rule_set_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_receipt.delete_receipt_rule_set(rule_set_name)
        assert exc_info.value.response['Error']['Code'] == error_code
