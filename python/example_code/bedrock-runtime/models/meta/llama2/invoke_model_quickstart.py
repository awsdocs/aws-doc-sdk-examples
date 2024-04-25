# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.bedrock-runtime.InvokeModel_Llama2_Quickstart]
# Send a prompt to Meta Llama 2 and print the response.

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
response = client.invoke_model(body=json.dumps(request), modelId=model_id)

# Decode the native response body.
model_response = json.loads(response["body"].read())

# Extract and print the generated text.
response_text = model_response["generation"]
print(response_text)

# Learn more about the Llama 2 prompt format at:
# https://llama.meta.com/docs/model-cards-and-prompt-formats/meta-llama-2

# snippet-end:[python.example_code.bedrock-runtime.InvokeModel_Llama2_Quickstart]
