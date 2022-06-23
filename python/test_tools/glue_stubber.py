# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Glue unit tests.
"""

from datetime import datetime
from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class GlueStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Glue unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client.

        :param client: A Boto3 Glue client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_get_crawler(self, crawler_name, state=None, error_code=None):
        expected_params = {'Name': crawler_name}
        response = {'Crawler': {'Name': crawler_name}}
        if state is not None:
            response['Crawler']['State'] = state
        self._stub_bifurcator(
            'get_crawler', expected_params, response, error_code=error_code)

    def stub_create_crawler(
            self, crawler_name, role_arn, db_name, db_prefix, s3_target, error_code=None):
        expected_params = {
            'Name': crawler_name, 'Role': role_arn, 'DatabaseName': db_name,
            'TablePrefix': db_prefix, 'Targets': {'S3Targets': [{'Path': s3_target}]}}
        response = {}
        self._stub_bifurcator(
            'create_crawler', expected_params, response, error_code=error_code)

    def stub_start_crawler(self, crawler_name, error_code=None):
        expected_params = {'Name': crawler_name}
        response = {}
        self._stub_bifurcator(
            'start_crawler', expected_params, response, error_code=error_code)

    def stub_get_database(self, db_name, error_code=None):
        expected_params = {'Name': db_name}
        response = {'Database': {'Name': db_name}}
        self._stub_bifurcator(
            'get_database', expected_params, response, error_code=error_code)

    def stub_get_tables(self, db_name, tables, error_code=None):
        expected_params = {'DatabaseName': db_name}
        response = {'TableList': tables}
        self._stub_bifurcator(
            'get_tables', expected_params, response, error_code=error_code)

    def stub_create_job(
            self, job_name, role_arn, bucket_name, job_script, error_code=None):
        expected_params = {
            'Name': job_name, 'Description': ANY, 'Role': role_arn,
            'Command': {
                'Name': ANY, 'ScriptLocation': f's3://{bucket_name}/{job_script}',
                'PythonVersion': ANY},
            'GlueVersion': ANY}
        response = {'Name': job_name}
        self._stub_bifurcator(
            'create_job', expected_params, response, error_code=error_code)

    def stub_start_job_run(self, job_name, args, run_id, error_code=None):
        expected_params = {'JobName': job_name, 'Arguments': args}
        response = {'JobRunId': run_id}
        self._stub_bifurcator(
            'start_job_run', expected_params, response, error_code=error_code)

    def stub_get_job_run(self, job_name, run_id, state, error_code=None):
        expected_params = {'JobName': job_name, 'RunId': run_id}
        response = {
            'JobRun': {
                'Id': run_id, 'JobName': job_name, 'CompletedOn': datetime.now(),
                'JobRunState': state}}
        self._stub_bifurcator(
            'get_job_run', expected_params, response, error_code=error_code)

    def stub_list_jobs(self, job_names, error_code=None):
        expected_params = {}
        response = {'JobNames': job_names}
        self._stub_bifurcator(
            'list_jobs', expected_params, response, error_code=error_code)

    def stub_get_job_runs(self, job_name, runs, error_code=None):
        expected_params = {'JobName': job_name}
        response = {'JobRuns': runs}
        self._stub_bifurcator(
            'get_job_runs', expected_params, response, error_code=error_code)

    def stub_delete_job(self, job_name, error_code=None):
        expected_params = {'JobName': job_name}
        response = {}
        self._stub_bifurcator(
            'delete_job', expected_params, response, error_code=error_code)

    def stub_delete_table(self, db_name, table_name, error_code=None):
        expected_params = {'DatabaseName': db_name, 'Name': table_name}
        response = {}
        self._stub_bifurcator(
            'delete_table', expected_params, response, error_code=error_code)

    def stub_delete_database(self, db_name, error_code=None):
        expected_params = {'Name': db_name}
        response = {}
        self._stub_bifurcator(
            'delete_database', expected_params, response, error_code=error_code)

    def stub_delete_crawler(self, crawler_name, error_code=None):
        expected_params = {'Name': crawler_name}
        response = {}
        self._stub_bifurcator(
            'delete_crawler', expected_params, response, error_code=error_code)
