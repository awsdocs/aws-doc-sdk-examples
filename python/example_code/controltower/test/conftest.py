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

# Add relative path to include ControlTowerWrapper.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))
import scenario_controltower
from controltower_wrapper import ControlTowerWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(
        self,
        controltower_client,
        controlcatalog_client,
        organizations_client,
        controltower_stubber,
        controlcatalog_stubber,
        organizations_stubber,
    ):
        self.controltower_client = controltower_client
        self.controlcatalog_client = controlcatalog_client
        self.organizations_client = organizations_client
        self.controltower_stubber = controltower_stubber
        self.controlcatalog_stubber = controlcatalog_stubber
        self.organizations_stubber = organizations_stubber
        self.scenario = scenario_controltower.ControlTowerScenario(
            controltower_wrapper=ControlTowerWrapper(
                self.controltower_client, self.controlcatalog_client
            ),
            org_client=self.organizations_client
        )


@pytest.fixture
def scenario_data(make_stubber):
    controltower_client = boto3.client("controltower")
    controlcatalog_client = boto3.client("controlcatalog")
    organizations_client = boto3.client("organizations")
    
    controltower_stubber = make_stubber(controltower_client)
    controlcatalog_stubber = make_stubber(controlcatalog_client)
    organizations_stubber = make_stubber(organizations_client)
    
    return ScenarioData(
        controltower_client,
        controlcatalog_client,
        organizations_client,
        controltower_stubber,
        controlcatalog_stubber,
        organizations_stubber,
    )


@pytest.fixture
def mock_wait(monkeypatch):
    return