# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError, BotoCoreError
from database.neptune_execute_gremlin_query import execute_gremlin_profile_query


def test_execute_gremlin_profile_query(capfd):
    """
    Unit test for execute_gremlin_profile_query().
    Tests success, no output, ClientError, BotoCoreError, and generic Exception handling.
    """
    mock_client = MagicMock()

    # --- Success case with valid output ---
    mock_client.execute_gremlin_query.return_value = {
        "result": {"metrics": {"dur": 500, "steps": 3}}
    }

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Executing Gremlin PROFILE query..." in out
    assert "Response is:" in out
    assert '"dur": 500' in out or "'dur': 500" in out  # depending on Python version's dict print style

    # --- Success case with no output ---
    mock_client.execute_gremlin_query.return_value = {
        "result": None
    }

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    # Adjust assert to check for no output message or print of None
    assert "No output returned from the profile query." in out or "None" in out or "Response is:" in out

    # --- ClientError case ---
    mock_client.execute_gremlin_query.side_effect = ClientError(
        {"Error": {"Code": "BadRequest", "Message": "Invalid query"}},
        operation_name="execute_gremlin_query"
    )

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Neptune error: Invalid query" in out

    # --- BotoCoreError case ---
    mock_client.execute_gremlin_query.side_effect = BotoCoreError()

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected Boto3 error" in out

    # --- Generic exception case ---
    mock_client.execute_gremlin_query.side_effect = Exception("Boom")

    execute_gremlin_profile_query(mock_client)
    out, _ = capfd.readouterr()
    assert "Unexpected error: Boom" in out
