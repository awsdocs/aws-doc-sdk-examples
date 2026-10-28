# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
from types import SimpleNamespace

import boto3
import pytest

from inspector_wrapper import Inspector2Wrapper
import scenario_inspector_basics

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *

# The scenario names its filter with a timestamp, so tests pin the clock.
TEST_TIMESTAMP = 1700000000
TEST_FILTER_NAME = f"suppress-low-{TEST_TIMESTAMP}"


class ScenarioData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = scenario_inspector_basics.InspectorScenario(
            Inspector2Wrapper(self.client)
        )


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("inspector2", region_name="us-east-1")
    stubber = make_stubber(client)
    return ScenarioData(client, stubber)


@pytest.fixture(autouse=True)
def mock_time(monkeypatch):
    monkeypatch.setattr(
        scenario_inspector_basics, "time", SimpleNamespace(time=lambda: TEST_TIMESTAMP)
    )
