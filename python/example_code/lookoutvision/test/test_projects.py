# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for projects.py.
"""

import datetime
import boto3
from botocore.exceptions import ClientError
import pytest

from models import Models
from projects import Projects


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_project(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    project_arn = 'test-arn'

    lookoutvision_stubber.stub_create_project(
        project_name, project_arn, error_code=error_code)

    if error_code is None:
        got_project_arn = Projects.create_project(lookoutvision_client, project_name)
        assert got_project_arn == project_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            Projects.create_project(lookoutvision_client, project_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_project(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    project_arn = 'test-arn'

    lookoutvision_stubber.stub_delete_project(
        project_name, project_arn, error_code=error_code)

    if error_code is None:
        Projects.delete_project(lookoutvision_client, project_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            Projects.delete_project(lookoutvision_client, project_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_projects'),
    ('TestException', 'stub_describe_project'),
    ('TestException', 'stub_list_models'),
])
def test_list_projects(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'
    project_arn = 'test-arn'
    created = datetime.datetime.now()
    model_version = 'test-model'
    dataset = {'DatasetType': 'testing', 'StatusMessage': 'nicely tested'}

    monkeypatch.setattr(Models, 'describe_model', lambda x, y, z: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            lookoutvision_stubber.stub_list_projects, [project_name],
            [{'arn': project_arn, 'created': created}])
        runner.add(lookoutvision_stubber.stub_describe_project, project_name, [dataset])
        runner.add(lookoutvision_stubber.stub_list_models, project_name, [model_version])

    if error_code is None:
        Projects.list_projects(lookoutvision_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            Projects.list_projects(lookoutvision_client)
        assert exc_info.value.response['Error']['Code'] == error_code
