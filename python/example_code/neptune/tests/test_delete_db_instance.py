# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError
from NeptuneScenario import delete_db_instance


@patch("NeptuneScenario.time.sleep", return_value=None)  # Not needed here, but safe if waiter is mocked differently later
def test_delete_db_instance(mock_sleep):
    """
    Unit test for delete_db_instance().
    Covers: successful deletion and ClientError case.
    """
    # --- Setup mock Neptune client ---
    mock_client = MagicMock()
    mock_waiter = MagicMock()
    mock_client.get_waiter.return_value = mock_waiter

    # --- Success scenario ---
    delete_db_instance(mock_client, "instance-1")

    mock_client.delete_db_instance.assert_called_once_with(
        DBInstanceIdentifier="instance-1",
        SkipFinalSnapshot=True
    )
    mock_client.get_waiter.assert_called_once_with("db_instance_deleted")
    mock_waiter.wait.assert_called_once_with(
        DBInstanceIdentifier="instance-1",
        WaiterConfig={"Delay": 30, "MaxAttempts": 40}
    )

    # --- ClientError scenario ---
    mock_client.reset_mock()
    mock_client.delete_db_instance.side_effect = ClientError(
        {
            "Error": {
                "Code": "InvalidDBInstanceState",
                "Message": "Instance is not in a deletable state"
            }
        },
        operation_name="DeleteDBInstance"
    )

    with pytest.raises(ClientError, match="Instance is not in a deletable state"):
        delete_db_instance(mock_client, "bad-instance")
