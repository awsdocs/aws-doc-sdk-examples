# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import boto3
import pytest

from activities import Activity
from state_machines import StateMachine
import get_started_state_machines

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, client, stubber, iam_client, iam_stubber):
        self.client = client
        self.stubber = stubber
        self.iam_client = iam_client
        self.iam_stubber = iam_stubber
        self.scenario = get_started_state_machines.StateMachineScenario(
            Activity(self.client), StateMachine(self.client), self.iam_client
        )


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("stepfunctions")
    stubber = make_stubber(client)
    iam_client = boto3.client("iam")
    iam_stubber = make_stubber(iam_client)
    return ScenarioData(client, stubber, iam_client, iam_stubber)


@pytest.fixture(autouse=True)
def mock_wait(monkeypatch):
    monkeypatch.setattr(get_started_state_machines, "wait", lambda x: None)
