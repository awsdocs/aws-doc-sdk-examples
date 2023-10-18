# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import boto3
import pytest

from support_wrapper import SupportWrapper
import get_started_support_cases

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = get_started_support_cases.SupportCasesScenario(
            SupportWrapper(self.client)
        )


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("support")
    stubber = make_stubber(client)
    return ScenarioData(client, stubber)


@pytest.fixture(autouse=True)
def mock_wait(monkeypatch):
    monkeypatch.setattr(get_started_support_cases, "wait", lambda x: None)
