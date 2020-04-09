# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
A class that wraps the botocore Stubber and implements a variety of
stub functions that are used by the Amazon S3 unit tests.

When tests are run against a actual AWS account, the S3Stubber class does not
set up stubs and passes all calls through to the Boto 3 S3 resource.
"""

import io
import json
from botocore.stub import Stubber, ANY


class S3Stubber(Stubber):
    """
    A class that wraps the botocore Stubber and implements a variety of
    stub functions that are used by the Amazon S3 unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 S3 resource.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        self.use_stubs = use_stubs
        self.region_name = client.meta.region_name
        if self.use_stubs:
            super().__init__(client)
        else:
            self.client = client

    def add_response(self, method, service_response, expected_params=None):
        """When using stubs, add a stubbed response."""
        if self.use_stubs:
            super().add_response(method, service_response, expected_params)

    def add_client_error(self, method, service_error_code='',
                         service_message='', http_status_code=400,
                         service_error_meta=None, expected_params=None,
                         response_meta=None):
        """When using stubs, add a stubbed error response."""
        if self.use_stubs:
            super().add_client_error(method, service_error_code, service_message,
                                     http_status_code, service_error_meta,
                                     expected_params, response_meta)

    def assert_no_pending_responses(self):
        """When using stubs, verify no more responses are waiting in the queue."""
        if self.use_stubs:
            super().assert_no_pending_responses()

    def stub_create_bucket(self, bucket_name, region_name):
        self.add_response(
            'create_bucket',
            expected_params={
                'Bucket': bucket_name,
                'CreateBucketConfiguration': {
                    'LocationConstraint': region_name
                }
            },
            service_response={}
        )

    def stub_create_bucket_error(self, bucket_name, error_code, region_name=None):
        expected_params = {
            'Bucket': bucket_name
        }
        if region_name:
            expected_params['CreateBucketConfiguration'] = {
                'LocationConstraint': region_name
            }

        self.add_client_error(
            'create_bucket',
            expected_params=expected_params,
            service_error_code=error_code
        )

    def stub_head_bucket(self, bucket_name, status_code=200):
        """The head bucket function is used by the bucket waiter to determine
        bucket existence."""
        self.add_response(
            'head_bucket',
            expected_params={'Bucket': bucket_name},
            service_response={'ResponseMetadata': {'HTTPStatusCode': status_code}}
        )

    def stub_head_bucket_error(self, bucket_name, error_code):
        self.add_client_error(
            'head_bucket',
            expected_params={'Bucket': bucket_name},
            service_error_code=str(error_code)
        )

    def stub_delete_bucket(self, bucket_name):
        self.add_response(
            'delete_bucket',
            expected_params={'Bucket': bucket_name},
            service_response={}
        )

    def stub_delete_bucket_error(self, bucket_name, error_code):
        self.add_client_error(
            'delete_bucket',
            expected_params={'Bucket': bucket_name},
            service_error_code=error_code
        )

    def stub_list_buckets(self, buckets):
        self.add_response(
            'list_buckets',
            expected_params={},
            service_response={
                'Buckets': [{
                    'Name': b.name
                } for b in buckets]
            }
        )

    def stub_get_bucket_acl(self, bucket_name, grant_names=None):
        service_response = {
            'Owner': {
                'DisplayName': 'test-owner',
                'ID': '123456789EXAMPLE'
            }
        }
        if grant_names:
            grants = []
            for grant_name in grant_names:
                if grant_name == 'owner':
                    grants.append({
                        'Grantee': {
                            'Type': 'CanonicalUser',
                            'DisplayName': 'test-owner',
                            'ID': '123456789EXAMPLE'
                        },
                        'Permission': 'FULL_CONTROL'
                    })
                elif grant_name == 'log_delivery':
                    grants.append({
                        'Grantee': {
                            'Type': 'Group',
                            'URI': 'http://acs.amazonaws.com/groups/s3/LogDelivery'
                        },
                        'Permission': 'WRITE'
                    })
            if grants:
                service_response['Grants'] = grants

        self.add_response(
            'get_bucket_acl',
            expected_params={'Bucket': bucket_name},
            service_response=service_response
        )

    def stub_put_bucket_acl(self, bucket_name):
        self.add_response(
            'put_bucket_acl',
            expected_params={
                'AccessControlPolicy': {
                    'Grants': [{
                        'Grantee': {
                            'Type': 'Group',
                            'URI': 'http://acs.amazonaws.com/groups/s3/LogDelivery'
                        },
                        'Permission': 'WRITE'
                    }],
                    'Owner': {
                        'DisplayName': 'test-owner',
                        'ID': '123456789EXAMPLE'
                    }
                },
                'Bucket': bucket_name
            },
            service_response={}
        )

    def stub_get_bucket_cors(self, bucket_name, cors_rules):
        self.add_response(
            'get_bucket_cors',
            expected_params={'Bucket': bucket_name},
            service_response={
                'CORSRules': cors_rules
            }
        )

    def stub_get_bucket_cors_error(self, bucket_name, error_code):
        self.add_client_error(
            'get_bucket_cors',
            expected_params={'Bucket': bucket_name},
            service_error_code=error_code
        )

    def stub_put_bucket_cors(self, bucket_name, cors_rules):
        self.add_response(
            'put_bucket_cors',
            expected_params={
                'Bucket': bucket_name,
                'CORSConfiguration': {
                    'CORSRules': cors_rules
                }
            },
            service_response={}
        )

    def stub_delete_bucket_cors(self, bucket_name):
        self.add_response(
            'delete_bucket_cors',
            expected_params={'Bucket': bucket_name},
            service_response={}
        )

    def stub_get_bucket_policy(self, bucket_name, policy):
        self.add_response(
            'get_bucket_policy',
            expected_params={'Bucket': bucket_name},
            service_response={'Policy': json.dumps(policy)}
        )

    def stub_get_bucket_policy_error(self, bucket_name, error_code):
        self.add_client_error(
            'get_bucket_policy',
            expected_params={'Bucket': bucket_name},
            service_error_code=error_code
        )

    def stub_put_bucket_policy(self, bucket_name, policy):
        self.add_response(
            'put_bucket_policy',
            expected_params={
                'Bucket': bucket_name,
                'Policy': json.dumps(policy)
            },
            service_response={}
        )

    def stub_put_bucket_policy_error(self, bucket_name, policy, error_code):
        self.add_client_error(
            'put_bucket_policy',
            expected_params={
                'Bucket': bucket_name,
                'Policy': json.dumps(policy)
            },
            service_error_code=error_code
        )

    def stub_delete_bucket_policy(self, bucket_name):
        self.add_response(
            'delete_bucket_policy',
            expected_params={'Bucket': bucket_name},
            service_response={}
        )

    def stub_get_bucket_lifecycle_configuration(self, bucket_name, lifecycle_rules):
        self.add_response(
            'get_bucket_lifecycle_configuration',
            expected_params={'Bucket': bucket_name},
            service_response={'Rules': lifecycle_rules}
        )

    def stub_get_bucket_lifecycle_configuration_error(self, bucket_name, error_code):
        self.add_client_error(
            'get_bucket_lifecycle_configuration',
            expected_params={'Bucket': bucket_name},
            service_error_code=error_code
        )

    def stub_put_bucket_lifecycle_configuration(self, bucket_name, lifecycle_rules):
        self.add_response(
            'put_bucket_lifecycle_configuration',
            expected_params={
                'Bucket': bucket_name,
                'LifecycleConfiguration': {'Rules': lifecycle_rules}
            },
            service_response={}
        )

    def stub_delete_bucket_lifecycle_configuration(self, bucket_name):
        self.add_response(
            'delete_bucket_lifecycle',
            expected_params={'Bucket': bucket_name},
            service_response={}
        )

    def stub_put_object(self, bucket_name, object_key):
        self.add_response(
            'put_object',
            expected_params={
                'Body': ANY,
                'Bucket': bucket_name,
                'Key': object_key},
            service_response={}
        )

    def stub_put_object_error(self, bucket_name, object_key, error_code):
        self.add_client_error(
            'put_object',
            expected_params={
                'Body': ANY,
                'Bucket': bucket_name,
                'Key': object_key
            },
            service_error_code=error_code
        )

    def stub_get_object(self, bucket_name, object_key, object_data):
        """Stub the get_object function. When the object data is a string,
        treat as a filename and open the file and read it as bytes."""
        if isinstance(object_data, bytes):
            data = object_data
        else:
            with open(object_data, 'rb') as file:
                data = file.read()

        self.add_response(
            'get_object',
            expected_params={
                'Bucket': bucket_name,
                'Key': object_key
            },
            service_response={
                'Body': io.BytesIO(data)
            }
        )

    def stub_get_object_error(self, bucket_name, object_key, error_code):
        self.add_client_error(
            'get_object',
            expected_params={
                'Bucket': bucket_name,
                'Key': object_key
            },
            service_error_code=error_code
        )

    def stub_delete_object(self, bucket_name, object_key):
        self.add_response(
            'delete_object',
            expected_params={
                'Bucket': bucket_name,
                'Key': object_key
            },
            service_response={}
        )

    def stub_head_object(self, bucket_name, object_key, status_code=200):
        self.add_response(
            'head_object',
            expected_params={'Bucket': bucket_name, 'Key': object_key},
            service_response={'ResponseMetadata': {'HTTPStatusCode': status_code}}
        )

    def stub_head_object_error(self, bucket_name, object_key, error_code):
        self.add_client_error(
            'head_object',
            expected_params={'Bucket': bucket_name, 'Key': object_key},
            service_error_code=error_code
        )

    def stub_list_objects(self, bucket_name, object_keys=None, prefix=None):
        if not object_keys:
            object_keys = []

        expected_params = {'Bucket': bucket_name}
        if prefix:
            expected_params['Prefix'] = prefix
        self.add_response(
            'list_objects',
            expected_params=expected_params,
            service_response={
                'Contents': [{
                    'Key': key
                } for key in object_keys]
            }
        )

    def stub_delete_objects(self, bucket_name, object_keys):
        self.add_response(
            'delete_objects',
            expected_params={
                'Bucket': bucket_name,
                'Delete': {
                    'Objects': [{
                        'Key': key
                    } for key in object_keys]
                }
            },
            service_response={
                'Deleted': [{
                    'Key': key
                } for key in object_keys]
            }
        )

    def stub_copy_object(self, src_bucket, src_object_key,
                         dest_bucket, dest_object_key):
        self.add_response(
            'copy_object',
            expected_params={
                'Bucket': dest_bucket,
                'Key': dest_object_key,
                'CopySource': {'Bucket': src_bucket, 'Key': src_object_key}
            },
            service_response={}
        )

    def stub_put_object_acl(self, bucket_name, object_key, email):
        self.add_response(
            'put_object_acl',
            expected_params={
                'Bucket': bucket_name,
                'Key': object_key,
                'AccessControlPolicy': {
                    'Grants': [{
                        'Grantee': {
                            'Type': 'CanonicalUser',
                            'ID': '123456789EXAMPLE',
                            'DisplayName': 'test-owner'
                        },
                        'Permission': 'FULL_CONTROL',
                    }, {
                        'Grantee': {
                            'EmailAddress': email,
                            'Type': 'AmazonCustomerByEmail'
                        },
                        'Permission': 'READ'
                    }],
                    'Owner': {
                        'DisplayName': 'test-owner',
                        'ID': '123456789EXAMPLE'
                    }
                }
            },
            service_response={}
        )

    def stub_get_object_acl(self, bucket_name, object_key, email=None):
        grants = [{
            'Grantee': {
                'Type': 'CanonicalUser',
                'ID': '123456789EXAMPLE',
                'DisplayName': 'test-owner'
            },
            'Permission': 'FULL_CONTROL',
        }]
        if email:
            grants.append({
                'Grantee': {
                    'Type': 'CanonicalUser',
                    'ID': '123456789EXAMPLE',
                    'DisplayName': 'arnav'
                },
                'Permission': 'READ'
            })

        self.add_response(
            'get_object_acl',
            expected_params={'Bucket': bucket_name, 'Key': object_key},
            service_response={
                'Grants': grants,
                'Owner': {
                    'DisplayName': 'test-owner',
                    'ID': '123456789EXAMPLE'
                }
            }
        )
