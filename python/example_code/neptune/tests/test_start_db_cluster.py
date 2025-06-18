# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import patch
import boto3
from neptune_scenario import start_db_cluster, TIMEOUT_SECONDS, POLL_INTERVAL_SECONDS
from neptune_stubber import Neptune  # Your custom stubber class

# Patch sleep to return immediately so polling is fast
@patch("neptune_scenario.time.sleep", return_value=None)
@patch("neptune_scenario.POLL_INTERVAL_SECONDS", 0.1)
@patch("neptune_scenario.TIMEOUT_SECONDS", 2)  # Enough time for 10 polls
def test_start_db_cluster_success(mock_sleep):
    cluster_id = "my-cluster"
    client = boto3.client("neptune", region_name="us-east-1")
    neptune = Neptune(client)

    neptune.stubber.add_response(
        "start_db_cluster",
        {"DBCluster": {"DBClusterIdentifier": cluster_id}},
        {"DBClusterIdentifier": cluster_id}
    )

    # Stub 9 "starting" statuses and 1 "available"
    statuses = ["starting"] * 9 + ["available"]
    for status in statuses:
        neptune.stubber.add_response(
            "describe_db_clusters",
            {
                "DBClusters": [{"DBClusterIdentifier": cluster_id, "Status": status}]
            },
            {"DBClusterIdentifier": cluster_id}
        )

    # Run the service method
    start_db_cluster(client, cluster_id)

    neptune.stubber.deactivate()

