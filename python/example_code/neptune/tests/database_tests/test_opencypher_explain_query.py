import types
from botocore.exceptions import ClientError, BotoCoreError
from botocore.response import StreamingBody
from database.neptune_execute_open_cypher_query import execute_open_cypher_explain_query
from neptune_data_stubber import NeptuneDateStubber  # adjust import path accordingly
import io

def test_execute_opencypher_explain_query(capfd):
    stubber = NeptuneDateStubber()
    client = stubber.get_client()
    stubber.activate()

    try:
        # --- Case 1: Successful result (StreamingBody with bytes) ---
        explain_payload = 'mocked byte explain output'
        stubber.add_execute_open_cypher_explain_query_stub(
            open_cypher_query="MATCH (n {code: 'ANC'}) RETURN n",
            explain_mode="details",
            results_payload=explain_payload
        )
        execute_open_cypher_explain_query(client)
        out, _ = capfd.readouterr()
        assert "Explain Results:" in out
        assert explain_payload in out

        stubber.stubber.assert_no_pending_responses()

        # --- Case 2: No results (empty StreamingBody) ---
        empty_stream = StreamingBody(io.BytesIO(b""), 0)
        stubber.stubber.add_response(
            "execute_open_cypher_explain_query",
            {"results": empty_stream},
            {
                "openCypherQuery": "MATCH (n {code: 'ANC'}) RETURN n",
                "explainMode": "details"
            }
        )
        execute_open_cypher_explain_query(client)
        out, _ = capfd.readouterr()
        assert "Explain Results:" in out
        assert out.strip().endswith("Explain Results:")

        stubber.stubber.assert_no_pending_responses()

    finally:
        stubber.deactivate()

    # --- Case 3: ClientError ---
    def run_client_error_case():
        stubber = NeptuneDateStubber()
        client = stubber.get_client()
        stubber.activate()
        try:
            stubber.stubber.add_client_error(
                method='execute_open_cypher_explain_query',
                service_error_code='BadRequest',
                service_message='Invalid OpenCypher query',
                expected_params={
                    "openCypherQuery": "MATCH (n {code: 'ANC'}) RETURN n",
                    "explainMode": "details"
                }
            )
            execute_open_cypher_explain_query(client)
            out, _ = capfd.readouterr()
            assert "Neptune error: Invalid OpenCypher query" in out
        finally:
            stubber.deactivate()

    run_client_error_case()

    # --- Case 4: BotoCoreError (monkeypatch) ---
    def raise_boto_core_error(*args, **kwargs):
        raise BotoCoreError()

    client.execute_open_cypher_explain_query = types.MethodType(raise_boto_core_error, client)
    execute_open_cypher_explain_query(client)
    out, _ = capfd.readouterr()
    assert "BotoCore error:" in out

    # --- Case 5: Generic Exception (monkeypatch) ---
    def raise_generic_exception(*args, **kwargs):
        raise Exception("Some generic error")

    client.execute_open_cypher_explain_query = types.MethodType(raise_generic_exception, client)
    execute_open_cypher_explain_query(client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Some generic error" in out


