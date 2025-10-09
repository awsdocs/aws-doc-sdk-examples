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

# Add relative path to include GuardDutyWrapper.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))
import scenario_guardduty_basics
from guardduty_wrapper import GuardDutyWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, guardduty_client, guardduty_stubber):
        self.guardduty_client = guardduty_client
        self.guardduty_stubber = guardduty_stubber
        self.wrapper = GuardDutyWrapper(guardduty_client)
        self.scenario = scenario_guardduty_basics.GuardDutyScenario(self.wrapper)


@pytest.fixture
def scenario_data(make_stubber):
    guardduty_client = boto3.client("guardduty")
    guardduty_stubber = make_stubber(guardduty_client)
    return ScenarioData(guardduty_client, guardduty_stubber)
