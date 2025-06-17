# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from botocore.exceptions import ClientError
import boto3
from neptune_stubber import Neptune
from neptune_scenario import check_instance_status  # your function to test

# Constants for polling & timeout - patch if needed
TIMEOUT_SECONDS = 10
POLL_INTERVAL_SECONDS = 1


def test_check_instance_status_with_neptune_stubber(monkeypatch):
    # Create real boto3 client + wrap with Neptune stubber
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(client)

    instance_id = "instance-1"

    # Prepare stubbed responses for describe_db_instances paginator pages
    # Each call to paginate() will return these pages in order:
    # First call returns status 'starting', second returns 'available'
    # Because the paginator returns an iterator of pages, each page is a dict

    stubbed_pages_starting = [{"DBInstances": [{"DBInstanceStatus": "starting"}]}]
    stubbed_pages_available = [{"DBInstances": [{"DBInstanceStatus": "available"}]}]

    # We need to stub `describe_db_instances` for each paginator page request
    # So stub two responses in sequence to simulate status change on subsequent polls
    stubber.stubber.add_response(
        "describe_db_instances",
        stubbed_pages_starting[0],
        expected_params={"DBInstanceIdentifier": instance_id},
    )
    stubber.stubber.add_response(
        "describe_db_instances",
        stubbed_pages_available[0],
        expected_params={"DBInstanceIdentifier": instance_id},
    )

    # Patch time.time to simulate time passing quickly (simulate elapsed time)
    times = [0, 1, 2, 3, 4, 5]
    monkeypatch.setattr("neptune_scenario.time.time", lambda: times.pop(0) if times else 5)

    # Patch time.sleep to avoid real wait during test
    monkeypatch.setattr("neptune_scenario.time.sleep", lambda s: None)

    # Patch format_elapsed_time to just return seconds + 's' string
    monkeypatch.setattr("neptune_scenario.format_elapsed_time", lambda x: f"{x}s")

    # Run the check_instance_status function (should exit once status 'available' is found)
    check_instance_status(stubber.client, instance_id, "available")


def test_check_instance_status_timeout(monkeypatch):
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(client)

    instance_id = "instance-timeout"

    # Always return status 'starting' to simulate never reaching 'available'
    stub_response = {"DBInstances": [{"DBInstanceStatus": "starting"}]}

    # Stub multiple responses (enough for timeout loops)
    for _ in range(10):
        stubber.stubber.add_response(
            "describe_db_instances",
            stub_response,
            expected_params={"DBInstanceIdentifier": instance_id},
        )

    # Patch time.time to simulate time passing beyond timeout (simulate elapsed time)
    times = list(range(15))  # simulate 15 seconds
    monkeypatch.setattr("neptune_scenario.time.time", lambda: times.pop(0) if times else 15)

    monkeypatch.setattr("neptune_scenario.time.sleep", lambda s: None)
    monkeypatch.setattr("neptune_scenario.format_elapsed_time", lambda x: f"{x}s")

    # Patch timeout and poll interval inside your module (adjust as needed)
    monkeypatch.setattr("neptune_scenario.TIMEOUT_SECONDS", 5)
    monkeypatch.setattr("neptune_scenario.POLL_INTERVAL_SECONDS", 1)

    with pytest.raises(RuntimeError, match="Timeout waiting for 'instance-timeout'"):
        check_instance_status(stubber.client, instance_id, "available")


def test_check_instance_status_client_error(monkeypatch):
    client = boto3.client("neptune")
    stubber = Neptune(client)

    instance_id = "not-there"

    # Stub a ClientError for describe_db_instances
    stubber.stubber.add_client_error(
        "describe_db_instances",
        service_error_code="DBInstanceNotFound",
        service_message="Instance not found",
        expected_params={"DBInstanceIdentifier": instance_id},
    )

    # Patch time.sleep and format_elapsed_time to avoid delays and keep output clean
    monkeypatch.setattr("neptune_scenario.time.sleep", lambda s: None)
    monkeypatch.setattr("neptune_scenario.format_elapsed_time", lambda x: f"{x}s")

    with pytest.raises(ClientError, match="Instance not found"):
        check_instance_status(stubber.client, instance_id, "available")

