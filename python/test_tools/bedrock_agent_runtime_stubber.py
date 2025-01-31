# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Bedrock Agents Runtime unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class BedrockAgentRuntimeStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Bedrock Agents Runtime unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Bedrock Agents Runtime client.
        :param use_stubs: When True, uses stubs to intercept requests. Otherwise,
                          passes requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_invoke_agent(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "invoke_agent", expected_params, response, error_code=error_code
        )
    def stub_invoke_flow(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "invoke_flow", expected_params, response, error_code=error_code
        )