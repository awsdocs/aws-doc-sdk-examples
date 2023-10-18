# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier:  Apache-2.0

"""
Unit tests for find_running_models.py.
"""

import pytest
from botocore.exceptions import ClientError
import datetime
import boto3
import models
from boto3.session import Session

from find_running_models import find_running_models, find_running_models_in_project


@pytest.mark.parametrize(
    "error_code,stop_on_method",
    [
        (None, None),
        ("TestException", "stub_list_models"),
        ("TestException", "stub_describe_model"),
    ],
)
def test_find_models_in_project(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client("lookoutvision")
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = "test-project_name"
    model = "test-model"
    model_version = "test-model"
    model_arn = "test-arn"
    description = "test description"
    status = "HOSTED"
    message = "Test message!"
    created = datetime.datetime.now()
    trained = created + datetime.timedelta(minutes=10)
    recall = 0.3
    precision = 0.5
    f1 = 0.7
    out_buck = "doc-example-bucket"
    out_folder = "test-folder"

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(lookoutvision_stubber.stub_list_models, project_name, [model])
        runner.add(
            lookoutvision_stubber.stub_describe_model,
            project_name,
            model_version,
            model_arn,
            status,
            {
                "description": description,
                "message": message,
                "created": created,
                "trained": trained,
                "recall": recall,
                "precision": precision,
                "f1": f1,
                "out_bucket": out_buck,
                "out_folder": out_folder,
            },
        )

    if error_code is None:
        running_models = find_running_models_in_project(
            lookoutvision_client, project_name
        )
        assert len(running_models) == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            find_running_models_in_project(lookoutvision_client, project_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code,stop_on_method",
    [
        (None, None),
        ("TestException", "stub_list_projects"),
        ("TestException", "stub_list_models"),
        ("TestException", "stub_describe_model"),
    ],
)
def test_find_running_models(
    make_stubber, stub_runner, monkeypatch, error_code, stop_on_method
):
    boto3_session = boto3.Session()
    lookoutvision_client = boto3.client("lookoutvision")
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = "test-project_name"
    model = "test-model"
    model_version = "test-model"
    model_arn = "test-arn"
    description = "test description"
    status = "HOSTED"
    message = "Test message!"
    created = datetime.datetime.now()
    trained = created + datetime.timedelta(minutes=10)
    recall = 0.3
    precision = 0.5
    f1 = 0.7
    out_buck = "doc-example-bucket"
    out_folder = "test-folder"
    project_arn = "test-arn"
    region = "us-east-1"

    monkeypatch.setattr(
        boto3_session, "client", lambda c, region_name: lookoutvision_client
    )
    # monkeypatch.setattr(Session, 'profile_name', 'lookoutvision-access')

    # Patch AWS Region list
    monkeypatch.setattr(
        boto3_session, "get_available_regions", lambda service_name: [region]
    )
    # Patch lookoutvision client to manage multiple AWS Region clients.
    # monkeypatch.setattr(boto3, 'client', get_boto_entity)

    # Set up stubbed calls needed to mock getting running models.
    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            lookoutvision_stubber.stub_list_projects,
            [project_name],
            [{"arn": project_arn, "created": created}],
        )
        runner.add(lookoutvision_stubber.stub_list_models, project_name, [model])
        runner.add(
            lookoutvision_stubber.stub_describe_model,
            project_name,
            model_version,
            model_arn,
            status,
            {
                "description": description,
                "message": message,
                "created": created,
                "trained": trained,
                "recall": recall,
                "precision": precision,
                "f1": f1,
                "out_bucket": out_buck,
                "out_folder": out_folder,
            },
        )

    if error_code is None:
        running_models = find_running_models(boto3_session)
        assert len(running_models) == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            find_running_models(boto3_session)
        assert exc_info.value.response["Error"]["Code"] == error_code
