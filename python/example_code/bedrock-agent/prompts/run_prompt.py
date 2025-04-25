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
def invoke_prompt(client, prompt_arn, variables):
    """
    Invokes a prompt with the specified input variables using the Converse API.

    Args:
        client: The Bedrock Runtime client.
        version (str, optional): The version or alias of the prompt to invoke.
        input_variables (dict): The input variables for the prompt.

    Returns:
        str: The generated playlist
    """
    try:
        logger.info("Generating playlist with prompt: %s", prompt_arn)

        playlist = ""
 
        response = client.converse(
            modelId=prompt_arn,
            promptVariables={
                "genre": {
                    "text": variables['genre']
                },
                "number": {
                    "text": variables['number']
                }
            }
        )


 

        message = response['output']['message']
        for content in message['content']:
            playlist = content['text']

        logger.info("Finished generating playlist with prompt: %s", prompt_arn)
    
        return playlist
  

        
    except Exception as e:
        logger.exception("Error invoking prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.invoke_prompt]


if __name__ == "__main__":
  

    prompt_id="94AWYDN2VQ"
    version = "arn:aws:bedrock:us-east-1:484315795920:prompt/94AWYDN2VQ:1"
    input_variables ={"genre": "pop", "number": "5"}

    bedrock_runtime_client = boto3.client(service_name='bedrock-runtime')
    #input_variables = json.loads(args.input_variables)
    playlist = invoke_prompt(bedrock_runtime_client, version, input_variables)
    print(playlist)
