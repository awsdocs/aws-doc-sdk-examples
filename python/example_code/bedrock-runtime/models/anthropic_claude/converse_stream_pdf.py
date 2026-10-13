# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Use the Conversation API to send a text message along with PDF as input to Anthropic Claude
# and print the response stream.

import boto3
from botocore.config import Config

config = Config(
    connect_timeout=1000,
    read_timeout=1000,
)
# Create a Bedrock Runtime client in the AWS Region you want to use.
session = boto3.session.Session(region_name="us-east-1")
bedrock_runtime = session.client(service_name="bedrock-runtime", config=config)
pdf_path = input("Enter the path to the PDF file: ")
prompt = """
Please analyze this PDF document and provide the following information:

1. Document Title
2. Main topics covered
3. Key findings or conclusions
4. Important dates or numbers mentioned
5. Summary in 3-4 sentences

Format your response in a clear, structured way.
"""

# Set the model ID, e.g., Claude Haiku 4.5.
# Claude Haiku 4.5 is available through an inference profile, so this uses the
# "us." cross-Region profile ID that matches the us-east-1 Region set above.
MODEL_ID = "us.anthropic.claude-haiku-4-5-20251001-v1:0"


def optimize_reel_prompt(user_prompt, ref_image):
    # open PDF
    with open(ref_image, "rb") as f:
        image = f.read()

    system = [{"text": "You are an expert in summarizing PDF docs."}]
    # payload of PDF as input
    messages = [
        {
            "role": "user",
            "content": [
                {
                    "document": {
                        "format": "pdf",
                        "name": "DocumentPDFmessages",
                        "source": {"bytes": image},
                    }
                },
                {"text": user_prompt},
            ],
        }
    ]
    # Configure the inference parameters.
    inf_params = {"maxTokens": 800, "temperature": 0.5}
    model_response = bedrock_runtime.converse_stream(
        modelId=MODEL_ID, messages=messages, system=system, inferenceConfig=inf_params
    )
    text = ""
    stream = model_response.get("stream")
    if stream:
        for event in stream:
            if "contentBlockDelta" in event:
                text += event["contentBlockDelta"]["delta"]["text"]
                print(event["contentBlockDelta"]["delta"]["text"], end="")
    return text


if __name__ == "__main__":
    txt = optimize_reel_prompt(prompt, pdf_path)
    print(txt)
