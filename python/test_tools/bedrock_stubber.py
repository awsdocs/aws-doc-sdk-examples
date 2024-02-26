# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Bedrock unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class BedrockStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Bedrock unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Bedrock client.
        :param use_stubs: When True, uses stubs to intercept requests. Otherwise,
                          passes requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_list_foundation_models(self, models, error_code=None):
        expected_params = {}
        response = {"modelSummaries": models}
        self._stub_bifurcator(
            "list_foundation_models", expected_params, response, error_code=error_code
        )

    def stub_get_foundation_model(self, model_identifier, error_code=None):
        expected_params = {"modelIdentifier": model_identifier}
        response = {}
        self._stub_bifurcator(
            "get_foundation_model", expected_params, response, error_code=error_code
        )
