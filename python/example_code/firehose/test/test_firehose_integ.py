# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Amazon Data Firehose Basics scenario.

These tests are self-running — they create and clean up their own
S3 bucket and IAM role automatically.
"""

import os
import time
import sys

import boto3
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "scenarios")))

from firehose_wrapper import FirehoseWrapper
from firehose_basics_scenario import FirehoseScenario, setup_resources, cleanup_resources


@pytest.fixture(scope="module")
def firehose_resources():
    """
    Module-scoped fixture that creates an S3 bucket and IAM role for tests,
    then cleans them up after all tests in this module have run.
    """
    region = "us-east-1"
    suffix = str(int(time.time()))

    bucket_arn, role_arn, bucket_name, role_name = setup_resources(region, suffix)

    yield {
        "bucket_arn": bucket_arn,
        "role_arn": role_arn,
        "bucket_name": bucket_name,
        "role_name": role_name,
        "region": region,
    }

    cleanup_resources(region, bucket_name, role_name)


@pytest.mark.integ
def test_scenario_firehose_basics(capsys, firehose_resources):
    """
    Integration test for the complete Firehose basics scenario.
    Uses auto-created S3 bucket and IAM role from the fixture.
    """
    timestamp = int(time.time())
    stream_name = f"firehose-integ-test-{timestamp}"

    wrapper = FirehoseWrapper.from_client()
    scenario = FirehoseScenario(wrapper)

    scenario.run_scenario(
        stream_name=stream_name,
        bucket_arn=firehose_resources["bucket_arn"],
        role_arn=firehose_resources["role_arn"],
    )
    capt = capsys.readouterr()
    assert "Firehose Basics Scenario complete" in capt.out


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
    result = wrapper.list_delivery_streams()
    assert isinstance(result, list)
