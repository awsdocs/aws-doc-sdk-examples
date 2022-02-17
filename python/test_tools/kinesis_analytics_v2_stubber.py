# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Kinesis Data Analytics v2 unit tests.
"""

import datetime
from test_tools.example_stubber import ExampleStubber


class KinesisAnalyticsV2Stubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Kinesis Data Analytics v2
    unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Kinesis Analytics v2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _make_application_detail(
            name, version_id, arn='test-app-arn', status='STARTING', env='SQL-1_0'):
        return {
            'ApplicationDetail': {
                'ApplicationName': name,
                'ApplicationVersionId': version_id,
                'ApplicationARN': arn,
                'RuntimeEnvironment': env,
                'ApplicationStatus': status,
                'CreateTimestamp': datetime.datetime.now()}}

    def stub_create_application(
            self, app_name, app_env, role_arn, app_arn, app_version_id,
            error_code=None):
        expected_params = {
            'ApplicationName': app_name,
            'RuntimeEnvironment': app_env,
            'ServiceExecutionRole': role_arn}
        response = self._make_application_detail(
            app_name, app_version_id, app_arn, env=app_env)
        self._stub_bifurcator(
            'create_application', expected_params, response, error_code=error_code)

    def stub_delete_application(self, name, create_timestamp, error_code=None):
        expected_params = {
            'ApplicationName': name, 'CreateTimestamp': create_timestamp}
        response = {}
        self._stub_bifurcator(
            'delete_application', expected_params, response, error_code=error_code)

    def stub_describe_application(
            self, name, version_id, arn, status='STARTING', error_code=None):
        expected_params = {'ApplicationName': name}
        response = self._make_application_detail(name, version_id, arn, status)
        self._stub_bifurcator(
            'describe_application', expected_params, response, error_code=error_code)

    def stub_describe_application_snapshot(
            self, app_name, snapshot_name, error_code=None):
        expected_params = {'ApplicationName': app_name, 'SnapshotName': snapshot_name}
        response = {
            'SnapshotDetails': {
                'SnapshotName': snapshot_name,
                'SnapshotStatus': 'CREATING',
                'ApplicationVersionId': 123,
                'SnapshotCreationTimestamp': datetime.datetime.now()}}
        self._stub_bifurcator(
            'describe_application_snapshot', expected_params, response, error_code=error_code)

    def stub_discover_input_schema(self, stream_arn, role_arn, error_code=None):
        expected_params = {
            'ResourceARN': stream_arn,
            'ServiceExecutionRole': role_arn,
            'InputStartingPositionConfiguration': {'InputStartingPosition': 'NOW'}}
        response = {
            'InputSchema': {
                'RecordFormat': {'RecordFormatType': 'JSON'},
                'RecordColumns': [
                    {
                        'Name': 'test-data',
                        'Mapping': 'test-col',
                        'SqlType': 'VARCHAR(4)'}]}}
        self._stub_bifurcator(
            'discover_input_schema', expected_params, response, error_code=error_code)

    def stub_add_application_input(
            self, app_name, app_version_id, input_prefix, stream_arn, input_schema,
            error_code=None):
        expected_params = {
            'ApplicationName': app_name,
            'CurrentApplicationVersionId': app_version_id,
            'Input': {
                'NamePrefix': input_prefix,
                'KinesisStreamsInput': {'ResourceARN': stream_arn},
                'InputSchema': input_schema}}
        response = {
            'ApplicationARN':
                f'arn:aws:kinesisanalytics:REGION:123456789012:application/{app_name}',
            'ApplicationVersionId': app_version_id,
            'InputDescriptions': [{
                'InputId': 'input-id-1',
                'NamePrefix': input_prefix,
                'InputSchema': input_schema
            }]
        }
        self._stub_bifurcator(
            'add_application_input', expected_params, response, error_code=error_code)

    def stub_add_application_output(
            self, app_name, app_version_id, stream_name, output_arn, error_code=None):
        expected_params = {
            'ApplicationName': app_name,
            'CurrentApplicationVersionId': app_version_id,
            'Output': {
                'Name': stream_name,
                'KinesisStreamsOutput': {'ResourceARN': output_arn},
                'DestinationSchema': {'RecordFormatType': 'JSON'}}}
        response = {
            'ApplicationARN':
                f'arn:aws:kinesisanalytics:REGION:123456789012:application/{app_name}',
            'ApplicationVersionId': app_version_id,
            'OutputDescriptions': [{
                'OutputId': 'test-output-id',
                'Name': stream_name,
                'KinesisStreamsOutputDescription': {
                    'ResourceARN': output_arn,
                    'RoleARN': 'test-role-arn'
                },
                'DestinationSchema': {'RecordFormatType': 'JSON'}}]}
        self._stub_bifurcator(
            'add_application_output', expected_params, response, error_code=error_code)

    def stub_update_application(
            self, app_name, app_version_id, code, error_code=None):
        expected_params = {
            'ApplicationName': app_name,
            'CurrentApplicationVersionId': app_version_id,
            'ApplicationConfigurationUpdate': {
                'ApplicationCodeConfigurationUpdate': {
                    'CodeContentTypeUpdate': 'PLAINTEXT',
                    'CodeContentUpdate': {
                        'TextContentUpdate': code}}}}
        response = self._make_application_detail(app_name, app_version_id)
        self._stub_bifurcator(
            'update_application', expected_params, response, error_code=error_code)

    def stub_start_application(self, app_name, input_id, error_code=None):
        expected_params = {
            'ApplicationName': app_name,
            'RunConfiguration': {
                'SqlRunConfigurations': [{
                    'InputId': input_id,
                    'InputStartingPositionConfiguration': {
                        'InputStartingPosition': 'NOW'}}]}}
        response = {}
        self._stub_bifurcator(
            'start_application', expected_params, response, error_code=error_code)

    def stub_stop_application(self, app_name, error_code=None):
        expected_params = {'ApplicationName': app_name}
        response = {}
        self._stub_bifurcator(
            'stop_application', expected_params, response, error_code=error_code)
