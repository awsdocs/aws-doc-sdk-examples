# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock
to invoke Amazon Bedrock managed prompts with the Converse operation.
"""

import logging
import boto3

from botocore.exceptions import ClientError


logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s: %(message)s'
)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock-runtime.invoke_prompt_converse]
def invoke_prompt(client, prompt_arn, variables):
    """
    Invokes a prompt with the specified input variables using the Converse API.

    Args:
        client: The Bedrock Runtime client.
        prompt_arn (str): The ARN of the prompt to invoke.
        variables (dict): Dictionary containing the input variables for the prompt.

    Returns:
        str: The generated response.
    """
    try:
        logger.info("Generating response with prompt: %s", prompt_arn)

        # Create promptVariables dictionary dynamically
        prompt_variables = {
            key: {"text": str(value)} for key, value in variables.items()
        }

        response = client.converse(
            modelId=prompt_arn,
            promptVariables=prompt_variables
        )

        # Extract the response text
        message = response['output']['message']
        result = ""
        for content in message['content']:
            result += content['text']

        logger.info("Finished generating response with prompt: %s", prompt_arn)
    
        return result
    
    except ClientError as e:
        logger.exception("Client error invoking prompt version: %s", str(e))
        raise
    except Exception as e:
        logger.error("Error invoking prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock-runtime.invoke_prompt_converse]


