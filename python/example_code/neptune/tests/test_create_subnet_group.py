# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock, patch
from NeptuneScenario import create_subnet_group  # Replace with your actual module path

@patch("NeptuneScenario.get_subnet_ids")
@patch("NeptuneScenario.get_default_vpc_id")
def test_create_subnet_group_success(mock_get_vpc, mock_get_subnets):
    """
    Unit test for create_subnet_group().
    Verifies successful creation and correct parsing of name and ARN.
    """
    mock_get_vpc.return_value = "vpc-1234"
    mock_get_subnets.return_value = ["subnet-1", "subnet-2"]

    mock_neptune = MagicMock()
    mock_neptune.create_db_subnet_group.return_value = {
        "DBSubnetGroup": {
            "DBSubnetGroupName": "test-group",
            "DBSubnetGroupArn": "arn:aws:neptune:us-east-1:123456789012:subnet-group:test-group"
        }
    }

    name, arn = create_subnet_group(mock_neptune, "test-group")

    assert name == "test-group"
    assert arn == "arn:aws:neptune:us-east-1:123456789012:subnet-group:test-group"
    mock_neptune.create_db_subnet_group.assert_called_once()
