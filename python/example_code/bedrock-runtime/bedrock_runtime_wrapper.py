# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Bedrock Runtime client
to run inference using Bedrock models.
"""

import base64
import json
import logging
import os
import random

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock-runtime.BedrockRuntimeWrapper.class]
# snippet-start:[python.example_code.bedrock-runtime.BedrockRuntimeWrapper.decl]
class BedrockRuntimeWrapper:
    """Encapsulates Amazon Bedrock Runtime actions."""

    def __init__(self, bedrock_runtime_client):
        """
        :param bedrock_runtime_client: A low-level client representing Amazon Bedrock Runtime.
                                       Describes the API operations for running inference using
                                       Bedrock models.
        """
        self.bedrock_runtime_client = bedrock_runtime_client

    # snippet-end:[python.example_code.bedrock-runtime.BedrockRuntimeWrapper.decl]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeAnthropicClaude]
    def invoke_claude(self, prompt):
        """
        Invokes the Anthropic Claude 2 model to run an inference using the input
        provided in the request body.

        :param prompt: The prompt that you want Claude to complete.
        :return: Inference response from the model.
        """

        try:

            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for Anthropic Claude, refer to:
            # https://docs.anthropic.com/claude/reference/complete_post

            # Claude requires you to enclose the prompt as follows:
            enclosed_prompt = "Human: " + prompt + "\n\nAssistant:"

            body = {
                "prompt": enclosed_prompt,
                "max_tokens_to_sample": 200,
                "temperature": 0.5,
                "stop_sequences": ["\n\nHuman:"]
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId="anthropic.claude-v2",
                body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            completion = response_body["completion"]

            return completion

        except ClientError:
            logger.error("Couldn't invoke Anthropic Claude")
            raise
    # snippet-end:[python.example_code.bedrock-runtime.InvokeAnthropicClaude]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeAi21Jurassic2]
    def invoke_jurassic2(self, prompt):
        """
        Invokes the AI21 Labs Jurassic-2 large-language model to run an inference
        using the input provided in the request body.

        :param prompt: The prompt that you want Jurassic-2 to complete.
        :return: Inference response from the model.
        """

        try:

            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
            # https://docs.ai21.com/reference/j2-complete-ref

            body = {
                "prompt": prompt,
                "temperature": 0.5,
                "maxTokens": 200,
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId="ai21.j2-mid-v1",
                body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            completion = response_body["completions"][0]["data"]["text"]

            return completion

        except ClientError:
            logger.error("Couldn't invoke Anthropic Claude 2")
            raise
    # snippet-end:[python.example_code.bedrock-runtime.InvokeAi21Jurassic2]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeStableDiffusion]
    def invoke_stable_diffusion(self, prompt, seed, style_preset=None):
        """
        Invokes the Stability.ai Stable Diffusion XL model to create an image using
        the input provided in the request body.

        :param prompt: The prompt that you want Stable Diffusion to complete.
        :param seed:
        :param style_preset:
        :return: Base64-encoded inference response from the model.
        """

        try:

            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for Stability.ai Diffusion models,
            # refer to: https://platform.stability.ai/docs/api-reference#tag/v1generation

            body = {
                "text_prompts": [{"text": prompt}],
                "seed": seed,
                "cfg_scale": 10,
                "steps": 30
            }

            if style_preset:
                body["style_preset"] = style_preset

            response = self.bedrock_runtime_client.invoke_model(
                modelId="stability.stable-diffusion-xl",
                body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            base64_image_data = response_body["artifacts"][0]["base64"]

            return base64_image_data

        except ClientError:
            logger.error("Couldn't invoke Stable Diffusion XL")
            raise
    # snippet-end:[python.example_code.bedrock-runtime.InvokeStableDiffusion]


# snippet-end:[python.example_code.bedrock-runtime.BedrockWrapper.class]

def save_image(base64_image_data):
    directory = "output"

    if not os.path.exists(directory):
        os.makedirs(directory)

    i = 1
    while os.path.exists(os.path.join(directory, f"image_{i}.png")):
        i += 1

    image_data = base64.b64decode(base64_image_data)

    file_path = os.path.join(directory, f"image_{i}.png")
    with open(file_path, 'wb') as file:
        file.write(image_data)

    return file_path

def invoke(wrapper, modelId, prompt, style_preset=None):
    print("-" * 88)
    print(f'Invoking: {modelId}')
    print("Prompt: " + prompt)

    try:
        if (modelId == "anthropic.claude-v2"):
            completion = wrapper.invoke_claude(prompt)
            print("Completion: " + completion.strip())

        elif (modelId == "ai21.j2-mid-v1"):
            completion = wrapper.invoke_jurassic2(prompt)
            print("Completion: " + completion.strip())

        elif (modelId == "stability.stable-diffusion-xl"):
            seed = random.randint(0, 4294967295)
            base64_image_data = wrapper.invoke_stable_diffusion(prompt, seed, style_preset)
            image_path = save_image(base64_image_data)
            print(f'The generated image has been saved to {image_path}')

    except ClientError:
        logger.exception("Couldn't invoke model %s", modelId)
        raise


def usage_demo():
    """
    Shows how to invoke different large-language models.
    This demonstration invokes Anthropic Claude 2 and AI21 Labs Jurassic-2 with
    a prompt and prints the respective model's completion.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Runtime demo.")
    print("-" * 88)

    client = boto3.client(
        service_name="bedrock-runtime",
        region_name="us-east-1"
    )

    wrapper = BedrockRuntimeWrapper(client)

    text_prompt = "Hi, who are you?"
    invoke(wrapper, "anthropic.claude-v2", text_prompt)
    invoke(wrapper, "ai21.j2-mid-v1", text_prompt)

    image_prompt = "A sunset over the ocean"
    style_preset = "photographic"
    invoke(wrapper, "stability.stable-diffusion-xl", image_prompt, style_preset)


if __name__ == "__main__":
    usage_demo()