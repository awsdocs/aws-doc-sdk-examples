# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import sys
import boto3
import pytest

sys.path.append("../..")
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, inspector_client, inspector_stubber):
        self.inspector_client = inspector_client
        self.inspector_stubber = inspector_stubber
        # Import here to avoid circular imports
        from inspector_wrapper import InspectorWrapper
        import scenario_inspector_basics

        self.wrapper = InspectorWrapper(inspector_client)
        self.scenario = scenario_inspector_basics.InspectorScenario(self.wrapper)


@pytest.fixture
def scenario_data(make_stubber):
    inspector_client = boto3.client("inspector2")
    inspector_stubber = make_stubber(inspector_client)
    return ScenarioData(inspector_client, inspector_stubber)
