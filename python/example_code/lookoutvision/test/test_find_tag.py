# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for find_tag.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import find_tag


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_projects'),
    ('TestException', 'stub_list_models'),
    ('TestException', 'stub_describe_model'),
    ('TestException', 'stub_list_tags_for_resource'),
])
def test_find_tag_in_projects(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project'
    model_version = 'test-model'
    model_arn = 'test-arn'
    key = 'test-key'
    value = 'test-value'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(lookoutvision_stubber.stub_list_projects, [project_name])
        runner.add(lookoutvision_stubber.stub_list_models, project_name, [model_version])
        runner.add(
            lookoutvision_stubber.stub_describe_model, project_name, model_version,
            model_arn)
        runner.add(
            lookoutvision_stubber.stub_list_tags_for_resource, model_arn, {key: value})

    if error_code is None:
        got_project = find_tag.find_tag_in_projects(lookoutvision_client, key, value)
        assert got_project == [{'Project': project_name, 'ModelVersion': model_version}]
    else:
        with pytest.raises(ClientError) as exc_info:
            find_tag.find_tag_in_projects(lookoutvision_client, key, value)
        assert exc_info.value.response['Error']['Code'] == error_code
