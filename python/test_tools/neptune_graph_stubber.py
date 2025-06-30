# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import io
from botocore.stub import Stubber
from botocore.response import StreamingBody
from botocore.config import Config

GRAPH_ID = "my-graph-id"
GRAPH_NAME = "my-test-graph"

class NeptuneGraphStubber:
    def __init__(self):
        """
        Create NeptuneGraph client and stubber with minimal retry config for testing.
        """
        config = Config(retries={"total_max_attempts": 1, "mode": "standard"}, read_timeout=None)
        self.client = boto3.client("neptune-graph", config=config)
        self.stubber = Stubber(self.client)

    def add_execute_query_stub(self, graph_id, query_string, language, explain_mode=None, parameters=None):
        """
        Add stub response for execute_query call matching parameters including optional explainMode and parameters.
        """
        expected_params = {
            "graphIdentifier": graph_id,
            "queryString": query_string,
            "language": language,
        }
        if explain_mode is not None:
            expected_params["explainMode"] = explain_mode
        if parameters is not None:
            expected_params["parameters"] = parameters

        # Example JSON payload response the service expects
        payload_bytes = b'{"results": [{"n": {"code": "ANC"}}]}'
        response_body = StreamingBody(io.BytesIO(payload_bytes), len(payload_bytes))
        response = {"payload": response_body}

        self.stubber.add_response("execute_query", response, expected_params)

    def add_create_graph_stub(self, graph_name, memory=16):
        """
        Add stub response for create_graph call matching graphName and provisionedMemory parameters.
        """
        expected_params = {
            "graphName": graph_name,
            "provisionedMemory": memory
        }
        response = {
            "id": "test-graph-id",  # Required field in response
            "name": graph_name,
            "arn": f"arn:aws:neptune-graph:us-east-1:123456789012:graph/{graph_name}",
            "endpoint": f"https://{graph_name}.cluster-neptune.amazonaws.com"
        }
        self.stubber.add_response("create_graph", response, expected_params)

    def activate(self):
        """
        Activate the stubber.
        """
        self.stubber.activate()

    def deactivate(self):
        """
        Deactivate the stubber.
        """
        self.stubber.deactivate()

    def get_client(self):
        """
        Return the stubbed NeptuneGraph client.
        """
        return self.client



