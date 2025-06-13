import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, BotoCoreError
from database.open_cypher_explain_example import execute_opencypher_explain_query


def test_execute_opencypher_explain_query(capfd):
    mock_client = MagicMock()

    # --- Case 1: Successful result (bytes) ---
    mock_client.execute_open_cypher_explain_query.return_value = {
        "results": b"mocked byte explain output"
    }
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Explain Results:" in out
    assert "mocked byte explain output" in out

    # --- Case 2: Successful result (str) ---
    mock_client.execute_open_cypher_explain_query.return_value = {
        "results": "mocked string explain output"
    }
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Explain Results:" in out
    assert "mocked string explain output" in out

    # --- Case 3: No results ---
    mock_client.execute_open_cypher_explain_query.return_value = {
        "results": None
    }
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "No explain results returned." in out

    # --- Case 4: ClientError ---
    mock_client.execute_open_cypher_explain_query.side_effect = ClientError(
        {"Error": {"Message": "Invalid OpenCypher query"}}, "ExecuteOpenCypherExplainQuery"
    )
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Neptune error: Invalid OpenCypher query" in out

    # --- Case 5: BotoCoreError ---
    mock_client.execute_open_cypher_explain_query.side_effect = BotoCoreError()
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "BotoCore error:" in out

    # --- Case 6: Generic Exception ---
    mock_client.execute_open_cypher_explain_query.side_effect = Exception("Some generic error")
    execute_opencypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Some generic error" in out
