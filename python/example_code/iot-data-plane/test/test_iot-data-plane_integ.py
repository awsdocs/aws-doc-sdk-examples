# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the AWS IoT Data Plane Basics Scenario.

Run with: pytest test_iot_data_scenario.py -v
"""

import pytest

from iot_data_wrapper import IoTDataPlaneWrapper
from scenario_iot_data_basics import IoTDataPlaneScenario


@pytest.mark.integ
def test_iot_data_plane_scenario(capsys):
    """
    Integration test that runs the full IoT Data Plane basics scenario.
    Verifies the scenario completes without errors and produces expected output.
    """
    wrapper = IoTDataPlaneWrapper.from_client()
    scenario = IoTDataPlaneScenario(wrapper)

    try:
        scenario.run_scenario()
    except Exception:
        # If scenario fails, ensure cleanup still happens
        try:
            scenario.cleanup()
        except Exception:
            pass
        raise

    captured = capsys.readouterr()
    assert "Thanks for watching!" in captured.out
    assert "Step 1" in captured.out or "Classic Device Shadow" in captured.out


@pytest.mark.integ
def test_iot_data_plane_hello(capsys):
    """
    Integration test for the Hello IoT Data Plane example.
    """
    from iot_data_hello import hello_iot_data_plane

    hello_iot_data_plane()

    captured = capsys.readouterr()
    assert "Hello, AWS IoT Data Plane!" in captured.out
    assert "Successfully connected to AWS IoT Data Plane!" in captured.out
