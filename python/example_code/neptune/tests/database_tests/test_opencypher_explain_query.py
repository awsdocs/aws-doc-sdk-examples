import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, BotoCoreError
from database.neptune_execute_open_cypher_query import execute_open_cypher_explain_query


class MockStreamingBody:
    def __init__(self, content: bytes):
        self._content = content

    def read(self):
        return self._content


def test_execute_opencypher_explain_query(capfd):
    mock_client = MagicMock()

    # --- Case 1: Successful result (StreamingBody with bytes) ---
    mock_client.execute_open_cypher_explain_query.return_value = {
        "results": MockStreamingBody(b"mocked byte explain output")
    }
    execute_open_cypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Explain Results:" in out
    assert "mocked byte explain output" in out

    # --- Case 2: No results (None) ---
    mock_client.execute_open_cypher_explain_query.return_value = {
        "results": None
    }
    execute_open_cypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "No explain results returned." in out

    # --- Case 3: ClientError ---
    mock_client.execute_open_cypher_explain_query.side_effect = ClientError(
        {"Error": {"Message": "Invalid OpenCypher query"}}, "ExecuteOpenCypherExplainQuery"
    )
    execute_open_cypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Neptune error: Invalid OpenCypher query" in out

    # --- Case 4: BotoCoreError ---
    mock_client.execute_open_cypher_explain_query.side_effect = BotoCoreError()
    execute_open_cypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "BotoCore error:" in out

    # --- Case 5: Generic Exception ---
    mock_client.execute_open_cypher_explain_query.side_effect = Exception("Some generic error")
    execute_open_cypher_explain_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Some generic error" in out
