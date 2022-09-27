# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import uuid
import boto3
import pytest

from instance_wrapper import InstanceWrapper
import scenario_get_started_instances

# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *


class InstanceData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = scenario_get_started_instances.RdsInstanceScenario(InstanceWrapper(self.client))


@pytest.fixture
def instance_data(make_stubber):
    client = boto3.client('rds')
    stubber = make_stubber(client)
    return InstanceData(client, stubber)


@pytest.fixture(autouse=True)
def mock_wait(monkeypatch):
    monkeypatch.setattr(scenario_get_started_instances, 'wait', lambda x: None)


@pytest.fixture(autouse=True)
def mock_uuid4(monkeypatch):
    monkeypatch.setattr(uuid, 'uuid4', lambda: 'test-guid')
