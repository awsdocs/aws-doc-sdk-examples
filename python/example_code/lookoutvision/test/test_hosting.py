# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for hosting.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from hosting import Hosting


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_model(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'
    min_units = 3

    lookoutvision_stubber.stub_start_model(project_name, model_version, min_units)
    lookoutvision_stubber.stub_describe_model(
        project_name, model_version, 'test-arn', 'HOSTED', error_code=error_code)

    if error_code is None:
        Hosting.start_model(lookoutvision_client, project_name, model_version, min_units)
    else:
        with pytest.raises(ClientError) as exc_info:
            Hosting.start_model(
                lookoutvision_client, project_name, model_version, min_units)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_stop_model(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'

    lookoutvision_stubber.stub_stop_model(project_name, model_version, 'STOPPING_HOSTING')
    lookoutvision_stubber.stub_describe_model(
        project_name, model_version, 'test-arn', 'TRAINED', error_code=error_code)

    if error_code is None:
        Hosting.stop_model(lookoutvision_client, project_name, model_version)
    else:
        with pytest.raises(ClientError) as exc_info:
            Hosting.stop_model(
                lookoutvision_client, project_name, model_version)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_projects'),
    ('TestException', 'stub_list_models'),
    ('TestException', 'stub_describe_model'),
])
def test_list_hosted(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'
    model_version = 'test-model'
    model_arn = 'test-arn'
    status = 'HOSTED'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(lookoutvision_stubber.stub_list_projects, [project_name])
        runner.add(lookoutvision_stubber.stub_list_models, project_name, [model_version])
        runner.add(
            lookoutvision_stubber.stub_describe_model, project_name, model_version,
            model_arn, status)

    if error_code is None:
        Hosting.list_hosted(lookoutvision_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            Hosting.list_hosted(lookoutvision_client)
        assert exc_info.value.response['Error']['Code'] == error_code
