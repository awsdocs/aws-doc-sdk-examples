# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.bedrock-runtime.InvokeModel_TitanTextEmbeddingsG1_Quickstart]
# Generate and print an embedding with Amazon Titan Text Embeddings G1

import boto3
import json

# Create a Bedrock Runtime client in the AWS Region of your choice.
client = boto3.client("bedrock-runtime", region_name="us-west-2")

# Set the model ID, e.g., Titan Text Embeddings G1.
model_id = "amazon.titan-embed-text-v1"

# The text to convert to an embedding.
input_text = "Please recommend books with a theme similar to the movie 'Inception'."

# Create the request for the model.
request = {"inputText": input_text}

# Encode and send the request.
response = client.invoke_model(body=json.dumps(request), modelId=model_id)

# Decode the model's native response body.
model_response = json.loads(response["body"].read())

# Extract and print the generated embedding.
embedding = model_response["embedding"]
print(embedding)

# snippet-end:[python.example_code.bedrock-runtime.InvokeModel_TitanTextEmbeddingsG1_Quickstart]
