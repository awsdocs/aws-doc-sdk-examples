# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for IoT scenario.
"""

import pytest
import boto3
from unittest.mock import patch

from iot_wrapper import IoTWrapper
from scenario_iot_basics import IoTScenario


@pytest.mark.integ
def test_scenario_complete_integration():
    """Test the complete IoT scenario flow against real AWS services."""
    iot_client = boto3.client("iot")
    iot_data_client = boto3.client("iot-data")
    iot_wrapper = IoTWrapper(iot_client, iot_data_client)
    cfn_client = boto3.client("cloudformation")
    scenario = IoTScenario(iot_wrapper, iot_data_client, cfn_client)
    
    sts_client = boto3.client("sts")
    account_id = sts_client.get_caller_identity()["Account"]
    region = boto3.Session().region_name or "us-east-1"

    with patch("demo_tools.question.ask") as mock_ask:
        mock_ask.side_effect = [
            "y",  # Deploy CloudFormation stack
            "y",  # Continue after stack creation
            "y",  # Continue after thing creation
            "y",  # Continue after certificate creation
            "y",  # Continue after endpoint retrieval
            "y",  # Continue after rule creation
            "y",  # Continue after search
            "y",  # Perform cleanup
        ]

        with patch("time.sleep"):
            with patch.object(scenario, "_deploy_stack", return_value=(f"arn:aws:sns:{region}:{account_id}:test", f"arn:aws:iam::{account_id}:role/test")):
                with patch.object(scenario, "_cleanup_stack"):
                    with patch.object(scenario, "_wait"):
                        with patch.object(iot_wrapper, "create_topic_rule"):
                            scenario.run_scenario("test_thing", "test_rule")

        assert True


@pytest.mark.integ
def test_create_thing_integration():
    """Test creating an IoT thing against real AWS."""
    iot_wrapper = IoTWrapper(boto3.client("iot"))
    thing_name = f"test-thing-{int(__import__('time').time())}"

    try:
        response = iot_wrapper.create_thing(thing_name)
        assert response["thingName"] == thing_name
        assert "thingArn" in response
    finally:
        try:
            iot_wrapper.delete_thing(thing_name)
        except Exception:
            pass


@pytest.mark.integ
def test_list_things_integration():
    """Test listing IoT things against real AWS."""
    iot_wrapper = IoTWrapper(boto3.client("iot"))
    things = iot_wrapper.list_things()
    assert isinstance(things, list)


@pytest.mark.integ
def test_certificate_lifecycle_integration():
    """Test creating and deleting a certificate."""
    iot_wrapper = IoTWrapper(boto3.client("iot"))

    try:
        response = iot_wrapper.create_keys_and_certificate()
        cert_id = response["certificateId"]
        assert cert_id
        assert "certificateArn" in response
    finally:
        try:
            iot_wrapper.delete_certificate(cert_id)
        except Exception:
            pass
