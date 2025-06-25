# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import create_db_cluster

def test_create_db_cluster():
    boto_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(boto_client)

    stubber.stub_create_db_cluster(
        cluster_id="test-cluster"
    )
    cluster_id = create_db_cluster(stubber.client, "test-cluster")
    assert cluster_id == "test-cluster"

    stubber.stubber.add_response(
        "create_db_cluster",
        {"DBCluster": {}},
        expected_params={
            "DBClusterIdentifier": "missing-id-cluster",
            "Engine": "neptune",
            "DeletionProtection": False,
            "BackupRetentionPeriod": 1
        }
    )
    with pytest.raises(RuntimeError, match="Cluster created but no ID returned"):
        create_db_cluster(stubber.client, "missing-id-cluster")

    stubber.stub_create_db_cluster(
        cluster_id="denied-cluster",
        error_code="AccessDenied"
    )
    with pytest.raises(RuntimeError) as exc_info:
        create_db_cluster(stubber.client, "denied-cluster")
    assert "AWS error [AccessDenied]" in str(exc_info.value)

    def raise_generic_exception(**kwargs):
        raise Exception("Unexpected failure")

    stubber.client.create_db_cluster = raise_generic_exception
    with pytest.raises(RuntimeError, match="Unexpected error creating DB cluster 'fail-cluster'"):
        create_db_cluster(stubber.client, "fail-cluster")
