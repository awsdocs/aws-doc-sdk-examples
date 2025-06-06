# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to run a complete
knowledge base scenario.

This example demonstrates how to:
- Create an IAM role for the knowledge base
- Create a knowledge base
- Get details of a knowledge base
- Update a knowledge base
- List knowledge bases in your account
- Delete the knowledge base and IAM role
"""

import logging
from knowledge_base import run_knowledge_base_scenario

def main():
    """
    Runs the Amazon Bedrock Knowledge Bases scenario.
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    run_knowledge_base_scenario()

if __name__ == "__main__":
    main()
