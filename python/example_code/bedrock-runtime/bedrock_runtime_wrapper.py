# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Bedrock Runtime client
to run inferences using Bedrock models.
"""

import asyncio
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
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html

            # Claude requires you to enclose the prompt as follows:
            enclosed_prompt = "Human: " + prompt + "\n\nAssistant:"

            body = {
                "prompt": enclosed_prompt,
                "max_tokens_to_sample": 200,
                "temperature": 0.5,
                "stop_sequences": ["\n\nHuman:"],
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId="anthropic.claude-v2", body=json.dumps(body)
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
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-jurassic2.html

            body = {
                "prompt": prompt,
                "temperature": 0.5,
                "maxTokens": 200,
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId="ai21.j2-mid-v1", body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            completion = response_body["completions"][0]["data"]["text"]

            return completion

        except ClientError:
            logger.error("Couldn't invoke Jurassic-2")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeAi21Jurassic2]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeMetaLlama2]
    def invoke_llama2(self, prompt):
        """
        Invokes the Meta Llama 2 large-language model to run an inference
        using the input provided in the request body.

        :param prompt: The prompt that you want Llama 2 to complete.
        :return: Inference response from the model.
        """

        try:
            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html

            body = {
                "prompt": prompt,
                "temperature": 0.5,
                "top_p": 0.9,
                "max_gen_len": 512,
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId="meta.llama2-13b-chat-v1", body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            completion = response_body["generation"]

            return completion

        except ClientError:
            logger.error("Couldn't invoke Llama 2")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeMetaLlama2]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeMistral7B]
    def invoke_mistral_7b(self, prompt):
        """
        Invokes the Mistral 7B model to run an inference using the input
        provided in the request body.

        :param prompt: The prompt that you want Mistral to complete.
        :return: List of inference responses from the model.
        """

        try:
            # Mistral instruct models provide optimal results when
            # embedding the prompt into the following template:
            instruction = f"<s>[INST] {prompt} [/INST]"

            model_id = "mistral.mistral-7b-instruct-v0:2"

            body = {
                "prompt": instruction,
                "max_tokens": 200,
                "temperature": 0.5,
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId=model_id, body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            outputs = response_body.get("outputs")

            completions = [output["text"] for output in outputs]

            return completions

        except ClientError:
            logger.error("Couldn't invoke Mistral 7B")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeMistral7B]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeMixtral8x7B]
    def invoke_mixtral_8x7b(self, prompt):
        """
        Invokes the Mixtral 8c7B model to run an inference using the input
        provided in the request body.

        :param prompt: The prompt that you want Mixtral to complete.
        :return: List of inference responses from the model.
        """

        try:
            # Mistral instruct models provide optimal results when
            # embedding the prompt into the following template:
            instruction = f"<s>[INST] {prompt} [/INST]"

            model_id = "mistral.mixtral-8x7b-instruct-v0:1"

            body = {
                "prompt": instruction,
                "max_tokens": 200,
                "temperature": 0.5,
            }

            response = self.bedrock_runtime_client.invoke_model(
                modelId=model_id, body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            outputs = response_body.get("outputs")

            completions = [output["text"] for output in outputs]

            return completions

        except ClientError:
            logger.error("Couldn't invoke Mixtral 8x7B")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeMixtral8x7B]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeStableDiffusion]
    def invoke_stable_diffusion(self, prompt, seed, style_preset=None):
        """
        Invokes the Stability.ai Stable Diffusion XL model to create an image using
        the input provided in the request body.

        :param prompt: The prompt that you want Stable Diffusion  to use for image generation.
        :param seed: Random noise seed (omit this option or use 0 for a random seed)
        :param style_preset: Pass in a style preset to guide the image model towards
                             a particular style.
        :return: Base64-encoded inference response from the model.
        """

        try:
            # The different model providers have individual request and response formats.
            # For the format, ranges, and available style_presets of Stable Diffusion models refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-stability-diffusion.html

            body = {
                "text_prompts": [{"text": prompt}],
                "seed": seed,
                "cfg_scale": 10,
                "steps": 30,
            }

            if style_preset:
                body["style_preset"] = style_preset

            response = self.bedrock_runtime_client.invoke_model(
                modelId="stability.stable-diffusion-xl", body=json.dumps(body)
            )

            response_body = json.loads(response["body"].read())
            base64_image_data = response_body["artifacts"][0]["base64"]

            return base64_image_data

        except ClientError:
            logger.error("Couldn't invoke Stable Diffusion XL")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeStableDiffusion]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeTitanImage]
    def invoke_titan_image(self, prompt, seed):
        """
        Invokes the Titan Image model to create an image using the input provided in the request body.

        :param prompt: The prompt that you want Amazon Titan to use for image generation.
        :param seed: Random noise seed (range: 0 to 2147483647)
        :return: Base64-encoded inference response from the model.
        """

        try:
            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for Titan Image models refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-image.html

            request = json.dumps(
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
            )

            response = self.bedrock_runtime_client.invoke_model(
                modelId="amazon.titan-image-generator-v1", body=request
            )

            response_body = json.loads(response["body"].read())
            base64_image_data = response_body["images"][0]

            return base64_image_data

        except ClientError:
            logger.error("Couldn't invoke Titan Image generator")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeTitanImage]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeModelWithResponseStream]
    async def invoke_model_with_response_stream(self, prompt):
        """
        Invokes the Anthropic Claude 2 model to run an inference and process the response stream.

        :param prompt: The prompt that you want Claude to complete.
        :return: Inference response from the model.
        """

        try:
            # The different model providers have individual request and response formats.
            # For the format, ranges, and default values for Anthropic Claude, refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html

            # Claude requires you to enclose the prompt as follows:
            enclosed_prompt = "Human: " + prompt + "\n\nAssistant:"

            body = {
                "prompt": enclosed_prompt,
                "max_tokens_to_sample": 1024,
                "temperature": 0.5,
                "stop_sequences": ["\n\nHuman:"],
            }

            response = self.bedrock_runtime_client.invoke_model_with_response_stream(
                modelId="anthropic.claude-v2", body=json.dumps(body)
            )

            for event in response.get("body"):
                chunk = json.loads(event["chunk"]["bytes"])["completion"]
                yield chunk

        except ClientError:
            logger.error("Couldn't invoke Anthropic Claude v2")
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeModelWithResponseStream]


def save_image(base64_image_data, model):
    output_dir = "output/" + model

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    i = 1
    while os.path.exists(os.path.join(output_dir, f"image_{i}.png")):
        i += 1

    image_data = base64.b64decode(base64_image_data)

    file_path = os.path.join(output_dir, f"image_{i}.png")
    with open(file_path, "wb") as file:
        file.write(image_data)

    return file_path


def invoke(wrapper, model_id, prompt, style_preset=None):
    print("-" * 88)
    print(f"Invoking: {model_id}")
    print("Prompt: " + prompt)

    try:
        if model_id == "anthropic.claude-v2":
            completion = wrapper.invoke_claude(prompt)
            print("Completion: " + completion)

        elif model_id == "ai21.j2-mid-v1":
            completion = wrapper.invoke_jurassic2(prompt)
            print("Completion: " + completion)

        elif model_id == "meta.llama2-13b-chat-v1":
            completion = wrapper.invoke_llama2(prompt)
            print("Completion: " + completion)

        elif model_id == "mistral.mistral-7b-instruct-v0:2":
            completions = wrapper.invoke_mistral_7b(prompt)
            for completion in completions:
                print("Completion: " + completion)

        elif model_id == "mistral.mixtral-8x7b-instruct-v0:1":
            completions = wrapper.invoke_mixtral_8x7b(prompt)
            for completion in completions:
                print("Completion: " + completion)

        elif model_id == "stability.stable-diffusion-xl":
            seed = random.randint(0, 4294967295)
            base64_image_data = wrapper.invoke_stable_diffusion(
                prompt, seed, style_preset
            )
            image_path = save_image(base64_image_data, "diffusion")
            print(f"The generated image has been saved to {image_path}")

        elif model_id == "amazon.titan-image-generator-v1":
            seed = random.randint(0, 2147483647)
            base64_image_data = wrapper.invoke_titan_image(prompt, seed)
            image_path = save_image(base64_image_data, "titan")
            print(f"The generated image has been saved to {image_path}")

    except ClientError:
        logger.exception("Couldn't invoke model %s", model_id)
        raise


async def invoke_with_response_stream(wrapper, model_id, prompt):
    print("-" * 88)
    print(f"Invoking: {model_id} with response stream")
    print("Prompt: " + prompt)
    print("\nResponse stream:")

    try:
        async for completion in wrapper.invoke_model_with_response_stream(prompt):
            print(completion, end="")

    except ClientError:
        logger.exception("Couldn't invoke model %s", model_id)
        raise

    print()


def usage_demo():
    """
    Demonstrates the invocation of various large-language and image generation models:
    Anthropic Claude 2, AI21 Labs Jurassic-2, and Stability.ai Stable Diffusion XL.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Runtime demo.")
    print("-" * 88)

    client = boto3.client(service_name="bedrock-runtime", region_name="us-west-2")

    wrapper = BedrockRuntimeWrapper(client)

    text_generation_prompt = "Hi, write a paragraph about yourself."

    invoke(wrapper, "anthropic.claude-v2", text_generation_prompt)

    invoke(wrapper, "ai21.j2-mid-v1", text_generation_prompt)

    invoke(wrapper, "meta.llama2-13b-chat-v1", text_generation_prompt)

    invoke(wrapper, "mistral.mistral-7b-instruct-v0:2", text_generation_prompt)

    invoke(wrapper, "mistral.mixtral-8x7b-instruct-v0:1", text_generation_prompt)

    asyncio.run(
        invoke_with_response_stream(
            wrapper, "anthropic.claude-v2", text_generation_prompt
        )
    )

    image_generation_prompt = "stylized picture of a cute old steampunk robot"

    image_style_preset = "photographic"

    invoke(
        wrapper,
        "stability.stable-diffusion-xl",
        image_generation_prompt,
        image_style_preset,
    )

    invoke(wrapper, "amazon.titan-image-generator-v1", image_generation_prompt)


# snippet-end:[python.example_code.bedrock-runtime.BedrockRuntimeWrapper.class]

if __name__ == "__main__":
    usage_demo()
