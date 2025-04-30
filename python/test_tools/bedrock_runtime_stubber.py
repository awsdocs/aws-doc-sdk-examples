# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Bedrock Runtime unit tests.

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

    def stub_invoke_model(self, expected_params, response, error_code=None):
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_mistral_7b(self, prompt, error_code=None):
        expected_params = {
            "modelId": "mistral.mistral-7b-instruct-v0:2",
            "body": json.dumps(
                {
                    "prompt": f"<s>[INST] {prompt} [/INST]",
                    "max_tokens": 200,
                    "temperature": 0.5,
                }
            ),
        }

        response = {
            "body": io.BytesIO(
                '{ "outputs": [ { "text": "Fake completion" } ] }'.encode("utf-8")
            ),
            "contentType": "",
        }

        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_mixtral_8x7b(self, prompt, error_code=None):
        expected_params = {
            "modelId": "mistral.mixtral-8x7b-instruct-v0:1",
            "body": json.dumps(
                {
                    "prompt": f"<s>[INST] {prompt} [/INST]",
                    "max_tokens": 200,
                    "temperature": 0.5,
                }
            ),
        }

        response = {
            "body": io.BytesIO(
                '{ "outputs": [ { "text": "Fake completion" } ] }'.encode("utf-8")
            ),
            "contentType": "",
        }

        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

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

    def stub_invoke_titan_image(self, prompt, seed, error_code=None):
        expected_params = {
            "modelId": "amazon.titan-image-generator-v1",
            "body": json.dumps(
                {
                    "taskType": "TEXT_IMAGE",
                    "textToImageParams": {"text": prompt},
                    "imageGenerationConfig": {
                        "numberOfImages": 1,
                        "quality": "standard",
                        "cfgScale": 8.0,
                        "height": 512,
                        "width": 512,
                        "seed": seed,
                    },
                }
            ),
        }

        response_body = io.BytesIO(
            json.dumps({"images": ["FakeBase64String=="]}).encode("utf-8")
        )

        response = {"body": response_body, "contentType": ""}
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_claude_3_with_text(self, prompt, error_code=None):
        expected_params = {
            "modelId": "anthropic.claude-3-sonnet-20240229-v1:0",
            "body": json.dumps(
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 1024,
                    "messages": [
                        {
                            "role": "user",
                            "content": [{"type": "text", "text": prompt}],
                        }
                    ],
                }
            ),
        }
        response = {
            "contentType": "",
            "body": io.BytesIO(
                json.dumps(
                    {
                        "content": [{"type": "text", "text": "Test response"}],
                        "usage": {"input_tokens": 0, "output_tokens": 0},
                    }
                ).encode("utf-8")
            ),
        }
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )

    def stub_invoke_claude_3_multimodal(
        self, prompt, base64_image_data, error_code=None
    ):
        expected_params = {
            "modelId": "anthropic.claude-3-sonnet-20240229-v1:0",
            "body": json.dumps(
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 2048,
                    "messages": [
                        {
                            "role": "user",
                            "content": [
                                {"type": "text", "text": prompt},
                                {
                                    "type": "image",
                                    "source": {
                                        "type": "base64",
                                        "media_type": "image/png",
                                        "data": base64_image_data,
                                    },
                                },
                            ],
                        }
                    ],
                }
            ),
        }
        response = {
            "contentType": "",
            "body": io.BytesIO(
                json.dumps(
                    {
                        "content": [{"type": "text", "text": "Test response"}],
                        "usage": {"input_tokens": 0, "output_tokens": 0},
                    }
                ).encode("utf-8")
            ),
        }
        self._stub_bifurcator(
            "invoke_model", expected_params, response, error_code=error_code
        )
    def stub_converse(self, expected_params, response, error_code=None):
        """
        Adds a stub for the converse function.
        
        :param expected_params: The parameters that are expected to be passed to the function.
        :param response: The response to return when the expected parameters are passed.
        :param error_code: The error code to raise when the expected parameters are passed.
                           If this is None, no error is raised.
        """
        self._stub_bifurcator(
            "converse", expected_params, response, error_code=error_code
        )
