import pytest
from botocore.exceptions import ClientError, BotoCoreError
from analytics.create_neptune_graph_example import execute_create_graph
from neptune_graph_stubber import NeptuneGraphStubber

class MockBotoCoreError(BotoCoreError):
    def __init__(self, message="BotoCore error occurred"):
        super().__init__()
        self.message = message

    def __str__(self):
        return self.message

def test_execute_create_graph(capfd):
    # --- Success case ---
    stubber = NeptuneGraphStubber()
    client = stubber.get_client()
    stubber.activate()

    stubber.add_create_graph_stub("test-graph")
    execute_create_graph(client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Creating Neptune graph..." in out
    assert "Graph created successfully!" in out
    assert "Graph Name: test-graph" in out
    assert "Graph ARN: arn:aws:neptune-graph:us-east-1:123456789012:graph/test-graph" in out
    assert "Graph Endpoint: https://test-graph.cluster-neptune.amazonaws.com" in out

    stubber.deactivate()  # deactivate the stubber before mocking

    # --- ClientError case ---
    def raise_client_error(**kwargs):
        raise ClientError(
            {"Error": {"Message": "Client error occurred"}}, "CreateGraph"
        )
    client.create_graph = raise_client_error
    execute_create_graph(client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Failed to create graph: Client error occurred" in out

    # --- BotoCoreError case ---
    def raise_boto_core_error(**kwargs):
        raise MockBotoCoreError()
    client.create_graph = raise_boto_core_error
    execute_create_graph(client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Failed to create graph: BotoCore error occurred" in out

    # --- Generic Exception case ---
    def raise_generic_error(**kwargs):
        raise Exception("Generic failure")
    client.create_graph = raise_generic_error
    execute_create_graph(client, "test-graph")
    out, _ = capfd.readouterr()
    assert "Unexpected error: Generic failure" in out
