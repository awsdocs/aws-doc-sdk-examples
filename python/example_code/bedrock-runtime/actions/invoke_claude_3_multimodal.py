import base64
import boto3
import json


def invoke_claude_3_multimodal(params=None):
    # snippet-start:[python.example_code.bedrock-runtime.InvokeClaude3Multimodal.config]

    # Initialize the Amazon Bedrock runtime client
    client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")

    # Read and encode the image file
    with open("actions/images/image.png", "rb") as image_file:
        image = base64.b64encode(image_file.read()).decode("utf8")
    # snippet-end:[python.example_code.bedrock-runtime.InvokeClaude3Multimodal.config]

    if params:
        client = params["client"] or client
        image = params["image"] or image

    # snippet-start:[python.example_code.bedrock-runtime.InvokeClaude3Multimodal.main]

    # Invoke the model with the prompt and the encoded image
    model_id = "anthropic.claude-3-sonnet-20240229-v1:0"
    prompt = "Tell me a short story about this image."

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
                                "text": prompt,
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

    # snippet-end:[python.example_code.bedrock-runtime.InvokeClaude3Multimodal.main]
    return response_body


if __name__ == "__main__":
    invoke_claude_3_multimodal()
