# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import boto3
import pytest

from keyspace import KeyspaceWrapper
import scenario_get_started_keyspaces

# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = scenario_get_started_keyspaces.KeyspaceScenario(KeyspaceWrapper(self.client))


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client('keyspaces')
    stubber = make_stubber(client)
    return ScenarioData(client, stubber)


@pytest.fixture(autouse=True)
def mock_wait(monkeypatch):
    monkeypatch.setattr(scenario_get_started_keyspaces, 'wait', lambda x: None)
