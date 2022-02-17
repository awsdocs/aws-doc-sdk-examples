# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon S3 unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import io
import json
from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class S3Stubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon S3 unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 S3 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def make_version(key, version_id, is_latest=None, last_modified=None):
        version = {
            'Key': key,
            'VersionId': version_id
        }
        if is_latest is not None:
            version['IsLatest'] = is_latest
        if last_modified:
            version['LastModified'] = last_modified
        return version

    def stub_create_bucket(self, bucket_name, region_name=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        if region_name is not None:
            expected_params['CreateBucketConfiguration'] = {
                'LocationConstraint': region_name}
        response = {}
        self._stub_bifurcator(
            'create_bucket', expected_params, response, error_code=error_code)

    def stub_head_bucket(self, bucket_name, status_code=200, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {'ResponseMetadata': {'HTTPStatusCode': status_code}}
        self._stub_bifurcator(
            'head_bucket', expected_params, response, error_code=error_code)

    def stub_delete_bucket(self, bucket_name, error_code=None):
        expected_params = {'Bucket': bucket_name}
        self._stub_bifurcator(
            'delete_bucket', expected_params, error_code=error_code)

    def stub_list_buckets(self, buckets, error_code=None):
        response = {
            'Buckets': [{
                'Name': b.name
            } for b in buckets]
        }
        self._stub_bifurcator(
            'list_buckets', response=response, error_code=error_code)

    def stub_get_bucket_acl(self, bucket_name, grant_names=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {
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
                response['Grants'] = grants
        self._stub_bifurcator(
            'get_bucket_acl', expected_params, response, error_code=error_code)

    def stub_put_bucket_acl(self, bucket_name, error_code=None):
        expected_params = {
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
        }
        response = {}
        self._stub_bifurcator(
            'put_bucket_acl', expected_params, response, error_code=error_code)

    def stub_get_bucket_cors(self, bucket_name, cors_rules=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {'CORSRules': cors_rules}
        self._stub_bifurcator(
            'get_bucket_cors', expected_params, response, error_code=error_code)

    def stub_put_bucket_cors(self, bucket_name, cors_rules, error_code=None):
        expected_params = {
            'Bucket': bucket_name,
            'CORSConfiguration': {
                'CORSRules': cors_rules
            }
        }
        response = {}
        self._stub_bifurcator(
            'put_bucket_cors', expected_params, response, error_code=error_code)

    def stub_delete_bucket_cors(self, bucket_name, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {}
        self._stub_bifurcator(
            'delete_bucket_cors', expected_params, response, error_code=error_code)

    def stub_get_bucket_policy(self, bucket_name, policy=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {'Policy': json.dumps(policy)}
        self._stub_bifurcator(
            'get_bucket_policy', expected_params, response, error_code=error_code)

    def stub_put_bucket_policy(self, bucket_name, policy=ANY, error_code=None):
        expected_params = {
            'Bucket': bucket_name,
            'Policy': policy if isinstance(policy, type(ANY)) else json.dumps(policy)
        }
        response = {}
        self._stub_bifurcator(
            'put_bucket_policy', expected_params, response, error_code=error_code)

    def stub_delete_bucket_policy(self, bucket_name, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {}
        self._stub_bifurcator(
            'delete_bucket_policy', expected_params, response, error_code=error_code)

    def stub_get_bucket_lifecycle_configuration(
            self, bucket_name, lifecycle_rules=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {'Rules': lifecycle_rules}
        self._stub_bifurcator(
            'get_bucket_lifecycle_configuration', expected_params, response, error_code=error_code)

    def stub_put_bucket_lifecycle_configuration(
            self, bucket_name, lifecycle_rules, error_code=None):
        expected_params = {
            'Bucket': bucket_name,
            'LifecycleConfiguration': {'Rules': lifecycle_rules}
        }
        response = {}
        self._stub_bifurcator(
            'put_bucket_lifecycle_configuration', expected_params, response, error_code=error_code)

    def stub_delete_bucket_lifecycle(self, bucket_name, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {}
        self._stub_bifurcator(
            'delete_bucket_lifecycle', expected_params, response, error_code=error_code)

    def stub_put_bucket_versioning(self, bucket_name, status, error_code=None):
        self._stub_bifurcator(
            'put_bucket_versioning',
            expected_params={
                'Bucket': bucket_name,
                'VersioningConfiguration': {'Status': status}
            },
            error_code=error_code)

    def stub_put_object(
            self, bucket_name, object_key, body=ANY, e_tag=None, error_code=None):
        expected_params = {
            'Body': body,
            'Bucket': bucket_name,
            'Key': object_key
        }
        response = {}
        if e_tag:
            response['ETag'] = e_tag
        self._stub_bifurcator(
            'put_object', expected_params, response, error_code=error_code)

    def stub_get_object(
            self, bucket_name, object_key, object_data=None, version_id=None,
            error_code=None):
        """Stub the get_object function. When the object data is a string,
        treat it as a file name, open the file, and read it as bytes."""
        expected_params = {
            'Bucket': bucket_name,
            'Key': object_key
        }
        if object_data:
            if isinstance(object_data, bytes):
                data = object_data
            else:
                with open(object_data, 'rb') as file:
                    data = file.read()
            response = {'Body': io.BytesIO(data)}
        else:
            response = {}
        if version_id:
            response['VersionId'] = version_id

        self._stub_bifurcator(
            'get_object',
            expected_params=expected_params,
            response=response,
            error_code=error_code)

    def stub_delete_object(
            self, bucket_name, object_key, obj_version_id=None, error_code=None):
        expected_params = {
            'Bucket': bucket_name,
            'Key': object_key
        }
        if obj_version_id:
            expected_params['VersionId'] = obj_version_id
        response = {}
        self._stub_bifurcator(
            'delete_object', expected_params, response, error_code=error_code)

    def stub_head_object(
            self, bucket_name, object_key, obj_version_id=None,
            status_code=200, error_code=None, response_meta=None,
            content_length=None):
        expected_params = {'Bucket': bucket_name, 'Key': object_key}
        if obj_version_id:
            expected_params['VersionId'] = obj_version_id
        response = {'ResponseMetadata': {'HTTPStatusCode': status_code}}
        if content_length is not None:
            response['ContentLength'] = content_length

        if not error_code:
            self.add_response(
                'head_object',
                expected_params=expected_params,
                service_response=response
            )
        else:
            self.add_client_error(
                'head_object',
                expected_params=expected_params,
                service_error_code=error_code,
                response_meta=response_meta
            )

    def stub_list_objects(
            self, bucket_name, object_keys=None, prefix=None, delimiter=None,
            error_code=None):
        if not object_keys:
            object_keys = []
        expected_params = {'Bucket': bucket_name}
        if prefix is not None:
            expected_params['Prefix'] = prefix
        if delimiter is not None:
            expected_params['Delimiter'] = delimiter
        response = {'Contents': [{'Key': key} for key in object_keys]}
        self._stub_bifurcator(
            'list_objects', expected_params, response, error_code=error_code)

    def stub_delete_objects(self, bucket_name, object_keys, error_code=None):
        expected_params = {
            'Bucket': bucket_name,
            'Delete': {'Objects': [{'Key': key} for key in object_keys]}
        }
        response = {'Deleted': [{'Key': key} for key in object_keys]}
        self._stub_bifurcator(
            'delete_objects', expected_params, response, error_code=error_code)

    def stub_copy_object(
            self, src_bucket, src_object_key,
            dest_bucket, dest_object_key, error_code=None):
        expected_params = {
            'Bucket': dest_bucket,
            'Key': dest_object_key,
            'CopySource': {'Bucket': src_bucket, 'Key': src_object_key}
        }
        response = {}
        self._stub_bifurcator(
            'copy_object', expected_params, response, error_code=error_code)

    def stub_put_object_acl(self, bucket_name, object_key, email, error_code=None):
        expected_params = {
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
        }
        response = {}
        self._stub_bifurcator(
            'put_object_acl', expected_params, response, error_code=error_code)

    def stub_get_object_acl(self, bucket_name, object_key, email=None, error_code=None):
        expected_params = {'Bucket': bucket_name, 'Key': object_key}
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
                    'Type': 'AmazonCustomerByEmail',
                    'EmailAddress': email
                },
                'Permission': 'READ'
            })
        response = {
            'Grants': grants,
            'Owner': {
                'DisplayName': 'test-owner',
                'ID': '123456789EXAMPLE'
            }
        }
        self._stub_bifurcator(
            'get_object_acl', expected_params, response, error_code=error_code)

    def stub_list_object_versions(
            self, bucket_name, prefix=None, versions=None,
            delete_markers=None, max_keys=None, error_code=None):
        expected_params = {'Bucket': bucket_name}
        response = {}
        if prefix:
            expected_params['Prefix'] = prefix
        if max_keys:
            expected_params['MaxKeys'] = max_keys
        if not error_code:
            if versions:
                response['Versions'] = versions
            if delete_markers:
                response['DeleteMarkers'] = delete_markers
        self._stub_bifurcator(
            'list_object_versions', expected_params, response, error_code=error_code)

    def stub_delete_object_versions(self, bucket_name, obj_key_versions,
                                    error_code=None):
        self._stub_bifurcator(
            'delete_objects',
            expected_params={
                'Bucket': bucket_name,
                'Delete': {'Objects': obj_key_versions}
            },
            error_code=error_code
        )

    def stub_generate_presigned_url(
            self, client_method, method_params, timeout, url, error_code=None):
        expected_params = {
            'ClientMethod': client_method, 'Params': method_params,
            'ExpiresIn': timeout}
        response = {url}
        self._stub_bifurcator(
            'generate_presigned_url', expected_params, response, error_code=error_code)
