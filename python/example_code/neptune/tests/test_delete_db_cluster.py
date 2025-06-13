# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError

from neptune_scenario import delete_db_cluster  # Update with actual module name

def test_delete_db_cluster():
    """
    Unit test for delete_db_cluster().
    Tests success, AWS ClientError, and unexpected exception scenarios.
    """
    # --- Success case ---
    mock_neptune = MagicMock()
    mock_neptune.delete_db_cluster.return_value = {}

    delete_db_cluster(mock_neptune, "test-cluster")
    mock_neptune.delete_db_cluster.assert_called_once_with(
        DBClusterIdentifier="test-cluster",
        SkipFinalSnapshot=True
    )

    # --- AWS ClientError is raised ---
    mock_neptune = MagicMock()
    mock_neptune.delete_db_cluster.side_effect = ClientError(
        {
            "Error": {
                "Code": "AccessDenied",
                "Message": "You are not authorized to delete this cluster"
            }
        },
        operation_name="DeleteDBCluster"
    )

    with pytest.raises(ClientError) as exc_info:
        delete_db_cluster(mock_neptune, "unauthorized-cluster")
    assert "AccessDenied" in str(exc_info.value)

    # --- Unexpected Exception raises as-is ---
    mock_neptune = MagicMock()
    mock_neptune.delete_db_cluster.side_effect = Exception("Unexpected error")

    with pytest.raises(Exception, match="Unexpected error"):
        delete_db_cluster(mock_neptune, "error-cluster")
