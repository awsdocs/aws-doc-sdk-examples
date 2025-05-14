# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.bedrock-runtime.DocumentUnderstanding_DeepSeek]
# Send and process a document with DeepSeek on Amazon Bedrock.

import boto3
from botocore.exceptions import ClientError

# Create a Bedrock Runtime client in the AWS Region you want to use.
client = boto3.client("bedrock-runtime", region_name="us-east-1")

# Set the model ID, e.g. DeepSeek-R1
model_id = "us.deepseek.r1-v1:0"

# Load the document
with open("example-data/amazon-nova-service-cards.pdf", "rb") as file:
    document_bytes = file.read()

# Start a conversation with a user message and the document
conversation = [
    {
        "role": "user",
        "content": [
            {"text": "Briefly compare the models described in this document"},
            {
                "document": {
                    # Available formats: html, md, pdf, doc/docx, xls/xlsx, csv, and txt
                    "format": "pdf",
                    "name": "Amazon Nova Service Cards",
                    "source": {"bytes": document_bytes},
                }
            },
        ],
    }
]

try:
    # Send the message to the model, using a basic inference configuration.
    response = client.converse(
        modelId=model_id,
        messages=conversation,
        inferenceConfig={"maxTokens": 2000, "temperature": 0.3},
    )

    # Extract and print the reasoning and response text.
    reasoning, response_text = "", ""
    for item in response["output"]["message"]["content"]:
        for key, value in item.items():
            if key == "reasoningContent":
                reasoning = value["reasoningText"]["text"]
            elif key == "text":
                response_text = value

    print(f"\nReasoning:\n{reasoning}")
    print(f"\nResponse:\n{response_text}")

except (ClientError, Exception) as e:
    print(f"ERROR: Can't invoke '{model_id}'. Reason: {e}")
    exit(1)

# snippet-end:[python.example_code.bedrock-runtime.DocumentUnderstanding_DeepSeek]
