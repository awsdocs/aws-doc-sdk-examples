# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest
from botocore.stub import Stubber
from example_code.neptune.hello_neptune import describe_db_clusters

@pytest.fixture
def neptune_client_stub():
    client = boto3.client("neptune", region_name="us-east-1")
    stubber = Stubber(client)
    stubber.activate()
    yield client, stubber
    stubber.deactivate()


def test_describe_db_clusters_with_stubber_single_page(neptune_client_stub, capsys):
    client, stubber = neptune_client_stub

    stubber.add_response("describe_db_clusters", {
        "DBClusters": [
            {"DBClusterIdentifier": "my-test-cluster", "Status": "available"},
            {"DBClusterIdentifier": "my-second-cluster", "Status": "modifying"}
        ]
    })

    describe_db_clusters(client)
    captured = capsys.readouterr()

    assert "my-test-cluster" in captured.out
    assert "available" in captured.out
    assert "my-second-cluster" in captured.out
    assert "modifying" in captured.out
