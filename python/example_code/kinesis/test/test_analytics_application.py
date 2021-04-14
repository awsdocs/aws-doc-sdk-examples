# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for analytics_application.py.
"""

import datetime
import boto3
from botocore.exceptions import ClientError, WaiterError
import pytest

from analyticsv2.analytics_application import KinesisAnalyticsApplicationV2


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_read_write_role(make_stubber, stub_runner, error_code):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    policy = 'test-policy'
    policy_arn = f'arn:aws:iam:REGION:123456789012:policy/{policy}'
    role = 'test-role'

    iam_stubber.stub_create_policy(policy, policy_arn, error_code=error_code)
    if error_code is None:
        iam_stubber.stub_create_role(role)
        iam_stubber.stub_attach_role_policy(role, policy_arn)
        iam_stubber.stub_get_policy(policy_arn)

    if error_code is None:
        got_role = KinesisAnalyticsApplicationV2.create_read_write_role(
            'test', 'input-arn', 'output-arn', iam_resource)
        assert got_role.name == role
    else:
        with pytest.raises(ClientError) as exc_info:
            KinesisAnalyticsApplicationV2.create_read_write_role(
                'test', 'input-arn', 'output-arn', iam_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app_name = 'test-app'
    app_arn = f'arn:aws:kinesisanalytics:REGION:123456789012:application/{app_name}'
    app_version_id = 1
    role_arn = 'test-role-arn'

    kinesisanalyticsv2_stubber.stub_create_application(
        app_name, 'SQL-1_0', role_arn, app_arn, app_version_id, error_code=error_code)

    if error_code is None:
        got_app_details = app.create(app_name, role_arn)
        assert app.name == app_name
        assert app.version_id == app_version_id
        assert app.arn == app_arn
        assert got_app_details['ApplicationName'] == app_name
    else:
        with pytest.raises(ClientError) as exc_info:
            app.create(app_name, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'
    app.create_timestamp = datetime.datetime.now()

    kinesisanalyticsv2_stubber.stub_delete_application(
        app.name, app.create_timestamp, error_code=error_code)

    if error_code is None:
        app.delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            app.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app_name = 'test-app'
    version_id = 1
    app_arn = f'arn:aws:kinesisanalytics:REGION:123456789012:application{app_name}'

    kinesisanalyticsv2_stubber.stub_describe_application(
        app_name, version_id, app_arn, error_code=error_code)

    if error_code is None:
        got_details = app.describe(app_name)
        assert got_details['ApplicationName'] == app_name
        assert app.name == app_name
        assert app.version_id == version_id
        assert app.arn == app_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            app.describe(app_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_snapshot(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app_name = 'test-app'
    snapshot_name = 'test-snapshot'

    kinesisanalyticsv2_stubber.stub_describe_application_snapshot(
        app_name, snapshot_name, error_code=error_code)

    if error_code is None:
        got_snapshot = app.describe_snapshot(app_name, snapshot_name)
        assert got_snapshot['SnapshotName'] == snapshot_name
    else:
        with pytest.raises(ClientError) as exc_info:
            app.describe_snapshot(app_name, snapshot_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_discover_input_schema(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    stream_arn = 'test-stream_arn'
    role_arn = 'test-role-arn'

    kinesisanalyticsv2_stubber.stub_discover_input_schema(
        stream_arn, role_arn, error_code=error_code)

    if error_code is None:
        got_schema = app.discover_input_schema(stream_arn, role_arn)
        assert 'RecordFormat' in got_schema
    else:
        with pytest.raises(ClientError) as exc_info:
            app.discover_input_schema(stream_arn, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_input(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'
    app.version_id = 1
    prefix = 'test-prefix'
    stream_arn = 'test-arn'
    input_schema = {
        'RecordFormat': {'RecordFormatType': 'JSON'},
        'RecordColumns': [
            {
                'Name': 'test-data',
                'Mapping': 'test-col',
                'SqlType': 'VARCHAR(4)'}]}

    kinesisanalyticsv2_stubber.stub_add_application_input(
        app.name, app.version_id, prefix, stream_arn, input_schema,
        error_code=error_code)

    if error_code is None:
        got_response = app.add_input(prefix, stream_arn, input_schema)
        assert got_response['InputDescriptions'][0]['InputSchema'] == input_schema
    else:
        with pytest.raises(ClientError) as exc_info:
            app.add_input(prefix, stream_arn, input_schema)
        assert exc_info.value.response['Error']['Code'] == error_code

@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_output(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'
    app.version_id = 1
    stream_name = 'test-stream_name'
    output_arn = 'test-arn'

    kinesisanalyticsv2_stubber.stub_add_application_output(
        app.name, app.version_id, stream_name, output_arn, error_code=error_code)

    if error_code is None:
        got_response = app.add_output(stream_name, output_arn)
        assert got_response[0]['KinesisStreamsOutputDescription'][
                   'ResourceARN'] == output_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            app.add_output(stream_name, output_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_code(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'
    app.version_id = 1
    code = 'test-code'

    kinesisanalyticsv2_stubber.stub_update_application(
        app.name, app.version_id, code, error_code=error_code)

    if error_code is None:
        got_details = app.update_code(code)
        assert got_details['ApplicationName'] == app.name
    else:
        with pytest.raises(ClientError) as exc_info:
            app.update_code(code)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'
    input_id = 'test-input_id'

    kinesisanalyticsv2_stubber.stub_start_application(
        app.name, input_id, error_code=error_code)

    if error_code is None:
        app.start(input_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            app.start(input_id)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_stop(make_stubber, error_code):
    kinesisanalyticsv2_client = boto3.client('kinesisanalyticsv2')
    kinesisanalyticsv2_stubber = make_stubber(kinesisanalyticsv2_client)
    app = KinesisAnalyticsApplicationV2(kinesisanalyticsv2_client)
    app.name = 'test-app'

    kinesisanalyticsv2_stubber.stub_stop_application(app.name, error_code=error_code)

    if error_code is None:
        app.stop()
    else:
        with pytest.raises(ClientError) as exc_info:
            app.stop()
        assert exc_info.value.response['Error']['Code'] == error_code
