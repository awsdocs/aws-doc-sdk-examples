# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import os
import boto3
import pytest

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include IoTWrapper.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))

from test_tools.fixtures.common import *
from iot_wrapper import IoTWrapper


class ScenarioData:
    """Encapsulates data for IoT scenario tests."""

    def __init__(self, iot_client, iot_stubber):
        self.iot_client = iot_client
        self.iot_stubber = iot_stubber
        self.wrapper = IoTWrapper(iot_client)


@pytest.fixture
def scenario_data(make_stubber):
    """Creates a ScenarioData object for IoT scenario tests."""
    iot_client = boto3.client("iot", region_name="us-east-1")
    iot_stubber = make_stubber(iot_client)
    return ScenarioData(iot_client, iot_stubber)
