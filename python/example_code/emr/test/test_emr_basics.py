# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for emr_basics.py functions.
"""

from unittest.mock import MagicMock
import pytest
import boto3
from botocore.exceptions import ClientError

import emr_basics


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_run_job_flow(make_stubber, make_unique_name, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_name = make_unique_name('cluster-')
    log_uri = 's3://test-bucket'
    release = 'emr-5.30.1'
    instance_type = 'm5.xlarge'
    instance_count = 3
    keep_alive = True
    steps = [{
        'name': make_unique_name('step-'),
        'script_uri': 's3://test-bucket',
        'script_args': ('--testing',)}]
    applications = ['test-app']
    cluster_id = 'i-123456789'
    job_flow_role = MagicMock()
    job_flow_role.name = 'job-flow-role'
    service_role = MagicMock()
    service_role.name = 'service_role'
    security_groups = \
        {'manager': MagicMock(id='sg-1234'), 'worker': MagicMock(id='sg-5678')}

    emr_stubber.stub_run_job_flow(
        cluster_name, log_uri, release, instance_type, instance_count, keep_alive,
        steps, applications, job_flow_role.name, service_role.name, security_groups,
        cluster_id, error_code=error_code)

    if error_code is None:
        got_id = emr_basics.run_job_flow(
            cluster_name, log_uri, keep_alive, applications, job_flow_role,
            service_role, security_groups, steps, emr_client)
        assert got_id == cluster_id
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.run_job_flow(
                cluster_name, log_uri, keep_alive, applications, job_flow_role,
                service_role, security_groups, steps, emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_cluster(make_stubber, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'test-id'
    cluster = {'Id': cluster_id, 'Name': 'cluster-name'}

    emr_stubber.stub_describe_cluster(cluster_id, cluster, error_code=error_code)

    if error_code is None:
        got_cluster = emr_basics.describe_cluster(cluster_id, emr_client)
        assert got_cluster == cluster
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.describe_cluster(cluster_id, emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_terminate_cluster(make_stubber, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'test-id'

    emr_stubber.stub_terminate_job_flows([cluster_id], error_code=error_code)

    if error_code is None:
        emr_basics.terminate_cluster(cluster_id, emr_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.terminate_cluster(cluster_id, emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_job_flow_steps(make_stubber, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'test-id'
    step_id = 'test-step-id'
    step = {
        'name': 'Example EMRFS Command Step',
        'script_uri': 's3://test-bucket/test-script',
        'script_args': ('--test', 'value'),
        'type': 'spark'}

    emr_stubber.stub_add_job_flow_steps(
        cluster_id, [step], [step_id], error_code=error_code)

    if error_code is None:
        got_id = emr_basics.add_step(
            cluster_id, step['name'], step['script_uri'], step['script_args'],
            emr_client)
        assert got_id == step_id
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.add_step(
                cluster_id, step['name'], step['script_uri'], step['script_args'],
                emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_steps(make_stubber, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'test-id'
    steps = [{
        'Id': f'id-{index}',
        'Name': f'step-{index}',
        'Status': {'State': 'COMPLETED'}
    } for index in range(3)]

    emr_stubber.stub_list_steps(cluster_id, steps, error_code=error_code)

    if error_code is None:
        got_steps = emr_basics.list_steps(cluster_id, emr_client)
        assert got_steps == steps
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.list_steps(cluster_id, emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_step(make_stubber, error_code):
    emr_client = boto3.client('emr')
    emr_stubber = make_stubber(emr_client)
    cluster_id = 'test-id'
    step = {
        'Id': f'test-step-id',
        'Name': f'test-step',
        'Status': {'State': 'COMPLETED'}
    }

    emr_stubber.stub_describe_step(cluster_id, step, error_code=error_code)

    if error_code is None:
        got_step = emr_basics.describe_step(cluster_id, step['Id'], emr_client)
        assert got_step == step
    else:
        with pytest.raises(ClientError) as exc_info:
            emr_basics.describe_step(cluster_id, step['Id'], emr_client)
        assert exc_info.value.response['Error']['Code'] == error_code
