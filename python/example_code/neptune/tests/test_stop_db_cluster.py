import boto3
import pytest
from botocore.stub import Stubber

# Example function that polls until DB cluster status is 'stopped'
def stop_db_cluster(client, cluster_id, max_attempts=10):
    waiter_attempts = 0
    while waiter_attempts < max_attempts:
        response = client.describe_db_clusters(DBClusterIdentifier=cluster_id)
        status = response['DBClusters'][0]['Status']
        if status == 'stopped':
            return True
        waiter_attempts += 1
    raise TimeoutError(f"DB Cluster {cluster_id} did not stop after {max_attempts} attempts")

@pytest.fixture
def neptune_client():
    # Use local dummy credentials for testing
    return boto3.client('neptune', region_name='us-west-2')

def test_stop_db_cluster_with_10_calls(neptune_client):
    cluster_id = "timeout-cluster"

    stubber = Stubber(neptune_client)

    # Stub first 9 calls with status 'stopping'
    for _ in range(9):
        stubber.add_response(
            "describe_db_clusters",
            {
                "DBClusters": [
                    {"DBClusterIdentifier": cluster_id, "Status": "stopping"}
                ]
            },
            {"DBClusterIdentifier": cluster_id}
        )

    # 10th call returns 'stopped'
    stubber.add_response(
        "describe_db_clusters",
        {
            "DBClusters": [
                {"DBClusterIdentifier": cluster_id, "Status": "stopped"}
            ]
        },
        {"DBClusterIdentifier": cluster_id}
    )

    stubber.activate()

    # Call the function under test - should not raise and return True
    result = stop_db_cluster(neptune_client, cluster_id, max_attempts=10)
    assert result is True

    stubber.deactivate()



