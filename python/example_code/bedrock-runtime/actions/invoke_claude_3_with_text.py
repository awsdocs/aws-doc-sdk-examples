import boto3
import json


def invoke_claude_3_with_text():
    """
    Invokes Anthropic Claude 3 on Amazon Bedrock to run an inference using the input
    provided in the request body.

    Learn more about the available inference parameters and response fields in the Amazon Bedrock User Guide:
    https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html

    :return: The inference response object returned by Claude 3.
    """
    # snippet-start:[python.example_code.bedrock-runtime.InvokeClaude3WithText.code]
    # Initialize the Amazon Bedrock runtime client
    client = boto3.client(service_name="bedrock-runtime", region_name="us-east-1")

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
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "text",
                                "text": prompt
                            }
                        ]
                    }
                ],
            }
        ),
    )

    # Process and print the response
    response_body = json.loads(response.get("body").read())
    for output in response_body.get("content", []):
        print(output["text"])

    # snippet-end:[python.example_code.bedrock-runtime.InvokeClaude3WithText.code]
    return response_body


if __name__ == "__main__":
    invoke_claude_3_with_text()
