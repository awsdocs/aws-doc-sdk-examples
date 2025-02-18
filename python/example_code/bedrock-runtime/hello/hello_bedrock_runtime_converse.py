# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


# snippet-start:[bedrock-runtime.example_code.hello_bedrock_converse.complete]

"""
Uses the Amazon Bedrock runtime client Converse operation to send a user message to a model.
"""
import logging
import boto3

from botocore.exceptions import ClientError


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def converse(brt, model_id, user_message):
    """
    Uses the Converse operation to send a user message to the supplied model.
    param brt: A bedrock runtime boto3 client
    param model_id: The model ID for the model that you want to use.
    param user message: The user message that you want to send to the model.

    :return: The text response from the model.
    """

    # Format the request payload using the model's native structure.
    conversation = [
    {
        "role": "user",
        "content": [{"text": user_message}],
    }
]

    try:
        # Send the message to the model, using a basic inference configuration.
        response = brt.converse(
            modelId=model_id,
            messages=conversation,
            inferenceConfig={"maxTokens": 512, "temperature": 0.5, "topP": 0.9},
        )

        # Extract and print the response text.
        response_text = response["output"]["message"]["content"][0]["text"]
        return response_text

    except (ClientError, Exception) as e:
        print(f"ERROR: Can't invoke '{model_id}'. Reason: {e}")
        raise


def main():
    """Entry point for the example. Uses the AWS SDK for Python (Boto3)
    to create an Amazon Bedrock runtime client. Then sends a user message to a model
    in the region set in the callers profile and credentials.
    """

    # Create an Amazon Bedrock Runtime client.
    brt = boto3.client("bedrock-runtime")

    # Set the model ID, e.g., Amazon Titan Text G1 - Express.
    model_id = "amazon.titan-text-express-v1"

    # Define the message for the model.
    message = "Describe the purpose of a 'hello world' program in one line."

    # Send the message to the model.
    response = converse(brt, model_id, message)

    print(f"Response: {response}")

    logger.info("Done.")


if __name__ == "__main__":
    main()

 # snippet-end:[bedrock-runtime.example_code.hello_bedrock_converse.complete]
 

