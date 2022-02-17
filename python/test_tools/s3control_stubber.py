# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS S3 Control unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

import io
import json
from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class S3ControlStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS S3 Control unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 S3 Control client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_job(self, account_id, role_arn, function_arn, bucket_name,
                        manifest_key, manifest_e_tag, job_id, error_code=None):
        expected_params = {
            'AccountId': account_id,
            'ConfirmationRequired': False,
            'Description': ANY,
            'Priority': 1,
            'RoleArn': role_arn,
            'Operation': {'LambdaInvoke': {'FunctionArn': function_arn}},
            'Manifest': {
                'Spec': {
                    'Format': 'S3BatchOperations_CSV_20180820',
                    'Fields': ['Bucket', 'Key']
                },
                'Location': {
                    'ObjectArn': f'arn:aws:s3:::{bucket_name}/{manifest_key}',
                    'ETag': manifest_e_tag
                }
            },
            'Report': {
                'Bucket': f'arn:aws:s3:::{bucket_name}',
                'Format': 'Report_CSV_20180820',
                'Enabled': True,
                'Prefix': ANY,
                'ReportScope': 'AllTasks'
            }
        }
        if not error_code:
            self.add_response(
                'create_job',
                expected_params=expected_params,
                service_response={'JobId': job_id}
            )
        else:
            self.add_client_error(
                'create_job',
                expected_params=expected_params,
                service_error_code=error_code
            )

    def stub_describe_job(self, account_id, job_id, status='Complete',
                          error_code=None):
        expected_params = {'AccountId': account_id, 'JobId': job_id}
        if not error_code:
            self.add_response(
                'describe_job',
                expected_params=expected_params,
                service_response={'Job': {'Status': status}}
            )
        else:
            self.add_client_error(
                'describe_job',
                expected_params=expected_params,
                service_error_code=error_code
            )
