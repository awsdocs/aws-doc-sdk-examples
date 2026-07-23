# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon Data Firehose Basics Scenario.

These tests run the full scenario against real AWS services.
No mocking of the Firehose service is used.

Run with: pytest test_firehose_scenario.py -v
"""

import pytest
import boto3

from firehose_wrapper import FirehoseWrapper
from scenario_firehose_basics import FirehoseScenario


@pytest.mark.integ
def test_run_firehose_scenario(capsys):
    """
    Integration test that runs the full Firehose basics scenario.
    Tests all operations: create, describe, list, tag, encrypt, put records,
    decrypt, and cleanup.
    """
    firehose_wrapper = FirehoseWrapper.from_client()
    scenario = FirehoseScenario(firehose_wrapper)

    try:
        scenario.run_scenario()
    except Exception as e:
        # If scenario fails, ensure cleanup still happens
        # (scenario has its own try/finally, but be safe)
        pytest.fail(f"Scenario failed with error: {e}")

    captured = capsys.readouterr()
    assert "Amazon Data Firehose Basics Scenario completed successfully!" in captured.out


@pytest.mark.integ
def test_hello_firehose(capsys):
    """
    Integration test for the Hello Firehose example.
    Verifies that ListDeliveryStreams can be called successfully.
    """
    from firehose_hello import hello_firehose

    try:
        hello_firehose()
    except Exception as e:
        pytest.fail(f"Hello Firehose failed with error: {e}")

    captured = capsys.readouterr()
    assert "Amazon Data Firehose Hello" in captured.out