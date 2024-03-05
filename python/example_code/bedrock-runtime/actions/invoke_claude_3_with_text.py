# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import json


def invoke_claude_3_with_text(params=None):
    # snippet-start:[python.example_code.bedrock-runtime.InvokeClaude3WithText.config]
    # Initialize the Amazon Bedrock runtime client
    client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")
    # snippet-end:[python.example_code.bedrock-runtime.InvokeClaude3WithText.config]

    if params:
        client = params["client"] or client

    # snippet-start:[python.example_code.bedrock-runtime.InvokeClaude3WithText.main]
    # Invoke Claude 3 with the text prompt
    model_id = "anthropic.claude-3-sonnet-20240229-v1:0"
    prompt = "Hello, who are you?"

    response = client.invoke_model(
        modelId=model_id,
        body=json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 1024,
                "messages": [
                    {"role": "user", "content": [{"type": "text", "text": prompt}]}
                ],
            }
        ),
    )

    # Process and print the response
    response_body = json.loads(response.get("body").read())
    for output in response_body.get("content", []):
        print(output["text"])

    # snippet-end:[python.example_code.bedrock-runtime.InvokeClaude3WithText.main]
    return response_body


if __name__ == "__main__":
    invoke_claude_3_with_text()
