# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for models.py.
"""

import datetime
import boto3
from botocore.exceptions import ClientError
import pytest

from models import Models


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_model(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'
    model_arn = 'test-arn'
    out_buck = 'doc-example-bucket'
    out_folder = 'test-results'
    training_results = f's3://{out_buck}/{out_folder}'
    status = 'TRAINED'

    lookoutvision_stubber.stub_create_model(
        project_name, out_buck, out_folder, model_arn, model_version)
    lookoutvision_stubber.stub_describe_model(
        project_name, model_version, model_arn, status, error_code=error_code)

    if error_code is None:
        got_status, got_version = Models.create_model(
            lookoutvision_client, project_name, training_results)
        assert got_status == status
        assert got_version == model_version
    else:
        with pytest.raises(ClientError) as exc_info:
            Models.create_model(lookoutvision_client, project_name, training_results)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_model(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'
    model_arn = 'test-arn'
    description = 'test description'
    status = 'TRAINED'
    message = 'Test message!'
    created = datetime.datetime.now()
    trained = created + datetime.timedelta(minutes=10)
    recall = .3
    precision = .5
    f1 = .7
    out_buck = 'doc-example-bucket'
    out_folder = 'test-folder'

    lookoutvision_stubber.stub_describe_model(
        project_name, model_version, model_arn, status, {
            'description': description, 'message': message, 'created': created,
            'trained': trained, 'recall': recall, 'precision': precision, 'f1': f1,
            'out_bucket': out_buck, 'out_folder': out_folder
        }, error_code=error_code)

    if error_code is None:
        Models.describe_model(lookoutvision_client, project_name, model_version)
    else:
        with pytest.raises(ClientError) as exc_info:
            Models.describe_model(lookoutvision_client, project_name, model_version)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_models(make_stubber, monkeypatch, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model = 'test-model'

    lookoutvision_stubber.stub_list_models(
        project_name, [model], error_code=error_code)

    monkeypatch.setattr(Models, 'describe_model', lambda x, y, z: None)

    if error_code is None:
        Models.describe_models(lookoutvision_client, project_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            Models.describe_models(lookoutvision_client, project_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_model(make_stubber, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'

    lookoutvision_stubber.stub_delete_model(project_name, model_version)
    lookoutvision_stubber.stub_list_models(project_name, [], error_code=error_code)

    if error_code is None:
        Models.delete_model(lookoutvision_client, project_name, model_version)
    else:
        with pytest.raises(ClientError) as exc_info:
            Models.delete_model(lookoutvision_client, project_name, model_version)
        assert exc_info.value.response['Error']['Code'] == error_code
