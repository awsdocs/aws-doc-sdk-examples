# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import stop_db_cluster

@pytest.fixture
def neptune_client():
    return boto3.client('neptune', region_name='us-west-2')

def test_stop_db_cluster_with_stubbed_responses(neptune_client):
    cluster_id = "timeout-cluster"
    neptune = Neptune(neptune_client)

    neptune.stubber.add_response(
        "stop_db_cluster",
        {"DBCluster": {"DBClusterIdentifier": cluster_id}},
        {"DBClusterIdentifier": cluster_id}
    )

    for _ in range(9):
        neptune.stubber.add_response(
            "describe_db_clusters",
            {
                "DBClusters": [
                    {"DBClusterIdentifier": cluster_id, "Status": "stopping"}
                ]
            },
            {"DBClusterIdentifier": cluster_id}
        )

    neptune.stubber.add_response(
        "describe_db_clusters",
        {
            "DBClusters": [
                {"DBClusterIdentifier": cluster_id, "Status": "stopped"}
            ]
        },
        {"DBClusterIdentifier": cluster_id}
    )

    result = stop_db_cluster(neptune.client, cluster_id)

    assert result is None
    neptune.stubber.deactivate()



