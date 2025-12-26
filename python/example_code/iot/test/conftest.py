# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import boto3
import pytest

from pathlib import Path

# This is needed so Python can find test_tools on the path.
sys.path.append(str(Path(__file__).parent.parent.parent.parent))
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
