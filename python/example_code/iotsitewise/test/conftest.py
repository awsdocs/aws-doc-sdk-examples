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

sys.path.append(script_dir)
import iotsitewise_getting_started
from iotsitewise_wrapper import IoTSitewiseWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(
        self,
        iot_sitewise_client,
        cloud_formation_resource,
        iot_sitewise_stubber,
        cloud_formation_stubber,
    ):
        self.iot_sitewise_client = iot_sitewise_client
        self.cloud_formation_resource = cloud_formation_resource
        self.iot_sitewise_stubber = iot_sitewise_stubber
        self.cloud_formation_stubber = cloud_formation_stubber
        self.scenario = iotsitewise_getting_started.IoTSitewiseGettingStarted(
            iot_sitewise_wrapper=IoTSitewiseWrapper(self.iot_sitewise_client),
            cloud_formation_resource=self.cloud_formation_resource,
        )


@pytest.fixture
def scenario_data(make_stubber):
    iot_sitewise_client = boto3.client("iotsitewise")
    iot_sitewise_stubber = make_stubber(iot_sitewise_client)
    cloud_formation_resource = boto3.resource("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_resource.meta.client)
    return ScenarioData(
        iot_sitewise_client,
        cloud_formation_resource,
        iot_sitewise_stubber,
        cloud_formation_stubber,
    )
