# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from botocore.exceptions import ClientError
from neptune_stubber import Neptune
from neptune_scenario import delete_db_subnet_group  # Adjust if needed


def test_delete_db_subnet_group():
    boto_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(boto_client)

    stubber.stub_delete_db_subnet_group("my-subnet-group")
    delete_db_subnet_group(stubber.client, "my-subnet-group")

    stubber.stub_delete_db_subnet_group(
        "unauthorized-subnet",
        error_code="AccessDenied"
    )

    with pytest.raises(ClientError) as exc_info:
        delete_db_subnet_group(stubber.client, "unauthorized-subnet")

    assert "AccessDenied" in str(exc_info.value)
