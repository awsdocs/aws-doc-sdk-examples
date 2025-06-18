# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import io
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, EndpointConnectionError, BotoCoreError

from database.neptune_execute_gremlin_explain_query import execute_gremlin_query

def test_execute_gremlin_query(capfd):
    """
    Unit test for execute_gremlin_query().
    Tests: success with output, ClientError, BotoCoreError, and general Exception.
    """
    # Mock the Neptune client
    mock_client = MagicMock()

    # --- Success case with valid StreamingBody output ---
    mock_body = io.BytesIO(b'{"explain": "details"}')
    mock_client.execute_gremlin_explain_query.return_value = {
        "output": mock_body
    }

    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Querying Neptune..." in out
    assert "Full Response:" in out
    assert '{"explain": "details"}' in out

    # --- ClientError case ---
    mock_client.execute_gremlin_explain_query.side_effect = ClientError(
        {"Error": {"Code": "BadRequest", "Message": "Invalid query"}},
        operation_name="ExecuteGremlinExplainQuery"
    )

    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Error calling Neptune: Invalid query" in out

    # --- Reset side effect ---
    mock_client.execute_gremlin_explain_query.side_effect = None

    # --- BotoCoreError (e.g., EndpointConnectionError) ---
    mock_client.execute_gremlin_explain_query.side_effect = EndpointConnectionError(
        endpoint_url="https://neptune.amazonaws.com"
    )

    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "BotoCore error:" in out

    # --- Reset side effect ---
    mock_client.execute_gremlin_explain_query.side_effect = None

    # --- Unexpected Exception case ---
    mock_client.execute_gremlin_explain_query.side_effect = Exception("Boom")

    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Boom" in out
