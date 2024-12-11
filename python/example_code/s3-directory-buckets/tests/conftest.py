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

# Add relative path to include SchedulerWrapper.
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))
import s3_express_getting_started

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(
        self,
        ec2_client,
        s3_client,
        iam_client,
        cloud_formation_resource,
        ec2_stubber,
        s3_stubber,
        iam_stubber,
        cloud_formation_stubber,
        region,
    ):
        self.ec2_client = ec2_client
        self.s3_client = s3_client
        self.iam_client = iam_client
        self.cloud_formation_resource = cloud_formation_resource
        self.ec2_stubber = ec2_stubber
        self.s3_stubber = s3_stubber
        self.iam_stubber = iam_stubber
        self.cloud_formation_stubber = cloud_formation_stubber
        self.region = region
        self.scenario = s3_express_getting_started.S3ExpressScenario(
            cloud_formation_resource=self.cloud_formation_resource,
            ec2_client=self.ec2_client,
            iam_client=self.iam_client,
        )
        s3_express_getting_started.use_press_enter_to_continue = False


@pytest.fixture
def scenario_data(make_stubber):
    cloud_formation_resource = boto3.resource("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_resource.meta.client)
    region = "us-east-1"
    ec2_client = boto3.client("ec2", region_name=region)
    ec2_stubber = make_stubber(ec2_client)

    iam_client = boto3.client("iam")
    iam_stubber = make_stubber(iam_client)

    s3_client = boto3.client("s3")
    s3_stubber = make_stubber(s3_client)
    return ScenarioData(
        ec2_client=ec2_client,
        s3_client=s3_client,
        iam_client=iam_client,
        cloud_formation_resource=cloud_formation_resource,
        ec2_stubber=ec2_stubber,
        s3_stubber=s3_stubber,
        iam_stubber=iam_stubber,
        cloud_formation_stubber=cloud_formation_stubber,
        region=region,
    )
