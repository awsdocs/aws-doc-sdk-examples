# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.bedrock-runtime.InvokeModelWithResponseStream_Llama2_Quickstart]
# Send a prompt to Meta Llama 2 and print the response stream in real-time.

import boto3
import json

# Create a Bedrock Runtime client in the AWS Region of your choice.
client = boto3.client("bedrock-runtime", region_name="us-west-2")

# Set the model ID, e.g., Llama 2 Chat 13B.
model_id = "meta.llama2-13b-chat-v1"

# Define the user message to send.
user_message = "Describe the purpose of a 'hello world' program in one line."

# Embed the message in Llama 2's prompt format.
prompt = f"<s>[INST] {user_message} [/INST]"

# Format the request payload using the model's native structure.
request = {
    "prompt": prompt,
    # Optional inference parameters:
    "max_gen_len": 512,
    "temperature": 0.5,
    "top_p": 0.9,
}

# Encode and send the request.
response_stream = client.invoke_model_with_response_stream(
    body=json.dumps(request),
    modelId=model_id,
)

# Extract and print the response text in real-time.
for event in response_stream["body"]:
    chunk = json.loads(event["chunk"]["bytes"])
    if "generation" in chunk:
        print(chunk["generation"], end="")

# Learn more about the Llama 2 prompt format at:
# https://llama.meta.com/docs/model-cards-and-prompt-formats/meta-llama-2

# snippet-end:[python.example_code.bedrock-runtime.InvokeModelWithResponseStream_Llama2_Quickstart]
