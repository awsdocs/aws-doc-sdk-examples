# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scenario_getting_started_crawlers_and_jobs.py functions.
"""

from datetime import datetime
import pytest
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY

import scaffold

@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_stack'),
])
def test_deploy(make_stubber, stub_runner, error_code, stop_on_method):
    cf_resource = boto3.resource('cloudformation')
    cf_stubber = make_stubber(cf_resource.meta.client)
    stack_name = 'test-stack'
    capabilities = ['CAPABILITY_NAMED_IAM']
    stack_id = 'test-stack-id'
    outputs = [{
        'OutputKey': f'test-out-{index}', 'OutputValue': f'test-val-{index}'} for index in range(3)]
    resources = [{
        'ResourceType': f'res-type-{index}',
        'LogicalResourceId': f'logical-id-{index}',
        'ResourceStatus': f'status-{index}',
        'PhysicalResourceId': f'physical-id-{index}',
        'LastUpdatedTimestamp': datetime.now()
    } for index in range(3)]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(cf_stubber.stub_create_stack, stack_name, ANY, capabilities, stack_id)
        runner.add(cf_stubber.stub_describe_stacks, stack_name, 'CREATE_COMPLETE', outputs)
        runner.add(cf_stubber.stub_describe_stacks, stack_name, 'CREATE_COMPLETE', outputs)
        runner.add(cf_stubber.stub_list_stack_resources, stack_name, resources)

    if error_code is None:
        got_outs = scaffold.deploy(__file__, stack_name, cf_resource)
        assert got_outs == {oput['OutputKey']: oput['OutputValue'] for oput in outputs}
    else:
        with pytest.raises(ClientError) as exc_info:
            scaffold.deploy(__file__, stack_name, cf_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_objects'),
    ('TestException', 'stub_delete_stack'),
])
def test_destroy(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    cf_resource = boto3.resource('cloudformation')
    cf_stubber = make_stubber(cf_resource.meta.client)
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    stack_name = 'test-stack'
    stack = cf_resource.Stack(stack_name)
    bucket_name = 'test-bucket'
    outputs = [{'OutputKey': 'BucketName', 'OutputValue': bucket_name}]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(cf_stubber.stub_describe_stacks, stack_name, 'CREATE_COMPLETE', outputs)
        runner.add(s3_stubber.stub_list_objects, bucket_name)
        runner.add(cf_stubber.stub_delete_stack, stack_name)
        runner.add(cf_stubber.stub_describe_stacks, stack_name, 'DELETE_COMPLETE')

    if error_code is None:
        scaffold.destroy(stack, cf_resource, s3_resource)
    else:
        with pytest.raises(ClientError) as exc_info:
            scaffold.destroy(stack, cf_resource, s3_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


