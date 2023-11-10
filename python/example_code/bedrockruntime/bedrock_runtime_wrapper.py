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


# snippet-start:[python.example_code.bedrockruntime.BedrockRuntimeWrapper.class]
# snippet-start:[python.example_code.bedrockruntime.BedrockRuntimeWrapper.decl]
class BedrockRuntimeWrapper:
    """Encapsulates Amazon Bedrock Runtime actions."""

    def __init__(self, bedrock_runtime_client):
        """
        :param bedrock_runtime_client: A low-level client representing Amazon Bedrock Runtime.
                                       Describes the API operations for running inference using
                                       Bedrock models.
        """
        self.bedrock_runtime_client = bedrock_runtime_client

    # snippet-end:[python.example_code.bedrockruntime.BedrockRuntimeWrapper.decl]

    # snippet-start:[python.example_code.bedrockruntime.InvokeModel]
    def invoke_model(self, modelId, prompt):
        """
        Invokes the specified Bedrock model to run inference using the input provided in the
        request body. You can use InvokeModel to run inference for text models, image models,
        and embedding models.

        :param modelId: Identifier of the model.
        :param prompt: The prompt to be sent to the model.
        :return: The inferred response from the model.
        """


        try:
            prompt = "Human: " + prompt + "\n\nAssistant:"

            global payload

            # Models by different providers use their own respective input formats. To see the format
            # and content for the individual models, refer to:
            # - https://docs.aws.amazon.com/bedrock/latest/APIReference/API_runtime_InvokeModel.html
            # - https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters.html

            if "anthropic.claude" in modelId:
                payload = {
                    "prompt": prompt,
                    "temperature": 0.5,
                    "max_tokens_to_sample": 200,
                    "stop_sequences": ["\n\nHuman:"]
                }

            elif "ai21.j2" in modelId:
                payload = {
                    "prompt": prompt,
                    "temperature": 0.5,
                    "maxTokens": 200,
                    "stopSequences": ["\nHuman:"]
                }

            body = json.dumps(payload)

            response = self.bedrock_runtime_client.invoke_model(
                modelId=modelId,
                body=body
            )

            response_body = json.loads(response["body"].read())

        except ClientError:
            logger.error("Couldn't invoke model %s", modelId)
            raise
        else:

            # Models by different providers use their own respective output formats. To see the format
            # and content for the individual models, refer to:
            # - https://docs.aws.amazon.com/bedrock/latest/APIReference/API_runtime_InvokeModel.html
            # - https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters.html

            if "anthropic.claude" in modelId:
                return response_body["completion"]
            elif "ai21.j2" in modelId:
                return response_body["completions"][0]["data"]["text"]


# snippet-end:[python.example_code.bedrockruntime.InvokeModel]


# snippet-end:[python.example_code.bedrockruntime.BedrockWrapper.class]


def invoke(wrapper, modelId, prompt):
    print(f'\nInvoking: {modelId}')
    print("Prompt: " + prompt)

    try:
        completion = wrapper.invoke_model(
            modelId=modelId,
            prompt=prompt
        )
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

