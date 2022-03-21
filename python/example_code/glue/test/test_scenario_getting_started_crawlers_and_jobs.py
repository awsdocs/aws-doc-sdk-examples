# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_getting_started_crawlers_and_jobs.py functions.
"""

from datetime import datetime
import time
import pytest
import boto3
from boto3.exceptions import S3UploadFailedError
from botocore.exceptions import ClientError

from scenario_getting_started_crawlers_and_jobs import GlueCrawlerJobScenario


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_upload_job_script(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket = s3_resource.Bucket('test-bucket')
    scenario = GlueCrawlerJobScenario(None, None, bucket)
    job_script = __file__

    s3_stubber.stub_put_object(bucket.name, job_script, error_code=error_code)

    if error_code is None:
        scenario.upload_job_script(job_script)
    else:
        with pytest.raises(S3UploadFailedError):
            scenario.upload_job_script(job_script)


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_get_crawler'),
    ('TestException', 'stub_create_crawler'),
    ('TestException', 'stub_start_crawler'),
    ('TestException', 'stub_get_database'),
    ('TestException', 'stub_get_tables'),
    ('TestException', 'stub_create_job'),
    ('TestException', 'stub_start_job_run'),
    ('TestException', 'stub_get_job_run'),
    ('TestException', 'stub_list_jobs'),
    ('TestException', 'stub_get_job_runs'),
    ('TestException', 'stub_delete_job'),
    ('TestException', 'stub_delete_table'),
    ('TestException', 'stub_delete_database'),
    ('TestException', 'stub_delete_crawler'),
])
def test_run(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    glue_client = boto3.client('glue')
    glue_stubber = make_stubber(glue_client)
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    crawler_name = 'test-crawler'
    db_name = 'test-db'
    db_prefix = 'test-'
    s3_target = 'test-s3-target'
    job_name = 'test-job'
    role_name = 'test-role'
    role_arn = 'arn:aws:iam::123456789012:role/test-role'
    scenario = GlueCrawlerJobScenario(
        glue_client, iam_resource.Role(role_name), s3_resource.Bucket('test-bucket'))
    tables = [{
        'Name': f'table-{index}', 'DatabaseName': db_name, 'CreateTime': datetime.now()
    } for index in range(3)]
    job_script = 'test-script.py'
    run_id = 'test-run-id'
    runs = [{
        'Id': f'{run_id}-{index}', 'JobName': job_name, 'CompletedOn': datetime.now(),
        'JobRunState': 'SUCCEEDED'} for index in range(3)]
    key = 'run-1'
    run_data = b'test-data'

    inputs = ['y', '1', 'y', '1', '1', '1', 'y', 'y', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))
    monkeypatch.setattr(time, 'sleep', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(glue_stubber.stub_get_crawler, crawler_name, error_code='EntityNotFoundException')
        runner.add(iam_stubber.stub_get_role, role_name, role_arn)
        runner.add(
            glue_stubber.stub_create_crawler, crawler_name, role_arn, db_name, db_prefix,
            s3_target)
        runner.add(glue_stubber.stub_get_crawler, crawler_name)
        runner.add(glue_stubber.stub_start_crawler, crawler_name)
        runner.add(glue_stubber.stub_get_crawler, crawler_name, 'READY')
        runner.add(glue_stubber.stub_get_database, db_name)
        runner.add(glue_stubber.stub_get_tables, db_name, tables)
        runner.add(
            glue_stubber.stub_create_job, job_name, role_arn, scenario.glue_bucket.name,
            job_script)
        runner.add(
            glue_stubber.stub_start_job_run, job_name, {
                '--input_database': db_name,
                '--input_table': tables[0]['Name'],
                '--output_bucket_url': f's3://{scenario.glue_bucket.name}/'},
            run_id)
        runner.add(glue_stubber.stub_get_job_run, job_name, run_id, 'SUCCEEDED')
        runner.add(
            s3_stubber.stub_list_objects, scenario.glue_bucket.name, prefix='run-',
            object_keys=[key])
        runner.add(
            s3_stubber.stub_head_object, scenario.glue_bucket.name, key,
            content_length=len(run_data))
        runner.add(s3_stubber.stub_get_object, scenario.glue_bucket.name, key, run_data)
        runner.add(glue_stubber.stub_list_jobs, [job_name])
        runner.add(glue_stubber.stub_get_job_runs, job_name, runs)
        runner.add(glue_stubber.stub_delete_job, job_name)
        runner.add(glue_stubber.stub_get_tables, db_name, [tables[0]])
        runner.add(glue_stubber.stub_delete_table, db_name, tables[0]['Name'])
        runner.add(glue_stubber.stub_delete_database, db_name)
        runner.add(glue_stubber.stub_delete_crawler, crawler_name)

    if error_code is None:
        scenario.run(
            crawler_name, db_name, db_prefix, s3_target, job_script, job_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run(
                crawler_name, db_name, db_prefix, s3_target, job_script, job_name)
        assert exc_info.value.response['Error']['Code'] == error_code
