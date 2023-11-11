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

    def stub_invoke_claude(self, prompt, error_code=None):
        expected_params = {
            "modelId": "anthropic.claude-v2",
            "body": json.dumps({
                "prompt": f'Human: {prompt}\n\nAssistant:',
                "max_tokens_to_sample": 200,
                "temperature": 0.5,
                "top_p": 1,
                "top_k": 250,
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

    def stub_invoke_jurassic2(self, prompt, error_code=None):
        expected_params = {
            "modelId": "ai21.j2-mid-v1",
            "body": json.dumps({
                "prompt": prompt,
                "temperature": 0.5,
                "topP": 1,
                "maxTokens": 200,
                "stopSequences": [],
                "countPenalty": {"scale": 0},
                "presencePenalty": {"scale": 0},
                "frequencyPenalty": {"scale": 0},
            })
        }

        response_body = io.BytesIO(json.dumps(
            {
                "completions": [{"data": {"text": "A test completion."}}]
            }
        ).encode("utf-8"))

        response = {
            "body": response_body,
            "contentType": ""
        }
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )
