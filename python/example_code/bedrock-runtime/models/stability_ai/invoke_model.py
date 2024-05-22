# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import base64
import os


def save_image(base64_image_data):
    output_dir = "output"

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    i = 1
    while os.path.exists(os.path.join(output_dir, f"image_{i}.png")):
        i += 1

    image_data = base64.b64decode(base64_image_data)

    file_path = os.path.join(output_dir, f"image_{i}.png")
    with open(file_path, "wb") as file:
        file.write(image_data)

    return file_path


# snippet-start:[python.example_code.bedrock-runtime.InvokeModel_StableDiffusion]
# Use the native inference API to create an image with Stability.ai Stable Diffusion

import boto3
import json
import random

# Create a Bedrock Runtime client in the AWS Region of your choice.
client = boto3.client("bedrock-runtime", region_name="us-east-1")

# Set the model ID, e.g., Stable Diffusion XL 1.
model_id = "stability.stable-diffusion-xl-v1"

# Define the image generation prompt for the model.
prompt = "A stylized picture of a cute old steampunk robot."

# Generate a random seed.
seed = random.randint(0, 4294967295)

# Format the request payload using the model's native structure.
native_request = {
    "text_prompts": [{"text": prompt}],
    "style_preset": "photographic",
    "seed": seed,
    "cfg_scale": 10,
    "steps": 30,
}

# Convert the native request to JSON.
request = json.dumps(native_request)

# Invoke the model with the request.
response = client.invoke_model(modelId=model_id, body=request)

# Decode the response body.
model_response = json.loads(response["body"].read())

# Extract the image data.
base64_image_data = model_response["artifacts"][0]["base64"]

# Save the generated image to a local folder.
image_path = save_image(base64_image_data)
print(f"The generated image has been saved to {image_path}")

# snippet-end:[python.example_code.bedrock-runtime.InvokeModel_StableDiffusion]
