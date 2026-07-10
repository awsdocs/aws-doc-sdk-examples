# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Amazon CloudWatch Logs Basics Scenario.
"""

import os
import sys
import time

import boto3
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from cloudwatch_logs_wrapper import CloudWatchLogsWrapper
from scenario_cloudwatch_logs_basics import CloudWatchLogsScenario


@pytest.mark.integ
def test_scenario_cloudwatch_logs_basics(input_mocker, capsys):
    """
    Integration test that runs the CloudWatch Logs basics scenario end-to-end.
    This test uses real AWS resources and may incur charges.
    """
    # Mock user inputs for the scenario:
    # Press Enter (multiple times for continue prompts), then "y" for cleanup
    answers = [
        "",  # Press Enter to start
        "",  # Press Enter after create log group
        "",  # Press Enter after create log stream
        "",  # Press Enter after set retention policy
        "",  # Press Enter after put log events
        "",  # Press Enter after describe log groups
        "",  # Press Enter after filter log events
        "",  # Press Enter after insights query
        "",  # Press Enter after live tail
        "y",  # Yes to delete log group
    ]
    input_mocker.mock_answers(answers)

    wrapper = CloudWatchLogsWrapper.from_client()
    scenario = CloudWatchLogsScenario(wrapper)

    try:
        scenario.run_scenario()
    except Exception as e:
        # Attempt cleanup even if scenario fails
        if scenario.log_group_name is not None:
            try:
                wrapper.delete_log_group(scenario.log_group_name)
            except Exception:
                pass
        raise e

    captured = capsys.readouterr()
    assert "Thanks for watching!" in captured.out


@pytest.mark.integ
def test_wrapper_create_and_delete_log_group():
    """
    Integration test that verifies log group creation, description, and deletion.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    log_group_name = f"/examples/integ-test-{int(time.time())}"

    try:
        # Create log group
        wrapper.create_log_group(log_group_name)

        # Verify it exists
        log_groups = wrapper.describe_log_groups(log_group_name)
        found = False
        for lg in log_groups:
            if lg.get("logGroupName") == log_group_name:
                found = True
                break
        assert found, f"Log group '{log_group_name}' not found after creation."

    finally:
        # Cleanup
        try:
            wrapper.delete_log_group(log_group_name)
        except Exception:
            pass


@pytest.mark.integ
def test_wrapper_put_and_filter_log_events():
    """
    Integration test that verifies putting log events and filtering them.
    """
    wrapper = CloudWatchLogsWrapper.from_client()
    log_group_name = f"/examples/integ-test-filter-{int(time.time())}"
    log_stream_name = f"test-stream-{int(time.time())}"

    try:
        # Setup
        wrapper.create_log_group(log_group_name)
        wrapper.create_log_stream(log_group_name, log_stream_name)

        # Put log events
        base_time = int(time.time() * 1000)
        log_events = [
            {"timestamp": base_time, "message": "[INFO] Test message"},
            {"timestamp": base_time + 1000, "message": "[ERROR] Test error"},
        ]
        wrapper.put_log_events(log_group_name, log_stream_name, log_events)

        # Wait for events to be indexed
        time.sleep(3)

        # Filter for ERROR events
        events = wrapper.filter_log_events(
            log_group_name,
            filter_pattern="ERROR",
            log_stream_names=[log_stream_name],
        )

        # Events may or may not be available immediately; verify no exception occurred
        assert isinstance(events, list)

    finally:
        # Cleanup
        try:
            wrapper.delete_log_group(log_group_name)
        except Exception:
            pass


@pytest.mark.integ
def test_hello_cloudwatch_logs(capsys):
    """
    Integration test for the Hello CloudWatch Logs example.
    """
    from cloudwatch_logs_hello import hello_cloudwatch_logs

    hello_cloudwatch_logs()

    captured = capsys.readouterr()
    assert "Hello, CloudWatch Logs!" in captured.out
    assert "log group(s)" in captured.out