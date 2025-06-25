# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from botocore.exceptions import ClientError
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import delete_db_cluster

def test_delete_db_cluster_success_and_clienterror():
    neptune_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(neptune_client)

    stubber.stub_delete_db_cluster("test-cluster")
    delete_db_cluster(neptune_client, "test-cluster")  # Should not raise

    stubber.stub_delete_db_cluster("unauthorized-cluster", error_code="AccessDenied")

    with pytest.raises(ClientError) as exc_info:
        delete_db_cluster(neptune_client, "unauthorized-cluster")

    assert "AccessDenied" in str(exc_info.value)

def test_delete_db_cluster_unexpected_exception(monkeypatch):
    client = boto3.client("neptune", region_name="us-east-1")

    def raise_unexpected_error(**kwargs):
        raise Exception("Unexpected error")

    monkeypatch.setattr(client, "delete_db_cluster", raise_unexpected_error)

    with pytest.raises(Exception, match="Unexpected error"):
        delete_db_cluster(client, "error-cluster")

