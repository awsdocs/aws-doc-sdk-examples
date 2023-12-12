# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Agents for Amazon Bedrock unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class BedrockAgentStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Bedrock Agent unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Agents for Amazon Bedrock client.
        :param use_stubs: When True, uses stubs to intercept requests. Otherwise,
                          passes requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_agent", expected_params, response, error_code=error_code
        )

    def stub_create_agent_action_group(self, expected_params, response, error_code=None):
        self._stub_bifurcator("create_agent_action_group", expected_params, response, error_code=error_code)

    def stub_delete_agent(self, agent_id, error_code=None):
        expected_params = {"agentId": agent_id, "skipResourceInUseCheck": False}
        response = {"agentStatus": "DELETING", "agentId": agent_id}
        self._stub_bifurcator(
            "delete_agent", expected_params, response, error_code=error_code
        )

    def stub_get_agent(self, agent_id, agent, error_code=None):
        expected_params = {"agentId": agent_id}
        response = {"agent": agent}
        self._stub_bifurcator(
            "get_agent", expected_params, response, error_code=error_code
        )

    def stub_list_agents(self, agents, error_code=None):
        expected_params = {}
        response = {"agentSummaries": agents}
        self._stub_bifurcator(
            "list_agents", expected_params, response, error_code=error_code
        )

    def stub_prepare_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "prepare_agent", expected_params, response, error_code=error_code
        )
