# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import types
from botocore.exceptions import ClientError, EndpointConnectionError
from test_tools.neptune_data_stubber import NeptuneDateStubber
from example_code.neptune.database.neptune_execute_gremlin_explain_query import execute_gremlin_query

def test_execute_gremlin_query(capfd):
    stubber = NeptuneDateStubber()
    client = stubber.get_client()
    stubber.activate()

    try:
        response_payload = '{"explain": "details"}'
        stubber.add_execute_gremlin_explain_query_stub(
            gremlin_query="g.V().has('code', 'ANC')",
            response_payload=response_payload
        )

        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Querying Neptune..." in out
        assert "Full Response:" in out
        assert '{"explain": "details"}' in out

        stubber.stubber.assert_no_pending_responses()
        stubber.stubber.add_client_error(
            method='execute_gremlin_explain_query',
            service_error_code='BadRequest',
            service_message='Invalid query',
            expected_params={"gremlinQuery": "g.V().has('code', 'ANC')"}
        )
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Error calling Neptune: Invalid query" in out

        stubber.stubber.assert_no_pending_responses()

        def raise_endpoint_connection_error(*args, **kwargs):
            raise EndpointConnectionError(endpoint_url="https://neptune.amazonaws.com")

        client.execute_gremlin_explain_query = types.MethodType(raise_endpoint_connection_error, client)
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "BotoCore error:" in out

        # --- Unexpected Exception case ---
        def raise_generic_exception(*args, **kwargs):
            raise Exception("Boom")

        client.execute_gremlin_explain_query = types.MethodType(raise_generic_exception, client)
        execute_gremlin_query(client)
        out, _ = capfd.readouterr()
        assert "Unexpected error: Boom" in out

    finally:
        stubber.deactivate()
