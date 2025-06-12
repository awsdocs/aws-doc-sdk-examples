import json
import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, EndpointConnectionError

from database.GremlinProfileQueryExample import execute_gremlin_profile_query  # Adjust path as needed


def test_execute_gremlin_profile_query(capfd):
    """
    Unit test for execute_gremlin_profile_query().
    Tests success, no output, ClientError, BotoCoreError, and general Exception handling.
    """
    # --- Success case with valid output ---
    mock_client = MagicMock()
    mock_client.execute_gremlin_profile_query.return_value = {
        "output": {"metrics": {"dur": 500, "steps": 3}}
    }

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Query Profile Output:" in out
    assert '"dur": 500' in out

    # --- Success case with no output ---
    mock_client.execute_gremlin_profile_query.return_value = {"output": None}
    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "No output returned from the profile query." in out

    # --- ClientError case ---
    mock_client.execute_gremlin_profile_query.side_effect = ClientError(
        {"Error": {"Code": "BadRequest", "Message": "Invalid query"}},
        operation_name="ExecuteGremlinProfileQuery"
    )
    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Neptune error: Invalid query" in out

    # --- BotoCoreError case ---
    mock_client.execute_gremlin_profile_query.side_effect = EndpointConnectionError(
        endpoint_url="https://neptune.amazonaws.com"
    )
    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected Boto3 error" in out

    # --- Unexpected exception case ---
    mock_client.execute_gremlin_profile_query.side_effect = Exception("Boom")
    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Boom" in out
