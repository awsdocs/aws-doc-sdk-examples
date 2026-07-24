# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon CloudWatch Logs Basics Scenario.

These tests run against real AWS resources and do NOT mock the CloudWatch Logs
service client. Resources are cleaned up in a finally block.

Run with: pytest test_cloudwatch_logs_scenario.py -v
"""

import time

import boto3
import pytest

from cloudwatch_logs_wrapper import CloudWatchLogsWrapper
from scenario_cloudwatch_logs import CloudWatchLogsScenario


@pytest.mark.integ
def test_scenario_run(capsys):
    """
    Integration test that runs the full CloudWatch Logs basics scenario
    and verifies it completes without error and produces expected output.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    scenario = CloudWatchLogsScenario(wrapper)

    # Run the full scenario (includes its own cleanup in finally block)
    scenario.run_scenario()

    captured = capsys.readouterr()
    assert "CloudWatch Logs Basics Scenario complete!" in captured.out
    assert "Log group deleted successfully." in captured.out


@pytest.mark.integ
def test_wrapper_create_and_delete_log_group():
    """
    Integration test that verifies log group creation and deletion
    via the wrapper class.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    log_group_name = f"sdk-integ-test-{int(time.time())}"

    try:
        # Create log group
        wrapper.create_log_group(log_group_name, tags={"test": "integration"})

        # Verify it exists
        groups = wrapper.describe_log_groups(log_group_name_prefix=log_group_name)
        found = [g for g in groups if g["logGroupName"] == log_group_name]
        assert len(found) == 1

        # Verify retention can be set
        wrapper.put_retention_policy(log_group_name, 7)
        groups = wrapper.describe_log_groups(log_group_name_prefix=log_group_name)
        found = [g for g in groups if g["logGroupName"] == log_group_name]
        assert found[0].get("retentionInDays") == 7
    finally:
        # Cleanup
        wrapper.delete_log_group(log_group_name)


@pytest.mark.integ
def test_wrapper_put_and_get_log_events():
    """
    Integration test that verifies putting and getting log events.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    log_group_name = f"sdk-integ-test-events-{int(time.time())}"
    log_stream_name = "test-stream-1"

    try:
        wrapper.create_log_group(log_group_name)
        wrapper.create_log_stream(log_group_name, log_stream_name)

        from datetime import datetime, timezone

        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        log_events = [
            {"timestamp": now_ms, "message": "INFO: Test event 1"},
            {"timestamp": now_ms + 1000, "message": "ERROR: Test error event"},
            {"timestamp": now_ms + 2000, "message": "INFO: Test event 3"},
        ]

        wrapper.put_log_events(log_group_name, log_stream_name, log_events)

        # Wait for ingestion
        time.sleep(3)

        # Retrieve events
        events = wrapper.get_log_events(
            log_group_name, log_stream_name, start_from_head=True
        )
        assert len(events) == 3

        # Filter for ERROR events
        error_events = wrapper.filter_log_events(log_group_name, "ERROR")
        assert len(error_events) >= 1
    finally:
        wrapper.delete_log_group(log_group_name)


@pytest.mark.integ
def test_wrapper_metric_filter():
    """
    Integration test that verifies creating and describing metric filters.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    log_group_name = f"sdk-integ-test-metric-{int(time.time())}"

    try:
        wrapper.create_log_group(log_group_name)

        wrapper.put_metric_filter(
            log_group_name=log_group_name,
            filter_name="TestErrorFilter",
            filter_pattern="ERROR",
            metric_name="TestErrors",
            metric_namespace="IntegTestNamespace",
            metric_value="1",
            default_value=0,
        )

        filters = wrapper.describe_metric_filters(
            log_group_name, filter_name_prefix="TestErrorFilter"
        )
        assert len(filters) == 1
        assert filters[0]["filterName"] == "TestErrorFilter"
        assert filters[0]["filterPattern"] == "ERROR"
    finally:
        wrapper.delete_log_group(log_group_name)


@pytest.mark.integ
def test_hello_cloudwatch_logs(capsys):
    """
    Integration test for the hello_cloudwatch_logs function.
    """
    from hello_cloudwatch_logs import hello_cloudwatch_logs

    hello_cloudwatch_logs()

    captured = capsys.readouterr()
    assert "Hello CloudWatch Logs" in captured.out
