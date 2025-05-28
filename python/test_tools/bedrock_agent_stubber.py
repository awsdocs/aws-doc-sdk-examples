# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Bedrock Agents unit tests.
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

        :param client: A Boto3 Amazon Bedrock Agents client.
        :param use_stubs: When True, uses stubs to intercept requests. Otherwise,
                          passes requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_agent", expected_params, response, error_code=error_code
        )

    def stub_create_agent_action_group(
            self, expected_params, response, error_code=None
    ):
        self._stub_bifurcator(
            "create_agent_action_group",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_agent_alias(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_agent_alias", expected_params, response, error_code=error_code
        )

    def stub_delete_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_agent", expected_params, response, error_code=error_code
        )

    def stub_delete_agent_alias(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_agent_alias", expected_params, response, error_code=error_code
        )

    def stub_get_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "get_agent", expected_params, response, error_code=error_code
        )

    def stub_list_agents(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_agents", expected_params, response, error_code=error_code
        )

    def stub_list_agent_action_groups(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_agent_action_groups", expected_params, response, error_code=error_code
        )

    def stub_list_agent_knowledge_bases(
            self, expected_params, response, error_code=None
    ):
        self._stub_bifurcator(
            "list_agent_knowledge_bases",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_prepare_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "prepare_agent", expected_params, response, error_code=error_code
        )
    def stub_create_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_flow", expected_params, response, error_code=error_code
        )

    def stub_get_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "get_flow", expected_params, response, error_code=error_code
        )


    def stub_prepare_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "prepare_flow", expected_params, response, error_code=error_code
        )

    def stub_update_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "update_flow", expected_params, response, error_code=error_code
        )

    def stub_list_flows(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_flows", expected_params, response, error_code=error_code
        )
    
    def stub_delete_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_flow", expected_params, response, error_code=error_code
        )
    def stub_create_flow_alias(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_flow_alias", expected_params, response, error_code=error_code
        )
    def stub_update_flow_alias(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "update_flow_alias", expected_params, response, error_code=error_code
        )

    def stub_list_flow_aliases(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_flow_aliases", expected_params, response, error_code=error_code
        )
    
    def stub_delete_flow_alias(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_flow_alias", expected_params, response, error_code=error_code
        )

    def stub_create_flow_version(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_flow_version", expected_params, response, error_code=error_code
        )
    def stub_get_flow_version(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "get_flow_version", expected_params, response, error_code=error_code
        )
    
    def stub_delete_flow_version(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_flow_version", expected_params, response, error_code=error_code
        )

    def stub_list_flow_versions(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_flow_versions", expected_params, response, error_code=error_code
        )

        # Prompt API stubs
    def stub_create_prompt(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_prompt", expected_params, response, error_code=error_code
        )
    def stub_create_prompt_version(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "create_prompt_version", expected_params, response, error_code=error_code
        )
        
    def stub_get_prompt(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "get_prompt", expected_params, response, error_code=error_code
        )
        
    def stub_delete_prompt(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "delete_prompt", expected_params, response, error_code=error_code
        )
        
    def stub_list_prompts(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "list_prompts", expected_params, response, error_code=error_code
        )
