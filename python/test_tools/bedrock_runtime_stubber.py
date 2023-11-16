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
            "body": json.dumps(
                {
                    "prompt": f"Human: {prompt}\n\nAssistant:",
                    "max_tokens_to_sample": 200,
                    "temperature": 0.5,
                    "stop_sequences": ["\n\nHuman:"],
                }
            ),
        }

        response = {
            "body": io.BytesIO(
                '{ "completion": "Fake completion response" }'.encode("utf-8")
            ),
            "contentType": "",
        }
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_jurassic2(self, prompt, error_code=None):
        expected_params = {
            "modelId": "ai21.j2-mid-v1",
            "body": json.dumps(
                {"prompt": prompt, "temperature": 0.5, "maxTokens": 200}
            ),
        }

        response_body = io.BytesIO(
            json.dumps(
                {"completions": [{"data": {"text": "Fake completion response."}}]}
            ).encode("utf-8")
        )

        response = {"body": response_body, "contentType": ""}

        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_llama2(self, prompt, error_code=None):
        expected_params = {
            "modelId": "meta.llama2-13b-chat-v1",
            "body": json.dumps(
                {"prompt": prompt, "temperature": 0.5, "top_p": 0.9, "max_gen_len": 512}
            ),
        }

        response_body = io.BytesIO(
            json.dumps({"generation": "Fake completion response."}).encode("utf-8")
        )

        response = {"body": response_body, "contentType": ""}

        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_model_with_response_stream(self, prompt, error_code=None):
        expected_params = {
            "modelId": "anthropic.claude-v2",
            "body": json.dumps(
                {
                    "prompt": f"Human: {prompt}\n\nAssistant:",
                    "max_tokens_to_sample": 1024,
                    "temperature": 0.5,
                    "stop_sequences": ["\n\nHuman:"],
                }
            ),
        }

        self._stub_bifurcator(
            "invoke_model_with_response_stream",
            expected_params,
            {},
            error_code=error_code,
        )

    def stub_invoke_stable_diffusion(self, prompt, style_preset, seed, error_code=None):
        expected_params = {
            "modelId": "stability.stable-diffusion-xl",
            "body": json.dumps(
                {
                    "text_prompts": [{"text": prompt}],
                    "seed": seed,
                    "cfg_scale": 10,
                    "steps": 30,
                    "style_preset": style_preset,
                }
            ),
        }

        response_body = io.BytesIO(
            json.dumps({"artifacts": [{"base64": "FakeBase64String=="}]}).encode(
                "utf-8"
            )
        )

        response = {"body": response_body, "contentType": ""}
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )
