# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


import pytest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError

from NeptuneScenario import stop_db_cluster  # Update as needed

# Use small values to speed up the test
POLL_INTERVAL_SECONDS = 0.1
TIMEOUT_SECONDS = 0.3


@patch("NeptuneScenario.time.sleep", return_value=None)  # avoid actual delay
@patch("NeptuneScenario.POLL_INTERVAL_SECONDS", POLL_INTERVAL_SECONDS)
@patch("NeptuneScenario.TIMEOUT_SECONDS", TIMEOUT_SECONDS)
def test_stop_db_cluster(mock_sleep):
    """
    Unit test for stop_db_cluster().
    Covers: success, timeout, stop call failure, and paginator failure.
    """
    # --- Success case ---
    mock_neptune = MagicMock()
    paginator_mock = MagicMock()
    mock_neptune.get_paginator.return_value = paginator_mock

    # First response: stopping, then stopped
    paginator_mock.paginate.side_effect = [
        [{'DBClusters': [{'Status': 'stopping'}]}],
        [{'DBClusters': [{'Status': 'stopped'}]}]
    ]
    mock_neptune.stop_db_cluster.return_value = {}

    stop_db_cluster(mock_neptune, "my-cluster")

    mock_neptune.stop_db_cluster.assert_called_once_with(DBClusterIdentifier="my-cluster")
    mock_neptune.get_paginator.assert_called_once_with("describe_db_clusters")
    assert paginator_mock.paginate.call_count == 2

    # --- Timeout case ---
    mock_neptune.reset_mock()
    paginator_mock = MagicMock()
    mock_neptune.get_paginator.return_value = paginator_mock

    def always_stopping(*args, **kwargs):
        return [{'DBClusters': [{'Status': 'stopping'}]}]

    paginator_mock.paginate.side_effect = always_stopping
    mock_neptune.stop_db_cluster.return_value = {}

    with pytest.raises(RuntimeError, match="Timeout waiting for cluster 'timeout-cluster' to stop."):
        stop_db_cluster(mock_neptune, "timeout-cluster")

    # --- stop_db_cluster raises ClientError ---
    mock_neptune.stop_db_cluster.side_effect = ClientError(
        {
            "Error": {
                "Code": "AccessDenied",
                "Message": "Not authorized"
            }
        },
        operation_name="StopDBCluster"
    )

    with pytest.raises(ClientError) as exc_info:
        stop_db_cluster(mock_neptune, "fail-cluster")
    assert exc_info.value.response["Error"]["Code"] == "AccessDenied"

    # --- Paginator throws ClientError ---
    mock_neptune.stop_db_cluster.side_effect = None  # clear previous error
    paginator_mock = MagicMock()
    mock_neptune.get_paginator.return_value = paginator_mock

    paginator_mock.paginate.side_effect = ClientError(
        {
            "Error": {
                "Code": "Throttling",
                "Message": "Too many requests"
            }
        },
        operation_name="DescribeDBClusters"
    )

    with pytest.raises(ClientError) as exc_info:
        stop_db_cluster(mock_neptune, "paginator-error")
    assert exc_info.value.response["Error"]["Code"] == "Throttling"
