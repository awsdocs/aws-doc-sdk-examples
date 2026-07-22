# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon Kinesis Data Streams basics scenario.

These tests run against real AWS resources and will incur charges.
Run with: pytest test_kinesis_integ.py -v
"""

import json
import logging
import time

import boto3
import pytest

from kinesis_wrapper import KinesisWrapper
from scenario_kinesis import KinesisScenario

logger = logging.getLogger(__name__)


def pytest_configure(config):
    config.addinivalue_line(
        "markers",
        "integ: integration test that requires and uses AWS resources.",
    )


@pytest.mark.integ
def test_scenario_runs_successfully(capsys):
    """
    Integration test that runs the full Kinesis basics scenario.
    Verifies the scenario completes without errors and produces expected output.
    """
    wrapper = KinesisWrapper.from_client()
    scenario = KinesisScenario(wrapper)
    scenario.run_scenario()

    captured = capsys.readouterr()
    assert "Scenario completed successfully!" in captured.out
    assert "Creating stream:" in captured.out
    assert "Stream Status: ACTIVE" in captured.out
    assert "Putting a single sensor record" in captured.out
    assert "Putting batch of 5 sensor records" in captured.out
    assert "Listing shards" in captured.out
    assert "Reading records from shards" in captured.out
    assert "Adding tags to stream" in captured.out
    assert "Verifying tags" in captured.out
    assert "Cleaning up" in captured.out


@pytest.mark.integ
def test_hello_kinesis(capsys):
    """
    Integration test for the Hello Kinesis example.
    Verifies it can connect to Kinesis and list streams.
    """
    from hello_kinesis import hello_kinesis

    hello_kinesis()

    captured = capsys.readouterr()
    assert "Hello, Amazon Kinesis!" in captured.out


@pytest.mark.integ
def test_wrapper_create_and_delete_stream():
    """
    Integration test for creating and deleting a Kinesis stream directly.
    Tests the wrapper methods independently.
    """
    wrapper = KinesisWrapper.from_client()
    stream_name = f"integ-test-stream-{int(time.time())}"

    try:
        # Create stream
        wrapper.create_stream(stream_name)

        # Wait for active
        description = wrapper.wait_for_stream_active(stream_name)
        assert description["StreamStatus"] == "ACTIVE"
        assert description["StreamARN"] is not None

        # List shards
        shards = wrapper.list_shards(stream_name)
        assert len(shards) > 0

    finally:
        # Cleanup
        try:
            wrapper.delete_stream(stream_name)
        except Exception as e:
            logger.warning("Cleanup failed for stream %s: %s", stream_name, e)