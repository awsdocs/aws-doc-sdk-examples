# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from botocore.exceptions import ClientError
import boto3
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import check_instance_status

def test_check_instance_status_with_neptune_stubber(monkeypatch):
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(client)

    instance_id = "instance-1"

    stubbed_pages_starting = [{"DBInstances": [{"DBInstanceStatus": "starting"}]}]
    stubbed_pages_available = [{"DBInstances": [{"DBInstanceStatus": "available"}]}]

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

    times = [0, 1, 2, 3, 4, 5]
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.time.time",
        lambda: times.pop(0) if times else 5,
    )
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.time.sleep", lambda s: None
    )
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.format_elapsed_time", lambda x: f"{x}s"
    )

    check_instance_status(stubber.client, instance_id, "available")


def test_check_instance_status_timeout(monkeypatch):
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(client)

    instance_id = "instance-timeout"

    stub_response = {"DBInstances": [{"DBInstanceStatus": "starting"}]}

    for _ in range(10):
        stubber.stubber.add_response(
            "describe_db_instances",
            stub_response,
            expected_params={"DBInstanceIdentifier": instance_id},
        )

    times = list(range(15))
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.time.time",
        lambda: times.pop(0) if times else 15,
    )
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.time.sleep", lambda s: None
    )
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.format_elapsed_time", lambda x: f"{x}s"
    )

    monkeypatch.setattr("example_code.neptune.neptune_scenario.TIMEOUT_SECONDS", 5)
    monkeypatch.setattr("example_code.neptune.neptune_scenario.POLL_INTERVAL_SECONDS", 1)

    with pytest.raises(RuntimeError, match=f"Timeout waiting for '{instance_id}'"):
        check_instance_status(stubber.client, instance_id, "available")

def test_check_instance_status_client_error(monkeypatch):
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(client)

    instance_id = "not-there"

    stubber.stubber.add_client_error(
        "describe_db_instances",
        service_error_code="DBInstanceNotFound",
        service_message="Instance not found",
        expected_params={"DBInstanceIdentifier": instance_id},
    )

    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.time.sleep", lambda s: None
    )
    monkeypatch.setattr(
        "example_code.neptune.neptune_scenario.format_elapsed_time", lambda x: f"{x}s"
    )

    with pytest.raises(ClientError, match="Instance not found"):
        check_instance_status(stubber.client, instance_id, "available")


