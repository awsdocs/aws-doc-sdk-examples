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

# Add parent directory so pinpoint_wrapper and the scenario can be imported.
sys.path.append(os.path.dirname(script_dir))
sys.path.append(os.path.join(os.path.dirname(script_dir), "scenarios"))

from pinpoint_wrapper import PinpointWrapper
import pinpoint_basics_scenario

# This is needed so Python can find test_tools on the path.
sys.path.append(os.path.join(script_dir, "../../.."))
from test_tools.fixtures.common import *  # noqa


class ScenarioData:
    def __init__(
        self,
        pinpoint_client,
        cf_client,
        sts_client,
        pinpoint_stubber,
        cf_stubber,
        sts_stubber,
    ):
        self.pinpoint_client = pinpoint_client
        self.cf_client = cf_client
        self.sts_client = sts_client
        self.pinpoint_stubber = pinpoint_stubber
        self.cf_stubber = cf_stubber
        self.sts_stubber = sts_stubber
        self.scenario = pinpoint_basics_scenario.PinpointScenario(
            pinpoint_wrapper=PinpointWrapper(self.pinpoint_client),
            cf_client=self.cf_client,
            sts_client=self.sts_client,
        )


@pytest.fixture
def scenario_data(make_stubber):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    cf_client = boto3.client("cloudformation", region_name="us-east-1")
    sts_client = boto3.client("sts", region_name="us-east-1")

    pinpoint_stubber = make_stubber(pinpoint_client)
    cf_stubber = make_stubber(cf_client)
    sts_stubber = make_stubber(sts_client)

    return ScenarioData(
        pinpoint_client,
        cf_client,
        sts_client,
        pinpoint_stubber,
        cf_stubber,
        sts_stubber,
    )
