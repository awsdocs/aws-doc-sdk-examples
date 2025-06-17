import pytest
import boto3
from botocore.exceptions import ClientError

from neptune_scenario import delete_db_cluster  # Your actual module
from neptune_stubber import Neptune  # Update path if needed

def test_delete_db_cluster_success_and_clienterror():
    neptune_client = boto3.client("neptune", region_name="us-east-1")
    stubber = Neptune(neptune_client)

    # --- Success case ---
    stubber.stub_delete_db_cluster("test-cluster")
    delete_db_cluster(neptune_client, "test-cluster")  # Should not raise

    # --- AWS ClientError is raised ---
    stubber.stub_delete_db_cluster("unauthorized-cluster", error_code="AccessDenied")

    with pytest.raises(ClientError) as exc_info:
        delete_db_cluster(neptune_client, "unauthorized-cluster")

    assert "AccessDenied" in str(exc_info.value)

def test_delete_db_cluster_unexpected_exception(monkeypatch):
    # Patch the client to raise a generic exception
    client = boto3.client("neptune", region_name="us-east-1")

    def raise_unexpected_error(**kwargs):
        raise Exception("Unexpected error")

    monkeypatch.setattr(client, "delete_db_cluster", raise_unexpected_error)

    with pytest.raises(Exception, match="Unexpected error"):
        delete_db_cluster(client, "error-cluster")

