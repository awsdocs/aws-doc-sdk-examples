# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest
from botocore.exceptions import ClientError
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import describe_db_clusters


@pytest.fixture
def neptune_client():
    return boto3.client("neptune", region_name="us-east-1")

def test_cluster_found_and_prints_info(neptune_client):
    stubber = Neptune(neptune_client)
    stubber.stubber.activate()
    stubber.stubber.add_response(
        "describe_db_clusters",
        {
            "DBClusters": [
                {
                    "DBClusterIdentifier": "test-cluster",
                    "Status": "available",
                    "Engine": "neptune",
                    "EngineVersion": "1.2.0.0",
                    "Endpoint": "test-endpoint",
                    "ReaderEndpoint": "reader-endpoint",
                    "AvailabilityZones": ["us-east-1a"],
                    "DBSubnetGroup": "default",
                    "VpcSecurityGroups": [{"VpcSecurityGroupId": "sg-12345"}],
                    "StorageEncrypted": True,
                    "IAMDatabaseAuthenticationEnabled": True,
                    "BackupRetentionPeriod": 7,
                    "PreferredBackupWindow": "07:00-09:00",
                    "PreferredMaintenanceWindow": "sun:05:00-sun:09:00",
                }
            ]
        },
        expected_params={"DBClusterIdentifier": "test-cluster"},
    )

    describe_db_clusters(neptune_client, "test-cluster")
    stubber.stubber.deactivate()

def test_cluster_not_found_raises_client_error(neptune_client):
    stubber = Neptune(neptune_client)
    stubber.stubber.activate()

    stubber.stubber.add_response(
        "describe_db_clusters",
        {"DBClusters": []},
        expected_params={"DBClusterIdentifier": "test-cluster"},
    )

    with pytest.raises(ClientError) as excinfo:
        describe_db_clusters(neptune_client, "test-cluster")

    assert excinfo.value.response["Error"]["Code"] == "DBClusterNotFound"
    stubber.stubber.deactivate()


def test_client_error_from_paginate_is_propagated(neptune_client):
    stubber = Neptune(neptune_client)
    stubber.stubber.activate()

    stubber.stub_describe_db_cluster_status(
        cluster_id="test-cluster",
        statuses=[],
        error_code="AccessDeniedException",
    )

    with pytest.raises(ClientError) as excinfo:
        describe_db_clusters(neptune_client, "test-cluster")

    assert excinfo.value.response["Error"]["Code"] == "AccessDeniedException"
    stubber.stubber.deactivate()

