# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError

from NeptuneScenario import delete_db_subnet_group  # Adjust if module name differs


def test_delete_db_subnet_group():
    """
    Unit test for delete_db_subnet_group().
    Covers success and ClientError cases.
    """
    mock_neptune = MagicMock()

    # --- Success case ---
    mock_neptune.delete_db_subnet_group.return_value = {}
    delete_db_subnet_group(mock_neptune, "my-subnet-group")
    mock_neptune.delete_db_subnet_group.assert_called_once_with(
        DBSubnetGroupName="my-subnet-group"
    )

    # --- ClientError case ---
    mock_neptune.delete_db_subnet_group.side_effect = ClientError(
        {
            "Error": {
                "Code": "AccessDenied",
                "Message": "You are not authorized to delete this subnet group"
            }
        },
        operation_name="DeleteDBSubnetGroup"
    )

    with pytest.raises(ClientError) as exc_info:
        delete_db_subnet_group(mock_neptune, "unauthorized-subnet")

    assert "You are not authorized" in str(exc_info.value)
