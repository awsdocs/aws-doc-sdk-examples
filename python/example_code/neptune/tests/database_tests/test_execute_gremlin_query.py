import types
import pytest
from botocore.exceptions import ClientError, BotoCoreError
from database.neptune_execute_gremlin_query import execute_gremlin_query
from neptune_data_stubber import NeptuneDateStubber

def test_execute_gremlin_query(capfd):
    stubber = NeptuneDateStubber()
    client = stubber.get_client()
    stubber.activate()

    try:
        # --- Success case with valid output ---
        stubber.add_execute_gremlin_query_stub(
            gremlin_query="g.V().has('code', 'ANC')",
            response_dict={"result": {"metrics": {"dur": 500, "steps": 3}}}
        )
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Executing Gremlin query..." in out
        assert "Response is:" in out
        assert '"dur": 500' in out or "'dur': 500" in out

        # --- Success case with None result ---
        stubber.stubber.assert_no_pending_responses()
        stubber.add_execute_gremlin_query_stub(
            gremlin_query="g.V().has('code', 'ANC')",
            response_dict={"result": None}
        )
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Response is:" in out
        assert "None" in out

        # --- ClientError case ---
        stubber.stubber.assert_no_pending_responses()
        stubber.stubber.add_client_error(
            method='execute_gremlin_query',
            service_error_code='BadRequest',
            service_message='Invalid query',
            expected_params={"gremlinQuery": "g.V().has('code', 'ANC')"}
        )
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Neptune error: Invalid query" in out

        # --- BotoCoreError case (Fix 1) ---
        stubber.stubber.assert_no_pending_responses()

        def raise_boto_core_error(*args, **kwargs):
            raise BotoCoreError()  # ✅ No arguments

        client.execute_gremlin_query = types.MethodType(raise_boto_core_error, client)
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Unexpected Boto3 error" in out

        # --- Generic Exception case ---
        def raise_generic_exception(*args, **kwargs):
            raise Exception("Boom")

        client.execute_gremlin_query = types.MethodType(raise_generic_exception, client)
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Unexpected error: Boom" in out

    finally:
        stubber.deactivate()
