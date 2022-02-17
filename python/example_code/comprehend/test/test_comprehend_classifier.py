# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for comprehend_classifier.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from comprehend_classifier import ComprehendClassifier, ClassifierMode, JobInputFormat

DATA_ROLE_ARN = 'arn:aws:iam:REGION:123456789012:role/test-role'
CLASSIFIER_ARN = 'arn:aws:comprehend:REGION:123456789012:document-classifier/test-name'


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create(make_stubber, error_code):
    comp_client = boto3.client('comprehend')
    comp_stubber = make_stubber(comp_client)
    comp_classifier = ComprehendClassifier(comp_client)
    name = 'test-name'
    lang_code = 'fr'
    bucket_name = 'test-bucket'
    training_key = 'test-key'
    mode = ClassifierMode.multi_label

    comp_stubber.stub_create_document_classifier(
        name, lang_code, bucket_name, training_key, DATA_ROLE_ARN, mode.value,
        CLASSIFIER_ARN, error_code=error_code)

    if error_code is None:
        got_classifier_arn = comp_classifier.create(
            name, lang_code, bucket_name, training_key, DATA_ROLE_ARN, mode)
        assert got_classifier_arn == CLASSIFIER_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_classifier.create(
                name, lang_code, bucket_name, training_key, DATA_ROLE_ARN, mode)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    classifier = {'DocumentClassifierArn': CLASSIFIER_ARN, 'Status': 'SUBMITTED'}

    comprehend_stubber.stub_describe_document_classifier(
        CLASSIFIER_ARN, classifier['Status'], error_code=error_code)

    if error_code is None:
        got_classifier = comp_class.describe(CLASSIFIER_ARN)
        assert got_classifier == classifier
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.describe(CLASSIFIER_ARN)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    status = 'TRAINED'
    classifiers = [{'DocumentClassifierArn': CLASSIFIER_ARN, 'Status': status}]

    comprehend_stubber.stub_list_document_classifiers(
        [CLASSIFIER_ARN], [status], error_code=error_code)

    if error_code is None:
        got_classifiers = comp_class.list()
        assert got_classifiers == classifiers
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.list()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    comp_class.classifier_arn = CLASSIFIER_ARN

    comprehend_stubber.stub_delete_document_classifier(
        CLASSIFIER_ARN, error_code=error_code)

    if error_code is None:
        comp_class.delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_job(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    comp_class.classifier_arn = CLASSIFIER_ARN
    job_name = 'test-job_name'
    input_bucket = 'input-bucket'
    input_key = 'input-key/'
    input_format = JobInputFormat.per_line
    output_bucket = 'output-bucket'
    output_key = 'output-key/'
    job_status = 'SUBMITTED'

    comprehend_stubber.stub_start_document_classification_job(
        CLASSIFIER_ARN, job_name, input_bucket, input_key, input_format.value,
        output_bucket, output_key, DATA_ROLE_ARN, job_status, error_code=error_code)

    if error_code is None:
        got_job = comp_class.start_job(
            job_name, input_bucket, input_key, input_format, output_bucket, output_key,
            DATA_ROLE_ARN)
        assert got_job == {'JobStatus': job_status}
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.start_job(
                job_name, input_bucket, input_key, input_format, output_bucket,
                output_key, DATA_ROLE_ARN)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_job(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    job_id = 'test-job_id'
    job_name = 'test-name'
    job_status = 'COMPLETED'

    comprehend_stubber.stub_describe_document_classification_job(
        job_id, job_name, job_status, error_code=error_code)

    if error_code is None:
        got_job = comp_class.describe_job(job_id)
        assert got_job == {
            'JobId': job_id, 'JobName': job_name, 'JobStatus': job_status}
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.describe_job(job_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_jobs(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_class = ComprehendClassifier(comprehend_client)
    jobs = ['job-1', 'job-2']

    comprehend_stubber.stub_list_document_classification_jobs(
        jobs, error_code=error_code)

    if error_code is None:
        got_jobs = comp_class.list_jobs()
        assert got_jobs == [{'JobId': job} for job in jobs]
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_class.list_jobs()
        assert exc_info.value.response['Error']['Code'] == error_code
