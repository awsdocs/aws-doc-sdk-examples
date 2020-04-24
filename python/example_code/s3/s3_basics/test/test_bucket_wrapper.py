# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for bucket_wrapper.py functions.
"""

import io
from urllib.parse import urlparse
import uuid
import pytest

from botocore.exceptions import ClientError

import bucket_wrapper


@pytest.mark.parametrize("region_name", ['us-west-2', 'eu-west-1', 'ap-southeast-1'])
def test_create_bucket(stub_and_patch, make_unique_name, region_name):
    """Test creating a bucket in various AWS Regions."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3', region_name)
    bucket_name = make_unique_name('bucket')

    stubber.stub_create_bucket(bucket_name, region_name)
    stubber.stub_head_bucket(bucket_name)

    bucket = bucket_wrapper.create_bucket(bucket_name, region_name)
    assert bucket_name == bucket.name

    if not stubber.use_stubs:
        bucket_wrapper.delete_bucket(bucket)


def test_create_bucket_no_region(stub_and_patch, make_unique_name):
    """Test that creating a bucket with no Region raises an error."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')

    stubber.stub_create_bucket(bucket_name,
                               error_code='IllegalLocationConstraintException')

    with pytest.raises(ClientError):
        bucket_wrapper.create_bucket(bucket_name)


def test_create_existing_bucket(stub_and_patch, make_unique_name, make_bucket):
    """Test that creating an existing bucket raises an error."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_create_bucket(bucket.name, stubber.region_name,
                               error_code='BucketAlreadyOwnedByYou')

    with pytest.raises(stubber.client.exceptions.BucketAlreadyOwnedByYou):
        bucket_wrapper.create_bucket(bucket.name, stubber.region_name)


def test_bucket_exists(stub_and_patch, make_unique_name, make_bucket):
    """Test that bucket existence is correctly determined."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')

    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_head_bucket(bucket.name)

    assert bucket_wrapper.bucket_exists(bucket.name)


def test_bucket_not_exists(stub_and_patch, make_unique_name, make_bucket):
    """Test that bucket nonexistence is correctly determined."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')

    stubber.stub_head_bucket(bucket_name, error_code='NoSuchBucket')

    assert not bucket_wrapper.bucket_exists(bucket_name)


def test_delete_empty_bucket(stub_and_patch, make_unique_name, make_bucket):
    """Test that deleting an empty bucket works as expected."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket_name = make_unique_name('bucket')

    stubber.stub_create_bucket(bucket_name, stubber.region_name)
    stubber.stub_head_bucket(bucket_name)
    bucket = bucket_wrapper.create_bucket(bucket_name, stubber.region_name)

    stubber.stub_delete_bucket(bucket.name)
    stubber.stub_head_bucket(bucket.name, 404)
    bucket_wrapper.delete_bucket(bucket)


def test_delete_full_bucket(stub_and_patch, make_unique_name, make_bucket):
    """Test that deleting a bucket that contains objects raises an error."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    object_key = 'test_bucket_wrapper.py'
    stubber.stub_put_object(bucket.name, object_key)
    stubber.stub_delete_bucket(bucket.name, error_code='BucketNotEmpty')

    obj = bucket.Object(object_key)
    obj.upload_fileobj(io.BytesIO(b"Test data."))
    with pytest.raises(ClientError):
        bucket_wrapper.delete_bucket(bucket)

    if not stubber.use_stubs:
        obj.delete()


def test_get_buckets(stub_and_patch, make_unique_name, make_bucket):
    """Test getting a list of buckets."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    created_buckets = [
        make_bucket(stubber, bucket_wrapper.get_s3())
        for ind in range(0, 5)
    ]

    stubber.stub_list_buckets(created_buckets)

    gotten_buckets = bucket_wrapper.get_buckets()
    intersection = [b for b in gotten_buckets if b in created_buckets]
    assert created_buckets == intersection


def test_grant_log_delivery_access(stub_and_patch, make_unique_name, make_bucket):
    """Test that using an ACL to grant access to the log delivery group succeeds."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_get_bucket_acl(bucket.name)
    stubber.stub_put_bucket_acl(bucket.name)
    stubber.stub_get_bucket_acl(bucket.name, ['owner', 'log_delivery'])

    bucket_wrapper.grant_log_delivery_access(bucket.name)

    acl = bucket_wrapper.get_acl(bucket.name)
    log_delivery_grantee = {
        'Grantee': {
            'Type': 'Group',
            'URI': 'http://acs.amazonaws.com/groups/s3/LogDelivery'
        },
        'Permission': 'WRITE'
    }
    assert log_delivery_grantee in acl.grants
    owner_grantee = {
        'Grantee': {
            'Type': 'CanonicalUser',
            'ID': acl.owner['ID'],
            'DisplayName': acl.owner['DisplayName']
        },
        'Permission': 'FULL_CONTROL'
    }
    assert owner_grantee in acl.grants


def test_get_acl(stub_and_patch, make_unique_name, make_bucket):
    """Test that getting a bucket ACL returns the expected values."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_get_bucket_acl(bucket.name, ['owner'])

    acl = bucket_wrapper.get_acl(bucket.name)
    assert len(acl.grants) == 1
    assert acl.owner['ID'] == acl.grants[0]['Grantee']['ID']
    assert acl.grants[0]['Permission'] == 'FULL_CONTROL'


def test_get_cors_expect_none(stub_and_patch, make_unique_name, make_bucket):
    """Test that getting CORS for a new bucket raises an error."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_get_bucket_cors(bucket.name, error_code='NoSuchCORSConfiguration')

    with pytest.raises(ClientError) as exc_info:
        _ = bucket_wrapper.get_cors(bucket.name)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchCORSConfiguration'


def test_put_get_delete_cors(stub_and_patch, make_unique_name, make_bucket):
    """Test that put, get, and delete of CORS on a bucket works as expected."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    cors_rules = [{
        'AllowedOrigins': ['http://www.example.com'],
        'AllowedMethods': ['PUT', 'POST', 'DELETE'],
        'AllowedHeaders': ['*']
    }]

    stubber.stub_put_bucket_cors(bucket.name, cors_rules)
    stubber.stub_get_bucket_cors(bucket.name, cors_rules)
    stubber.stub_delete_bucket_cors(bucket.name)
    stubber.stub_get_bucket_cors(bucket.name, error_code='NoSuchCORSConfiguration')

    bucket_wrapper.put_cors(bucket.name, cors_rules)

    cors = bucket_wrapper.get_cors(bucket.name)
    assert cors.cors_rules == cors_rules

    bucket_wrapper.delete_cors(bucket.name)
    with pytest.raises(ClientError) as exc_info:
        _ = bucket_wrapper.get_cors(bucket.name)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchCORSConfiguration'


def test_put_bucket_policy_bad_version(stub_and_patch, make_unique_name, make_bucket):
    """
    Test that a policy version other than 2012-10-17 or 2008-10-17 fails.
    This is because the version is the version of the policy format and so must be
    a recognized version string.
    """
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    put_policy = {
        'Version': '2020-03-17'
    }

    stubber.stub_put_bucket_policy(bucket.name, put_policy,
                                   error_code='MalformedPolicy')

    with pytest.raises(ClientError) as exc_info:
        bucket_wrapper.put_policy(bucket.name, put_policy)
    assert exc_info.value.response['Error']['Code'] == 'MalformedPolicy'


@pytest.mark.skip_if_real_aws
def test_put_get_delete_bucket_policy(stub_and_patch, make_unique_name, make_bucket):
    """
    Test that put, get, delete on a bucket policy works as expected.
    To run this test with the non-stubbed AWS service, you must update the principal
    ARN to an existing AWS user, or the test will fail.
    """
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    policy_id = uuid.uuid1()

    put_policy = {
        'Version': '2012-10-17',
        'Id': str(policy_id),
        'Statement': [{
            'Effect': 'Allow',
            'Principal': {'AWS': 'arn:aws:iam::111122223333:user/Martha'},
            'Action': [
                's3:GetObject',
                's3:ListBucket'
            ],
            'Resource': [
                f'arn:aws:s3:::{bucket.name}/*',
                f'arn:aws:s3:::{bucket.name}'
            ]
        }]
    }

    stubber.stub_put_bucket_policy(bucket.name, put_policy)
    stubber.stub_get_bucket_policy(bucket.name, put_policy)
    stubber.stub_delete_bucket_policy(bucket.name)
    stubber.stub_get_bucket_policy(bucket.name, error_code='NoSuchBucketPolicy')

    bucket_wrapper.put_policy(bucket.name, put_policy)
    policy = bucket_wrapper.get_policy(bucket.name)
    assert put_policy == policy
    bucket_wrapper.delete_policy(bucket.name)
    with pytest.raises(ClientError) as exc_info:
        _ = bucket_wrapper.get_policy(bucket.name)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchBucketPolicy'


def test_get_bucket_lifecycle_configuration(stub_and_patch, make_unique_name,
                                            make_bucket):
    """Test that getting the lifecycle configuration of a new bucket raises an error."""
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    stubber.stub_get_bucket_lifecycle_configuration(
        bucket.name, error_code='NoSuchLifecycleConfiguration'
    )

    with pytest.raises(ClientError) as exc_info:
        _ = bucket_wrapper.get_lifecycle_configuration(bucket.name)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchLifecycleConfiguration'


def test_put_get_delete_bucket_lifecycle_configuration(
        stub_and_patch, make_unique_name, make_bucket):
    """
    Test that put, get, delete of lifecycle configuration on a bucket works
    as expected.
    """
    stubber = stub_and_patch(bucket_wrapper, 'get_s3')
    bucket = make_bucket(stubber, bucket_wrapper.get_s3())

    put_rules = [{
        'ID': str(uuid.uuid1()),
        'Filter': {
            'And': {
                'Prefix': 'monsters/',
                'Tags': [{'Key': 'type', 'Value': 'zombie'}]
            }
        },
        'Status': 'Enabled',
        'Expiration': {'Days': 28}
    }, {
        'ID': str(uuid.uuid1()),
        'Filter': {
            'And': {
                'Prefix': 'monsters/',
                'Tags': [{'Key': 'type', 'Value': 'frankenstein'}]
            }
        },
        'Status': 'Enabled',
        'Transitions': [{'Days': 365, 'StorageClass': 'GLACIER'}]
    }]

    stubber.stub_put_bucket_lifecycle_configuration(bucket.name, put_rules)
    stubber.stub_get_bucket_lifecycle_configuration(bucket.name, put_rules)
    stubber.stub_delete_bucket_lifecycle_configuration(bucket.name)
    stubber.stub_get_bucket_lifecycle_configuration(
        bucket.name, error_code='NoSuchLifecycleConfiguration'
    )

    bucket_wrapper.put_lifecycle_configuration(bucket.name, put_rules)
    rules = bucket_wrapper.get_lifecycle_configuration(bucket.name)
    assert rules == put_rules
    bucket_wrapper.delete_lifecycle_configuration(bucket.name)
    with pytest.raises(ClientError) as exc_info:
        _ = bucket_wrapper.get_lifecycle_configuration(bucket.name)
    assert exc_info.value.response['Error']['Code'] == 'NoSuchLifecycleConfiguration'


def test_generate_presigned_post(make_unique_name):
    """Test that generating a presigned POST URL works as expected."""
    bucket_name = make_unique_name('bucket')
    object_key = make_unique_name('object')
    expires_in = 60

    response = bucket_wrapper.generate_presigned_post(bucket_name, object_key,
                                                      expires_in)
    segments = urlparse(response['url'])
    assert all([segments.scheme, segments.netloc, segments.path])
    assert response['fields']['key'] == object_key


@pytest.mark.parametrize('client_method,method_parameters', [
    ('list_objects', {'Bucket': 'test-bucket'}),
    ('put_object', {'Bucket': 'test-bucket', 'Key': 'test-object'})
])
def test_generate_presigned_url(make_unique_name, client_method,
                                method_parameters):
    """"Test that generating a presigned URL works as expected."""
    bucket_name = make_unique_name('bucket')
    expires_in = 60

    url = bucket_wrapper.generate_presigned_url(
        bucket_name, client_method, method_parameters, expires_in
    )
    segments = urlparse(url)
    assert all([segments.scheme, segments.netloc, segments.path])
