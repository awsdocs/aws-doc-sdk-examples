import boto3
import io
from botocore.stub import Stubber
from botocore.response import StreamingBody
from botocore.config import Config
import json


class NeptuneDateStubber:
    def __init__(self):
        """
        Create NeptuneData client and stubber with minimal retry config for testing.
        """
        config = Config(connect_timeout=10, read_timeout=30, retries={'max_attempts': 3})
        self.client = boto3.client("neptunedata", config=config, endpoint_url="https://fake-endpoint:8182")
        self.stubber = Stubber(self.client)

    def _make_streaming_body(self, data_str: str):
        data_bytes = data_str.encode("utf-8")
        return StreamingBody(io.BytesIO(data_bytes), len(data_bytes))

    def add_execute_gremlin_explain_query_stub(self, gremlin_query, response_payload):
        expected_params = {"gremlinQuery": gremlin_query}
        response = {"output": self._make_streaming_body(response_payload)}
        self.stubber.add_response("execute_gremlin_explain_query", response, expected_params)

    def add_execute_gremlin_profile_query_stub(self, gremlin_query, response_payload):
        expected_params = {"gremlinQuery": gremlin_query}
        response = {"output": self._make_streaming_body(response_payload)}
        self.stubber.add_response("execute_gremlin_profile_query", response, expected_params)

    def add_execute_gremlin_query_stub(self, gremlin_query, response_dict):
        expected_params = {"gremlinQuery": gremlin_query}
        # The real code expects response['result'] as a normal dict/json, not StreamingBody
        self.stubber.add_response("execute_gremlin_query", response_dict, expected_params)

    def add_execute_open_cypher_query_stub(self, open_cypher_query, parameters=None, results_dict=None):
        expected_params = {"openCypherQuery": open_cypher_query}
        if parameters:
            expected_params["parameters"] = parameters if isinstance(parameters, str) else json.dumps(parameters)
        if results_dict is None:
            results_dict = {}
        self.stubber.add_response("execute_open_cypher_query", results_dict, expected_params)

    def add_execute_open_cypher_explain_query_stub(self, open_cypher_query, explain_mode, results_payload):
        expected_params = {"openCypherQuery": open_cypher_query, "explainMode": explain_mode}
        response = {"results": self._make_streaming_body(results_payload)}
        self.stubber.add_response("execute_open_cypher_explain_query", response, expected_params)

    def activate(self):
        self.stubber.activate()

    def deactivate(self):
        self.stubber.deactivate()

    def get_client(self):
        return self.client



