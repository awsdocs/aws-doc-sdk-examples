import io
import pytest
from botocore.response import StreamingBody
from botocore.exceptions import ClientError
from analytics.neptune_analytics_query_example import run_open_cypher_query
from neptune_graph_stubber import NeptuneGraphStubber

GRAPH_ID = "test-graph-id"

def test_execute_gremlin_profile_query(capfd):
    stubber = NeptuneGraphStubber()
    client = stubber.get_client()

    # --- Success case with payload ---
    stubber.activate()
    payload_bytes = b'{"results": "some data"}'
    response_body = StreamingBody(io.BytesIO(payload_bytes), len(payload_bytes))

    stubber.stubber.add_response(
        "execute_query",
        {"payload": response_body},
        {
            "graphIdentifier": GRAPH_ID,
            "queryString": "MATCH (n {code: 'ANC'}) RETURN n",
            "language": "OPEN_CYPHER"
        }
    )
    run_open_cypher_query(client, GRAPH_ID)
    out, _ = capfd.readouterr()
    assert '{"results": "some data"}' in out
    stubber.deactivate()

    # --- Success case with empty payload ---
    stubber.activate()
    empty_payload = StreamingBody(io.BytesIO(b''), 0)

    stubber.stubber.add_response(
        "execute_query",
        {"payload": empty_payload},
        {
            "graphIdentifier": GRAPH_ID,
            "queryString": "MATCH (n {code: 'ANC'}) RETURN n",
            "language": "OPEN_CYPHER"
        }
    )
    run_open_cypher_query(client, GRAPH_ID)
    out, _ = capfd.readouterr()
    assert out.strip() == ""  # Empty line
    stubber.deactivate()

    # --- ClientError case ---
    stubber.activate()
    stubber.stubber.add_client_error(
        "execute_query",
        service_error_code="ValidationException",  # <-- Updated to a valid exception code
        service_message="Client error occurred",
        http_status_code=400,
        expected_params={
            "graphIdentifier": GRAPH_ID,
            "queryString": "MATCH (n {code: 'ANC'}) RETURN n",
            "language": "OPEN_CYPHER"
        }
    )
    run_open_cypher_query(client, GRAPH_ID)
    out, _ = capfd.readouterr()
    assert "ClientError: Client error occurred" in out
    stubber.deactivate()

    # --- Generic Exception case ---
    # Deactivate stubber to allow monkeypatching client.execute_query
    stubber.deactivate()

    def raise_generic_error(**kwargs):
        raise Exception("Generic failure")

    client.execute_query = raise_generic_error
    run_open_cypher_query(client, GRAPH_ID)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Generic failure" in out

