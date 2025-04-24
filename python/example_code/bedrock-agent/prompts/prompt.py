# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock
to manage Amazon Bedrock managed prompts.
"""

import logging
from botocore.exceptions import ClientError


logging.basicConfig(
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock.create_prompt]
def create_prompt(client, prompt_name, prompt_description, prompt_template, model_id=None):
    """
    Creates an Amazon Bedrock managed prompt.

    Args:
    client: Amazon Bedrock Agent boto3 client.
    prompt_name (str): The name for the new prompt.
    prompt_description (str): The description for the new prompt.
    prompt_template (str): The template for the prompt.
    model_id (str, optional): The model ID to associate with the prompt.

    Returns:
        dict: The response from CreatePrompt.
    """
    try:
        logger.info("Creating prompt: %s.", prompt_name)
        
        # Create a variant with the template
        variant = {
            "name": "default",
            "templateType": "TEXT",
            "templateConfiguration": {
                "text": {
                    "text": prompt_template,
                    "inputVariables": []
                }
            }
        }
        
        # Extract input variables from the template
        # Look for patterns like {{variable_name}}
        import re
        variables = re.findall(r'{{(.*?)}}', prompt_template)
        for var in variables:
            variant["templateConfiguration"]["text"]["inputVariables"].append({"name": var.strip()})
        
        # Add model ID if provided
        if model_id:
            variant["modelId"] = model_id
        
        # Create the prompt with the variant
        create_params = {
            'name': prompt_name,
            'description': prompt_description,
            'variants': [variant]
        }
            
        response = client.create_prompt(**create_params)

        logger.info("Successfully created prompt: %s. ID: %s",
                    prompt_name,
                    response['id'])

        return response

    except ClientError as e:
        logger.exception("Client error creating prompt: %s", str(e))
        raise

    except Exception as e:
        logger.exception("Unexpected error creating prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.create_prompt]

# snippet-start:[python.example_code.bedrock.get_prompt]
def get_prompt(client, prompt_id):
    """
    Gets an Amazon Bedrock managed prompt.

    Args:
    client: Amazon Bedrock Agent boto3 client.
    prompt_id (str): The identifier of the prompt that you want to get.

    Returns:
        dict: The response from the GetPrompt operation.
    """
    try:
        logger.info("Getting prompt ID: %s.", prompt_id)

        response = client.get_prompt(
            promptIdentifier=prompt_id
        )

        logger.info("Retrieved prompt ID: %s. Name: %s", 
                    prompt_id,
                    response['name'])

        return response

    except ClientError as e:
        logger.exception("Client error getting prompt: %s", str(e))
        raise

    except Exception as e:
        logger.exception("Unexpected error getting prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.get_prompt]

# snippet-start:[python.example_code.bedrock.update_prompt]
def update_prompt(client, prompt_id, prompt_name=None, prompt_description=None, prompt_template=None, model_id=None):
    """
    Updates an Amazon Bedrock managed prompt.

    Args:
    client: Amazon Bedrock Agent boto3 client.
    prompt_id (str): The ID for the prompt that you want to update.
    prompt_name (str, optional): The new name for the prompt.
    prompt_description (str, optional): The new description for the prompt.
    prompt_template (str, optional): The new template for the prompt.
    model_id (str, optional): The new model ID to associate with the prompt.

    Returns:
        dict: Prompt information if successful.
    """
    try:
        logger.info("Updating prompt: %s.", prompt_id)
        
        # Create update parameters
        update_params = {
            'promptIdentifier': prompt_id,
            'name': prompt_name if prompt_name else prompt_id  # Use prompt_id as name if not provided
        }
            
        if prompt_description:
            update_params['description'] = prompt_description
            
        # If we need to update the template, we need to update the variant
        if prompt_template or model_id:
            variant = {
                "name": "default",
                "templateType": "TEXT",
                "templateConfiguration": {
                    "text": {
                        "text": prompt_template,
                        "inputVariables": []
                    }
                }
            }
            
            # Update input variables
            import re
            variables = re.findall(r'{{(.*?)}}', prompt_template)
            variant['templateConfiguration']['text']['inputVariables'] = [{"name": var.strip()} for var in variables]
            
            # Update model ID if provided
            if model_id:
                variant['modelId'] = model_id
                
            # Add the variant to the update parameters
            update_params['variants'] = [variant]
            
        response = client.update_prompt(**update_params)

        logger.info("Successfully updated prompt: %s. ID: %s",
                    prompt_name if prompt_name else prompt_id,
                    response['id'])

        return response

    except ClientError as e:
        logger.exception("Client error updating prompt: %s", str(e))
        raise

    except Exception as e:
        logger.exception("Unexpected error updating prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.update_prompt]

# snippet-start:[python.example_code.bedrock.delete_prompt]
def delete_prompt(client, prompt_id, skip_resource_in_use_check=False):
    """
    Deletes an Amazon Bedrock managed prompt.

    Args:
    client: Amazon Bedrock Agent boto3 client.
    prompt_id (str): The identifier of the prompt that you want to delete.
    skip_resource_in_use_check (bool, optional): Whether to skip checking if the prompt is in use.
                                               This parameter is ignored as it's not supported by the API.

    Returns:
        dict: The response from the DeletePrompt operation.
    """
    try:
        logger.info("Deleting prompt ID: %s.", prompt_id)

        response = client.delete_prompt(
            promptIdentifier=prompt_id
        )

        logger.info("Finished deleting prompt ID: %s", prompt_id)

        return response

    except ClientError as e:
        logger.exception("Client error deleting prompt: %s", str(e))
        raise

    except Exception as e:
        logger.exception("Unexpected error deleting prompt: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.delete_prompt]

# snippet-start:[python.example_code.bedrock.list_prompts]
def list_prompts(client, max_results=10):
    """
    Lists Amazon Bedrock managed prompts.

    Args:
        client: Amazon Bedrock Agent boto3 client.
        max_results (int): Maximum number of results to return per page.

    Returns:
        list: A list of prompt summaries.
    """
    try:
        finished = False
        all_prompts = []
        next_token = None

        logger.info("Listing prompts:")

        while not finished:
            if next_token:
                response = client.list_prompts(maxResults=max_results, nextToken=next_token)
            else:
                response = client.list_prompts(maxResults=max_results)

            all_prompts.extend(response.get('promptSummaries', []))

            if 'nextToken' in response:
                next_token = response['nextToken']
            else:
                finished = True

        logger.info("Successfully listed %s prompts.", len(all_prompts))
        return all_prompts

    except ClientError as e:
        logger.exception("Client error listing prompts: %s", str(e))
        raise
    except Exception as e:
        logger.exception("Unexpected error listing prompts: %s", str(e))
        raise
# snippet-end:[python.example_code.bedrock.list_prompts]
