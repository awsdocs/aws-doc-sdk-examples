# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys

import boto3
import pytest
import os


script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include SchedulerWrapper.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))
import scheduler_scenario
from scheduler_wrapper import SchedulerWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(
        self,
        scheduler_client,
        cloud_formation_resource,
        scheduler_stubber,
        cloud_formation_stubber,
    ):
        self.scheduler_client = scheduler_client
        self.cloud_formation_resource = cloud_formation_resource
        self.scheduler_stubber = scheduler_stubber
        self.cloud_formation_stubber = cloud_formation_stubber
        self.scenario = scheduler_scenario.SchedulerScenario(
            scheduler_wrapper=SchedulerWrapper(self.scheduler_client),
            cloud_formation_resource=self.cloud_formation_resource,
        )


@pytest.fixture
def scenario_data(make_stubber):
    scheduler_client = boto3.client("scheduler")
    scheduler_stubber = make_stubber(scheduler_client)
    cloud_formation_resource = boto3.resource("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_resource.meta.client)
    return ScenarioData(
        scheduler_client,
        cloud_formation_resource,
        scheduler_stubber,
        cloud_formation_stubber,
    )


@pytest.fixture
def mock_wait(monkeypatch):
    return
