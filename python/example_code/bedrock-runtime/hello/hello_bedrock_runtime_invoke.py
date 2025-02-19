# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


# snippet-start:[bedrock-runtime.example_code.hello_bedrock_invoke.complete]

"""
Uses the Amazon Bedrock runtime client InvokeModel operation to send a prompt to a model.
"""
import logging
import json
import boto3


from botocore.exceptions import ClientError


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def invoke_model(brt, model_id, prompt):
    """
    Invokes the specified model with the supplied prompt.
    param brt: A bedrock runtime boto3 client
    param model_id: The model ID for the model that you want to use.
    param prompt: The prompt that you want to send to the model.

    :return: The text response from the model.
    """

    # Format the request payload using the model's native structure.
    native_request = {
        "inputText": prompt,
        "textGenerationConfig": {
            "maxTokenCount": 512,
            "temperature": 0.5,
            "topP": 0.9
        }
    }

    # Convert the native request to JSON.
    request = json.dumps(native_request)

    try:
        # Invoke the model with the request.
        response = brt.invoke_model(modelId=model_id, body=request)

        # Decode the response body.
        model_response = json.loads(response["body"].read())

        # Extract and print the response text.
        response_text = model_response["results"][0]["outputText"]
        return response_text

    except (ClientError, Exception) as e:
        print(f"ERROR: Can't invoke '{model_id}'. Reason: {e}")
        raise


def main():
    """Entry point for the example. Uses the AWS SDK for Python (Boto3)
    to create an Amazon Bedrock runtime client. Then sends a prompt to a model
    in the region set in the callers profile and credentials.
    """

    # Create an Amazon Bedrock Runtime client.
    brt = boto3.client("bedrock-runtime")

    # Set the model ID, e.g., Amazon Titan Text G1 - Express.
    model_id = "amazon.titan-text-express-v1"

    # Define the prompt for the model.
    prompt = "Describe the purpose of a 'hello world' program in one line."

    # Send the prompt to the model.
    response = invoke_model(brt, model_id, prompt)

    print(f"Response: {response}")

    logger.info("Done.")


if __name__ == "__main__":
    main()

 # snippet-end:[bedrock-runtime.example_code.hello_bedrock_invoke.complete]
