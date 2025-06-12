import pytest
from unittest.mock import MagicMock, patch
from botocore.exceptions import ClientError
from NeptuneScenario import check_instance_status


@patch("NeptuneScenario.time.sleep", return_value=None)
@patch("NeptuneScenario.time.time")
@patch("NeptuneScenario.format_elapsed_time", side_effect=lambda x: f"{x}s")
def test_check_instance_status(mock_format_time, mock_time, mock_sleep):
    """
    Fast unit test for check_instance_status().
    Covers: success, timeout, ClientError.
    """
    # --- Setup Neptune mock client ---
    mock_client = MagicMock()
    mock_paginator = MagicMock()
    mock_client.get_paginator.return_value = mock_paginator

    # --- Success scenario ---
    # Simulate time progressing quickly
    mock_time.side_effect = [0, 1, 2, 3, 4, 5]  # enough for 2 loops

    # Simulate: starting -> available
    mock_paginator.paginate.side_effect = [
        [{"DBInstances": [{"DBInstanceStatus": "starting"}]}],
        [{"DBInstances": [{"DBInstanceStatus": "available"}]}]
    ]

    check_instance_status(mock_client, "instance-1", "available")
    assert mock_client.get_paginator.called
    assert mock_paginator.paginate.called

    # --- Timeout scenario ---
    # Reset mocks
    mock_client.reset_mock()
    mock_paginator = MagicMock()
    mock_client.get_paginator.return_value = mock_paginator

    # Provide enough time values to loop 4–5 times
    mock_time.side_effect = list(range(20))  # 0 to 19

    # Always returns 'starting'
    mock_paginator.paginate.side_effect = lambda **kwargs: [
        {"DBInstances": [{"DBInstanceStatus": "starting"}]}
    ]

    # Shrink TIMEOUT to 3s inside test scope
    with patch("NeptuneScenario.TIMEOUT_SECONDS", 3), patch("NeptuneScenario.POLL_INTERVAL_SECONDS", 1):
        with pytest.raises(RuntimeError, match="Timeout waiting for 'instance-timeout'"):
            check_instance_status(mock_client, "instance-timeout", "available")

    # --- ClientError scenario ---
    mock_paginator.paginate.side_effect = ClientError(
        {
            "Error": {
                "Code": "DBInstanceNotFound",
                "Message": "Instance not found"
            }
        },
        operation_name="DescribeDBInstances"
    )

    with pytest.raises(ClientError, match="Instance not found"):
        check_instance_status(mock_client, "not-there", "available")
