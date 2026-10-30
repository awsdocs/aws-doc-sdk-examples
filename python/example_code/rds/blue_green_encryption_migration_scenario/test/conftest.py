# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests for the Amazon RDS
Blue/Green Deployment encryption migration example.
"""

import os
import sys

import boto3
import pytest

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include the wrapper and scenario modules.
sys.path.append(os.path.dirname(script_dir))
import rds_blue_green_encryption_migration_wrapper
from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)
from rds_blue_green_encryption_migration_scenario import (
    BlueGreenEncryptionMigrationScenario,
)
from cloudformation_helper import CloudFormationHelper

# This is needed so Python can find test_tools on the path.
sys.path.append(os.path.join(script_dir, "../../../.."))
from test_tools.fixtures.common import *


class ScenarioData:
    """Holds the stubbed clients, helpers, and scenario used by the tests."""

    def __init__(self, client, stubber, cf_client, cf_stubber):
        self.client = client
        self.stubber = stubber
        self.cf_client = cf_client
        self.cf_stubber = cf_stubber
        self.wrapper = BlueGreenEncryptionMigrationWrapper(self.client)
        self.cfn_helper = CloudFormationHelper(self.cf_client)
        self.scenario = BlueGreenEncryptionMigrationScenario(
            self.wrapper, self.cfn_helper
        )


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("rds", region_name="us-east-1")
    stubber = make_stubber(client)
    cf_client = boto3.client("cloudformation", region_name="us-east-1")
    cf_stubber = make_stubber(cf_client)
    return ScenarioData(client, stubber, cf_client, cf_stubber)


@pytest.fixture(autouse=True)
def mock_sleep(monkeypatch):
    """Skips the polling delays in the wrapper's wait functions."""
    monkeypatch.setattr(
        rds_blue_green_encryption_migration_wrapper.time, "sleep", lambda x: None
    )
