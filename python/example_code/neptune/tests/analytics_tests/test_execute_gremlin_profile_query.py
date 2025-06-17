import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError
from analytics.neptune_analytics_query_example import run_open_cypher_query  # Adjust import


class FakePayload:
    def __init__(self, data: bytes):
        self._data = data
    def read(self):
        return self._data


def test_execute_gremlin_profile_query(capfd):
    mock_client = MagicMock()
    graph_id = "test-graph-id"

    # --- Success case with Payload ---
    mock_client.execute_query.return_value = {
        "Payload": FakePayload(b'{"results": "some data"}')
    }
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert '{"results": "some data"}' in out

    # --- Success case with no Payload ---
    mock_client.execute_query.return_value = {}
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert "No query result returned." in out

    # --- ClientError case ---
    mock_client.execute_query.side_effect = ClientError(
        {"Error": {"Message": "Client error occurred"}}, "ExecuteQuery"
    )
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert "NeptuneGraph ClientError: Client error occurred" in out

    # --- Generic exception case ---
    mock_client.execute_query.side_effect = Exception("Generic failure")
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Generic failure" in out
