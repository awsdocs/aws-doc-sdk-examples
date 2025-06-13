# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
from unittest.mock import MagicMock
from botocore.exceptions import ClientError

from hello_neptune import describe_db_clusters  # replace with actual import


@pytest.fixture
def mock_neptune_client():
    """Return a mocked boto3 Neptune client."""
    return MagicMock()


def test_describe_db_clusters_unit(mock_neptune_client, capsys):
    """
    Unit test for describe_db_clusters with paginator.
    Mocks the Neptune client's paginator and verifies expected output is printed.
    """

    # Create a mock paginator
    mock_paginator = MagicMock()
    mock_neptune_client.get_paginator.return_value = mock_paginator

    # Mock pages returned by paginate()
    mock_paginator.paginate.return_value = [
        {
            "DBClusters": [
                {
                    "DBClusterIdentifier": "my-test-cluster",
                    "Status": "available"
                }
            ]
        },
        {
            "DBClusters": [
                {
                    "DBClusterIdentifier": "my-second-cluster",
                    "Status": "modifying"
                }
            ]
        }
    ]

    try:
        # Call the function with the mocked client
        describe_db_clusters(mock_neptune_client)

        # Capture stdout
        captured = capsys.readouterr()

        # Check that expected outputs from both pages were printed
        assert "my-test-cluster" in captured.out
        assert "available" in captured.out
        assert "my-second-cluster" in captured.out
        assert "modifying" in captured.out

        # Ensure get_paginator was called with correct operation
        mock_neptune_client.get_paginator.assert_called_once_with("describe_db_clusters")

        # Ensure paginate method was called
        mock_paginator.paginate.assert_called_once()

    except ClientError as e:
        pytest.fail(f"AWS ClientError occurred: {e.response['Error']['Message']}")
    except Exception as e:
        pytest.fail(f"Unexpected error: {str(e)}")
