import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError
from analytics.neptune_analytics_query_example import run_open_cypher_query  # Adjust import as needed


class FakePayload:
    def __init__(self, data: bytes):
        self._data = data

    def read(self):
        return self._data


class EmptyPayload:
    def read(self):
        return b''  # empty bytes simulates empty payload


# Fake exceptions to satisfy except clauses in service code
class FakeInternalServerException(Exception):
    pass


class FakeBadRequestException(Exception):
    pass


class FakeLimitExceededException(Exception):
    pass


def test_execute_gremlin_profile_query(capfd):
    mock_client = MagicMock()
    graph_id = "test-graph-id"

    # Attach fake exceptions for service error handling
    mock_client.exceptions = MagicMock()
    mock_client.exceptions.InternalServerException = FakeInternalServerException
    mock_client.exceptions.BadRequestException = FakeBadRequestException
    mock_client.exceptions.LimitExceededException = FakeLimitExceededException

    # --- Success case with payload ---
    mock_client.execute_query.return_value = {
        "payload": FakePayload(b'{"results": "some data"}')
    }
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert '{"results": "some data"}' in out

    # --- Success case with empty payload ---
    mock_client.execute_query.return_value = {"payload": EmptyPayload()}
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert out == "\n"

    # --- ClientError case ---
    mock_client.execute_query.side_effect = ClientError(
        {"Error": {"Message": "Client error occurred"}}, "ExecuteQuery"
    )
    run_open_cypher_query(mock_client, graph_id)
    out, _ = capfd.readouterr()
    assert "ClientError: Client error occurred" in out

    # --- Generic exception case ---
    mock_client.execute_query.side_effect = Exception("Generic failure")

    # Call function inside try/except, but **capture output immediately**
    try:
        run_open_cypher_query(mock_client, graph_id)
    except Exception:
        pass

    out, _ = capfd.readouterr()
    assert "Unexpected error: Generic failure" in out
