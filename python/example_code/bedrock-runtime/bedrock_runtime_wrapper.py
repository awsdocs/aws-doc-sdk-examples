# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Bedrock Runtime client
to run inference using Bedrock models.
"""

import logging
import boto3
import json
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
        Invokes theAnthropic Claude large-language model to run an inference using
        the input provided in the request body.

        :param prompt: The prompt that you want Claude to complete.
        :return: Inference response from the model.
        """

        try:

            # Claude requires you to format the prompt as follows:
            formatted_prompt = "Human: " + prompt + "\n\nAssistant:"

            # To see the format, ranges, and default values for the model parameters refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html
            body = json.dumps({
                # Required parameters
                "prompt": formatted_prompt,
                "max_tokens_to_sample": 200,
                # Optional parameters
                "temperature": 0.5,
                "top_p": 1,
                "top_k": 250,
                "stop_sequences": ["\n\nHuman:"]
            })

            response = self.bedrock_runtime_client.invoke_model(
                modelId="anthropic.claude-v2",
                body=body
            )

            # To see the format and content of the response body, refer to:
            # https://docs.anthropic.com/claude/reference/complete_post
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

            # To see the format, ranges, and default values for the model parameters refer to:
            # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-jurassic2.html
            body = json.dumps({
                # Required parameters
                "prompt": prompt,
                # Optional parameters
                "temperature": 0.5,
                "topP": 1,
                "maxTokens": 200,
                "stopSequences": [],
                "countPenalty": {"scale": 0},
                "presencePenalty": {"scale": 0},
                "frequencyPenalty": {"scale": 0},
            })

            response = self.bedrock_runtime_client.invoke_model(
                modelId="ai21.j2-mid-v1",
                body=body
            )

            # To see the format and content of the response body, refer to:
            # https://docs.ai21.com/reference/j2-complete-ref
            response_body = json.loads(response["body"].read())
            completion = response_body["completions"][0]["data"]["text"]

            return completion

        except ClientError:
            logger.error("Couldn't invoke Anthropic Claude")
            raise
    # snippet-end:[python.example_code.bedrock-runtime.InvokeAi21Jurassic2]


# snippet-end:[python.example_code.bedrock-runtime.BedrockWrapper.class]


def invoke(wrapper, modelId, prompt):
    print(f'\nInvoking: {modelId}')
    print("Prompt: " + prompt)

    try:
        if (modelId == "anthropic.claude-v2"):
            completion = wrapper.invoke_claude(prompt)
            print("Completion: " + completion.strip())

        elif (modelId == "ai21.j2-mid-v1"):
            completion = wrapper.invoke_jurassic2(prompt)
            print("Completion: " + completion.strip())

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

    prompt = "Hi, who are you?"

    invoke(wrapper, "anthropic.claude-v2", prompt)
    invoke(wrapper, "ai21.j2-mid-v1", prompt)


if __name__ == "__main__":
    usage_demo()
