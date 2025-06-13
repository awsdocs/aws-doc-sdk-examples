# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError

from neptune_scenario import create_db_cluster  # Replace with your actual module path

def test_create_db_cluster():
    """
    Unit test for create_db_cluster().
    Tests success, missing cluster ID, ClientError, and unexpected exceptions,
    all in one test to follow the single-method test style.
    """
    # --- Success case ---
    mock_neptune = MagicMock()
    mock_neptune.create_db_cluster.return_value = {
        "DBCluster": {
            "DBClusterIdentifier": "test-cluster"
        }
    }
    cluster_id = create_db_cluster(mock_neptune, "test-cluster")
    assert cluster_id == "test-cluster"
    mock_neptune.create_db_cluster.assert_called_once()

    # --- Missing cluster ID raises RuntimeError ---
    mock_neptune.create_db_cluster.return_value = {"DBCluster": {}}
    with pytest.raises(RuntimeError, match="Cluster created but no ID returned"):
        create_db_cluster(mock_neptune, "missing-id-cluster")

    # --- ClientError is wrapped and re-raised ---
    mock_neptune.create_db_cluster.side_effect = ClientError(
        {
            "Error": {
                "Code": "AccessDenied",
                "Message": "You do not have permission."
            }
        },
        operation_name="CreateDBCluster"
    )
    with pytest.raises(ClientError) as exc_info:
        create_db_cluster(mock_neptune, "denied-cluster")
    assert "Failed to create DB cluster 'denied-cluster'" in str(exc_info.value)

    # --- Unexpected exception raises RuntimeError ---
    mock_neptune.create_db_cluster.side_effect = Exception("Unexpected failure")
    with pytest.raises(RuntimeError, match="Unexpected error creating DB cluster"):
        create_db_cluster(mock_neptune, "fail-cluster")
