# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon S3 versioning tests.
"""
from urllib import parse
import pytest
from botocore.stub import ANY

import sys
# This is needed so Python can find test_tools in the path.
sys.path.append('../../..')
from test_tools.fixtures.common import *


@pytest.fixture
def make_event():
    """Returns a function that makes an Amazon S3 batch event."""
    def _func(bucket_name, obj_key, extra_data=None, version_id=None):
        task = {
            'taskId': 'test-task-id',
            's3Key': parse.quote(f'{obj_key}|{extra_data}') if extra_data else obj_key,
            's3BucketArn': f'arn:aws:::{bucket_name}'
        }
        if version_id:
            task['s3VersionId'] = version_id
        return {
            'invocationId': 'test-invocation-id',
            'invocationSchemaVersion': 'test-schema-version',
            'tasks': [task]
        }
    return _func


@pytest.fixture
def make_result():
    """Returns a function that makes an Amazon S3 batch result."""
    def _func(code):
        return {
            'invocationSchemaVersion': 'test-schema-version',
            'treatMissingKeysAs': 'PermanentFailure',
            'invocationId': 'test-invocation-id',
            'results': [{
                'taskId': 'test-task-id',
                'resultCode': code,
                'resultString': ANY
            }]
        }
    return _func
