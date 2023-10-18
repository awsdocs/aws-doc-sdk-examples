# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for projects.py.
"""

import datetime
import boto3
from botocore.exceptions import ClientError
import pytest

from hello import Hello


@pytest.mark.parametrize(
    "error_code,stop_on_method", [(None, None), ("TestException", "stub_list_projects")]
)
def test_hello(make_stubber, stub_runner, error_code, stop_on_method):
    lookoutvision_client = boto3.client("lookoutvision")
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = "test-project"
    project_arn = "test-arn"
    created = datetime.datetime.now()
    model_version = "test-model"
    dataset = {"DatasetType": "testing", "StatusMessage": "nicely tested"}

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            lookoutvision_stubber.stub_list_projects,
            [project_name],
            [{"arn": project_arn, "created": created}],
        )

    if error_code is None:
        Hello.list_projects(lookoutvision_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            Hello.list_projects(lookoutvision_client)
        assert exc_info.value.response["Error"]["Code"] == error_code
