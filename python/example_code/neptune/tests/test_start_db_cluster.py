# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import start_db_cluster

def test_start_db_cluster_success(monkeypatch):
    cluster_id = "my-cluster"
    client = boto3.client("neptune", region_name="us-east-1")
    neptune = Neptune(client)

    neptune.stubber.add_response(
        "start_db_cluster",
        {"DBCluster": {"DBClusterIdentifier": cluster_id}},
        {"DBClusterIdentifier": cluster_id}
    )

    statuses = ["starting"] * 5 + ["available"]
    for status in statuses:
        neptune.stubber.add_response(
            "describe_db_clusters",
            {"DBClusters": [{"DBClusterIdentifier": cluster_id, "Status": status}]},
            {"DBClusterIdentifier": cluster_id}
        )

    monkeypatch.setattr("example_code.neptune.neptune_scenario.time.sleep", lambda _: None)

    monkeypatch.setattr("example_code.neptune.neptune_scenario.POLL_INTERVAL_SECONDS", 0.01)
    monkeypatch.setattr("example_code.neptune.neptune_scenario.TIMEOUT_SECONDS", 1)

    start_db_cluster(client, cluster_id)
    neptune.stubber.deactivate()



