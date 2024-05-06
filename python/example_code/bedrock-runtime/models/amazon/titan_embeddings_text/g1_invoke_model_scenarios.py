# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

This class demonstrates how to use InvokeModel with Amazon Titan Text Embeddings G1
on Amazon Bedrock.

For more examples in different programming languages check out the Amazon Bedrock User Guide at:
https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
"""

import boto3
import json
import pprint
import sys

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("../..")
import demo_tools.question as q


# snippet-start:[python.example_code.bedrock-runtime.InvokeModel_TitanTextEmbeddingsG1_AdditionalFields]
def invoke_model(input_text, client=None):
    """
    Invoke Amazon Titan Text Embeddings G1 and print the response.

    :param input_text: The text to convert to an embedding.
    :param client:     An optional Bedrock Runtime client instance.
                       Defaults to None if not provided.
    :return: The model's response object.
    """

    # Create a Bedrock Runtime client if not provided.
    client = client or boto3.client("bedrock-runtime", region_name="us-west-2")

    # Set the model ID, e.g., Titan Text Embeddings G1.
    model_id = "amazon.titan-embed-text-v1"

    # Create the request for the model.
    request = {"inputText": input_text}

    # Encode and send the request.
    response = client.invoke_model(
        body=json.dumps(request),
        modelId=model_id,
    )

    # Decode the response
    model_response = json.loads(response["body"].read())

    # Extract and print the generated embedding and the input text token count.
    embedding = model_response["embedding"]
    input_token_count = model_response["inputTextTokenCount"]

    print(f"Embedding: {embedding}\n")
    print(f"Input token count: {input_token_count}")

    return model_response


# snippet-end:[python.example_code.bedrock-runtime.InvokeModel_TitanTextEmbeddingsG1_AdditionalFields]


def run_demo():
    print(f"{'=' * 80}\nWelcome to the Amazon Bedrock demo!")

    print(f"{'=' * 80}\nDemo: Create an embedding\n{'-' * 80}")
    print("Type an input text you'd like to turn into an embedding:")
    input_text = q.ask("", q.non_empty)

    print(f"{'-' * 80}")
    print(f"Embedding the text: '{input_text}'")

    print(f"{'-' * 80}")
    response = invoke_model(input_text)

    input(f"{'-' * 80}\nPress Enter to see the detailed response...")
    print("Returned response:")
    pprint.pprint(response)

    print(f"{'=' * 80}\nThanks for running the Amazon Bedrock demo!")
    print("For more examples in many different programming languages check out:")
    print(
        "https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html"
    )
    print("=" * 80)


if __name__ == "__main__":
    run_demo()
