# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from botocore.exceptions import ClientError
from neptune_stubber import Neptune
from neptune_scenario import create_db_cluster  # Your actual function


def test_create_db_cluster():
    # Create Boto3 Neptune client and attach Stubber wrapper
    boto_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(boto_client)

    # --- Success case ---
    stubber.stub_create_db_cluster(
        cluster_id="test-cluster"
        # engine, deletion_protection, backup_retention_period defaulted
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

    # --- ClientError is wrapped in RuntimeError ---
    stubber.stub_create_db_cluster(
        cluster_id="denied-cluster",
        error_code="AccessDenied"
    )
    with pytest.raises(RuntimeError) as exc_info:
        create_db_cluster(stubber.client, "denied-cluster")
    assert "AWS error [AccessDenied]" in str(exc_info.value)

    # --- Unexpected exception raises RuntimeError ---
    def raise_generic_exception(**kwargs):
        raise Exception("Unexpected failure")

    stubber.client.create_db_cluster = raise_generic_exception
    with pytest.raises(RuntimeError, match="Unexpected error creating DB cluster 'fail-cluster'"):
        create_db_cluster(stubber.client, "fail-cluster")
