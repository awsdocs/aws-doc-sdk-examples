# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock
to invoke Amazon Bedrock managed prompts.
"""

import argparse
import boto3
import json
import logging

logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s: %(message)s'
)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock.invoke_prompt]
def invoke_prompt(client, prompt_id, version, input_variables):
    """
    Invokes a prompt with the specified input variables.

    Args:
        client: The Bedrock Runtime client.
        prompt_id (str): The ID of the prompt to invoke.
        version (str, optional): The version or alias of the prompt to invoke.
        input_variables (dict): The input variables for the prompt.

    Returns:
        dict: The response from InvokePrompt.
    """
    try:
        logger.info("Invoking prompt ID: %s", prompt_id)
        if version:
            logger.info("Using version/alias: %s", version)
        else:
            logger.info("Using latest version")

        # Format the prompt for Claude model
        # Claude requires a specific format for the prompt
        # Check if we have product info or generic variables
        if all(key in input_variables for key in ['product_name', 'category', 'features', 'audience']):
            # Product description prompt
            product_info = input_variables
            prompt_text = f"""Human: Create a compelling product description for the following product:

Product Name: {product_info['product_name']}
Category: {product_info['category']}
Key Features: {product_info['features']}
Target Audience: {product_info['audience']}

Please write a concise, engaging, and professional product description that highlights the benefits and features.
"""
            # Add price point if available
            if 'price_point' in product_info:
                prompt_text += f"\nPrice Point: {product_info['price_point']}\n"
        else:
            # Generic prompt with whatever variables we have
            prompt_text = "Human: Here are the input variables:\n\n"
            for key, value in input_variables.items():
                prompt_text += f"{key}: {value}\n"
            prompt_text += "\nPlease process these variables and provide a response.\n"
            
        prompt_text += "\nAssistant:"
        
        # Prepare the request body
        request_body = {
            "modelId": "anthropic.claude-v2",
            "contentType": "application/json",
            "accept": "application/json",
            "body": json.dumps({
                "prompt": prompt_text,
                "max_tokens_to_sample": 500,
                "temperature": 0.7,
                "top_p": 0.9,
            })
        }
        
        # Invoke the model
        response = client.invoke_model(**request_body)
        
        # Parse the response
        response_body = json.loads(response['body'].read().decode())
        
        # Return the output
        return {
            "output": response_body['completion'].strip()
        }
        
    except Exception as e:
        logger.exception("Error invoking prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.invoke_prompt]

def main():
    """
    Entry point for the invoke prompt script.
    """
    parser = argparse.ArgumentParser(
        description="Invoke an Amazon Bedrock managed prompt."
    )
    parser.add_argument(
        '--region',
        default='us-east-1',
        help="The AWS Region to use."
    )
    parser.add_argument(
        '--prompt-id',
        required=True,
        help="The ID of the prompt to invoke."
    )
    parser.add_argument(
        '--version',
        help="The version of the prompt to invoke."
    )
    parser.add_argument(
        '--input-variables',
        required=True,
        help="The input variables for the prompt as a JSON string."
    )
    args = parser.parse_args()

    bedrock_runtime_client = boto3.client('bedrock-runtime', region_name=args.region)
    
    try:
        input_vars = json.loads(args.input_variables)
        
        result = invoke_prompt(
            bedrock_runtime_client,
            args.prompt_id,
            args.version,
            input_vars
        )
        
        print("\nGenerated Output:")
        print(result['output'])
        
    except Exception as e:
        logger.exception("Error: %s", str(e))
        
if __name__ == "__main__":
    main()
