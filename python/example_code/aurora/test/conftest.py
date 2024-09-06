# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import uuid

import boto3
import pytest
import scenario_get_started_aurora
from aurora_wrapper import AuroraWrapper

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")


class ClusterData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = scenario_get_started_aurora.AuroraClusterScenario(
            AuroraWrapper(self.client)
        )


@pytest.fixture
def cluster_data(make_stubber):
    client = boto3.client("rds")
    stubber = make_stubber(client)
    return ClusterData(client, stubber)


@pytest.fixture(autouse=True)
def mock_wait(monkeypatch):
    monkeypatch.setattr(scenario_get_started_aurora, "wait", lambda x: None)


@pytest.fixture(autouse=True)
def mock_uuid4(monkeypatch):
    monkeypatch.setattr(uuid, "uuid4", lambda: "test-guid")