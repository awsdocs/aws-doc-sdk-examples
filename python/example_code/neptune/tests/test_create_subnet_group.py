# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError
from neptune_scenario import create_subnet_group  # Adjust the import path as necessary

# Mocking external functions to isolate the unit test
@patch("NeptuneScenario.get_subnet_ids")
@patch("NeptuneScenario.get_default_vpc_id")
def test_create_subnet_group(mock_get_vpc, mock_get_subnets):
    """
    Unit test for create_subnet_group().
    Verifies successful creation and correct parsing of name and ARN.
    """
    # --- Setup Mocks ---
    mock_get_vpc.return_value = "vpc-1234"
    mock_get_subnets.return_value = ["subnet-1", "subnet-2"]

    mock_neptune = MagicMock()
    mock_neptune.create_db_subnet_group.return_value = {
        "DBSubnetGroup": {
            "DBSubnetGroupName": "test-group",
            "DBSubnetGroupArn": "arn:aws:neptune:us-east-1:123456789012:subnet-group:test-group"
        }
    }

    # --- Success Case ---
    name, arn = create_subnet_group(mock_neptune, "test-group")
    assert name == "test-group"
    assert arn == "arn:aws:neptune:us-east-1:123456789012:subnet-group:test-group"
    mock_neptune.create_db_subnet_group.assert_called_once_with(
        DBSubnetGroupName="test-group",
        DBSubnetGroupDescription="My Neptune subnet group",
        SubnetIds=["subnet-1", "subnet-2"],
        Tags=[{"Key": "Environment", "Value": "Dev"}]
    )

    # --- Missing Name or ARN ---
    mock_neptune.create_db_subnet_group.return_value = {"DBSubnetGroup": {}}
    with pytest.raises(RuntimeError, match="Response missing subnet group name or ARN"):
        create_subnet_group(mock_neptune, "missing-id-group")

    # --- ClientError Handling ---
    mock_neptune.create_db_subnet_group.side_effect = ClientError(
        {"Error": {"Code": "AccessDenied", "Message": "Permission denied"}},
        operation_name="CreateDBSubnetGroup"
    )
    with pytest.raises(ClientError, match="Failed to create subnet group 'denied-group'"):
        create_subnet_group(mock_neptune, "denied-group")

    # --- Unexpected Exception ---
    mock_neptune.create_db_subnet_group.side_effect = Exception("Unexpected failure")
    with pytest.raises(RuntimeError, match="Unexpected error creating subnet group 'fail-group'"):
        create_subnet_group(mock_neptune, "fail-group")
