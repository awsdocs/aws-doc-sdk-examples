# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for emrfs_step.py functions.
"""

import pytest
import boto3
from botocore.exceptions import ClientError

import emrfs_step


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_run_job_flow(make_stubber, make_unique_name, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'i-123456789'
    step = {
        'name': 'Example EMRFS Command Step',
        'command': 'test-command',
        'bucket_url': 's3://test-bucket/metadata-folder',
        'type': 'emrfs'
    }
    step_id = 's-123456789'

    emr_stubber.stub_add_job_flow_steps(
        cluster_id, [step], [step_id], error_code=error_code)

    if error_code is None:
        got_id = emrfs_step.add_emrfs_step(
            step['command'], step['bucket_url'], cluster_id, emr_client)
        assert got_id == step_id
    else:
        with pytest.raises(ClientError) as exc_info:
            emrfs_step.add_emrfs_step(
                step['command'], step['bucket_url'], cluster_id, emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code
