# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
import pytest

from keyspace import KeyspaceWrapper
from scenario_get_started_keyspaces import KeyspaceScenario
from query import QueryManager


@pytest.fixture
def mock_wait(monkeypatch):
    return


@pytest.mark.skip(
    reason="Skip until shared resources are part of the Docker environment."
)
@pytest.mark.integ
def test_run_keyspace_scenario_integ(input_mocker, capsys):
    scenario = KeyspaceScenario(KeyspaceWrapper.from_client())

    input_mocker.mock_answers(
        [
            "doc_example_test_keyspace",  # Create keyspace.
            "movietabletest",  # Create table.
            "",  # Ensure TLS cert.
            1,  # Query table.
            "",
            "y",  # Update and restore table.
            "y",  # Cleanup.
        ]
    )

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out
