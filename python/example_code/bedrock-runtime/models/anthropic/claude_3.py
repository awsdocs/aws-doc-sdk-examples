# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Bedrock Runtime client
to run inferences using Anthropic Claude 3 models.
"""

import base64
import json
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock-runtime.Claude3Wrapper.class]
class Claude3Wrapper:
    """Encapsulates Claude 3 model invocations using the Amazon Bedrock Runtime client."""

    def __init__(self, client=None):
        """
        :param client: A low-level client representing Amazon Bedrock Runtime.
                       Describes the API operations for running inference using Bedrock models.
                       Default: None
        """
        self.client = client

    # snippet-start:[python.example_code.bedrock-runtime.InvokeAnthropicClaude3Text]
    def invoke_claude_3_with_text(self, prompt):
        """
        Invokes Anthropic Claude 3 Sonnet to run an inference using the input
        provided in the request body.

        :param prompt: The prompt that you want Claude 3 to complete.
        :return: Inference response from the model.
        """

        # Initialize the Amazon Bedrock runtime client
        client = self.client or boto3.client(
            service_name="bedrock-runtime", region_name="us-east-1"
        )

        # Invoke Claude 3 with the text prompt
        model_id = "anthropic.claude-3-sonnet-20240229-v1:0"

        try:
            response = client.invoke_model(
                modelId=model_id,
                body=json.dumps(
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
            )

            # Process and print the response
            result = json.loads(response.get("body").read())
            input_tokens = result["usage"]["input_tokens"]
            output_tokens = result["usage"]["output_tokens"]
            output_list = result.get("content", [])

            print("Invocation details:")
            print(f"- The input length is {input_tokens} tokens.")
            print(f"- The output length is {output_tokens} tokens.")

            print(f"- The model returned {len(output_list)} response(s):")
            for output in output_list:
                print(output["text"])

            return result

        except ClientError as err:
            logger.error(
                "Couldn't invoke Claude 3 Sonnet. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeAnthropicClaude3Text]

    # snippet-start:[python.example_code.bedrock-runtime.InvokeAnthropicClaude3Multimodal]
    def invoke_claude_3_multimodal(self, prompt, base64_image_data):
        """
        Invokes Anthropic Claude 3 Sonnet to run a multimodal inference using the input
        provided in the request body.

        :param prompt:            The prompt that you want Claude 3 to use.
        :param base64_image_data: The base64-encoded image that you want to add to the request.
        :return: Inference response from the model.
        """

        # Initialize the Amazon Bedrock runtime client
        client = self.client or boto3.client(
            service_name="bedrock-runtime", region_name="us-east-1"
        )

        # Invoke the model with the prompt and the encoded image
        model_id = "anthropic.claude-3-sonnet-20240229-v1:0"
        request_body = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 2048,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": prompt,
                        },
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

        try:
            response = client.invoke_model(
                modelId=model_id,
                body=json.dumps(request_body),
            )

            # Process and print the response
            result = json.loads(response.get("body").read())
            input_tokens = result["usage"]["input_tokens"]
            output_tokens = result["usage"]["output_tokens"]
            output_list = result.get("content", [])

            print("Invocation details:")
            print(f"- The input length is {input_tokens} tokens.")
            print(f"- The output length is {output_tokens} tokens.")

            print(f"- The model returned {len(output_list)} response(s):")
            for output in output_list:
                print(output["text"])

            return result
        except ClientError as err:
            logger.error(
                "Couldn't invoke Claude 3 Sonnet. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.bedrock-runtime.InvokeAnthropicClaude3Multimodal]


# snippet-end:[python.example_code.bedrock-runtime.Claude3Wrapper.class]


def usage_demo():
    """
    Demonstrates the invocation of Claude 3 models.
    """

    print("-" * 88)
    print("Welcome to the Amazon Bedrock Runtime demo with Anthropic Claude 3.")
    print("-" * 88)

    client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")
    wrapper = Claude3Wrapper(client)

    # Invoke Claude 3 with a text prompt
    text_prompt = "Hi, write a short sentence about yourself"
    print(f"Invoking Claude 3 Sonnet with '{text_prompt}'...")
    wrapper.invoke_claude_3_with_text(text_prompt)
    print("-" * 88)

    # Invoke Claude 3 with a multimodal prompt (text and image)
    multimodal_text_prompt = "Tell me a short story about this image."
    image_path = "resources/images/robot.png"
    with open(image_path, "rb") as image_file:
        image = base64.b64encode(image_file.read()).decode("utf8")
    print(
        f"Invoking Claude 3 Sonnet with '{multimodal_text_prompt}' and {image_path} ..."
    )
    wrapper.invoke_claude_3_multimodal(multimodal_text_prompt, image)
    print("-" * 88)


if __name__ == "__main__":
    usage_demo()
