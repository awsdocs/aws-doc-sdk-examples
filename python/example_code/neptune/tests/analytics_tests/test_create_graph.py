# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError, BotoCoreError
from analytics.create_neptune_graph_example import execute_create_graph  # Adjust import as needed

def test_execute_create_graph(capfd):
    mock_client = MagicMock()

    # --- Success case ---
    mock_client.create_graph.return_value = {
        "GraphName": "test-graph",
        "GraphArn": "arn:aws:neptune:region:123456789012:graph/test-graph",
        "GraphEndpoint": "https://test-graph.endpoint"
    }

    execute_create_graph(mock_client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Creating Neptune graph..." in out
    assert "Graph created successfully!" in out
    assert "Graph Name: test-graph" in out
    assert "Graph ARN: arn:aws:neptune:region:123456789012:graph/test-graph" in out
    assert "Graph Endpoint: https://test-graph.endpoint" in out

    # --- ClientError case ---
    mock_client.create_graph.side_effect = ClientError(
        {"Error": {"Message": "Client error occurred"}}, "CreateGraph"
    )
    execute_create_graph(mock_client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Failed to create graph: Client error occurred" in out

    # --- BotoCoreError case ---
    mock_client.create_graph.side_effect = BotoCoreError()
    execute_create_graph(mock_client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Failed to create graph:" in out  # check prefix only

    # --- Generic Exception case ---
    mock_client.create_graph.side_effect = Exception("Generic failure")
    execute_create_graph(mock_client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Unexpected error: Generic failure" in out
