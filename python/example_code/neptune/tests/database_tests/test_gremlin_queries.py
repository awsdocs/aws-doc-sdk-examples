import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, BotoCoreError
from database.neptune_gremlin_query_example import execute_gremlin_query

def test_execute_gremlin_query(capfd):
    # Mock the client
    mock_client = MagicMock()

    # --- Case 1: Success with result ---
    mock_client.execute_gremlin_query.return_value = {
        "result": {"data": ["some", "nodes"]}
    }
    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Querying Neptune..." in out
    assert "Query Result:" in out
    assert "some" in out

    # --- Case 2: Success with no result ---
    mock_client.execute_gremlin_query.return_value = {"result": None}
    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "No result returned from the query." in out

    # --- Case 3: ClientError ---
    mock_client.execute_gremlin_query.side_effect = ClientError(
        {"Error": {"Message": "BadRequest"}}, operation_name="ExecuteGremlinQuery"
    )
    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Error calling Neptune: BadRequest" in out

    # --- Case 4: BotoCoreError ---
    mock_client.execute_gremlin_query.side_effect = BotoCoreError()
    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "BotoCore error:" in out

    # --- Case 5: Generic exception ---
    mock_client.execute_gremlin_query.side_effect = Exception("Unexpected failure")
    execute_gremlin_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Unexpected failure" in out
