# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run AWS Batch unit tests.
"""

import sys
import os
import boto3
import pytest

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative paths to include the wrapper and scenario modules.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))
sys.path.append(os.path.join(os.path.dirname(script_dir), "scenarios"))

from batch_wrapper import BatchWrapper
import batch_basics_scenario

# Add relative path to include demo_tools / test_tools without need for setup.
# From this test directory, the main `python` folder is three levels up:
# test -> batch -> example_code -> python
sys.path.append(os.path.join(script_dir, "../../.."))

from test_tools.fixtures.common import *

REGION = "us-east-1"


class ScenarioData:
    """Bundles the client, stubber, wrapper, and scenario for tests."""

    def __init__(self, batch_client, batch_stubber):
        self.batch_client = batch_client
        self.batch_stubber = batch_stubber
        self.wrapper = BatchWrapper(self.batch_client)
        self.scenario = batch_basics_scenario.BatchScenario(batch_wrapper=self.wrapper)


@pytest.fixture
def scenario_data(make_stubber):
    """Create a ScenarioData instance with a stubbed Batch client."""
    batch_client = boto3.client("batch", region_name=REGION)
    batch_stubber = make_stubber(batch_client)
    return ScenarioData(batch_client, batch_stubber)


@pytest.fixture
def mock_wait(monkeypatch):
    """Patch the wrapper's time.sleep so polling waits are instantaneous."""
    monkeypatch.setattr(batch_basics_scenario.time, "sleep", lambda x: None)
    import batch_wrapper

    monkeypatch.setattr(batch_wrapper.time, "sleep", lambda x: None)
