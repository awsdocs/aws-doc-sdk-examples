# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock
to create and use Amazon Bedrock managed prompts.

This scenario demonstrates the following:
1. Create a managed prompt
2. Invoke the prompt
3. Update the prompt
4. Invoke the updated prompt
5. Clean up resources (optional)
"""

import argparse
import boto3
import logging
import time
import os
import sys

# Add the parent directory to sys.path if needed
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
if parent_dir not in sys.path:
    sys.path.insert(0, parent_dir)

# Now import the modules
from prompts.prompt import create_prompt, update_prompt, delete_prompt
from prompts.run_prompt import invoke_prompt

logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s: %(message)s'
)
logger = logging.getLogger(__name__)

def wait_for_prompt_status(client, prompt_id, target_status, max_attempts=30, delay=2):
    """Wait for a prompt to reach a specific status."""
    logger.info("Waiting for prompt %s to reach status: %s", prompt_id, target_status)
    
    # For Amazon Bedrock prompts, we don't need to wait for a specific status
    # as they are immediately available after creation
    logger.info("Prompt %s is ready to use", prompt_id)
    return True

def run_scenario(bedrock_client, bedrock_runtime_client, model_id, cleanup=True):
    """
    Runs the Amazon Bedrock managed prompt scenario.
    
    Args:
        bedrock_client: The Amazon Bedrock Agent client.
        bedrock_runtime_client: The Amazon Bedrock Runtime client.
        model_id (str): The model ID to use for the prompt.
        cleanup (bool): Whether to clean up resources at the end of the scenario.
        
    Returns:
        dict: A dictionary containing the created resources.
    """
    prompt_id = None
    resources = {}
    
    try:
        # Step 1: Create a prompt
        print("\n=== Step 1: Creating a prompt ===")
        prompt_name = f"ProductDescriptionGenerator-{int(time.time())}"
        prompt_description = "Generates product descriptions for e-commerce websites"
        prompt_template = """
        Create a compelling product description for an e-commerce website.

        Product Name: {{product_name}}
        Product Category: {{category}}
        Key Features: {{features}}
        Target Audience: {{audience}}

        The description should be engaging, highlight the key features, and appeal to the target audience.
        Keep it between 100-150 words.
        """
        
        create_response = create_prompt(
            bedrock_client,
            prompt_name,
            prompt_description,
            prompt_template,
            model_id
        )
        
        prompt_id = create_response['id']
        resources['prompt_id'] = prompt_id
        print(f"Created prompt: {prompt_name} with ID: {prompt_id}")
        
        # Wait for the prompt to be ready
        wait_for_prompt_status(bedrock_client, prompt_id, "Available")
        
        # Step 2: Invoke the prompt directly
        print("\n=== Step 2: Invoking the prompt ===")
        input_variables = {
            "product_name": "UltraFit Smart Watch",
            "category": "Wearable Technology",
            "features": "Heart rate monitoring, GPS tracking, 7-day battery life, water resistant to 50m",
            "audience": "Fitness enthusiasts and active professionals"
        }
        
        result = invoke_prompt(
            bedrock_runtime_client,
            prompt_id,
            None,  # No version specified
            input_variables
        )
        
        print("\nGenerated Product Description:")
        print(result['output'])
        
        # Step 3: Update the prompt
        print("\n=== Step 3: Updating the prompt ===")
        updated_template = """
        Create a compelling product description for an e-commerce website.

        Product Name: {{product_name}}
        Product Category: {{category}}
        Key Features: {{features}}
        Target Audience: {{audience}}
        Price Point: {{price_point}}

        The description should be engaging, highlight the key features, and appeal to the target audience.
        Emphasize the value proposition based on the price point.
        Keep it between 100-150 words.
        """
        
        update_prompt(
            bedrock_client,
            prompt_id,
            prompt_template=updated_template
        )
        
        print("Updated prompt template to include price point")
        
        # Wait for the prompt to be ready after update
        wait_for_prompt_status(bedrock_client, prompt_id, "Available")
        
        # Step 4: Invoke the updated prompt
        print("\n=== Step 4: Invoking the updated prompt ===")
        updated_input_variables = {
            "product_name": "UltraFit Smart Watch",
            "category": "Wearable Technology",
            "features": "Heart rate monitoring, GPS tracking, 7-day battery life, water resistant to 50m",
            "audience": "Fitness enthusiasts and active professionals",
            "price_point": "Premium ($299)"
        }
        
        updated_result = invoke_prompt(
            bedrock_runtime_client,
            prompt_id,
            None,  # No version specified
            updated_input_variables
        )
        
        print("\nGenerated Product Description (with price point):")
        print(updated_result['output'])
        
        # Step 5: Clean up resources (optional)
        if cleanup:
            print("\n=== Step 5: Cleaning up resources ===")
            
            # Delete the prompt
            print(f"Deleting prompt {prompt_id}...")
            delete_prompt(bedrock_client, prompt_id)
            
            print("Cleanup complete")
        else:
            print("\n=== Resources were not cleaned up ===")
            print(f"Prompt ID: {prompt_id}")
        
        return resources
        
    except Exception as e:
        logger.exception("Error in scenario: %s", str(e))
        
        # Attempt to clean up if an error occurred and cleanup was requested
        if cleanup and prompt_id:
            try:
                print("\nCleaning up resources after error...")
                
                # Delete the prompt
                try:
                    delete_prompt(bedrock_client, prompt_id)
                    print("Cleanup after error complete")
                except Exception as cleanup_error:
                    logger.error("Error during cleanup: %s", str(cleanup_error))
            except Exception as final_error:
                logger.error("Final error during cleanup: %s", str(final_error))
        
        # Re-raise the original exception
        raise

def main():
    """
    Entry point for the Amazon Bedrock managed prompt scenario.
    """
    parser = argparse.ArgumentParser(
        description="Run the Amazon Bedrock managed prompt scenario."
    )
    parser.add_argument(
        '--region',
        default='us-east-1',
        help="The AWS Region to use."
    )
    parser.add_argument(
        '--model-id',
        default='anthropic.claude-v2',
        help="The model ID to use for the prompt."
    )
    parser.add_argument(
        '--cleanup',
        action='store_true',
        default=True,
        help="Clean up resources at the end of the scenario."
    )
    parser.add_argument(
        '--no-cleanup',
        action='store_false',
        dest='cleanup',
        help="Don't clean up resources at the end of the scenario."
    )
    args = parser.parse_args()

    bedrock_client = boto3.client('bedrock-agent', region_name=args.region)
    bedrock_runtime_client = boto3.client('bedrock-runtime', region_name=args.region)
    
    print("=== Amazon Bedrock Managed Prompt Scenario ===")
    print(f"Region: {args.region}")
    print(f"Model ID: {args.model_id}")
    print(f"Cleanup resources: {args.cleanup}")
    
    try:
        run_scenario(
            bedrock_client,
            bedrock_runtime_client,
            args.model_id,
            args.cleanup
        )
        
    except Exception as e:
        logger.exception("Error running scenario: %s", str(e))
        
if __name__ == "__main__":
    main()
