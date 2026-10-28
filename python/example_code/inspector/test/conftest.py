# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import os
import sys
from types import SimpleNamespace

import boto3
import pytest

# Build the import paths from this file's location so the tests work from any
# working directory: the example folder (wrapper and scenario) and the python
# folder (test_tools and demo_tools).
script_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(script_dir))
sys.path.append(os.path.join(script_dir, "../../.."))

from inspector_wrapper import Inspector2Wrapper  # noqa: E402
import scenario_inspector_basics  # noqa: E402
from test_tools.fixtures.common import *  # noqa: E402,F401,F403

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
