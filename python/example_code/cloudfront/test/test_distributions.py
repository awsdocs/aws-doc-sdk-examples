# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for distributions.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from distributions import CloudFrontWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_distributions(make_stubber, error_code):
    cloudfront_client = boto3.client('cloudfront')
    cloudfront_stubber = make_stubber(cloudfront_client)
    cloudfront = CloudFrontWrapper(cloudfront_client)
    distribs = [{
        'name': f'distrib-name-{index}',
        'id': f'distrib-{index}',
        'cert_source': 'acm',
        'cert': "Hi, I'm a certificate!"
    } for index in range(3)]

    cloudfront_stubber.stub_list_distributions(distribs, error_code=error_code)

    if error_code is None:
        cloudfront.list_distributions()
    else:
        with pytest.raises(ClientError) as exc_info:
            cloudfront.list_distributions()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_distribution(make_stubber, monkeypatch, error_code):
    cloudfront_client = boto3.client('cloudfront')
    cloudfront_stubber = make_stubber(cloudfront_client)
    cloudfront = CloudFrontWrapper(cloudfront_client)
    distrib_id = 'test-id'
    comment = 'Test comment.'
    etag = 'etag'

    inputs = ['test-id', comment]
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    cloudfront_stubber.stub_get_distribution_config(distrib_id, comment, etag)
    cloudfront_stubber.stub_update_distribution(
        distrib_id, comment, etag, error_code=error_code)

    if error_code is None:
        cloudfront.update_distribution()
    else:
        with pytest.raises(ClientError) as exc_info:
            cloudfront.update_distribution()
        assert exc_info.value.response['Error']['Code'] == error_code
