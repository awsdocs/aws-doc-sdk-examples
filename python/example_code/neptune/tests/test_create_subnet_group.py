# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
from unittest.mock import patch
from test_tools.neptune_stubber import Neptune
from example_code.neptune.neptune_scenario import create_subnet_group

@patch("example_code.neptune.neptune_scenario.get_subnet_ids")
@patch("example_code.neptune.neptune_scenario.get_default_vpc_id")
def test_create_subnet_group(mock_get_vpc, mock_get_subnets):
    mock_get_vpc.return_value = "vpc-1234"
    mock_get_subnets.return_value = ["subnet-1", "subnet-2"]

    boto_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(boto_client)

    stubber.stub_create_db_subnet_group(
        group_name="test-group",
        subnet_ids=["subnet-1", "subnet-2"],
        group_arn="arn:aws:neptune:us-east-1:123456789012:subnet-group:test-group",
        description="My Neptune subnet group",
        tags=[{"Key": "Environment", "Value": "Dev"}]
    )

    create_subnet_group(boto_client, "test-group")

