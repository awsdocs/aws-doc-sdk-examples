# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon EC2 Bedrock Runtime unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

import io
import json
from test_tools.example_stubber import ExampleStubber


class BedrockRuntimeStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Bedrock Runtime unit tests.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Bedrock Runtime client.
        :param use_stubs: When True, uses stubs to intercept requests. Otherwise,
                          passes requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_invoke_model(self, model_id, prompt, error_code=None):
        expected_params = {
            "modelId": model_id,
            "body": json.dumps({
                "prompt": f'Human: {prompt}\n\nAssistant:',
                "temperature": 0.5,
                "max_tokens_to_sample": 200,
                "stop_sequences": ["\n\nHuman:"]
            })
        }
        response = {
            "body": io.BytesIO('{ "completion": "A test completion" }'.encode("utf-8")),
            "contentType": ""
        }
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )
