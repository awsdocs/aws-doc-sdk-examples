# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import types
from botocore.exceptions import ClientError, BotoCoreError
from test_tools.neptune_data_stubber import NeptuneDateStubber
from example_code.neptune.database.neptune_execute_gremlin_profile_query import run_profile_query

def test_run_profile_query(capfd):
    stubber = NeptuneDateStubber()
    client = stubber.get_client()
    stubber.activate()

    try:
        # --- Success case with streaming output ---
        profile_response_payload = '{"metrics": {"dur": 500, "steps": 3}}'
        stubber.add_execute_gremlin_profile_query_stub(
            gremlin_query="g.V().has('code', 'ANC')",
            response_payload=profile_response_payload
        )
        run_profile_query(client)
        out, _ = capfd.readouterr()
        assert "Running Gremlin PROFILE query..." in out
        assert "Profile Query Result:" in out
        assert '"dur": 500' in out or "'dur': 500" in out

        # --- Success case with no output (output=None) ---
        stubber.stubber.assert_no_pending_responses()
        stubber.add_execute_gremlin_profile_query_stub(
            gremlin_query="g.V().has('code', 'ANC')",
            response_payload=""  # Empty string simulates no output
        )
        run_profile_query(client)
        out, _ = capfd.readouterr()
        # Because output is streaming body, empty string means output.read() returns '', so "No explain output returned." should NOT print
        # So, test that something is printed (could be empty)
        assert "Profile Query Result:" in out

        # --- ClientError case ---
        stubber.stubber.assert_no_pending_responses()
        stubber.stubber.add_client_error(
            method='execute_gremlin_profile_query',
            service_error_code='BadRequest',
            service_message='Invalid query',
            expected_params={"gremlinQuery": "g.V().has('code', 'ANC')"}
        )
        run_profile_query(client)
        out, _ = capfd.readouterr()
        assert "Failed to execute PROFILE query:" in out or "Neptune error:" in out or "Invalid query" in out

        # --- BotoCoreError case ---
        stubber.stubber.assert_no_pending_responses()

        def raise_boto_core_error(*args, **kwargs):
            raise BotoCoreError()

        client.execute_gremlin_profile_query = types.MethodType(raise_boto_core_error, client)
        run_profile_query(client)
        out, _ = capfd.readouterr()
        assert "Failed to execute PROFILE query:" in out or "BotoCore error" in out or "Unexpected Boto3 error" in out

        # --- Generic Exception case ---
        def raise_generic_exception(*args, **kwargs):
            raise Exception("Boom")

        client.execute_gremlin_profile_query = types.MethodType(raise_generic_exception, client)
        run_profile_query(client)
        out, _ = capfd.readouterr()
        assert "Failed to execute PROFILE query: Boom" in out

    finally:
        stubber.deactivate()
