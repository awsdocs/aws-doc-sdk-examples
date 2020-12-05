# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for comprehend_topic_modeler.py
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from comprehend_topic_modeler import ComprehendTopicModeler, JobInputFormat

DATA_ACCESS_ROLE_ARN = 'arn:aws:iam:REGION:123456789012:role/test-role'


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_job(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    modeler = ComprehendTopicModeler(comprehend_client)
    job_name = 'test-job_name'
    input_bucket = 'input-bucket'
    input_key = 'input/'
    input_format = JobInputFormat.per_line
    output_bucket = 'output-bucket'
    output_key = 'output/'
    job_id = 'test-job-id'
    job_status = 'SUBMITTED'

    comprehend_stubber.stub_start_topics_detection_job(
        job_name, input_bucket, input_key, input_format.value, output_bucket,
        output_key, DATA_ACCESS_ROLE_ARN, job_id, job_status, error_code=error_code)

    if error_code is None:
        got_response = modeler.start_job(
            job_name, input_bucket, input_key, input_format, output_bucket,
            output_key, DATA_ACCESS_ROLE_ARN)
        assert got_response == {'JobId': job_id, 'JobStatus': job_status}
    else:
        with pytest.raises(ClientError) as exc_info:
            modeler.start_job(
                job_name, input_bucket, input_key, input_format, output_bucket,
                output_key, DATA_ACCESS_ROLE_ARN)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_job(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    modeler = ComprehendTopicModeler(comprehend_client)
    job_id = 'test-job_id'

    comprehend_stubber.stub_describe_topics_detection_job(
        job_id, error_code=error_code)

    if error_code is None:
        got_job = modeler.describe_job(job_id)
        assert got_job['JobId'] == job_id
    else:
        with pytest.raises(ClientError) as exc_info:
            modeler.describe_job(job_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_jobs(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    modeler = ComprehendTopicModeler(comprehend_client)
    job_ids = [f'job-{index}' for index in range(5)]

    comprehend_stubber.stub_list_topics_detection_jobs(job_ids, error_code=error_code)

    if error_code is None:
        got_jobs = modeler.list_jobs()
        assert [job['JobId'] for job in got_jobs] == job_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            modeler.list_jobs()
        assert exc_info.value.response['Error']['Code'] == error_code
