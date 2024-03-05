import base64
import boto3
import json

# Initialize the Amazon Bedrock runtime client
client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")

# Read and encode the image file
with open("images/image.png", "rb") as image_file:
    image = base64.b64encode(image_file.read()).decode("utf8")

# Invoke the model with the encoded image in the request body
model_id = "anthropic.claude-3-sonnet-20240229-v1:0"

response = client.invoke_model(
    modelId=model_id,
    body=json.dumps(
        {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 2048,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": "Tell me a short story about this image.",
                        },
                        {
                            "type": "image",
                            "source": {
                                "type": "base64",
                                "media_type": "image/png",
                                "data": image,
                            },
                        },
                    ],
                }
            ],
        }
    ),
)

# Process and print the response
response_body = json.loads(response.get("body").read())
for output in response_body.get("content", []):
    print(output["text"])
