# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bucket_wrapper.py functions.
"""

from urllib.parse import urlparse
import pytest

import boto3
from botocore.exceptions import ClientError

from bucket_wrapper import BucketWrapper


@pytest.mark.parametrize('region, error_code', [
    (None, None),
    ('eu-west-1', None),
    (None, 'TestException')])
def test_create(make_stubber, region, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    stub_region = region if region is not None else s3_resource.meta.client.meta.region_name
    s3_stubber.stub_create_bucket(bucket_name, stub_region, error_code=error_code)
    if error_code is None:
        s3_stubber.stub_head_bucket(bucket_name)

    if error_code is None:
        wrapper.create(region)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create(region)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_exists(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_head_bucket(bucket_name, error_code=error_code)

    got_exists = wrapper.exists()
    if error_code is None:
        assert got_exists
    else:
        assert not got_exists


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    created_buckets = [
        s3_resource.Bucket(f'{bucket_name}-{ind}') for ind in range(0, 5)]

    s3_stubber.stub_list_buckets(created_buckets, error_code=error_code)

    if error_code is None:
        got_buckets = wrapper.list(s3_resource)
        assert got_buckets == created_buckets
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list(s3_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_delete_bucket(bucket_name, error_code=error_code)
    if error_code is None:
        s3_stubber.stub_head_bucket(bucket_name, 404)

    if error_code is None:
        wrapper.delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_grant_log_delivery_access(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_get_bucket_acl(bucket_name)
    s3_stubber.stub_put_bucket_acl(bucket_name, error_code=error_code)

    if error_code is None:
        wrapper.grant_log_delivery_access()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.grant_log_delivery_access()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_acl(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_get_bucket_acl(bucket_name, ['owner'], error_code=error_code)

    if error_code is None:
        got_acl = wrapper.get_acl()
        assert len(got_acl.grants) == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_acl()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_cors(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    cors_rules = [{
        'AllowedOrigins': ['http://www.example.com'],
        'AllowedMethods': ['PUT', 'POST', 'DELETE'],
        'AllowedHeaders': ['*']
    }]

    s3_stubber.stub_put_bucket_cors(bucket_name, cors_rules, error_code=error_code)

    if error_code is None:
        wrapper.put_cors(cors_rules)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_cors(cors_rules)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_cors(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    cors_rules = [{
        'AllowedOrigins': ['http://www.example.com'],
        'AllowedMethods': ['PUT', 'POST', 'DELETE'],
        'AllowedHeaders': ['*']
    }]

    s3_stubber.stub_get_bucket_cors(bucket_name, cors_rules, error_code=error_code)

    if error_code is None:
        got_rules = wrapper.get_cors()
        assert got_rules.cors_rules == cors_rules
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_cors()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_cors(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_delete_bucket_cors(bucket_name, error_code=error_code)

    if error_code is None:
        wrapper.delete_cors()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_cors()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_policy(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    policy = {
        'Version': '2012-10-17',
        'Id': 'test-policy',
        'Statement': [{
            'Effect': 'Allow',
            'Principal': {'AWS': 'arn:aws:iam::111122223333:user/Martha'},
            'Action': [
                's3:GetObject',
                's3:ListBucket'
            ],
            'Resource': [
                f'arn:aws:s3:::{bucket_name}/*',
                f'arn:aws:s3:::{bucket_name}'
            ]
        }]
    }

    s3_stubber.stub_put_bucket_policy(bucket_name, error_code=error_code)

    if error_code is None:
        wrapper.put_policy(policy)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_policy(policy)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_policy(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    policy = {
        'Version': '2012-10-17',
        'Id': 'test-policy',
        'Statement': [{
            'Effect': 'Allow',
            'Principal': {'AWS': 'arn:aws:iam::111122223333:user/Martha'},
            'Action': [
                's3:GetObject',
                's3:ListBucket'
            ],
            'Resource': [
                f'arn:aws:s3:::{bucket_name}/*',
                f'arn:aws:s3:::{bucket_name}'
            ]
        }]
    }

    s3_stubber.stub_get_bucket_policy(bucket_name, policy, error_code=error_code)

    if error_code is None:
        got_policy = wrapper.get_policy()
        assert got_policy == policy
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_policy()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_policy(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_delete_bucket_policy(bucket_name, error_code=error_code)

    if error_code is None:
        wrapper.delete_policy()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_policy()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_lifecycle_configuration(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    rules = [{
        'ID': 'test-id',
        'Filter': {
            'And': {
                'Prefix': 'monsters/',
                'Tags': [{'Key': 'type', 'Value': 'frankenstein'}]
            }
        },
        'Status': 'Enabled',
        'Transitions': [{'Days': 365, 'StorageClass': 'GLACIER'}]
    }]

    s3_stubber.stub_put_bucket_lifecycle_configuration(
        bucket_name, rules, error_code=error_code)

    if error_code is None:
        wrapper.put_lifecycle_configuration(rules)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_lifecycle_configuration(rules)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_lifecycle_configuration(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    rules = [{
        'ID': 'test-id',
        'Filter': {
            'And': {
                'Prefix': 'monsters/',
                'Tags': [{'Key': 'type', 'Value': 'frankenstein'}]
            }
        },
        'Status': 'Enabled',
        'Transitions': [{'Days': 365, 'StorageClass': 'GLACIER'}]
    }]

    s3_stubber.stub_get_bucket_lifecycle_configuration(
        bucket_name, rules, error_code=error_code)

    if error_code is None:
        got_rules = wrapper.get_lifecycle_configuration()
        assert got_rules == rules
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_lifecycle_configuration()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_lifecycle_configuration(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))

    s3_stubber.stub_delete_bucket_lifecycle(bucket_name, error_code=error_code)

    if error_code is None:
        wrapper.delete_lifecycle_configuration()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_lifecycle_configuration()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_generate_presigned_post(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    bucket_name = 'test-bucket_name'
    wrapper = BucketWrapper(s3_resource.Bucket(bucket_name))
    key = 'test-key'

    response = wrapper.generate_presigned_post(key, 60)
    segments = urlparse(response['url'])
    assert all([segments.scheme, segments.netloc, segments.path])
    assert response['fields']['key'] == key
