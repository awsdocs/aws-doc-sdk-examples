# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Amazon Data Firehose Basics scenario.

Prerequisites:
- An S3 bucket ARN (set via FIREHOSE_BUCKET_ARN environment variable)
- An IAM role ARN (set via FIREHOSE_ROLE_ARN environment variable)
"""

import os
import time
import sys

import boto3
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from firehose_wrapper import FirehoseWrapper
from scenario_firehose_basics import FirehoseScenario


@pytest.mark.integ
def test_scenario_firehose_basics(capsys):
    """
    Integration test for the complete Firehose basics scenario.
    Requires FIREHOSE_BUCKET_ARN and FIREHOSE_ROLE_ARN environment variables.
    """
    bucket_arn = os.environ.get("FIREHOSE_BUCKET_ARN")
    role_arn = os.environ.get("FIREHOSE_ROLE_ARN")

    if not bucket_arn or not role_arn:
        pytest.skip(
            "FIREHOSE_BUCKET_ARN and FIREHOSE_ROLE_ARN environment "
            "variables are required for integration tests."
        )

    timestamp = int(time.time())
    stream_name = f"firehose-integ-test-{timestamp}"

    wrapper = FirehoseWrapper.from_client()
    scenario = FirehoseScenario(wrapper)

    try:
        scenario.run_scenario(
            stream_name=stream_name,
            bucket_arn=bucket_arn,
            role_arn=role_arn,
        )
        capt = capsys.readouterr()
        assert "Firehose Basics Scenario complete" in capt.out
    finally:
        # Ensure cleanup even if scenario fails partway through
        try:
            wrapper.delete_delivery_stream(stream_name)
        except Exception:
            pass  # Ignore cleanup errors - stream may already be deleted


@pytest.mark.integ
def test_hello_firehose(capsys):
    """
    Integration test for the hello Firehose example.
    """
    from firehose_hello import hello_firehose

    firehose_client = boto3.client("firehose")
    hello_firehose(firehose_client)

    capt = capsys.readouterr()
    assert "Hello, Amazon Data Firehose!" in capt.out


@pytest.mark.integ
def test_list_delivery_streams():
    """
    Integration test for listing delivery streams.
    """
    wrapper = FirehoseWrapper.from_client()
    # This should not raise an exception
    result = wrapper.list_delivery_streams()
    assert isinstance(result, list)