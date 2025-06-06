# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import boto3
from botocore.exceptions import ClientError
from HelloNeptune import describe_db_clusters

@pytest.fixture(scope="module")
def neptune_client():
    """Create a real Neptune boto3 client for integration testing."""
    client = boto3.client("neptune", region_name="us-east-1")
    yield client

def test_describe_db_clusters_integration(neptune_client, capsys):
    """
    Integration test for describe_db_clusters.
    Verifies that the function runs without exception and prints expected output.
    """

    try:
        describe_db_clusters(neptune_client)

        # Capture printed output
        captured = capsys.readouterr()

        # We expect at least some output if clusters exist
        # Just check output contains some key phrases
        assert "Cluster Identifier:" in captured.out or "No clusters found." in captured.out

    except ClientError as e:
        pytest.fail(f"AWS ClientError occurred: {e.response['Error']['Message']}")
    except Exception as e:
        pytest.fail(f"Unexpected error: {str(e)}")
