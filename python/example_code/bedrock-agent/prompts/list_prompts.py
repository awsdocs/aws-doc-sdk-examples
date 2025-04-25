# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock
to list Amazon Bedrock managed prompts.
"""

import argparse
import boto3
import logging

from prompt import list_prompts

logging.basicConfig(
    level=logging.INFO,
    format='%(levelname)s: %(message)s'
)
logger = logging.getLogger(__name__)

def main():
    """
    Lists Amazon Bedrock managed prompts.
    """
    parser = argparse.ArgumentParser(
        description="Lists Amazon Bedrock managed prompts."
    )
    parser.add_argument(
        '--region',
        default='us-east-1',
        help="The AWS Region to use."
    )
    args = parser.parse_args()

    # Use bedrock-agent client for prompt management
    bedrock_client = boto3.client('bedrock-agent', region_name=args.region)
    
    try:

        # List all prompts
        print("\n=== Amazon Bedrock Managed Prompts ===")
        prompts = list_prompts(bedrock_client)
        
        if not prompts:
            print("No prompts found in this region.")
            return
            
        for prompt in prompts:
            print(f"ID: {prompt['id']}")
            print(f"Name: {prompt['name']}")
            print(f"Description: {prompt.get('description', 'No description')}")
            print(f"ARN: {prompt.get('arn', 'No ARN')}")
            print(f"Created: {prompt.get('createdAt', 'Unknown')}")
            print(f"Updated: {prompt.get('updatedAt', 'Unknown')}")
            print(f"Version: {prompt.get('version', 'Unknown')}")
            print("-" * 40)
        
    except Exception as e:
        logger.exception("Error listing prompts: %s", str(e))
        
if __name__ == "__main__":
    main()
